package com.ethan.common.utils.collection;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Set 工具类
 *
 * 
 */
public class SetUtils {

    public static <T> Set<T> asSet(T... objs) {
        return new HashSet<>(Arrays.asList(objs));
    }

}
