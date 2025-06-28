package org.jh.forum.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * @author SugarMGP
 * @TableName file
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "file")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class File extends BaseEntity {
    private String blake3;

    private String objectKey;
}