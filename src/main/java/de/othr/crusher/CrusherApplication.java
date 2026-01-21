package de.othr.crusher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class CrusherApplication {

  public static void main(String[] args) {
    SpringApplication.run(CrusherApplication.class, args);
  }
}
