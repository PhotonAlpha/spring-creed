package com.creed.constant;

/**
 * @className: IStream
 * @author: Ethan
 * @date: 7/12/2021
 **/
@FunctionalInterface
public interface IStream {
    String streamName();

    static IStream defaultStream() {
        return () -> "employeeFile";
    }
}
