package org.jh.forum.start.converter;

import org.jh.forum.api.dubbo.GetPostListReq;
import org.jh.forum.api.dubbo.PostListElement;
import org.jh.forum.api.dubbo.PublishPostReq;
import org.jh.forum.common.constants.CategoryEnum;
import org.jh.forum.common.dto.request.GetPostListRequest;
import org.jh.forum.common.dto.request.PublishPostRequest;
import org.jh.forum.common.dto.response.GetAdminPostListElement;
import org.jh.forum.common.dto.response.GetMyPostListElement;
import org.jh.forum.common.dto.response.GetPostListElement;
import org.jh.forum.server.utils.EnumUtil;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @author SugarMGP
 */
@Mapper(componentModel = "spring", collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED)
public interface PostConverter {
    default CategoryEnum map(String value) {
        return EnumUtil.getEnumByField(CategoryEnum.class, CategoryEnum::getValue, value);
    }

    PublishPostReq toProto(PublishPostRequest request);

    @Mapping(target = "base.page", source = "page")
    @Mapping(target = "base.pageSize", source = "pageSize")
    GetPostListReq toProto(GetPostListRequest request);

    @Mapping(target = "publisherInfo", source = "userInfo")
    GetPostListElement toListDTO(PostListElement element);

    @Mapping(target = "publisher", source = "userInfo.nickname")
    GetAdminPostListElement toAdminListDTO(PostListElement element);

    GetMyPostListElement toMyListDTO(PostListElement element);
}
