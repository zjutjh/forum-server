package org.jh.forum.start.converter;

import cn.hutool.core.util.EnumUtil;
import org.jh.forum.api.dubbo.message.PostListElement;
import org.jh.forum.common.constants.CategoryEnum;
import org.jh.forum.common.dto.response.GetAdminPostListElement;
import org.jh.forum.common.dto.response.GetMyPostListElement;
import org.jh.forum.common.dto.response.GetPostListElement;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @author SugarMGP
 */
@Mapper(componentModel = "spring", collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED)
public interface PostConverter {
    default CategoryEnum map(String value) {
        return EnumUtil.getBy(CategoryEnum::getValue, value);
    }

    default String map(CategoryEnum value) {
        return value != null ? value.getValue() : "";
    }

    @Mapping(target = "publisherInfo", source = "userInfo")
    GetPostListElement toListDTO(PostListElement element);

    @Mapping(target = "publisher", source = "userInfo.nickname")
    GetAdminPostListElement toAdminListDTO(PostListElement element);

    GetMyPostListElement toMyListDTO(PostListElement element);
}
