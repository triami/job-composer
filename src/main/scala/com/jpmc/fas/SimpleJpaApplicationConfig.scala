package com.jpmc.fas

/**
  * Created by amit on 8/4/16.
  */

import com.jpmc.fas.repository.PersonRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.{Bean, ComponentScan, Configuration}
import org.springframework.boot.autoconfigure.EnableAutoConfiguration

@Configuration
@EnableAutoConfiguration
@ComponentScan
class SimpleJpaApplicationConfig {}
