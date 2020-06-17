package com.ethan.elastic;

import com.ethan.elastic.util.ElasticSearchBulkUtil;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Maps;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Cancellable;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * https://discuss.elastic.co/t/problem-setting-text-field-as-not-analyzed/90763/3    Java设置properties
 */
@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ElasticApplication.class)
public class ElasticTest {
    public static final String INDEX_NAME = "my_index";
    @Autowired
    private RestHighLevelClient client;
    @Autowired
    private ElasticSearchBulkUtil bulkUtil;
    private IndexRequest indexRequest = new IndexRequest(INDEX_NAME);
    private UpdateRequest updateRequest = new UpdateRequest();
    private DeleteRequest deleteRequest = new DeleteRequest(INDEX_NAME);
    private SearchRequest infoSearch = new SearchRequest(INDEX_NAME);
    private BulkRequest bulkRequest = new BulkRequest(INDEX_NAME);

    @Test
    void createIndexTest() throws IOException {
        String book1 = book1();

        indexRequest.id("80001").source(book1, XContentType.JSON);
        IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
        log.info("-----------------------end:{}", indexResponse);

        //IndexRequest index2 = new IndexRequest(INDEX_NAME).id("80002").source(book1, XContentType.JSON);
        //Cancellable response = client.indexAsync(index2, RequestOptions.DEFAULT, new ActionListener<IndexResponse>() {
        //    @Override
        //    public void onResponse(IndexResponse indexResponse) {
        //        log.info("Async-----------------------end:{}", indexResponse);
        //    }
        //
        //    @Override
        //    public void onFailure(Exception e) {
        //        log.info("Async-----------------------Error", e);
        //    }
        //});

    }
    @Test
    void getAPITest() throws IOException {
        BoolQueryBuilder builder = QueryBuilders.boolQuery();
//        builder.must(QueryBuilders.termQuery("language", "bluce"));
//        builder.must(QueryBuilders.termQuery("fullname", "li"));
        builder.must(QueryBuilders.termQuery("language", "java")); //如果有空格，会被分词

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(builder);
        sourceBuilder.from(0);
        sourceBuilder.size(20);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        infoSearch.source(sourceBuilder);

        SearchResponse response = client.search(infoSearch, RequestOptions.DEFAULT);
        long cnt =  response.getHits().getTotalHits().value;
        log.info("result.hit={}", cnt);

        for (SearchHit hit : response.getHits()) {
            Map<String, Object> map = hit.getSourceAsMap();
            log.info("map={}", map.toString());
            log.info("docId={}", hit.getId());
        }
    }

    @Test
    void updateAPITest() {
        /**
         * PUT blog/_doc/RTmpRnIB4KQiFA2dtK3w?version=8&version_type=external
         * {
         *   "script": "ctx._source.title=\"Python 科学计算 - update\" "
         * }
         */
        Script script = new Script(ScriptType.INLINE, Script.DEFAULT_SCRIPT_LANG, "ctx._source.title=\"Python 科学计算 - update\"", Collections.emptyMap());

        updateRequest.index(INDEX_NAME).id("80002").script(script);
        try {
            UpdateResponse response = client.update(updateRequest, RequestOptions.DEFAULT);
            log.info("update response:{}", response);
        } catch (IOException e) {
            log.info("update failure{}", e);
            e.printStackTrace();
        }
    }

    @Test
    void deleteAPITest() throws IOException {
        deleteRequest.id("80002");
        DeleteResponse response = client.delete(deleteRequest, RequestOptions.DEFAULT);
        log.info("delete result---------{}", response);
    }

    @Test
    @DisplayName("bulk 批量操作")
    void bulkRequestAPITest() {
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.add(new IndexRequest(INDEX_NAME)
                .id("b001")
                .source(book1(), XContentType.JSON));
        bulkRequest.add(new IndexRequest(INDEX_NAME)
                .id("b002")
                .source(book2(), XContentType.JSON));
        bulkRequest.add(new IndexRequest(INDEX_NAME)
                .id("b003")
                .source(book3(), XContentType.JSON));
        //异步
        Cancellable callback = client.bulkAsync(bulkRequest, RequestOptions.DEFAULT, new ActionListener<>() {
            @Override
            public void onResponse(BulkResponse bulkItemResponses) {
                log.info("bulkAsync:{}", bulkItemResponses);
            }

            @Override
            public void onFailure(Exception e) {
                log.info("bulkAsync error:{}", e);
            }
        });
    }

    /**
     * BulkProcessor 简化bulk API的使用，并且使整个批量操作透明化。
     * BulkProcessor 的执行需要三部分组成：
     *
     * 1. RestHighLevelClient :执行bulk请求并拿到响应对象。
     * 2. BulkProcessor.Listener：在执行bulk request之前、之后和当bulk response发生错误时调用。
     * 3. ThreadPool：bulk request在这个线程池中执行操作，这使得每个请求不会被挡住，在其他请求正在执行时，也可以接收新的请求。
     *
     */
    @Test
    @DisplayName("bulk processor 用法测试")
    void bulkProcessorAPITest() {
        BulkProcessor processor = bulkUtil.getBulkProcessor();
        processor.add(new IndexRequest(INDEX_NAME)
                .id("b001")
                .source(book1(), XContentType.JSON));
        processor.add(new IndexRequest(INDEX_NAME)
                .id("b002")
                .source(book2(), XContentType.JSON));
        processor.add(new IndexRequest(INDEX_NAME)
                .id("b003")
                .source(book3(), XContentType.JSON));

        processor.flush();

    }
    @Test
    @DisplayName("bulk processor 删除测试数据")
    void bulkProcessorDeleteAPITest() {
        BulkProcessor processor = bulkUtil.getBulkProcessor();
        processor.add(new DeleteRequest(INDEX_NAME)
                .id("b001"));
        processor.add(new DeleteRequest(INDEX_NAME)
                .id("b002"));
        processor.add(new DeleteRequest(INDEX_NAME)
                .id("b003"));
        processor.flush();

    }

    //    查询所有flag为1的记录，然后根据f_code字段进行分组，在增加一个根据s_code自己进行子分组
    @Test
    void aggregateQueryAPITest() throws IOException {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(0);

        // 增加bool过滤条件
        BoolQueryBuilder booleanQuery = QueryBuilders.boolQuery().filter(QueryBuilders.termQuery("flag","1"));
        searchSourceBuilder.query(booleanQuery);

        // 增加聚集条件
        AggregationBuilder aggregationBuilder = AggregationBuilders.terms("f-cnt").field("f_code").subAggregation(
                AggregationBuilders.terms("s-cnt").field("s_code")
        );
        searchSourceBuilder.aggregation(aggregationBuilder);

        // 查询并解析查询结果
        infoSearch.source(searchSourceBuilder);
        SearchResponse response = client.search(infoSearch.source(searchSourceBuilder), RequestOptions.DEFAULT);

        Terms ta1 = response.getAggregations().get("f-cnt");
        List<? extends Terms.Bucket> hit1 = ta1.getBuckets();

        for (Terms.Bucket bucket: hit1 ) {
            log.info("key={},cnt={}",bucket.getKey(), bucket.getDocCount());

            Terms ta2 = bucket.getAggregations().get("s-cnt");
            List<? extends Terms.Bucket> hit2 = ta2.getBuckets();
            for (Terms.Bucket bucket2: hit2 ) {
                log.info("key2={}, cnt2={}", bucket2.getKey(), bucket2.getDocCount());
            }
        }
    }

    private String book1() {
        return "{\n" +
                "    \"id\": 1,\n" +
                "    \"title\": \"Java 编程思想\",\n" +
                "    \"language\": \"java\",\n" +
                "    \"author\": \"Bruce Eckel\",\n" +
                "    \"price\": 70.2,\n" +
                "    \"publish_time\": \"2007-10-01\",\n" +
                "    \"description\": \"Java 学习必读经典,殿堂级著作！赢得了全球程序员的广泛赞誉\"\n" +
                "}";
    }
    private String book2() {
        return "{\n" +
                "    \"id\": 2,\n" +
                "    \"title\": \"Java 程序性能优化\",\n" +
                "    \"language\": \"java\",\n" +
                "    \"author\": \"葛一鸣\",\n" +
                "    \"price\": 46.50,\n" +
                "    \"publish_time\": \"2012-08-01\",\n" +
                "    \"description\": \"让你的Java 程序更快、更稳定。深入剖析软件设计层面、代码层面、JVM 虚拟机层面的优化方法\"\n" +
                "}";
    }
    private String book3() {
        return "{\n" +
                "    \"id\": 3,\n" +
                "    \"title\": \"Python 科学计算\",\n" +
                "    \"language\": \"python\",\n" +
                "    \"author\": \"张若愚\",\n" +
                "    \"price\": 81.40,\n" +
                "    \"publish_time\": \"2016-05-01\",\n" +
                "    \"description\": \"零基础学python ，光盘中作者独家整合开发winPython 运行环境，涵盖了Python 各个扩展库\"\n" +
                "}";
    }
    private String book4() {
        return "{\n" +
                "    \"id\": 4,\n" +
                "    \"title\": \"Python 基础教程\",\n" +
                "    \"language\": \"python\",\n" +
                "    \"author\": \"Helant\",\n" +
                "    \"price\": 54.50,\n" +
                "    \"publish_time\": \"2014-03-01\",\n" +
                "    \"description\": \"经典的Python 入门教程， 层次鲜明，结构严谨，内容翔实\"\n" +
                "}";
    }
    private String book5() {
        return "{\n" +
                "    \"id\": 5,\n" +
                "    \"title\": \"JavaScript 高级程序设计\",\n" +
                "    \"language\": \"javascript\",\n" +
                "    \"author\": \"Nicholas C. Zakas\",\n" +
                "    \"price\": 66.40,\n" +
                "    \"publish_time\": \"2012-10-01\",\n" +
                "    \"description\": \"JavaScript 技术经典名著\"\n" +
                "}";
    }
    private String book6() {
        return "{\n" +
                "    \"id\": 6,\n" +
                "    \"title\": \"JavaScript设计模式与开发实践\",\n" +
                "    \"language\": \"javascript\",\n" +
                "    \"author\": \"曾探\",\n" +
                "    \"price\": 59.0,\n" +
                "    \"publish_time\": \"2015-05-01\",\n" +
                "    \"description\": \"本书将教会你如何把经典的设计模式应用到JavaScript语言中，编写出优美高效、结构化和可维护的代码\"\n" +
                "}";
    }

}
