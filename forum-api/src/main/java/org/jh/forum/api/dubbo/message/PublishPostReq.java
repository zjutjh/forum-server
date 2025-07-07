package org.jh.forum.api.dubbo.message;

import lombok.Data;

import java.util.List;

@Data
public class PublishPostReq {
    private String title;
    private String content;
    private String category;
    private List<String> topics;
    private List<Long> attachmentIds;
}