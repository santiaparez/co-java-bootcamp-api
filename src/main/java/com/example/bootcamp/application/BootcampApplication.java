package com.example.bootcamp.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.example.bootcamp")
public class BootcampApplication {
  public static void main(String[] args) { SpringApplication.run(BootcampApplication.class, args); }
}
