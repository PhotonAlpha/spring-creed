package com.ethan.elastic.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class ElasticsearchConfiguration
        implements FactoryBean<RestHighLevelClient>, InitializingBean, DisposableBean
{
	private static final Logger log = org.slf4j.LoggerFactory.getLogger(ElasticsearchConfiguration.class);
	@Value("${spring.data.elasticsearch.host}")
    private String host;
    @Value("${spring.data.elasticsearch.port}")
    private int port;
    @Value("${spring.data.elasticsearch.username}")
    private String username;
    @Value("${spring.data.elasticsearch.password}")
    private String password;

    private RestHighLevelClient restHighLevelClient;

    @Override
    public void destroy() throws Exception {
        try {
            log.info("Closing elasticSearch client");
            if (restHighLevelClient != null) {
                restHighLevelClient.close();
            }
        } catch (IOException e) {
            log.error(" Error Closing elasticSearch client", e);
            e.printStackTrace();
        }
    }

    @Override
    public RestHighLevelClient getObject() throws Exception {
        return restHighLevelClient;
    }

    @Override
    public Class<?> getObjectType() {
        return RestHighLevelClient.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        buildClient();
    }

    private void buildClient() {
        RestClientBuilder builder = RestClient.builder(new HttpHost(host, port));
        this.restHighLevelClient = new RestHighLevelClient(builder);
    }

    /* alternative
    @Bean(name = "highClient", destroyMethod = "close")
    public RestHighLevelClient client() {
        return new RestHighLevelClient(RestClient.builder(new HttpHost(host,port)));
    }

    public static final String INDEX_NAME = "my_index";
    @Bean
    public IndexRequest buildIndexRequest(){
        return new IndexRequest(INDEX_NAME);
    }*/
}
