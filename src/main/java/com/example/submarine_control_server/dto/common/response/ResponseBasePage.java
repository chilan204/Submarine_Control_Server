package com.example.submarine_control_server.dto.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseBasePage<T> {
    private PageData<T> data;
    private String message;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PageData<T> {
        private java.util.List<T> content;
        private boolean empty;
        private boolean first;
        private boolean last;
        private int page;
        private int size;
        private Object sort;
        private long totalElements;
        private int totalPages;
    }
}



