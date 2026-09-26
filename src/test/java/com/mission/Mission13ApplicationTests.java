package com.mission;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing // 생성일과 수정일 등 감시기능 활성화하여 DB 반영
@SpringBootTest
class Mission13ApplicationTests {

    @Test
    void contextLoads() {
    }

}
