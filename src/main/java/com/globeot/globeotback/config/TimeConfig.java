package com.globeot.globeotback.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.JacksonModule;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.module.SimpleModule;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * DB·서버는 UTC로 시간을 저장한다.
 * API 응답의 LocalDateTime에 UTC 표시(Z)를 붙여서
 * 브라우저가 사용자 시간대(한국 시간)로 올바르게 바꿔 보여주도록 한다.
 * 예: 2026-11-24T05:46:13.123 → "2026-11-24T05:46:13.123Z"
 */
@Configuration
public class TimeConfig {

    @Bean
    public JacksonModule utcLocalDateTimeModule() {
        SimpleModule module = new SimpleModule("utc-local-date-time");
        module.addSerializer(LocalDateTime.class, new ValueSerializer<>() {
            @Override
            public void serialize(LocalDateTime value, JsonGenerator gen, SerializationContext ctxt) {
                gen.writeString(value.toInstant(ZoneOffset.UTC).toString());
            }
        });
        return module;
    }
}
