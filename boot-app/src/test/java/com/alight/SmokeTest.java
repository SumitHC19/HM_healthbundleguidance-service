package com.alight;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class SmokeTest {

    @Test
    public void startFromMain() throws Exception {
        Application.main(new String[] {});
    }
}
