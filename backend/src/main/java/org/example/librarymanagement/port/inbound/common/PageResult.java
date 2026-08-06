package org.example.librarymanagement.port.inbound.common;

import java.util.List;

/**
 * dtos này là bỏ bọc phân trang dùng chung cho toàn bộ dự án
 * PageResult
 * @param <T>
 */
public class PageResult<T> {
    private final List<T> items;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;
    public PageResult(List<T> items,int page,int size,long totalElements,int totalPages){
        this.items=items;
        this.page=page;
        this.size=size;
        this.totalElements=totalElements;
        this.totalPages=totalPages;
    }
    public List<T> getItems() {
        return items;
    }
    public int getPage() {
        return page;
    }
    public int getSize() {
        return size;
    }
    public long getTotalElements() {
        return totalElements;
    }
    public int getTotalPages() {
        return totalPages;
    }
}