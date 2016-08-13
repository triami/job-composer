package com.jpmc.fas.test.support

/**
  * Created by amit on 8/4/16.
  */

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.{ComponentScan, Configuration}

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = Array("com.jpmc.fas"))
class TestJpaApplicationConfig {}
