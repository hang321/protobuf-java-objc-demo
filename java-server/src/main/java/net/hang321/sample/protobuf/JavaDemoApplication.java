package net.hang321.sample.protobuf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.integration.annotation.IntegrationComponentScan;

/**
 * @Author: Steve Chan
 **/
@ComponentScan(basePackages={"net.hang321.sample.protobuf"})
@IntegrationComponentScan
@ImportResource("classpath:spring-integration.xml")
@SpringBootApplication
public class JavaDemoApplication {
  public static void main(String[] args) {
    SpringApplication.run(JavaDemoApplication.class, args);
  }
}
