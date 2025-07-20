package org.jh.forum.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author SugarMGP
 * @TableName file
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "file")
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class File extends BaseEntity {
    private String blake3;

    private String objectKey;
}