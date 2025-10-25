package org.jh.forum.common.validator;

import org.apache.commons.lang3.StringUtils;
import org.jh.forum.common.annotation.NoExternalLink;
import org.jh.forum.common.constants.ExceptionEnum;
import org.jh.forum.common.exceptions.ApiException;
import org.nibor.autolink.LinkExtractor;
import org.nibor.autolink.LinkSpan;
import org.nibor.autolink.LinkType;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.EnumSet;

/**
 * 禁止外链注解校验器
 *
 * @author SugarMGP
 * @see NoExternalLink
 */
public class NoExternalLinkValidator implements ConstraintValidator<NoExternalLink, String> {

    private final LinkExtractor extractor = LinkExtractor.builder()
            .linkTypes(EnumSet.of(LinkType.URL, LinkType.WWW))
            .build();

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isBlank(value)) {
            return true;
        }
        Iterable<LinkSpan> links = extractor.extractLinks(value);
        for (LinkSpan link : links) {
            String url = value.substring(link.getBeginIndex(), link.getEndIndex());
            throw new ApiException(ExceptionEnum.EXTERNAL_LINK_NOT_ALLOWED);
        }
        return true;
    }
}
