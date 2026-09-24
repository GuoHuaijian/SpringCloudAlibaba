package com.sloth.boot.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 数字工具类测试。
 */
@DisplayName("NumberUtil 测试")
class NumberUtilTest {

    @Test
    @DisplayName("元转分正常换算")
    void yuan2FenNormal() {
        assertThat(NumberUtil.yuan2Fen(new BigDecimal("1.23"))).isEqualTo(123L);
        assertThat(NumberUtil.yuan2Fen(new BigDecimal("0"))).isEqualTo(0L);
    }

    @Test
    @DisplayName("元转分四舍五入，不截断")
    void yuan2FenRoundsHalfUp() {
        // 旧实现 longValue() 会截断为 199，属于金额错误
        assertThat(NumberUtil.yuan2Fen(new BigDecimal("1.999"))).isEqualTo(200L);
        assertThat(NumberUtil.yuan2Fen(new BigDecimal("0.005"))).isEqualTo(1L);
        assertThat(NumberUtil.yuan2Fen(new BigDecimal("-1.235"))).isEqualTo(-124L);
    }

    @Test
    @DisplayName("元转分 null 参数抛 IllegalArgumentException")
    void yuan2FenRejectsNull() {
        assertThatThrownBy(() -> NumberUtil.yuan2Fen(null))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("分转元正常换算")
    void fen2YuanNormal() {
        assertThat(NumberUtil.fen2Yuan(123L)).isEqualByComparingTo(new BigDecimal("1.23"));
    }

    @Test
    @DisplayName("分转元 null 参数抛 IllegalArgumentException")
    void fen2YuanRejectsNull() {
        assertThatThrownBy(() -> NumberUtil.fen2Yuan(null))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
