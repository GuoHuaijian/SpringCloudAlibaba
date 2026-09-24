package com.sloth.boot.common.security.desensitize;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 脱敏工具测试。
 */
@DisplayName("DesensitizeUtil 测试")
class DesensitizeUtilTest {

    @Test
    @DisplayName("身份证号末位含 X 时脱敏不泄露明文")
    void idCardWithTrailingX() {
        assertThat(DesensitizeUtil.idCard("11010519491231002X")).isEqualTo("1101****002X");
    }

    @Test
    @DisplayName("纯数字身份证号脱敏保留前后四位")
    void idCardNumeric() {
        assertThat(DesensitizeUtil.idCard("110105194912310021")).isEqualTo("1101****0021");
    }

    @Test
    @DisplayName("过短身份证号原样返回")
    void idCardTooShort() {
        assertThat(DesensitizeUtil.idCard("1234567")).isEqualTo("1234567");
    }

    @Test
    @DisplayName("空值原样返回")
    void idCardBlank() {
        assertThat(DesensitizeUtil.idCard("")).isEmpty();
        assertThat(DesensitizeUtil.idCard(null)).isNull();
    }

    @Test
    @DisplayName("标准 11 位手机号保留前3后4")
    void mobileStandard() {
        assertThat(DesensitizeUtil.mobilePhone("13812345678")).isEqualTo("138****5678");
    }

    @Test
    @DisplayName("非 11 位手机号不泄露明文")
    void mobileNonStandardLength() {
        assertThat(DesensitizeUtil.mobilePhone("12345")).isEqualTo("1****");
        assertThat(DesensitizeUtil.mobilePhone("123")).isEqualTo("***");
        assertThat(DesensitizeUtil.mobilePhone("12345678901234")).isEqualTo("123****1234");
    }

    @Test
    @DisplayName("本地部分含 + 的邮箱仍被脱敏")
    void emailWithPlusTag() {
        assertThat(DesensitizeUtil.email("john+tag@example.com")).isEqualTo("j****@example.com");
    }

    @Test
    @DisplayName("含点号的本地部分邮箱仍被脱敏")
    void emailWithDot() {
        assertThat(DesensitizeUtil.email("john.doe@example.com")).isEqualTo("j****@example.com");
    }

    @Test
    @DisplayName("无 @ 分隔符的输入不泄露明文")
    void emailWithoutAtSign() {
        assertThat(DesensitizeUtil.email("not-an-email")).isEqualTo("n****");
        assertThat(DesensitizeUtil.email("a")).isEqualTo("a");
    }

    @Test
    @DisplayName("19 位银行卡号仅保留前4后4")
    void bankCard19Digits() {
        assertThat(DesensitizeUtil.bankCard("6222111122223333444")).isEqualTo("6222****3444");
    }

    @Test
    @DisplayName("过短银行卡号完全遮蔽")
    void bankCardTooShort() {
        assertThat(DesensitizeUtil.bankCard("1234567")).isEqualTo("*******");
        assertThat(DesensitizeUtil.bankCard("12345678")).isEqualTo("1234****5678");
    }

    @Test
    @DisplayName("空值原样返回")
    void blankInputs() {
        assertThat(DesensitizeUtil.mobilePhone("")).isEmpty();
        assertThat(DesensitizeUtil.mobilePhone(null)).isNull();
        assertThat(DesensitizeUtil.email("")).isEmpty();
        assertThat(DesensitizeUtil.bankCard("")).isEmpty();
    }
}
