package com.jpmc.fas.test.support;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by amit on 8/7/16.
 */

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.jpmc.fas")
public class TestJpaApplicationConfig2 {
}
