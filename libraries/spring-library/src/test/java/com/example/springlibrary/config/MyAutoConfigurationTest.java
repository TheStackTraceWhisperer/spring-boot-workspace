package com.example.springlibrary.config;

import com.example.springlibrary.service.MyService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class MyAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(MyAutoConfiguration.class));

    @Test
    void serviceIsAutoConfigured() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(MyService.class);
            assertThat(context.getBean(MyService.class).greet()).isEqualTo("Hello from MyService!");
        });
    }

    @Test
    void serviceBacksOff() {
        contextRunner
                .withBean("myService", MyService.class, () -> new MyService() {
                    @Override
                    public String greet() {
                        return "Custom Greeting";
                    }
                })
                .run(context -> {
                    assertThat(context).hasSingleBean(MyService.class);
                    assertThat(context.getBean(MyService.class).greet()).isEqualTo("Custom Greeting");
                });
    }
}
