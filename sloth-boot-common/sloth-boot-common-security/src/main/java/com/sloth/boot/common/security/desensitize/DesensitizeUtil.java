package com.sloth.boot.common.security.desensitize;

import cn.hutool.core.util.StrUtil;

/**
 * 脱敏工具类
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public final class DesensitizeUtil {

    private DesensitizeUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 手机号脱敏（保留前3后4）
     * <p>
     * 使用字符级处理而非正则，避免长度异常的输入（非 11 位）不匹配正则而明文泄露。
     *
     * @param mobile 手机号
     * @return 脱敏后的手机号
     */
    public static String mobilePhone(String mobile) {
        if (StrUtil.isBlank(mobile)) {
            return mobile;
        }
        if (mobile.length() <= 3) {
            return "*".repeat(mobile.length());
        }
        if (mobile.length() <= 7) {
            return mobile.charAt(0) + "*".repeat(mobile.length() - 1);
        }
        return mobile.substring(0, 3) + "****" + mobile.substring(mobile.length() - 4);
    }

    /**
     * 身份证号脱敏（保留前4后4）
     * <p>
     * 使用字符级处理而非正则，避免末位为 {@code X} 的身份证号无法匹配而明文泄露。
     *
     * @param idCard 身份证号
     * @return 脱敏后的身份证号
     */
    public static String idCard(String idCard) {
        if (StrUtil.isBlank(idCard) || idCard.length() < 8) {
            return idCard;
        }
        int length = idCard.length();
        return idCard.substring(0, 4) + "****" + idCard.substring(length - 4);
    }

    /**
     * 邮箱脱敏（保留@前首字母与域名部分）
     * <p>
     * 使用字符级处理而非正则，避免本地部分含 {@code +}、{@code .} 等非单词字符时不匹配正则而明文泄露。
     *
     * @param email 邮箱
     * @return 脱敏后的邮箱
     */
    public static String email(String email) {
        if (StrUtil.isBlank(email)) {
            return email;
        }
        int atIndex = email.indexOf('@');
        if (atIndex < 1) {
            // 无有效 @ 分隔符时，仅保留首字符，其余全部遮蔽
            return email.length() == 1 ? email : email.charAt(0) + "****";
        }
        return email.charAt(0) + "****" + email.substring(atIndex);
    }

    /**
     * 银行卡号脱敏（保留前4后4）
     * <p>
     * 使用字符级处理而非正则，避免 19 位等超长卡号超出正则窗口而泄露尾部明文。
     *
     * @param bankCard 银行卡号
     * @return 脱敏后的银行卡号
     */
    public static String bankCard(String bankCard) {
        if (StrUtil.isBlank(bankCard)) {
            return bankCard;
        }
        if (bankCard.length() < 8) {
            return "*".repeat(bankCard.length());
        }
        return bankCard.substring(0, 4) + "****" + bankCard.substring(bankCard.length() - 4);
    }

    /**
     * 中文名称脱敏（保留姓）
     *
     * @param chineseName 中文名称
     * @return 脱敏后的名称
     */
    public static String chineseName(String chineseName) {
        if (StrUtil.isBlank(chineseName) || chineseName.length() <= 1) {
            return chineseName;
        }
        return chineseName.replaceAll("(\\S)\\S*", "$1*");
    }

    /**
     * 地址脱敏
     *
     * @param address       地址
     * @param sensitiveSize 脱敏长度
     * @return 脱敏后的地址
     */
    public static String address(String address, int sensitiveSize) {
        if (StrUtil.isBlank(address) || address.length() <= sensitiveSize) {
            return address;
        }
        return address.replaceAll(".{" + sensitiveSize + "}(?!$)", "***");
    }

    /**
     * 自定义脱敏
     *
     * @param str       原始字符串
     * @param prefixLen 前缀保留长度
     * @param suffixLen 后缀保留长度
     * @return 脱敏后的字符串
     */
    public static String custom(String str, int prefixLen, int suffixLen) {
        if (StrUtil.isBlank(str) || str.length() <= prefixLen + suffixLen) {
            return str;
        }
        int length = str.length();
        String prefix = str.substring(0, prefixLen);
        String suffix = str.substring(length - suffixLen);
        return prefix + "*".repeat(length - prefixLen - suffixLen) + suffix;
    }
}
