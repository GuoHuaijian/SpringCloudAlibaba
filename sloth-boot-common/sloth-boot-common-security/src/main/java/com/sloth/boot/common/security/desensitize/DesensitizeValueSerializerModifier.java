package com.sloth.boot.common.security.desensitize;

import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.SerializationConfig;
import tools.jackson.databind.ser.BeanPropertyWriter;
import tools.jackson.databind.ser.ValueSerializerModifier;

import java.util.List;

/**
 * 脱敏序列化修改器。
 * <p>
 * 自动检测字段上的 {@link Desensitize} 注解，将 String 类型字段的序列化器
 * 替换为 {@link DesensitizeSerializer}，实现零侵入的自动脱敏。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class DesensitizeValueSerializerModifier extends ValueSerializerModifier {

    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config,
                                                     BeanDescription.Supplier beanDescSupplier,
                                                     List<BeanPropertyWriter> beanProperties) {
        BeanDescription beanDesc = beanDescSupplier.get();
        for (BeanPropertyWriter writer : beanProperties) {
            if (String.class.equals(writer.getType().getRawClass())) {
                Desensitize annotation = writer.getAnnotation(Desensitize.class);
                if (annotation == null) {
                    annotation = findFieldAnnotation(beanDesc, writer.getName());
                }
                if (annotation != null) {
                    @SuppressWarnings("unchecked")
                    tools.jackson.databind.ValueSerializer<Object> serializer = (tools.jackson.databind.ValueSerializer<Object>)
                            (tools.jackson.databind.ValueSerializer<?>) new DesensitizeSerializer(annotation);
                    writer.assignSerializer(serializer);
                }
            }
        }
        return beanProperties;
    }

    private Desensitize findFieldAnnotation(BeanDescription beanDesc, String fieldName) {
        // 沿继承链查找字段，避免父类字段上的 @Desensitize 静默失效
        Class<?> current = beanDesc.getBeanClass();
        while (current != null && current != Object.class) {
            try {
                return current.getDeclaredField(fieldName).getAnnotation(Desensitize.class);
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }
        return null;
    }
}
