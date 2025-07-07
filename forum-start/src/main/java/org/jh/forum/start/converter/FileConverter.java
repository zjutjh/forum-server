package org.jh.forum.start.converter;

import cn.hutool.core.util.EnumUtil;
import org.jh.forum.api.dubbo.message.GetAttachmentInfoResp;
import org.jh.forum.common.constants.AttachmentTypeEnum;
import org.jh.forum.common.dto.response.GetAttachmentInfoResponse;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;

/**
 * @author SugarMGP
 */
@Mapper(componentModel = "spring", collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED)
public interface FileConverter {
    default AttachmentTypeEnum map(String value) {
        return EnumUtil.getBy(AttachmentTypeEnum::getValue, value);
    }

    GetAttachmentInfoResponse toDTO(GetAttachmentInfoResp resp);
}
