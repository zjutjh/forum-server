package org.jh.forum.api.dubbo.message;

import lombok.Data;

@Data
public class BaseListReq {
    private int page;
    private int pageSize;
}