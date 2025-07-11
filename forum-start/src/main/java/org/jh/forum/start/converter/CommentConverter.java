package org.jh.forum.start.converter;

import org.jh.forum.common.dto.CommentListElementDTO;
import org.jh.forum.common.dto.ReplyListElementDTO;
import org.jh.forum.common.dto.response.CommentElement;
import org.jh.forum.common.dto.response.ReplyElement;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @author qianqianzyk
 */
@Mapper(componentModel = "spring", collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED)
public interface CommentConverter {
    @Mapping(target = "publisherInfo", source = "userInfo")
    CommentElement toCommentListDTO(CommentListElementDTO dto);

    @Mapping(target = "publisherInfo", source = "userInfo")
    ReplyElement toReplyListDTO(ReplyListElementDTO dto);
} 