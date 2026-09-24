package com.sloth.boot.common.doc;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 接口文档自动配置测试。
 * <p>
 * 验证条件装配：默认 OpenAPI 与分组注册、属性开关、自定义分组与用户 Bean 覆盖。
 */
@DisplayName("DocAutoConfiguration 测试")
class DocAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(DocAutoConfiguration.class));

    @Test
    @DisplayName("默认注册 OpenAPI 与默认分组")
    void defaultRegistersOpenApiAndGroup() {
        contextRunner.run(context -> {
            OpenAPI openApi = context.getBean(OpenAPI.class);
            assertThat(openApi.getInfo()).isNotNull();
            assertThat(openApi.getInfo().getTitle()).isEqualTo("Sloth Boot API");
            assertThat(context).hasSingleBean(GroupedOpenApi.class);
            assertThat(context.getBean(GroupedOpenApi.class).getGroup()).isEqualTo("default");
            assertThat(context).hasBean("customGroupedOpenApis");
            assertThat((List<?>) context.getBean("customGroupedOpenApis")).isEmpty();
        });
    }

    @Test
    @DisplayName("sloth.doc.enabled=false 时不注册任何文档 Bean")
    void disabledByPropertySkipsRegistration() {
        contextRunner.withPropertyValues("sloth.doc.enabled=false").run(context -> {
            assertThat(context).doesNotHaveBean(OpenAPI.class);
            assertThat(context).doesNotHaveBean(GroupedOpenApi.class);
        });
    }

    @Test
    @DisplayName("自定义标题覆盖默认值")
    void customTitleIsBound() {
        contextRunner.withPropertyValues("sloth.doc.title=Demo API")
            .run(context -> assertThat(context.getBean(OpenAPI.class).getInfo().getTitle())
                .isEqualTo("Demo API"));
    }

    @Test
    @DisplayName("配置 groups 时注册自定义分组")
    void customGroupsAreRegistered() {
        contextRunner.withPropertyValues(
                "sloth.doc.groups[0].name=inner",
                "sloth.doc.groups[0].paths[0]=/api/inner/**")
            .run(context -> {
                List<?> groups = (List<?>) context.getBean("customGroupedOpenApis");
                assertThat(groups).hasSize(1);
                assertThat(((GroupedOpenApi) groups.get(0)).getGroup()).isEqualTo("inner");
            });
    }

    @Test
    @DisplayName("用户自定义 OpenAPI Bean 优先生效")
    void userDefinedOpenApiWins() {
        contextRunner.withBean(OpenAPI.class, OpenAPI::new)
            .run(context -> assertThat(context).hasSingleBean(OpenAPI.class));
    }
}
