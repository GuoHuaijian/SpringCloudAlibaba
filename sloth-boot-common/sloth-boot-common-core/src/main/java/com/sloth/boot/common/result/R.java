package com.sloth.boot.common.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sloth.boot.common.constant.CommonConstant;
import com.sloth.boot.common.context.TraceContext;
import com.sloth.boot.common.exception.ErrorCode;
import com.sloth.boot.common.exception.GlobalErrorCode;
import com.sloth.boot.common.util.I18nMessages;
import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应结果
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 响应码
     */
    private int code;

    /**
     * 响应消息
     */
    private String msg;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 请求追踪ID
     */
    private String traceId;

    /**
     * 响应时间戳
     */
    private long timestamp;

    /**
     * 私有构造函数
     */
    private R() {
        this.timestamp = System.currentTimeMillis();
        this.traceId = TraceContext.getTraceId();
    }

    /**
     * 成功响应
     *
     * @param <T> 数据类型
     * @return 统一响应
     */
    public static <T> R<T> ok() {
        R<T> r = new R<>();
        r.setCode(CommonConstant.SUCCESS);
        r.setMsg(I18nMessages.getMessage("sloth.success"));
        return r;
    }

    /**
     * 成功响应
     *
     * @param data 响应数据
     * @param <T>  数据类型
     * @return 统一响应
     */
    public static <T> R<T> ok(T data) {
        R<T> r = ok();
        r.setData(data);
        return r;
    }

    /**
     * 成功响应
     *
     * @param msg  响应消息
     * @param data 响应数据
     * @param <T>  数据类型
     * @return 统一响应
     */
    public static <T> R<T> ok(String msg, T data) {
        R<T> r = ok();
        r.setMsg(msg);
        r.setData(data);
        return r;
    }

    /**
     * 失败响应
     *
     * @param <T> 数据类型
     * @return 统一响应
     */
    public static <T> R<T> fail() {
        R<T> r = new R<>();
        r.setCode(CommonConstant.FAIL);
        r.setMsg(I18nMessages.getMessage("sloth.fail"));
        return r;
    }

    /**
     * 失败响应
     *
     * @param msg 响应消息
     * @param <T> 数据类型
     * @return 统一响应
     */
    public static <T> R<T> fail(String msg) {
        R<T> r = fail();
        r.setMsg(msg);
        return r;
    }

    /**
     * 失败响应
     *
     * @param code 响应码
     * @param msg  响应消息
     * @param <T>  数据类型
     * @return 统一响应
     */
    public static <T> R<T> fail(int code, String msg) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setMsg(msg);
        return r;
    }

    /**
     * 失败响应
     *
     * @param errorCode 错误码
     * @param <T>       数据类型
     * @return 统一响应
     */
    public static <T> R<T> fail(ErrorCode errorCode) {
        R<T> r = new R<>();
        r.setCode(errorCode.getCode());
        r.setMsg(resolveMsg(errorCode));
        return r;
    }

    /**
     * 判断是否成功
     *
     * @return 是否成功
     */
    public boolean isSuccess() {
        return CommonConstant.SUCCESS == this.code;
    }

    /**
     * 从 ErrorCode 构建失败响应
     *
     * @param errorCode 错误码
     * @param <T>       数据类型
     * @return 统一响应
     */
    public static <T> R<T> build(ErrorCode errorCode) {
        R<T> r = new R<>();
        r.setCode(errorCode.getCode());
        r.setMsg(resolveMsg(errorCode));
        return r;
    }

    /**
     * 从异常构建失败响应
     * <p>
     * 非 {@link BaseException} 的异常统一返回内部错误提示，避免将内部异常消息
     * （可能含 SQL、连接串、类名等敏感信息）暴露给调用方；原始消息请记录在服务端日志。
     *
     * @param throwable 异常
     * @param <T>       数据类型
     * @return 统一响应
     */
    public static <T> R<T> from(Throwable throwable) {
        R<T> r = new R<>();
        if (throwable instanceof com.sloth.boot.common.exception.BaseException baseEx) {
            r.setCode(baseEx.getErrorCode().getCode());
            r.setMsg(baseEx.getMessage());
        } else {
            r.setCode(GlobalErrorCode.INTERNAL_ERROR.getCode());
            r.setMsg(I18nMessages.getMessage("sloth.error.internal"));
        }
        return r;
    }

    /**
     * 解析错误码的国际化消息。
     * <p>
     * 优先通过 {@link ErrorCode#getI18nKey()} 查找国际化消息，
     * 未配置 i18nKey 或查找失败时回退到 {@link ErrorCode#getMsg()}。
     *
     * @param errorCode 错误码
     * @return 国际化消息或默认消息
     */
    private static String resolveMsg(ErrorCode errorCode) {
        String i18nKey = errorCode.getI18nKey();
        if (i18nKey != null) {
            String resolved = I18nMessages.getMessage(i18nKey);
            if (!resolved.equals(i18nKey)) {
                return resolved;
            }
        }
        return errorCode.getMsg();
    }
}
