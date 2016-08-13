package com.jpmc.fas

import javax.persistence.{EntityManager, criteria}
import javax.persistence.criteria._

import com.jpmc.fas.model.{City, Person}
import com.jpmc.fas.parser.FasJobSpecParser
import com.jpmc.fas.repository.PersonRepository
import org.junit.{Assert, Test}
import org.junit.runner.RunWith
import org.scalatest.{BeforeAndAfter, FlatSpec, ShouldMatchers}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

/**
  * Created by amit on 8/4/16.
  */
//@RunWith(classOf[SpringJUnit4ClassRunner])
//@SpringApplicationConfiguration(classes = Array(classOf[SimpleJpaApplicationConfig]))
class JobSpecParserTests {

  //@Autowired var repo: PersonRepository = _
  //@Autowired var em: EntityManager = _

  import collection.JavaConversions._



  @Test
  def sgouldBeAbleToAddEntitiesAndFetchThemViaEm(){
    val jobSpec =""" a.b.c, b.d.f, max(f.e.g), min(g.e.h), count(g.e.h) where b.d.e == "1" and b.d == 1 or b.a == 1 """
    println(FasJobSpecParser.main(Array(jobSpec)))
  }

}
