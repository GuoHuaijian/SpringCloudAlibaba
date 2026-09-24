package com.sloth.boot.common.util.jackson;

import com.sloth.boot.common.enums.IBaseEnum;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;

/**
 * IBaseEnum 反序列化器
 * <p>
 * 支持从 {@code {"code": 1, "desc": "xxx"}} 对象格式或裸 code 数字格式反序列化，
 * 与 {@link IBaseEnumSerializer} 的序列化格式保持一致。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class IBaseEnumDeserializer extends ValueDeserializer<IBaseEnum> {

    private static final String CODE_FIELD = "code";

    /**
     * 由 {@link IBaseEnumDeserializerModifier} 传入的目标枚举类型，根值反序列化时
     * {@code getContextualType()} 可能为 null，此时使用该字段。
     */
    private final Class<?> enumClass;

    public IBaseEnumDeserializer() {
        this(null);
    }

    public IBaseEnumDeserializer(Class<?> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public IBaseEnum deserialize(JsonParser p, DeserializationContext ctxt) {
        Class<?> targetClass = resolveEnumClass(ctxt);
        if (targetClass == null || !targetClass.isEnum() || !IBaseEnum.class.isAssignableFrom(targetClass)) {
            throw ctxt.weirdStringException(p.getText(), Object.class, "类型不是 IBaseEnum 枚举");
        }

        int code = readCode(p, ctxt, targetClass);
        for (Object enumConstant : targetClass.getEnumConstants()) {
            IBaseEnum baseEnum = (IBaseEnum) enumConstant;
            if (baseEnum.getCode() == code) {
                return baseEnum;
            }
        }
        throw ctxt.weirdStringException(String.valueOf(code), targetClass, "枚举中没有值为 " + code + " 的枚举");
    }

    private Class<?> resolveEnumClass(DeserializationContext ctxt) {
        if (enumClass != null) {
            return enumClass;
        }
        JavaType contextualType = ctxt.getContextualType();
        return contextualType != null ? contextualType.getRawClass() : null;
    }

    /**
     * 读取枚举 code。对象格式缺失 code、code 为 null 或类型非法时快速失败，
     * 避免静默落入 0 值枚举造成数据错误。
     *
     * @param p           JSON 解析器
     * @param ctxt        反序列化上下文
     * @param targetClass 目标枚举类型
     * @return 枚举 code
     */
    private int readCode(JsonParser p, DeserializationContext ctxt, Class<?> targetClass) {
        if (p.currentToken() == JsonToken.START_OBJECT) {
            return readCodeFromObject(p, ctxt, targetClass);
        }
        return readScalarCode(p, ctxt, targetClass);
    }

    private int readCodeFromObject(JsonParser p, DeserializationContext ctxt, Class<?> targetClass) {
        boolean codeFound = false;
        int code = 0;
        JsonToken token;
        while ((token = p.nextToken()) != JsonToken.END_OBJECT) {
            if (token != JsonToken.PROPERTY_NAME) {
                throw ctxt.weirdStringException(String.valueOf(p.getText()), targetClass, "非法的枚举对象格式");
            }
            String fieldName = p.currentName();
            p.nextToken();
            if (CODE_FIELD.equals(fieldName)) {
                code = readScalarCode(p, ctxt, targetClass);
                codeFound = true;
            } else {
                p.skipChildren();
            }
        }
        if (!codeFound) {
            throw ctxt.weirdStringException(String.valueOf(p.getText()), targetClass, "对象格式缺少 code 字段");
        }
        return code;
    }

    private int readScalarCode(JsonParser p, DeserializationContext ctxt, Class<?> targetClass) {
        JsonToken token = p.currentToken();
        if (token == JsonToken.VALUE_NUMBER_INT) {
            return p.getValueAsInt();
        }
        if (token == JsonToken.VALUE_STRING) {
            String text = p.getText().trim();
            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException ex) {
                throw ctxt.weirdStringException(text, targetClass, "枚举 code 不是合法的整数");
            }
        }
        throw ctxt.weirdStringException(String.valueOf(p.getText()), targetClass, "不支持的枚举 code 类型");
    }
}
