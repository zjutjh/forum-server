package org.jh.forum.server.switchs;

import lombok.Data;

/**
 * @author MangoGovo
 */
@Data
public class SuperLoginSwitch {
    // 白名单用户 ID
    Long[] whiteList;
    // 是否启用
    Boolean enabled;
}
