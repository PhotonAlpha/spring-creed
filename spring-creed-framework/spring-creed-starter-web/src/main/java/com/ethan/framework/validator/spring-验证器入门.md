# spring boot 自定义验证
## 实现方式一 ： `implements org.springframework.validation.Validator`
此实现方式可以用来验证请求体，当需要查询数据库验证数据正确性的时候，可以使用此方式

使用步骤：
1. `implements org.springframework.validation.Validator`,如 `DeptValidator`
2. 在controller中添加如下代码:
    ```java
    @Resource
    private DeptValidator deptValidator;
    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        if (Optional.ofNullable(binder.getTarget()).map(Object::getClass).filter(deptValidator::supports).isPresent()) {
            binder.addValidators(deptValidator);
        }
    }
    ```
   每次request请求都会调用一次`@InitBinder`，启用验证器
3. 添加`@Validated` 在 `@RequestBody DeptCreateReqVO reqVO` 处

发起请求就会进入验证器

## 实现方式二 ： `implements jakarta.validation.ConstraintValidator<IdIdentifier, Object>`  
此实现方式可以用来验证请求体中的某一个字段或者请求参数，如`@PathVariable` `@RequestHeader` `请求体中的某个字段`，当需要查询数据库验证数据正确性的时候，可以使用此方式

1. 创建注解
    ```java
    @Target({METHOD, FIELD, PARAMETER})
    @Retention(RUNTIME)
    @Documented
    @Constraint(validatedBy = {IdIdentifierValidator.class})
    public @interface IdIdentifier {
        String message() default "id not found ";
    
        Class<?>[] groups() default {};
    
        Class<? extends Payload>[] payload() default {};
    }
    ```
2. 实现 `implements ConstraintValidator<IdIdentifier, Object>`, 如 `IdIdentifierValidator.java`
3. 启用注解，在类上需要添加`@Validated`, 如果自定义`IdIdentifier`会用到分组的话，还需要将分组添加到类上。
    ```java
        @Validated({Default.class, ReferenceNumGroup.class})
        public class DeptController {}
    ```
   在类级别上使用 `@Validated` 注解时，并不要求使用自定义注解才会生效。`@Validated` 注解可用于**启用方法参数的验证**，而**无论参数是否使用了自定义注解，都将受到 `@Validated` 注解的影响。**。   

4. `@PathVariable @IdIdentifier Long id` 
添加注解启用验证器
5. 如果是验证 `@RequestBody DeptCreateReqVO reqVO`，则需要在使用`@RequestBody @Validated({Default.class, ReferenceNumGroup.class}) DeptCreateReqVO reqVO`，这样会验证requestBody中的属性。

## 基于业务实现自定义校验器
### 实体类
```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Artisan {

    private String id;


    @NotEmpty(message = "Code不能为空")
    private String code;

    @NotBlank(message = "名字为必填项")
    private String name;


    @Length(min = 8, max = 12, message = "password长度必须位于8到12之间")
    private String password;


    @Email(message = "请填写正确的邮箱地址")
    private String email;


    private String sex;

    private String phone;

}
```
### 定义两个自定义注解
```java
/**
 *
 * 自定义 "用户唯一" 校验注解 .唯一包含 -----------> 用户名+手机号码+邮箱
 * @author artisan
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE})
@Constraint(validatedBy = ArtisanValidator.UniqueArtisanValidator.class)
public @interface UniqueArtisan {

    String message() default "用户名、手机号码、邮箱不允许与现存用户重复";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

/**
 *  表示一个用户的信息是无冲突的
 *  “无冲突”是指该用户的敏感信息与其他用户不重合，比如将一个注册用户的邮箱、手机，修改成与另外一个已存在的注册用户一致的值，这样不行
 * @author artisan
 */
@Documented
@Retention(RUNTIME)
@Target({FIELD, METHOD, PARAMETER, TYPE})
@Constraint(validatedBy = ArtisanValidator.NotConflictArtisanValidator.class)
public @interface NotConflictArtisan {


   String message() default "用户名称、邮箱、手机号码与现存用户产生重复";

   Class<?>[] groups() default {};

   Class<? extends Payload>[] payload() default {};
}
```
### 自定义校验器
```java
@Slf4j
public class ArtisanValidator<T extends Annotation> implements ConstraintValidator<T, Artisan> {

    protected Predicate<Artisan> predicate = c -> true;

    @Resource
    protected ArtisanDao artisanDao;

    @Override
    public boolean isValid(Artisan artisan, ConstraintValidatorContext constraintValidatorContext) {
        return artisanDao == null || predicate.test(artisan);
    }

    /**
     * 校验用户是否唯一
     * 即判断数据库是否存在当前新用户的信息，如用户名，手机，邮箱
     */
    public static class UniqueArtisanValidator extends ArtisanValidator<UniqueArtisan> {
        @Override
        public void initialize(UniqueArtisan uniqueArtisan) {
            predicate = c -> !artisanDao.existsByNameOrEmailOrPhone(c.getName(), c.getEmail(), c.getPhone());
        }
    }

    /**
     * 校验是否与其他用户冲突
     * 将用户名、邮件、电话改成与现有完全不重复的，或者只与自己重复的，就不算冲突
     */
    public static class NotConflictArtisanValidator extends ArtisanValidator<NotConflictArtisan> {
        @Override
        public void initialize(NotConflictArtisan notConflictUser) {
            predicate = c -> {
                log.info("user detail is {}", c);
                Collection<Artisan> collection = artisanDao.findByNameOrEmailOrPhone(c.getName(), c.getEmail(), c.getPhone());
                // 将用户名、邮件、电话改成与现有完全不重复的，或者只与自己重复的，就不算冲突
                return collection.isEmpty() || (collection.size() == 1 && collection.iterator().next().getId().equals(c.getId()));
            };
        }
    }
}
```
自定义验证注解需要实现 `ConstraintValidator` 接口。
- 第一个参数是 自定义注解类型
- 第二个参数是 被注解字段的类     因为需要校验多个参数， 直接传入用户对象。

需要提到的一点是 `ConstraintValidator` 接口的实现类无需添加 `@Component` 它在启动的时候就已经被加载到容器中了。

使用`Predicate`函数式接口对业务规则进行判断.

### 验证
```java
@RestController
@RequestMapping("/buziVa/artisan")
@Slf4j
@Validated
public class ArtisanController {

    @Autowired
    private ArtisanDao artisanDao;


    // POST 方法
    @PostMapping
    public Artisan createUser(@UniqueArtisan @Valid Artisan user) {
        Artisan savedUser = artisanDao.save(user);
        log.info("save user id is {}", savedUser.getId());
        return savedUser;
    }

    // PUT
    @SneakyThrows
    @PutMapping
    public Artisan updateUser(@NotConflictArtisan @Valid @RequestBody Artisan artisan) {
        Artisan editUser = artisanDao.save(artisan);
        log.info("update artisan is {}", editUser);
        return editUser;
    }

}
```

只需要在方法上加入自定义注解即可，业务逻辑中不需要添加任何业务规则的代码。

### LocalValidatorFactoryBean手动校验
```java
@Service
public class Test4Service {
	
	// 注入校验工厂类
    @Autowired
    private LocalValidatorFactoryBean validator;

    public void check(Test4Form form) {

        // 使用JDK的校验对象(无法check一览中的值,只有Spring提供的LocalValidatorFactoryBean才能都check)
        // Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<Test4Form>> errorResult;
		
		// 记性条件进行分组check,校验全部或者仅校验AgeGroup分组
        if (!form.getOperateFlag()) {
            errorResult = validator.validate(form, Default.class);
        } else {
            errorResult = validator.validate(form, Test4Form.AgeGroup.class);
        }
		
		// 若存在错误消息的话
        if (!ObjectUtils.isEmpty(errorResult) && errorResult.size() > 0) {

            Map<Integer, ErrorItemEntity> itemEntityHashMap = new HashMap<>();
            List<ErrorItemEntity> tableItemList = new ArrayList<>();
			
			/*
				默认校验之后的errorResult是无序的,我们通过其中的getPropertyPath
				获取到bean中的属性名称,然后通过反射工具类,获取该属性上标记的
				@CheckMsgOrder注解所对应的value值,然后根据此value值进行排序
			*/ 
            for (ConstraintViolation<Test4Form> error : errorResult) {

                String errorFieldName = error.getPropertyPath().toString();
                int checkOrderValue = ReflectionUtil.getCheckOrderValue(form.getClass(), errorFieldName);
                if (checkOrderValue == 0) {
                    tableItemList.add(ErrorItemEntity.of(error.getMessage(), errorFieldName));
                    continue;
                }

                itemEntityHashMap.put(checkOrderValue, ErrorItemEntity.of(error.getMessage(), errorFieldName));
            }

            // 根据Map中的key进行排序,保证错误消息按照注解@CheckMsgOrder的值由小到大进行排序
            List<ErrorItemEntity> errorList = new ArrayList<>();
            itemEntityHashMap.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEachOrdered(item -> errorList.add(item.getValue()));
            errorList.addAll(tableItemList);
            throw new ValidationException(errorList);
        }
    }
}

```
### 国际化校验
```java
@Configuration
public class InternationalConfig implements WebMvcConfigurer {

    // 默认解析器,用来设置当前会话默认的国际化语言
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver sessionLocaleResolver = new SessionLocaleResolver();
        // 指定当前项目的默认语言是中文
        sessionLocaleResolver.setDefaultLocale(Locale.SIMPLIFIED_CHINESE);
        return sessionLocaleResolver;
    }

    // 默认拦截器,用来指定切换国际化语言的参数名
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {

        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        /*
            设置国际化请求参数为language
            设置完成之后,URL中的 ?language=zh 表示读取国际化文件messages_zh.properties
         */
        localeChangeInterceptor.setParamName("language");
        return localeChangeInterceptor;
    }

    // 自定义国际化环境下要显示的校验消息
    @Bean
    public LocalValidatorFactoryBean localValidatorFactoryBean() {

        LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();

        // 使用Spring加载国际化资源文件
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        /*
            设置资源文件的前缀名称
            配合 localeChangeInterceptor 中设置的?language所对应的参数值
            就可以加载对应国际化校验消息
         */
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding("UTF-8");

        localValidatorFactoryBean.setValidationMessageSource(messageSource);
        return localValidatorFactoryBean;
    }

    // 将我们自定义的国际化语言参数拦截器放入Spring MVC的默认配置中
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }
}

```

messages_zh.properties
```properties
# 確認Q
1008Q=确定要放弃编辑的内容吗？
1019Q=确定要取消预约吗？
1021Q=确定要删除吗？

# エラーE
1001E=请输入{msgArgs}。
1002E=请选择{msgArgs}。
1003E=请输入{msgArgs}全角假名。
1004E=输入的{msgArgs}日期格式不正确。
1005E=请输入半角数字。
1006E={msgArgs}最多不能超过{max}文字。
```

### 小结
通过上面几步操作，业务校验便和业务逻辑就完全分离开来，在需要校验时用@Validated注解自动触发，或者通过代码手动触发执行。

这些注解应用于控制器、服务层、持久层等任何层次的代码之中。

在开发时可以将不带业务含义的格式校验注解放到 Bean 的类定义之上，将带业务逻辑的校验放到 Bean 的类定义的外面。

区别是放在类定义中的注解能够自动运行，而放到类外面则需要明确标出@Validated注解时才会运行。





## 实现方式三：

结合Json-schema实现校验
参考：com.ethan.framework.validator.context.JsonSchemaValidator

在线工具
- https://www.jsonschemavalidator.net/
- https://transform.tools/json-to-json-schema