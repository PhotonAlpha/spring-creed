package com.ethan.common.utils;

import com.ethan.common.utils.notification.BookInformation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author EthanCao
 * @description spring-creed
 * @date 8/5/25
 */
public class JsonPathUtilsTest {
    public String json() {
        return """
                {
                    "store": {
                        "book": [
                            {
                                "category": "reference",
                                "author": "Nigel Rees",
                                "title": "Sayings of the Century",
                                "price": 8.95
                            },
                            {
                                "category": "fiction",
                                "author": "Evelyn Waugh",
                                "title": "Sword of Honour",
                                "price": 12.99
                            },
                            {
                                "category": "fiction",
                                "author": "Herman Melville",
                                "title": "Moby Dick",
                                "isbn": "0-553-21311-3",
                                "price": 8.99
                            },
                            {
                                "category": "fiction",
                                "author": "J. R. R. Tolkien",
                                "title": "The Lord of the Rings",
                                "isbn": "0-395-19395-8",
                                "price": 22.99
                            }
                        ],
                        "bicycle": {
                            "color": "red",
                            "price": 19.95
                        }
                    },
                    "expensive": 10
                }
                """;
    }

    @Test
    void readAllTest() {
        String json = json();
        List<String> stringList = JsonPathUtils.readAll(json, "$.store.book[*].price", String.class);
        Assertions.assertFalse(stringList.isEmpty());
        Assertions.assertInstanceOf(String.class, stringList.get(0));

        List<Integer> intList = JsonPathUtils.readAll(json, "$.store.book[*].price", Integer.class);
        Assertions.assertFalse(stringList.isEmpty());
        Assertions.assertInstanceOf(Integer.class, intList.get(0));
    }
    @Test
    void readTest() {
        String json = json();
        String result = JsonPathUtils.read(json, "$.store.book[0].price", String.class);
        Assertions.assertInstanceOf(String.class, result);

        Integer intResult = JsonPathUtils.read(json, "$.store.book[0].price", Integer.class);
        Assertions.assertInstanceOf(Integer.class, intResult);
    }
    @Test
    void readTest_default() {
        String json = json();
        String result = JsonPathUtils.read(json, "$.store.book[10].price","100");
        assertEquals("100", result);

        Integer intResult = JsonPathUtils.read(json, "$.store.book[10].price", Integer.class, 100);
        assertEquals(100, intResult);
    }
    @Test
    void readFirstTest_default() {
        String json = json();
        String result = JsonPathUtils.readFirst(json, "$..store.book[10].price","100");
        assertEquals("100", result);

        Double intResult = JsonPathUtils.readFirst(json, "$..store.book[10].price", Double.class, 100D);
        assertEquals(100D , intResult);
    }
    @Test
    void readFirstTest() {
        String json = json();
        String result = JsonPathUtils.readFirst(json, "$..store.book[0].price");
        assertEquals("8.95", result);

        Double intResult = JsonPathUtils.readFirst(json, "$..store.book[0].price", Double.class);
        assertEquals(8.95 , intResult);
    }
    @Test
    void readTest_null() {
        String json = null;
        String result = JsonPathUtils.read(json, "$.store.book[10].price","100");
        assertEquals("100", result);

        Integer intResult = JsonPathUtils.read(json, "$.store.book[10].price", Integer.class, 100);
        assertEquals(100, intResult);

        List<String> stringList = JsonPathUtils.readAll(json, "$.store.book[*].price", String.class);
        Assertions.assertTrue(stringList.isEmpty());
    }

    @Test
    void readToArrayPojo() {
        String jsonString2 = """
                {
                }
                """;
        List<BookInformation> books = JsonPathUtils.readAll(jsonString2, "$.store.book[*]", BookInformation.class);
        assertEquals(0, books.size());
        books = JsonPathUtils.readAll(json(), "$.store.book[*]", BookInformation.class);
        assertEquals(4, books.size());
    }
}
