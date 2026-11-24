package com.globeot.globeotback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class GlobeotBackApplication {

    public static void main(String[] args) {
        // 저장 시간은 UTC 기준 (로컬 PC 시간대와 무관하게 서버와 동일하게 맞춤)
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        SpringApplication.run(GlobeotBackApplication.class, args);
    }
}
