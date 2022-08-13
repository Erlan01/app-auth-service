package ai.ecma.auth;

import ai.ecma.auth.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
@EnableJpaRepositories(basePackages = "ai.ecma.lib.repository")
@EntityScan(basePackages = "ai.ecma.lib.entity")
@EnableConfigurationProperties(AppProperties.class)
public class AppAuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppAuthServiceApplication.class, args);
    }

}
