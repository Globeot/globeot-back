package com.globeot.globeotback.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class TimeConfigTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(JacksonAutoConfiguration.class))
            .withUserConfiguration(TimeConfig.class);

    @Test
    void localDateTimeIsSerializedAsUtc() {
        runner.run(ctx -> {
            JsonMapper mapper = ctx.getBean(JsonMapper.class);
            String json = mapper.writeValueAsString(Map.of("createdAt", LocalDateTime.of(2026, 11, 24, 5, 46, 13, 123_000_000)));
            assertThat(json).isEqualTo("{\"createdAt\":\"2026-11-24T05:46:13.123Z\"}");
        });
    }
}
