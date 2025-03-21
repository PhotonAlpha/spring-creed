package com.ethan.common.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Schema(name = "分页结果")
@Data
public final class PageResult<T> implements Serializable {

    @Schema(name = "数据", required = true)
    private List<T> list;

    @Schema(name = "总量", required = true)
    private Long total;

    public PageResult() {
    }

    public PageResult(List<T> list, Long total) {
        this.list = list;
        this.total = total;
    }

    public PageResult(Long total) {
        this.list = new ArrayList<>();
        this.total = total;
    }

    public static <T> PageResult<T> empty() {
        return new PageResult<>(0L);
    }

    public static <T> PageResult<T> empty(Long total) {
        return new PageResult<>(total);
    }

    public static <T>PageResult of(List<T> list, Long total) {
        return new PageResult(list, total);
    }
}
