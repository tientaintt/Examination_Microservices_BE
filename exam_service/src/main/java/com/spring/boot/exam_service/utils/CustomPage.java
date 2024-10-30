package com.spring.boot.exam_service.utils;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public class CustomPage<T> extends PageImpl<T> {
    private Pageable pageable;
    private Sort sort;

    public CustomPage(List<T> content, Pageable pageable, long total, Sort sort) {
        super(content, pageable, total);
        this.pageable = pageable;
        this.sort = sort;
    }

    public Pageable getPageable() {
        return pageable;
    }

    public void setPageable(Pageable pageable) {
        this.pageable = pageable;
    }

    public Sort getSort() {
        return sort;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }
}
