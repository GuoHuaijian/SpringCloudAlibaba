package com.sloth.boot.common.util.jackson;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Jackson 自定义序列化器测试。
 */
@DisplayName("Jackson 自定义序列化器测试")
class JacksonCustomSerializerTest {

    private final ObjectMapper mapper = JacksonConfigUtil.createConfiguredMapper();

    @Test
    @DisplayName("IBaseEnum 序列化后能反序列化还原（对象格式）")
    void enumRoundTripObjectFormat() throws Exception {
        String json = mapper.writeValueAsString(TestStatus.ACTIVE);
        assertThat(json).contains("\"code\":1").contains("\"desc\":\"启用\"");

        TestStatus parsed = mapper.readValue(json, TestStatus.class);
        assertThat(parsed).isEqualTo(TestStatus.ACTIVE);
    }

    @Test
    @DisplayName("IBaseEnum 支持裸 code 数字反序列化")
    void enumDeserializeBareNumber() throws Exception {
        TestStatus parsed = mapper.readValue("1", TestStatus.class);
        assertThat(parsed).isEqualTo(TestStatus.ACTIVE);
    }

    @Test
    @DisplayName("IBaseEnum 未知 code 抛出可读异常")
    void enumDeserializeUnknownCode() {
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> mapper.readValue("99", TestStatus.class))
            .isInstanceOf(tools.jackson.core.JacksonException.class);
    }

    @Test
    @DisplayName("IBaseEnum 支持字符串 code 反序列化")
    void enumDeserializeStringCode() throws Exception {
        TestStatus parsed = mapper.readValue("\"1\"", TestStatus.class);
        assertThat(parsed).isEqualTo(TestStatus.ACTIVE);
    }

    @Test
    @DisplayName("IBaseEnum 对象格式缺失 code 时快速失败，不静默落到 0 值枚举")
    void enumDeserializeObjectMissingCode() {
        org.assertj.core.api.Assertions
            .assertThatThrownBy(() -> mapper.readValue("{\"desc\":\"启用\"}", TestStatus.class))
            .isInstanceOf(tools.jackson.core.JacksonException.class);
    }

    @Test
    @DisplayName("IBaseEnum code 为 null 时快速失败")
    void enumDeserializeNullCode() {
        org.assertj.core.api.Assertions
            .assertThatThrownBy(() -> mapper.readValue("{\"code\":null}", TestStatus.class))
            .isInstanceOf(tools.jackson.core.JacksonException.class);
    }

    @Test
    @DisplayName("IBaseEnum 非法字符串 code 时快速失败")
    void enumDeserializeInvalidStringCode() {
        org.assertj.core.api.Assertions
            .assertThatThrownBy(() -> mapper.readValue("\"abc\"", TestStatus.class))
            .isInstanceOf(tools.jackson.core.JacksonException.class);
    }

    @Test
    @DisplayName("Long 序列化为字符串，long 保持数字")
    void longAndPrimitiveLongSerialization() throws Exception {
        LongWrapper wrapper = new LongWrapper();
        String json = mapper.writeValueAsString(wrapper);
        assertThat(json).contains("\"id\":\"9007199254740993\"").contains("\"timestamp\":1710000000000");
    }

    @Test
    @DisplayName("LocalDateTime 字段 @JsonFormat 优先于全局默认格式")
    void dateTimeRespectsJsonFormat() throws Exception {
        TimeWrapper wrapper = new TimeWrapper();
        LocalDateTime time = LocalDateTime.of(2026, 8, 15, 10, 30, 0);
        wrapper.time = time;
        wrapper.defaultTime = time;

        String json = mapper.writeValueAsString(wrapper);
        assertThat(json).contains("\"time\":\"2026/08/15 10:30:00\"");
        assertThat(json).contains("\"defaultTime\":\"2026-08-15 10:30:00\"");

        TimeWrapper parsed = mapper.readValue(json, TimeWrapper.class);
        assertThat(parsed.time).isEqualTo(time);
        assertThat(parsed.defaultTime).isEqualTo(time);
    }

    @Test
    @DisplayName("LocalDate 与 LocalTime 字段 @JsonFormat 生效")
    void dateAndTimeRespectJsonFormat() throws Exception {
        DateWrapper wrapper = new DateWrapper();
        wrapper.day = LocalDate.of(2026, 8, 15);
        wrapper.time = LocalTime.of(10, 30, 0);

        String json = mapper.writeValueAsString(wrapper);
        assertThat(json).contains("\"day\":\"2026-08-15\"");
        assertThat(json).contains("\"time\":\"10:30:00\"");

        DateWrapper parsed = mapper.readValue(json, DateWrapper.class);
        assertThat(parsed.day).isEqualTo(LocalDate.of(2026, 8, 15));
        assertThat(parsed.time).isEqualTo(LocalTime.of(10, 30, 0));
    }

    static class LongWrapper {
        public Long id = 9007199254740993L;
        public long timestamp = 1710000000000L;
    }

    static class TimeWrapper {
        @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
        public LocalDateTime time;
        public LocalDateTime defaultTime;
    }

    static class DateWrapper {
        @JsonFormat(pattern = "yyyy-MM-dd")
        public LocalDate day;
        @JsonFormat(pattern = "HH:mm:ss")
        public LocalTime time;
    }
}
