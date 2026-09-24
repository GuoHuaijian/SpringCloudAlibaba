package com.sloth.boot.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 数字工具类
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public final class NumberUtil {

    private NumberUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 加法运算
     *
     * @param a 加数
     * @param b 加数
     * @return 和
     * @throws IllegalArgumentException 如果任一参数为 null
     */
    public static BigDecimal add(BigDecimal a, BigDecimal b) {
        requireNonNull(a, "a");
        requireNonNull(b, "b");
        return a.add(b);
    }

    /**
     * 减法运算
     *
     * @param a 被减数
     * @param b 减数
     * @return 差
     * @throws IllegalArgumentException 如果任一参数为 null
     */
    public static BigDecimal subtract(BigDecimal a, BigDecimal b) {
        requireNonNull(a, "a");
        requireNonNull(b, "b");
        return a.subtract(b);
    }

    /**
     * 乘法运算
     *
     * @param a 乘数
     * @param b 乘数
     * @return 积
     * @throws IllegalArgumentException 如果任一参数为 null
     */
    public static BigDecimal multiply(BigDecimal a, BigDecimal b) {
        requireNonNull(a, "a");
        requireNonNull(b, "b");
        return a.multiply(b);
    }

    /**
     * 除法运算
     *
     * @param a 被除数
     * @param b 除数
     * @return 商
     * @throws IllegalArgumentException 如果任一参数为 null
     */
    public static BigDecimal divide(BigDecimal a, BigDecimal b) {
        return divide(a, b, 2);
    }

    /**
     * 除法运算
     *
     * @param a     被除数
     * @param b     除数
     * @param scale 小数位数
     * @return 商
     * @throws IllegalArgumentException 如果任一参数为 null
     */
    public static BigDecimal divide(BigDecimal a, BigDecimal b, int scale) {
        requireNonNull(a, "a");
        requireNonNull(b, "b");
        return a.divide(b, scale, RoundingMode.HALF_UP);
    }

    /**
     * 四舍五入
     *
     * @param number 数字
     * @param scale  小数位数
     * @return 四舍五入后的数字
     * @throws IllegalArgumentException 如果 number 为 null
     */
    public static BigDecimal roundHalfUp(BigDecimal number, int scale) {
        requireNonNull(number, "number");
        return number.setScale(scale, RoundingMode.HALF_UP);
    }

    /**
     * 格式化金额（保留2位小数）
     *
     * @param number 金额
     * @return 格式化后的金额字符串
     * @throws IllegalArgumentException 如果 number 为 null
     */
    public static String formatMoney(BigDecimal number) {
        requireNonNull(number, "number");
        return number.setScale(2, RoundingMode.HALF_UP).toString();
    }

    /**
     * 元转分。按四舍五入保留到分。
     *
     * @param yuan 元
     * @return 分
     * @throws IllegalArgumentException 如果 yuan 为 null
     * @throws ArithmeticException      如果结果超出 long 表示范围
     */
    public static Long yuan2Fen(BigDecimal yuan) {
        requireNonNull(yuan, "yuan");
        return yuan.movePointRight(2).setScale(0, RoundingMode.HALF_UP).longValueExact();
    }

    /**
     * 分转元
     *
     * @param fen 分
     * @return 元
     * @throws IllegalArgumentException 如果 fen 为 null
     */
    public static BigDecimal fen2Yuan(Long fen) {
        if (fen == null) {
            throw new IllegalArgumentException("参数 fen 不能为 null");
        }
        return new BigDecimal(fen).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
    }

    /**
     * 取两个值的最大值
     *
     * @param a 值a
     * @param b 值b
     * @return 最大值
     * @throws IllegalArgumentException 如果任一参数为 null
     */
    public static BigDecimal max(BigDecimal a, BigDecimal b) {
        requireNonNull(a, "a");
        requireNonNull(b, "b");
        return a.max(b);
    }

    /**
     * 取两个值的最小值
     *
     * @param a 值a
     * @param b 值b
     * @return 最小值
     * @throws IllegalArgumentException 如果任一参数为 null
     */
    public static BigDecimal min(BigDecimal a, BigDecimal b) {
        requireNonNull(a, "a");
        requireNonNull(b, "b");
        return a.min(b);
    }

    /**
     * 求和
     *
     * @param values 值集合
     * @return 总和
     * @throws IllegalArgumentException 如果 values 为 null
     */
    public static BigDecimal sum(java.util.Collection<BigDecimal> values) {
        if (values == null) {
            throw new IllegalArgumentException("参数 values 不能为 null");
        }
        BigDecimal result = BigDecimal.ZERO;
        for (BigDecimal value : values) {
            if (value != null) {
                result = result.add(value);
            }
        }
        return result;
    }

    /**
     * 百分比计算 (a / b * 100)
     *
     * @param a 分子
     * @param b 分母
     * @return 百分比值
     * @throws IllegalArgumentException 如果任一参数为 null 或 b 为零
     */
    public static BigDecimal percentage(BigDecimal a, BigDecimal b) {
        return percentage(a, b, 2);
    }

    /**
     * 百分比计算 (a / b * 100)
     *
     * @param a     分子
     * @param b     分母
     * @param scale 小数位数
     * @return 百分比值
     * @throws IllegalArgumentException 如果任一参数为 null 或 b 为零
     */
    public static BigDecimal percentage(BigDecimal a, BigDecimal b, int scale) {
        requireNonNull(a, "a");
        requireNonNull(b, "b");
        return a.multiply(new BigDecimal(100)).divide(b, scale, RoundingMode.HALF_UP);
    }

    /**
     * 空安全比较
     *
     * @param a 值a
     * @param b 值b
     * @return 比较结果，null 被视为最小值
     */
    public static int compare(BigDecimal a, BigDecimal b) {
        if (a == b) {
            return 0;
        }
        if (a == null) {
            return -1;
        }
        if (b == null) {
            return 1;
        }
        return a.compareTo(b);
    }

    /**
     * 判断 a 是否大于 b
     *
     * @param a 值a
     * @param b 值b
     * @return 是否大于
     */
    public static boolean isGreaterThan(BigDecimal a, BigDecimal b) {
        return compare(a, b) > 0;
    }

    /**
     * 判断 a 是否小于 b
     *
     * @param a 值a
     * @param b 值b
     * @return 是否小于
     */
    public static boolean isLessThan(BigDecimal a, BigDecimal b) {
        return compare(a, b) < 0;
    }

    /**
     * 判断是否为零
     *
     * @param value 值
     * @return 是否为零
     */
    public static boolean isZero(BigDecimal value) {
        return value == null || BigDecimal.ZERO.compareTo(value) == 0;
    }

    /**
     * 判断是否为正数
     *
     * @param value 值
     * @return 是否为正数
     */
    public static boolean isPositive(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * 判断是否为负数
     *
     * @param value 值
     * @return 是否为负数
     */
    public static boolean isNegative(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) < 0;
    }

    /**
     * 安全创建 BigDecimal（null 输入返回 null）
     *
     * @param value 字符串值
     * @return BigDecimal 对象，null 输入返回 null
     */
    public static BigDecimal of(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return new BigDecimal(value);
    }

    /**
     * 安全创建 BigDecimal（null 输入返回 null）
     *
     * @param value Long 值
     * @return BigDecimal 对象，null 输入返回 null
     */
    public static BigDecimal of(Long value) {
        if (value == null) {
            return null;
        }
        return new BigDecimal(value);
    }

    /**
     * 安全转换为 int
     *
     * @param value BigDecimal 值
     * @return int 值，null 输入返回 0
     */
    public static int toInt(BigDecimal value) {
        return value == null ? 0 : value.intValue();
    }

    /**
     * 安全转换为 long
     *
     * @param value BigDecimal 值
     * @return long 值，null 输入返回 0
     */
    public static long toLong(BigDecimal value) {
        return value == null ? 0L : value.longValue();
    }

    private static void requireNonNull(BigDecimal value, String name) {
        if (value == null) {
            throw new IllegalArgumentException("参数 " + name + " 不能为 null");
        }
    }
}
