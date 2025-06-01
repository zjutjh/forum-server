package org.jh.forum.common.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author qianqianzyk
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GetReplyListResponse extends BaseListResponse<ReplyResponse>{
}
