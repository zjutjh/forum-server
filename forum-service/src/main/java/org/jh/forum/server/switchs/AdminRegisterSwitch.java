package org.jh.forum.server.switchs;

import lombok.Data;

/**
 * @author MangoGovo
 * 用于控制管理员注册接口的开关
 */
@Data
public class AdminRegisterSwitch {
    String key;
    Boolean enabled;
}
