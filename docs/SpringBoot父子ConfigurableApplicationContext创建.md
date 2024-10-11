# [Multi-context spring-boot application: how to define standard spring-boot properties for each child context](https://stackoverflow.com/questions/54308472/multi-context-spring-boot-application-how-to-define-standard-spring-boot-proper)

如何创建父子**ConfigurableApplicationContext**？

背景：遇到一个需求就是需要比较一个application启动时properties相比于之前的版本是否有变化，因此我们就需要加载之前的`spring.config.location`与当前版本的差异。

## 1. 创建父Spring Application

```java
    public static SpringApplicationBuilder parentBuilder;
    public static ConfigurableApplicationContext context;

    public static void main(String[] args) {
        SpringApplicationBuilder parentBuilder =
                new SpringApplicationBuilder(ParentApplication.class);
        ParentApplication.parentBuilder = parentBuilder;
        parentBuilder.properties("spring.profiles.active=local");
        context = parentBuilder.run(args);
    }
```

## 2. 在调用service的时候配置环境变量并创建ConfigurableApplicationContext

```java
public EnvironmentConfigurationVo getAllProperties(String subFolder, RouteDefinition routeDefinition, int sourceIndex) {
        Assert.notNull(routeDefinition, "routeDefinition can not be null");
        Map<String, Object> initialProperties = new HashMap<>();

        initialProperties.put("spring.profiles.active", routeDefinition.getProfiles());
        String springProfilesInclude = propertiesMatrixService.getSpringProfilesInclude(subFolder, routeDefinition);
        initialProperties.put("spring.profiles.include", springProfilesInclude);
        String springConfigLocation = propertiesMatrixService.getSpringConfigLocation(subFolder, routeDefinition);
        initialProperties.put("spring.config.location", springConfigLocation);
        String springConfigAdditionalLocation = propertiesMatrixService.getSpringConfigAdditionalLocation(subFolder, routeDefinition);
        initialProperties.put("spring.config.additional-location", springConfigAdditionalLocation);

        ConfigurableApplicationContext context = ParentApplication.parentBuilder.child(ChildApplication.class)
                .web(WebApplicationType.NONE)
                .bannerMode(Banner.Mode.OFF)
                .properties(initialProperties).run(new String[0]);
  // 由于child Application Context会继承父Application Context所有属性以及Environment，因此提取之前需要先进行过滤
        List<String> parentPropertySources = context.getEnvironment().getPropertySources().stream().map(PropertySource::getName)
                .filter(name -> StringUtils.containsAny(name, LOCAL_RESOURCE)).toList();
        for (String sourceName : parentPropertySources) {
            context.getEnvironment().getPropertySources().remove(sourceName);
        }

        ConfigurableEnvironment configurableEnvironment = context.getBean(ConfigurableEnvironment.class);
        // remove parent profiles
        ConfigurableEnvironment parentEnvironment = ParentApplication.context.getBean(ConfigurableEnvironment.class);
        Set<String> parentActiveProfiles = Stream.of(parentEnvironment.getActiveProfiles()).collect(Collectors.toSet());
        String[] childActiveProfile = Arrays.stream(configurableEnvironment.getActiveProfiles()).filter(p -> !parentActiveProfiles.contains(p)).toArray(String[]::new);
        context.getEnvironment().setActiveProfiles(childActiveProfile);
        // load all properties
        PropertySource<?> defaultProperties = propertySources.get("systemInitLogging");
        if (Objects.nonNull(defaultProperties)) {//调整systemInitLogging的优先级最低，这样能读取原本配置文件中的值
            propertySources.remove("systemInitLogging");
            propertySources.addLast(defaultProperties);
        }
        EnvironmentConfigurationVo environmentConfiguration = loadAllProperties(configurableEnvironment, sourceIndex);
    }
```

### 3. 利用actuator读取当前active的properties

```java
@Resource
private EnvironmentEndpointProperties environmentEndpointProperties;
@Resource
private ObjectProvider<SanitizingFunction> sanitizingFunctions;

protected Map<Pair<String, String>, String> loadAllActiveProperties(ConfigurableEnvironment configurableEnvironment) {
        log.info("loadingAllActiveProperties...{}", String.join(",", configurableEnvironment.getActiveProfiles()));

        MutablePropertySources propertySources = configurableEnvironment.getPropertySources();
        EnvironmentEndpoint ev = new EnvironmentEndpoint(configurableEnvironment, sanitizingFunctions, environmentEndpointProperties.getShowValues());
        Iterator<PropertySource<?>> iterator = propertySources.iterator();
        Map<Pair<String, String>, String> propMap = new TreeMap<>(Comparator.naturalOrder());

        while (iterator.hasNext()) {
            PropertySource<?> propertySource = iterator.next();
            if (propertySource instanceof EnumerablePropertySource<?> enumerablePropertySource) {
                String[] propertyNames = enumerablePropertySource.getPropertyNames();
                for (String propertyName : propertyNames) {
                    log.debug("propertyName:{}", propertyName);
                    EnvironmentEndpoint.EnvironmentEntryDescriptor descriptor = ev.environmentEntry(propertyName);
                    EnvironmentEndpoint.PropertySummaryDescriptor activeProperty = descriptor.getProperty();
                    log.debug(" activeProperty:{}", activeProperty);
                    propMap.put(Pair.of(propertyName, String.valueOf(activeProperty.getValue())), activeProperty.getSource());
                }
            }
        }
        log.info("loadingAllActiveProperties...size {}", propMap.size());
        return propMap;
    }
```



### 4. 处理child application启动时，因为未找到 xxx.yml or xxx.spring-logback.xml导致的读取失败问题。

问题的根源在Spring 启动的时候，如果未找到文件，默认会抛出异常。无法通过配置关闭这些选项。

例如 `spring.config.import=file:xxx/xxx.yml` or `logging.config=xxx.spring-logback.xml`

解决办法就是重写代码，并跳过处理相关的异常. 在项目中创建包 `org.springframework.boot.context.config`,这一步很重要，是用来覆盖Spring的源码的。然后重写相应的代码。

```java
//class1
public class ConfigDataEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    /**
     * The default order for the processor.
     */
    public static final int ORDER = Ordered.HIGHEST_PRECEDENCE + 10;

    /**
     * Property used to determine what action to take when a
     * {@code ConfigDataLocationNotFoundException} is thrown.
     * @see ConfigDataNotFoundAction
     */
    public static final String ON_LOCATION_NOT_FOUND_PROPERTY = ConfigDataEnvironment.ON_NOT_FOUND_PROPERTY;

    private final DeferredLogFactory logFactory;

    private final Log logger;

    private final ConfigurableBootstrapContext bootstrapContext;

    private final ConfigDataEnvironmentUpdateListener environmentUpdateListener;

    public ConfigDataEnvironmentPostProcessor(DeferredLogFactory logFactory,
                                              ConfigurableBootstrapContext bootstrapContext) {
        this(logFactory, bootstrapContext, null);
    }

    private ConfigDataEnvironmentPostProcessor(DeferredLogFactory logFactory,
                                               ConfigurableBootstrapContext bootstrapContext,
                                               ConfigDataEnvironmentUpdateListener environmentUpdateListener) {
        this.logFactory = logFactory;
        this.logger = logFactory.getLog(getClass());
        this.bootstrapContext = bootstrapContext;
        this.environmentUpdateListener = environmentUpdateListener;
    }

    @Override
    public int getOrder() {
        return ORDER;
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
      // 检查是否是child application
        if (application.getAllSources().stream().findFirst()
                .filter(sour -> ((Class<?>) sour).isAssignableFrom(ChildApplication.class))
                .isPresent()) {
          //如果是的话，就创建覆盖 logging.config
            Map<String,Object> prop = new HashMap<>();
            prop.put("logging.config", "");
            environment.getPropertySources().addLast(new MapPropertySource("systemInitLogging", prop));
        }
        postProcessEnvironment(environment, application.getResourceLoader(), application.getAdditionalProfiles());
    }

    void postProcessEnvironment(ConfigurableEnvironment environment, ResourceLoader resourceLoader,
                                Collection<String> additionalProfiles) {
        this.logger.trace("Post-processing environment to add config data");
        resourceLoader = (resourceLoader != null) ? resourceLoader : new DefaultResourceLoader();
        getConfigDataEnvironment(environment, resourceLoader, additionalProfiles).processAndApply();
    }

    ConfigDataEnvironment getConfigDataEnvironment(ConfigurableEnvironment environment, ResourceLoader resourceLoader,
                                                   Collection<String> additionalProfiles) {
        return new ConfigDataEnvironment(this.logFactory, this.bootstrapContext, environment, resourceLoader,
                additionalProfiles, this.environmentUpdateListener);
    }

    /**
     * Apply {@link ConfigData} post-processing to an existing {@link Environment}. This
     * method can be useful when working with an {@link Environment} that has been created
     * directly and not necessarily as part of a {@link SpringApplication}.
     * @param environment the environment to apply {@link ConfigData} to
     */
    public static void applyTo(ConfigurableEnvironment environment) {
        applyTo(environment, null, null, Collections.emptyList());
    }

    /**
     * Apply {@link ConfigData} post-processing to an existing {@link Environment}. This
     * method can be useful when working with an {@link Environment} that has been created
     * directly and not necessarily as part of a {@link SpringApplication}.
     * @param environment the environment to apply {@link ConfigData} to
     * @param resourceLoader the resource loader to use
     * @param bootstrapContext the bootstrap context to use or {@code null} to use a
     * throw-away context
     * @param additionalProfiles any additional profiles that should be applied
     */
    public static void applyTo(ConfigurableEnvironment environment, ResourceLoader resourceLoader,
                               ConfigurableBootstrapContext bootstrapContext, String... additionalProfiles) {
        applyTo(environment, resourceLoader, bootstrapContext, Arrays.asList(additionalProfiles));
    }

    /**
     * Apply {@link ConfigData} post-processing to an existing {@link Environment}. This
     * method can be useful when working with an {@link Environment} that has been created
     * directly and not necessarily as part of a {@link SpringApplication}.
     * @param environment the environment to apply {@link ConfigData} to
     * @param resourceLoader the resource loader to use
     * @param bootstrapContext the bootstrap context to use or {@code null} to use a
     * throw-away context
     * @param additionalProfiles any additional profiles that should be applied
     */
    public static void applyTo(ConfigurableEnvironment environment, ResourceLoader resourceLoader,
                               ConfigurableBootstrapContext bootstrapContext, Collection<String> additionalProfiles) {
        DeferredLogFactory logFactory = Supplier::get;
        bootstrapContext = (bootstrapContext != null) ? bootstrapContext : new DefaultBootstrapContext();
        ConfigDataEnvironmentPostProcessor postProcessor = new ConfigDataEnvironmentPostProcessor(logFactory,
                bootstrapContext);
        postProcessor.postProcessEnvironment(environment, resourceLoader, additionalProfiles);
    }

    /**
     * Apply {@link ConfigData} post-processing to an existing {@link Environment}. This
     * method can be useful when working with an {@link Environment} that has been created
     * directly and not necessarily as part of a {@link SpringApplication}.
     * @param environment the environment to apply {@link ConfigData} to
     * @param resourceLoader the resource loader to use
     * @param bootstrapContext the bootstrap context to use or {@code null} to use a
     * throw-away context
     * @param additionalProfiles any additional profiles that should be applied
     * @param environmentUpdateListener optional
     * {@link ConfigDataEnvironmentUpdateListener} that can be used to track
     * {@link Environment} updates.
     */
    public static void applyTo(ConfigurableEnvironment environment, ResourceLoader resourceLoader,
                               ConfigurableBootstrapContext bootstrapContext, Collection<String> additionalProfiles,
                               ConfigDataEnvironmentUpdateListener environmentUpdateListener) {
        DeferredLogFactory logFactory = Supplier::get;
        bootstrapContext = (bootstrapContext != null) ? bootstrapContext : new DefaultBootstrapContext();
        ConfigDataEnvironmentPostProcessor postProcessor = new ConfigDataEnvironmentPostProcessor(logFactory,
                bootstrapContext, environmentUpdateListener);
        postProcessor.postProcessEnvironment(environment, resourceLoader, additionalProfiles);
    }

}

//class2
public class ConfigDataEnvironment {

    /**
     * Property used override the imported locations.
     */
    static final String LOCATION_PROPERTY = "spring.config.location";

    /**
     * Property used to provide additional locations to import.
     */
    static final String ADDITIONAL_LOCATION_PROPERTY = "spring.config.additional-location";

    /**
     * Property used to provide additional locations to import.
     */
    static final String IMPORT_PROPERTY = "spring.config.import";

    /**
     * Property used to determine what action to take when a
     * {@code ConfigDataNotFoundAction} is thrown.
     * @see ConfigDataNotFoundAction
     */
    static final String ON_NOT_FOUND_PROPERTY = "spring.config.on-not-found";

    /**
     * Default search locations used if not {@link #LOCATION_PROPERTY} is found.
     */
    static final ConfigDataLocation[] DEFAULT_SEARCH_LOCATIONS;
    static {
        List<ConfigDataLocation> locations = new ArrayList<>();
        locations.add(ConfigDataLocation.of("optional:classpath:/;optional:classpath:/config/"));
        locations.add(ConfigDataLocation.of("optional:file:./;optional:file:./config/;optional:file:./config/*/"));
        DEFAULT_SEARCH_LOCATIONS = locations.toArray(new ConfigDataLocation[0]);
    }

    private static final ConfigDataLocation[] EMPTY_LOCATIONS = new ConfigDataLocation[0];

    private static final Bindable<ConfigDataLocation[]> CONFIG_DATA_LOCATION_ARRAY = Bindable
            .of(ConfigDataLocation[].class);

    private static final Bindable<List<String>> STRING_LIST = Bindable.listOf(String.class);

    private static final ConfigDataEnvironmentContributors.BinderOption[] ALLOW_INACTIVE_BINDING = {};

    private static final ConfigDataEnvironmentContributors.BinderOption[] DENY_INACTIVE_BINDING = { ConfigDataEnvironmentContributors.BinderOption.FAIL_ON_BIND_TO_INACTIVE_SOURCE };

    private final DeferredLogFactory logFactory;

    private final Log logger;

    private final ConfigDataNotFoundAction notFoundAction;

    private final ConfigurableBootstrapContext bootstrapContext;

    private final ConfigurableEnvironment environment;

    private final ConfigDataLocationResolvers resolvers;

    private final Collection<String> additionalProfiles;

    private final ConfigDataEnvironmentUpdateListener environmentUpdateListener;

    private final ConfigDataLoaders loaders;

    private final ConfigDataEnvironmentContributors contributors;

    /**
     * Create a new {@link ConfigDataEnvironment} instance.
     * @param logFactory the deferred log factory
     * @param bootstrapContext the bootstrap context
     * @param environment the Spring {@link Environment}.
     * @param resourceLoader {@link ResourceLoader} to load resource locations
     * @param additionalProfiles any additional profiles to activate
     * @param environmentUpdateListener optional
     * {@link ConfigDataEnvironmentUpdateListener} that can be used to track
     * {@link Environment} updates.
     */
    ConfigDataEnvironment(DeferredLogFactory logFactory, ConfigurableBootstrapContext bootstrapContext,
                          ConfigurableEnvironment environment, ResourceLoader resourceLoader, Collection<String> additionalProfiles,
                          ConfigDataEnvironmentUpdateListener environmentUpdateListener) {
        Binder binder = Binder.get(environment);
        this.logFactory = logFactory;
        this.logger = logFactory.getLog(getClass());
        this.notFoundAction = binder.bind(ON_NOT_FOUND_PROPERTY, ConfigDataNotFoundAction.class)
                .orElse(ConfigDataNotFoundAction.FAIL);
        this.bootstrapContext = bootstrapContext;
        this.environment = environment;
        this.resolvers = createConfigDataLocationResolvers(logFactory, bootstrapContext, binder, resourceLoader);
        this.additionalProfiles = additionalProfiles;
        this.environmentUpdateListener = (environmentUpdateListener != null) ? environmentUpdateListener
                : ConfigDataEnvironmentUpdateListener.NONE;
        this.loaders = new ConfigDataLoaders(logFactory, bootstrapContext,
                SpringFactoriesLoader.forDefaultResourceLocation(resourceLoader.getClassLoader()));
        this.contributors = createContributors(binder);
    }

    protected ConfigDataLocationResolvers createConfigDataLocationResolvers(DeferredLogFactory logFactory,
                                                                            ConfigurableBootstrapContext bootstrapContext, Binder binder, ResourceLoader resourceLoader) {
      // 此处重写Resolvers， 跳过异常验证
        return new MyConfigDataLocationResolvers(logFactory, bootstrapContext, binder, resourceLoader,
                SpringFactoriesLoader.forDefaultResourceLocation(resourceLoader.getClassLoader()));
    }

    private ConfigDataEnvironmentContributors createContributors(Binder binder) {
        this.logger.trace("Building config data environment contributors");
        MutablePropertySources propertySources = this.environment.getPropertySources();
        List<ConfigDataEnvironmentContributor> contributors = new ArrayList<>(propertySources.size() + 10);
        PropertySource<?> defaultPropertySource = null;
        for (PropertySource<?> propertySource : propertySources) {
            if (DefaultPropertiesPropertySource.hasMatchingName(propertySource)) {
                defaultPropertySource = propertySource;
            }
            else {
                this.logger.trace(LogMessage.format("Creating wrapped config data contributor for '%s'",
                        propertySource.getName()));
                contributors.add(ConfigDataEnvironmentContributor.ofExisting(propertySource));
            }
        }
        contributors.addAll(getInitialImportContributors(binder));
        if (defaultPropertySource != null) {
            this.logger.trace("Creating wrapped config data contributor for default property source");
            contributors.add(ConfigDataEnvironmentContributor.ofExisting(defaultPropertySource));
        }
        return createContributors(contributors);
    }

    protected ConfigDataEnvironmentContributors createContributors(
            List<ConfigDataEnvironmentContributor> contributors) {
        return new ConfigDataEnvironmentContributors(this.logFactory, this.bootstrapContext, contributors);
    }

    ConfigDataEnvironmentContributors getContributors() {
        return this.contributors;
    }

    private List<ConfigDataEnvironmentContributor> getInitialImportContributors(Binder binder) {
        List<ConfigDataEnvironmentContributor> initialContributors = new ArrayList<>();
        addInitialImportContributors(initialContributors, bindLocations(binder, IMPORT_PROPERTY, EMPTY_LOCATIONS));
        addInitialImportContributors(initialContributors,
                bindLocations(binder, ADDITIONAL_LOCATION_PROPERTY, EMPTY_LOCATIONS));
        addInitialImportContributors(initialContributors,
                bindLocations(binder, LOCATION_PROPERTY, DEFAULT_SEARCH_LOCATIONS));
        return initialContributors;
    }

    private ConfigDataLocation[] bindLocations(Binder binder, String propertyName, ConfigDataLocation[] other) {
        return binder.bind(propertyName, CONFIG_DATA_LOCATION_ARRAY).orElse(other);
    }

    private void addInitialImportContributors(List<ConfigDataEnvironmentContributor> initialContributors,
                                              ConfigDataLocation[] locations) {
        for (int i = locations.length - 1; i >= 0; i--) {
            initialContributors.add(createInitialImportContributor(locations[i]));
        }
    }

    private ConfigDataEnvironmentContributor createInitialImportContributor(ConfigDataLocation location) {
        this.logger.trace(LogMessage.format("Adding initial config data import from location '%s'", location));
        return ConfigDataEnvironmentContributor.ofInitialImport(location);
    }

    /**
     * Process all contributions and apply any newly imported property sources to the
     * {@link Environment}.
     */
    void processAndApply() {
        ConfigDataImporter importer = new ConfigDataImporter(this.logFactory, this.notFoundAction, this.resolvers,
                this.loaders);
        registerBootstrapBinder(this.contributors, null, DENY_INACTIVE_BINDING);
        ConfigDataEnvironmentContributors contributors = processInitial(this.contributors, importer);
        ConfigDataActivationContext activationContext = createActivationContext(
                contributors.getBinder(null, ConfigDataEnvironmentContributors.BinderOption.FAIL_ON_BIND_TO_INACTIVE_SOURCE));
        contributors = processWithoutProfiles(contributors, importer, activationContext);
        activationContext = withProfiles(contributors, activationContext);
        contributors = processWithProfiles(contributors, importer, activationContext);
        applyToEnvironment(contributors, activationContext, importer.getLoadedLocations(),
                importer.getOptionalLocations());
    }

    private ConfigDataEnvironmentContributors processInitial(ConfigDataEnvironmentContributors contributors,
                                                             ConfigDataImporter importer) {
        this.logger.trace("Processing initial config data environment contributors without activation context");
        contributors = contributors.withProcessedImports(importer, null);
        registerBootstrapBinder(contributors, null, DENY_INACTIVE_BINDING);
        return contributors;
    }

    private ConfigDataActivationContext createActivationContext(Binder initialBinder) {
        this.logger.trace("Creating config data activation context from initial contributions");
        try {
            return new ConfigDataActivationContext(this.environment, initialBinder);
        }
        catch (BindException ex) {
            if (ex.getCause() instanceof InactiveConfigDataAccessException inactiveException) {
                throw inactiveException;
            }
            throw ex;
        }
    }

    private ConfigDataEnvironmentContributors processWithoutProfiles(ConfigDataEnvironmentContributors contributors,
                                                                     ConfigDataImporter importer, ConfigDataActivationContext activationContext) {
        this.logger.trace("Processing config data environment contributors with initial activation context");
        contributors = contributors.withProcessedImports(importer, activationContext);
        registerBootstrapBinder(contributors, activationContext, DENY_INACTIVE_BINDING);
        return contributors;
    }

    private ConfigDataActivationContext withProfiles(ConfigDataEnvironmentContributors contributors,
                                                     ConfigDataActivationContext activationContext) {
        this.logger.trace("Deducing profiles from current config data environment contributors");
        Binder binder = contributors.getBinder(activationContext,
                (contributor) -> !contributor.hasConfigDataOption(ConfigData.Option.IGNORE_PROFILES),
                ConfigDataEnvironmentContributors.BinderOption.FAIL_ON_BIND_TO_INACTIVE_SOURCE);
        try {
            Set<String> additionalProfiles = new LinkedHashSet<>(this.additionalProfiles);
            additionalProfiles.addAll(getIncludedProfiles(contributors, activationContext));
            Profiles profiles = new Profiles(this.environment, binder, additionalProfiles);
            return activationContext.withProfiles(profiles);
        }
        catch (BindException ex) {
            if (ex.getCause() instanceof InactiveConfigDataAccessException inactiveException) {
                throw inactiveException;
            }
            throw ex;
        }
    }

    private Collection<? extends String> getIncludedProfiles(ConfigDataEnvironmentContributors contributors,
                                                             ConfigDataActivationContext activationContext) {
        PlaceholdersResolver placeholdersResolver = new ConfigDataEnvironmentContributorPlaceholdersResolver(
                contributors, activationContext, null, true);
        Set<String> result = new LinkedHashSet<>();
        for (ConfigDataEnvironmentContributor contributor : contributors) {
            ConfigurationPropertySource source = contributor.getConfigurationPropertySource();
            if (source != null && !contributor.hasConfigDataOption(ConfigData.Option.IGNORE_PROFILES)) {
                Binder binder = new Binder(Collections.singleton(source), placeholdersResolver);
                binder.bind(Profiles.INCLUDE_PROFILES, STRING_LIST).ifBound((includes) -> {
                    if (!contributor.isActive(activationContext)) {
                        InactiveConfigDataAccessException.throwIfPropertyFound(contributor, Profiles.INCLUDE_PROFILES);
                        InactiveConfigDataAccessException.throwIfPropertyFound(contributor,
                                Profiles.INCLUDE_PROFILES.append("[0]"));
                    }
                    result.addAll(includes);
                });
            }
        }
        return result;
    }

    private ConfigDataEnvironmentContributors processWithProfiles(ConfigDataEnvironmentContributors contributors,
                                                                  ConfigDataImporter importer, ConfigDataActivationContext activationContext) {
        this.logger.trace("Processing config data environment contributors with profile activation context");
        contributors = contributors.withProcessedImports(importer, activationContext);
        registerBootstrapBinder(contributors, activationContext, ALLOW_INACTIVE_BINDING);
        return contributors;
    }

    private void registerBootstrapBinder(ConfigDataEnvironmentContributors contributors,
                                         ConfigDataActivationContext activationContext, ConfigDataEnvironmentContributors.BinderOption... binderOptions) {
        this.bootstrapContext.register(Binder.class,
                BootstrapRegistry.InstanceSupplier.from(() -> contributors.getBinder(activationContext, binderOptions))
                        .withScope(BootstrapRegistry.Scope.PROTOTYPE));
    }

    private void applyToEnvironment(ConfigDataEnvironmentContributors contributors,
                                    ConfigDataActivationContext activationContext, Set<ConfigDataLocation> loadedLocations,
                                    Set<ConfigDataLocation> optionalLocations) {
        checkForInvalidProperties(contributors);
        checkMandatoryLocations(contributors, activationContext, loadedLocations, optionalLocations);
        MutablePropertySources propertySources = this.environment.getPropertySources();
        applyContributor(contributors, activationContext, propertySources);
        DefaultPropertiesPropertySource.moveToEnd(propertySources);
        Profiles profiles = activationContext.getProfiles();
        this.logger.trace(LogMessage.format("Setting default profiles: %s", profiles.getDefault()));
        this.environment.setDefaultProfiles(StringUtils.toStringArray(profiles.getDefault()));
        this.logger.trace(LogMessage.format("Setting active profiles: %s", profiles.getActive()));
        this.environment.setActiveProfiles(StringUtils.toStringArray(profiles.getActive()));
        this.environmentUpdateListener.onSetProfiles(profiles);
    }

    private void applyContributor(ConfigDataEnvironmentContributors contributors,
                                  ConfigDataActivationContext activationContext, MutablePropertySources propertySources) {
        this.logger.trace("Applying config data environment contributions");
        for (ConfigDataEnvironmentContributor contributor : contributors) {
            PropertySource<?> propertySource = contributor.getPropertySource();
            if (contributor.getKind() == ConfigDataEnvironmentContributor.Kind.BOUND_IMPORT && propertySource != null) {
                if (!contributor.isActive(activationContext)) {
                    this.logger
                            .trace(LogMessage.format("Skipping inactive property source '%s'", propertySource.getName()));
                }
                else {
                    this.logger
                            .trace(LogMessage.format("Adding imported property source '%s'", propertySource.getName()));
                    propertySources.addLast(propertySource);
                    this.environmentUpdateListener.onPropertySourceAdded(propertySource, contributor.getLocation(),
                            contributor.getResource());
                }
            }
        }
    }

    private void checkForInvalidProperties(ConfigDataEnvironmentContributors contributors) {
        for (ConfigDataEnvironmentContributor contributor : contributors) {
            InvalidConfigDataPropertyException.throwIfPropertyFound(contributor);
        }
    }

    private void checkMandatoryLocations(ConfigDataEnvironmentContributors contributors,
                                         ConfigDataActivationContext activationContext, Set<ConfigDataLocation> loadedLocations,
                                         Set<ConfigDataLocation> optionalLocations) {
        Set<ConfigDataLocation> mandatoryLocations = new LinkedHashSet<>();
        for (ConfigDataEnvironmentContributor contributor : contributors) {
            if (contributor.isActive(activationContext)) {
                mandatoryLocations.addAll(getMandatoryImports(contributor));
            }
        }
        for (ConfigDataEnvironmentContributor contributor : contributors) {
            if (contributor.getLocation() != null) {
                mandatoryLocations.remove(contributor.getLocation());
            }
        }
        mandatoryLocations.removeAll(loadedLocations);
        mandatoryLocations.removeAll(optionalLocations);
				// 当获取的环境变量中的import yml 不包含 optional：， 需要手动跳过，否则就会抛异常
        mandatoryLocations = mandatoryLocations.stream().filter(lo -> org.apache.commons.lang3.StringUtils.containsAny(lo.toString(), "/scripts", "classpath")).collect(Collectors.toSet());
        if (!mandatoryLocations.isEmpty()) {
            for (ConfigDataLocation mandatoryLocation : mandatoryLocations) {
                this.notFoundAction.handle(this.logger, new ConfigDataLocationNotFoundException(mandatoryLocation));
            }
        }
    }

    private Set<ConfigDataLocation> getMandatoryImports(ConfigDataEnvironmentContributor contributor) {
        List<ConfigDataLocation> imports = contributor.getImports();
        Set<ConfigDataLocation> mandatoryLocations = new LinkedHashSet<>(imports.size());
        for (ConfigDataLocation location : imports) {
            if (!location.isOptional()) {
                mandatoryLocations.add(location);
            }
        }
        return mandatoryLocations;
    }

}
//class3
@Slf4j
public class MyConfigDataLocationResolvers extends ConfigDataLocationResolvers {
    public MyConfigDataLocationResolvers(DeferredLogFactory logFactory, ConfigurableBootstrapContext bootstrapContext, Binder binder, ResourceLoader resourceLoader, SpringFactoriesLoader springFactoriesLoader) {
        super(logFactory, bootstrapContext, binder, resourceLoader, springFactoriesLoader);
    }

    @Override
    List<ConfigDataResolutionResult> resolve(ConfigDataLocationResolverContext context, ConfigDataLocation location,
                                             Profiles profiles) {
        if (location == null) {
            return Collections.emptyList();
        }
        for (ConfigDataLocationResolver<?> resolver : getResolvers()) {
            if (resolver.isResolvable(context, location)) {
                if (StringUtils.containsAny(location.toString(), "/scripts", "classpath")) {
                    return resolve(resolver, context, location, profiles);
                } else {
                  // 如果是不包含optional的yml文件，并且是无法读取的，手动添加optional：，这样就可以跳过文件加载验证
                    log.info("illegal location:{}", location);
                    ConfigDataLocation optionalLocation = ConfigDataLocation.of(ConfigDataLocation.OPTIONAL_PREFIX + location.toString());
                    return resolve(resolver, context, optionalLocation, profiles);
                }
            }
        }
        throw new UnsupportedConfigDataLocationException(location);
    }

    List<ConfigDataResolutionResult> resolve(ConfigDataLocationResolver<?> resolver,
                                                     ConfigDataLocationResolverContext context, ConfigDataLocation location, Profiles profiles) {
        List<ConfigDataResolutionResult> resolved = resolve(location, false, () -> resolver.resolve(context, location));
        if (profiles == null) {
            return resolved;
        }
        List<ConfigDataResolutionResult> profileSpecific = resolve(location, true,
                () -> resolver.resolveProfileSpecific(context, location, profiles));
        return merge(resolved, profileSpecific);
    }

    private List<ConfigDataResolutionResult> resolve(ConfigDataLocation location, boolean profileSpecific,
                                                     Supplier<List<? extends ConfigDataResource>> resolveAction) {
        List<ConfigDataResource> resources = nonNullList(resolveAction.get());
        List<ConfigDataResolutionResult> resolved = new ArrayList<>(resources.size());
        for (ConfigDataResource resource : resources) {
            resolved.add(new ConfigDataResolutionResult(location, resource, profileSpecific));
        }
        return resolved;
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> nonNullList(List<? extends T> list) {
        return (list != null) ? (List<T>) list : Collections.emptyList();
    }

    private <T> List<T> merge(List<T> list1, List<T> list2) {
        List<T> merged = new ArrayList<>(list1.size() + list2.size());
        merged.addAll(list1);
        merged.addAll(list2);
        return merged;
    }
}
```

至此就可以跳过不存在的 xxxx/xxx.yml 或 xxx/spring-logback.xml等，导致child application启动失败的问题。

### 总结： 由于child Application Context会继承父Application Context所有属性以及Environment，因此提取之前需要先进行过滤