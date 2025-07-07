package org.jh.forum.api.dubbo.message;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GetPostListReq extends BaseListReq {
    private String category;
    private int sortType;
}