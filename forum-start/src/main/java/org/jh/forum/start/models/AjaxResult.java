package org.jh.forum.start.models;

import com.alibaba.nacos.api.remote.response.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Patrick_Star
 * @date 2025/4/24
 */
@Data
@AllArgsConstructor
public class AjaxResult<T> {
    private Integer code;
    private String msg;
    private T data;

    public static final String SUCCESS_MSG = "success";

    public static final String FAIL_MSG = "fail";

    public static <N> AjaxResult<N> SUCCESS() { return new AjaxResult<>(ResponseCode.SUCCESS.getCode(), SUCCESS_MSG,null); }

    public static <N> AjaxResult<N> SUCCESS(N data) { return new AjaxResult<>(ResponseCode.SUCCESS.getCode(), SUCCESS_MSG, data); }

    public static <N> AjaxResult<N> SUCCESS(String msg, N data) { return new AjaxResult<>(ResponseCode.SUCCESS.getCode(), msg, data); }

    public static <N> AjaxResult<N> FAIL() { return new AjaxResult<>(ResponseCode.FAIL.getCode(), FAIL_MSG, null); }

    public static <N> AjaxResult<N> FAIL(N data) { return new AjaxResult<>(ResponseCode.FAIL.getCode(), FAIL_MSG, data); }

    public static <N> AjaxResult<N> FAIL(String msg, N data) { return new AjaxResult<>(ResponseCode.FAIL.getCode(), msg, data); }
}
