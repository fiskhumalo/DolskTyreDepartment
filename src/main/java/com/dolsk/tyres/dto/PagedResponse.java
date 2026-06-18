package com.dolsk.tyres.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Generic paginated response wrapper.
 * Compatible with Spring Data's Page abstraction.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponse<T> {

    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;
}
