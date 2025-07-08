package org.jh.forum.start.converter;

import org.jh.forum.api.dubbo.GetAttachmentInfoResp;
import org.jh.forum.common.constants.AttachmentTypeEnum;
import org.jh.forum.common.dto.response.GetAttachmentInfoResponse;
import org.jh.forum.server.utils.EnumUtil;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;

/**
 * @author SugarMGP
 */
@Mapper(componentModel = "spring", collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED)
public interface FileConverter {
    default AttachmentTypeEnum map(String value) {
        return EnumUtil.getEnumByField(AttachmentTypeEnum.class, AttachmentTypeEnum::getValue, value);
    }

    GetAttachmentInfoResponse toDTO(GetAttachmentInfoResp resp);
}
