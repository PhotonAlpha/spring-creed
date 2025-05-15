package com.ethan.agent.factory.filter;

import com.ethan.agent.adaptor.MockApiConfigResolver;
import com.ethan.agent.adaptor.apache.MockApiConfig;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StopWatch;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;

/**
 * @author EthanCao
 * @description spring-creed-agent
 */
@Slf4j
public class RouteRegisterEndpointFilter extends OncePerRequestFilter implements ApplicationContextAware, Ordered {
    private static final String DEFAULT_AUTHORIZATION_ENDPOINT_URI = "/endpoint/register";
    private final RequestMatcher authorizationEndpointMatcher;
    public static final ObjectMapper MAPPER = new ObjectMapper();
    private ApplicationContext applicationContext;
    @Getter
    private static MockApiConfig mockApiConfig;
    private StopWatch stopWatch = new StopWatch("CamelRouteRegisterWatcher");
    public RouteRegisterEndpointFilter() {
        this.authorizationEndpointMatcher = createDefaultRequestMatcher(DEFAULT_AUTHORIZATION_ENDPOINT_URI);
        log.info("@.@[CamelRouteRegisterEndpointFilter registered!]@.@");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!this.authorizationEndpointMatcher.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        if (stopWatch.isRunning()) {
            stopWatch.stop();
        }
        stopWatch = new StopWatch("CamelRouteRegisterWatcher");

        RequestConfigPayload requestConfigPayload = null;
        try {
            requestConfigPayload = MAPPER.readValue(request.getInputStream(), RequestConfigPayload.class);
        } catch (IOException e) {
            this.sendAuthorizationResponse(request, response, new ResponsePayload("Please follow below request payload format!"));
            return;
        }
        log.info("@.@[RequestConfigPayload:{}]@.@", requestConfigPayload);
        // var routeDefinitionList = new ArrayList<RouteDefinition>();
        // var restDefinitionList = new ArrayList<RestDefinition>();
        ResponsePayload responseBody = null;
        // List<String> installedRoutes = new ArrayList<>();
        // List<String> installedRests = new ArrayList<>();
        // try {
        //     loadRouteDefinitionsViaXml(requestConfigPayload, routeDefinitionList);
        //
        //     loadRestDefinitionsViaXml(requestConfigPayload, restDefinitionList);
        //
        //     SpringCamelContext camelContext = applicationContext.getBean(SpringCamelContext.class);
        //     //1. remove existing routes
        //     List<String> uninstalledRoutes = new ArrayList<>();
        //     uninstallRouteDefinitions(camelContext, restDefinitionList, uninstalledRoutes, routeDefinitionList);
        //
        //     if (InstallAction.UNINSTALL.equals(requestConfigPayload.getAction())) {
        //         System.out.println(prettyPrint());
        //         this.sendAuthorizationResponse(request, response, new ResponsePayload(installedRoutes, installedRests, uninstalledRoutes));
        //         return;
        //     }
        //     //2. add/update routes
        //     addRouteDefinitions(camelContext, routeDefinitionList, installedRoutes);
        //     addRestDefinitions(camelContext, restDefinitionList);
        //     installRestDefinitions(restDefinitionList, camelContext, installedRests);
        //     printRunningRests(camelContext);
        //     System.out.println(prettyPrint());
        //
        //     responseBody = new ResponsePayload(installedRoutes, installedRests, uninstalledRoutes);
        // } catch (Exception e) {
        //     log.error("@.@[addRestDefinitions:{}]@.@", e.getMessage());
        //     responseBody = new ResponsePayload(e.getMessage());
        // }
        this.sendAuthorizationResponse(request, response, responseBody);
    }

    // private static void loadRestDefinitionsViaXml(RequestConfigPayload requestConfigPayload, ArrayList<RestDefinition> restDefinitionList) {
    //     // ApplicationContext xmlApplicationContext = new FileSystemXmlApplicationContext("file:" + requestConfigPayload.getRestPath());
    //     List<String> restPaths = requestConfigPayload.getRestPath();
    //     if (!CollectionUtils.isEmpty(restPaths)) {
    //         for (String restPath : restPaths) {
    //             log.info("@.@[loadRestDefinitionsViaXml:{}]@.@", restPath);
    //             ApplicationContext xmlApplicationContext = new ClassPathXmlApplicationContext(restPath);
    //             String[] beanDefinitionNames = xmlApplicationContext.getBeanDefinitionNames();
    //             for (String beanDefinitionName : beanDefinitionNames) {
    //                 List routeDefinitions = xmlApplicationContext.getBean(beanDefinitionName, List.class);
    //                 for (Object o : routeDefinitions) {
    //                     if (o instanceof RestDefinition) {
    //                         var definition = (RestDefinition) o;
    //                         restDefinitionList.add(definition);
    //                     }
    //                 }
    //             }
    //         }
    //     }
    // }
    //
    // private static void loadRouteDefinitionsViaXml(RequestConfigPayload requestConfigPayload, List<RouteDefinition> routeDefinitionList) {
    //     // ApplicationContext xmlApplicationContext = new FileSystemXmlApplicationContext("file:" + requestConfigPayload.getRoutePath());
    //     List<String> routePaths = requestConfigPayload.getRoutePath();
    //     if (!CollectionUtils.isEmpty(routePaths)) {
    //         for (String routePath : routePaths) {
    //             log.info("@.@[loadRouteDefinitionsViaXml:{}]@.@", routePath);
    //             ApplicationContext xmlApplicationContext = new ClassPathXmlApplicationContext(routePath);
    //             String[] beanDefinitionNames = xmlApplicationContext.getBeanDefinitionNames();
    //             for (String beanDefinitionName : beanDefinitionNames) {
    //                 List routeDefinitions = xmlApplicationContext.getBean(beanDefinitionName, List.class);
    //                 for (Object o : routeDefinitions) {
    //                     if (o instanceof RouteDefinition) {
    //                         var definition = (RouteDefinition) o;
    //                         routeDefinitionList.add(definition);
    //                     }
    //                 }
    //             }
    //         }
    //     }
    // }
    //
    // private void uninstallRouteDefinitions(SpringCamelContext camelContext, ArrayList<RestDefinition> restDefinitionList, List<String> uninstalledRoutes, ArrayList<RouteDefinition> routeDefinitionList) {
    //     try {
    //         stopWatch.start("uninstallRouteDefinitions");
    //         List<RouteDefinition> runningRouteDefinitions = camelContext.getRouteDefinitions();
    //         for (final RestDefinition xmlDefinition : restDefinitionList) {
    //             final List<RouteDefinition> routeDefinitions = xmlDefinition.asRouteDefinition(camelContext);
    //             for (RouteDefinition routeDefinition : routeDefinitions) {
    //                 String routeId = findRestByUrl(runningRouteDefinitions, routeDefinition, xmlDefinition);
    //                 if (StringUtils.isNotBlank(routeId)) {
    //                     camelContext.stopRoute(routeId);
    //                     camelContext.removeRoute(routeId);
    //                     uninstalledRoutes.add(routeId);
    //                 }
    //             }
    //         }
    //         for (RouteDefinition routeDefinition : routeDefinitionList) {
    //             String routeId = findRouteByUrl(runningRouteDefinitions, routeDefinition);
    //             if (StringUtils.isNotBlank(routeId)) {
    //                 camelContext.stopRoute(routeId);
    //                 camelContext.removeRoute(routeId);
    //                 uninstalledRoutes.add(routeId);
    //             }
    //         }
    //         var quartzRoutes = findQuartzRoutes(runningRouteDefinitions);
    //         log.debug("@.@[closing quartzRoutes:{}]@.@", quartzRoutes);
    //         uninstalledRoutes.addAll(quartzRoutes);
    //     } catch (Exception e) {
    //         //ignore
    //         log.error("@.@[removeRouteDefinitions exception:{}]@.@", e.getMessage());
    //     } finally {
    //         stopWatch.stop();
    //     }
    // }
    //
    // private void addRouteDefinitions(SpringCamelContext camelContext, ArrayList<RouteDefinition> routeDefinitionList, List<String> installedRoutes) {
    //     try {
    //         stopWatch.start("addRouteDefinitions");
    //         camelContext.addRouteDefinitions(routeDefinitionList);
    //
    //         // add installed Routes name
    //         routeDefinitionList.forEach(rou -> installedRoutes.add(rou.getId()));
    //     } catch (Exception e) {
    //         //ignore
    //         log.error("@.@[addRouteDefinitions exception:{}]@.@", e.getMessage());
    //     } finally {
    //         stopWatch.stop();
    //     }
    // }
    //
    // private void addRestDefinitions(SpringCamelContext camelContext, ArrayList<RestDefinition> restDefinitionList) {
    //     try {
    //         stopWatch.start("addRestDefinitions");
    //         camelContext.addRestDefinitions(restDefinitionList);
    //     } catch (Exception e) {
    //         //ignore
    //         log.error("@.@[addRestDefinitions exception:{}]@.@", e.getMessage());
    //     } finally {
    //         stopWatch.stop();
    //     }
    // }
    //
    // private void installRestDefinitions(List<RestDefinition> restDefinitionList, SpringCamelContext camelContext, List<String> installedRests) {
    //     try {
    //         stopWatch.start("installRestDefinitions");
    //         for (final RestDefinition xmlDefinition : restDefinitionList) {
    //             final List<RouteDefinition> routeDefinitions = xmlDefinition.asRouteDefinition(camelContext);
    //
    //             camelContext.addRouteDefinitions(routeDefinitions);
    //
    //             // add installed Rests name
    //             routeDefinitions.forEach(def -> installedRests.add(def.getId()));
    //         }
    //     } catch (Exception e) {
    //         //ignore
    //         log.error("@.@[installRestDefinitions REST exception:{}]@.@", e.getMessage());
    //     } finally {
    //         stopWatch.stop();
    //     }
    // }
    //
    // public List<String> findQuartzRoutes(List<RouteDefinition> runningRouteDefinitions) {
    //     return runningRouteDefinitions.stream().filter(rdef -> StringUtils.contains(rdef.getInputs().get(0).getUri(), "quartz2"))
    //             .map(OptionalIdentifiedDefinition::getId)
    //             .collect(Collectors.toList());
    // }
    // public void printRunningRests(CamelContext camelContext) {
    //     log.info("@.@[------Printing Running Rests Start------]@.@");
    //     camelContext.getRoutes().stream()
    //             .filter(route -> BooleanUtils.toBoolean((String) route.getProperties().getOrDefault("rest", "false")))
    //             .map(Route::getConsumer)
    //             .map(Objects::toString)
    //             .forEach(log::info);
    //     log.info("@.@[------Printing Running Rests End------]@.@");
    // }
    // public String findRouteByUrl(List<RouteDefinition> runningRouteDefinitions, RouteDefinition definition) {
    //     if (definition.hasCustomIdAssigned()) {
    //         if (runningRouteDefinitions.stream().anyMatch(def -> StringUtils.equals(def.getId(), definition.getId()))) {
    //             log.info("@.@[Route hasCustomIdAssigned:{}]@.@", definition.getId());
    //             return definition.getId();
    //         } else {
    //             log.info("@.@[Route {} not running]@.@", definition.getId());
    //         }
    //     }
    //     String uri = definition.getInputs().get(0).getUri();
    //     String routeId = runningRouteDefinitions.stream().filter(rdef -> StringUtils.equals(rdef.getInputs().get(0).getUri(), uri))
    //             .findFirst()
    //             .map(OptionalIdentifiedDefinition::getId)
    //             .orElse("");
    //     log.info("@.@[looking for Route uri:{} routeId:{}]@.@", uri, routeId);
    //     return routeId;
    // }
    //
    // public String findRestByUrl(List<RouteDefinition> runningRouteDefinitions, RouteDefinition definition, RestDefinition restDefinition) {
    //     if (definition.hasCustomIdAssigned()) {
    //         if (runningRouteDefinitions.stream().anyMatch(def -> StringUtils.equals(def.getId(), definition.getId()))) {
    //             log.info("@.@[Rest hasCustomIdAssigned:{}]@.@", definition.getId());
    //             return definition.getId();
    //         } else {
    //             log.info("@.@[Rest findRestByUrl {} not running]@.@", definition.getId());
    //         }
    //     }
    //
    //     String uri = definition.getInputs().get(0).getUri();
    //     String restPath = extraUrl(uri);
    //     log.info("@.@[checking restPath:{}]@.@", restPath);
    //     String restDefinitionPath = restDefinition.getPath();
    //     String routeId = runningRouteDefinitions.stream()
    //             .filter(rdef ->
    //                     StringUtils.equals(extraUrl(rdef.getInputs().get(0).getUri()), restPath) &&
    //                             StringUtils.equals(rdef.getRestDefinition().getPath(), restDefinitionPath)
    //             )
    //             .findFirst()
    //             .map(OptionalIdentifiedDefinition::getId)
    //             .orElse("");
    //
    //     log.info("@.@[Rest looking for uri:{} routeId:{}]@.@", restPath, routeId);
    //     return routeId;
    // }
    // private String extraUrl(String uri) {
    //     log.trace("@.@[extraUrl:{}]@.@", uri);
    //     String regex = ".*:(.*?)(\\?.*)?$";
    //     Pattern pattern = Pattern.compile(regex);
    //     Matcher matcher = pattern.matcher(uri);
    //     if (matcher.matches()) {
    //         return matcher.group(1);
    //     }
    //     return "";
    // }

    private String prettyPrint() {
        StringBuilder sb = new StringBuilder("StopWatch '" + stopWatch.getId() + "': running time = " + stopWatch.getTotalTimeMillis() + " s");
        sb.append('\n');
        sb.append("---------------------------------------------\n");
        sb.append("ms         %     Task name\n");
        sb.append("---------------------------------------------\n");
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMinimumIntegerDigits(9);
        nf.setGroupingUsed(false);
        NumberFormat pf = NumberFormat.getPercentInstance();
        pf.setMinimumIntegerDigits(3);
        pf.setGroupingUsed(false);
        for (StopWatch.TaskInfo task : stopWatch.getTaskInfo()) {
            sb.append(nf.format(task.getTimeMillis())).append("  ");
            sb.append(pf.format((double) task.getTimeNanos() / stopWatch.getTotalTimeNanos())).append("  ");
            sb.append(task.getTaskName()).append('\n');
        }
        return sb.toString();
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        mockApiConfig = MockApiConfigResolver.simple().apply(applicationContext);
        log.debug("@.@[mockApiConfig:{}]@.@", mockApiConfig);
    }

    private static RequestMatcher createDefaultRequestMatcher(String authorizationEndpointUri) {

        return new AntPathRequestMatcher(authorizationEndpointUri,
                HttpMethod.POST.name());
    }

    private void sendAuthorizationResponse(HttpServletRequest request, HttpServletResponse response,
                                           ResponsePayload responseBody) throws IOException {
        response.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        IOUtils.write(MAPPER.writeValueAsBytes(responseBody), response.getOutputStream());
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    static class ResponsePayload {
        private List<String> installedRoutes;
        private List<String> installedRests;
        private List<String> uninstalledRoutes;
        private String errorMessage;
        private RequestConfigPayload installPattern;
        private RequestConfigPayload uninstallPattern;

        public ResponsePayload(List<String> installedRoutes, List<String> installedRests, List<String> uninstalledRoutes) {
            this.installedRoutes = installedRoutes;
            this.installedRests = installedRests;
            this.uninstalledRoutes = uninstalledRoutes;
        }

        public ResponsePayload(String errorMessage) {
            this.errorMessage = errorMessage;
            this.installPattern = new RequestConfigPayload(Collections.singletonList("rests/rest-bulk-2fa.xml"), Collections.singletonList("routes/route-bulk-2fa.xml"), InstallAction.INSTALL);
            this.uninstallPattern = new RequestConfigPayload(Collections.singletonList("rests/rest-bulk-2fa.xml"), Collections.singletonList("routes/route-bulk-2fa.xml"), InstallAction.UNINSTALL);
        }
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class RequestConfigPayload {
        private List<String> restPath;
        private List<String> routePath;
        private InstallAction action;
    }

    enum InstallAction {
        INSTALL,UNINSTALL
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }
}
