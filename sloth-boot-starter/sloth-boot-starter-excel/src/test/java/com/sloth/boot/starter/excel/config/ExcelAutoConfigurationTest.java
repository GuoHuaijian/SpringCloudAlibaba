package com.sloth.boot.starter.excel.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Excel 自动配置测试。
 * <p>
 * 验证条件装配：默认注册 {@link ExcelTemplate}、属性开关与用户 Bean 覆盖。
 */
@DisplayName("ExcelAutoConfiguration 测试")
class ExcelAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(ExcelAutoConfiguration.class));

    @Test
    @DisplayName("默认注册 ExcelTemplate 并绑定默认配置")
    void defaultRegistersExcelTemplate() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(ExcelTemplate.class);
            ExcelProperties properties = context.getBean(ExcelProperties.class);
            assertThat(properties.isEnabled()).isTrue();
            assertThat(properties.getImportConfig().getBatchSize()).isEqualTo(1000);
            assertThat(properties.getExportConfig().isAutoSizeColumn()).isTrue();
        });
    }

    @Test
    @DisplayName("sloth.excel.enabled=false 时不注册 ExcelTemplate")
    void disabledByPropertySkipsTemplate() {
        contextRunner.withPropertyValues("sloth.excel.enabled=false")
            .run(context -> assertThat(context).doesNotHaveBean(ExcelTemplate.class));
    }

    @Test
    @DisplayName("配置属性可覆盖导入批处理大小")
    void importBatchSizeIsBindable() {
        contextRunner.withPropertyValues("sloth.excel.import-config.batch-size=500")
            .run(context -> assertThat(context.getBean(ExcelProperties.class).getImportConfig().getBatchSize())
                .isEqualTo(500));
    }

    @Test
    @DisplayName("用户自定义 ExcelTemplate 优先生效")
    void userDefinedTemplateWins() {
        contextRunner.withBean(ExcelTemplate.class, ExcelTemplate::new)
            .run(context -> assertThat(context).hasSingleBean(ExcelTemplate.class));
    }
}
