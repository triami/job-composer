package com.jpmc.fas.parser


import java.lang.reflect.{Field, ParameterizedType}
import java.util

import scala.util.Try
import scala.util.parsing.combinator.JavaTokenParsers
import scala.util.parsing.combinator._

/**
  * Created by amit on 8/7/16.
  */
class FasJobSpecParser extends JavaTokenParsers {

  /**
    * a.b.c, b.d.f, max(f.e.g), min(g.e.h), count(g.e.h) where b.d.e == "1" and b.d == 1 or (b.a == 1)
    *
    */







  def simpleAggregateFunctionToken: Parser[String] = "max" | "count" | "min" | "avg"
  def infixPredicateOperatorToken: Parser[String] = "==" | "!=" | ">" | "<" | ">=" | "<="
  def logicalOperatorToken: Parser[String] = "and" | "or"
  def valueType: Parser[Any] = stringLiteral | floatingPointNumber | decimalNumber | wholeNumber



  trait SelectableElem
  case class PathElem(root: String, path: Seq[String]) extends SelectableElem
  case class AggregationElem(aggregateFunction: String, pathElem: PathElem) extends SelectableElem

  trait LogicallyGroupable
  case class FilterElem(predicateOperator: String, operandL: Any, operandR: Any) extends LogicallyGroupable
  case class ConjunctionElem(operandL: LogicallyGroupable , operandR: LogicallyGroupable) extends LogicallyGroupable
  case class DisjunctionElem(operandL: LogicallyGroupable, operandR: LogicallyGroupable) extends LogicallyGroupable

  def pathElem: Parser[PathElem] = ("(" ~> pathElem <~ ")") | ident ~ rep1("." ~> ident) ^^ { it => PathElem(it._1, it._2) }
  def aggregationElem: Parser[AggregationElem] = ("(" ~> aggregationElem <~ ")") | simpleAggregateFunctionToken ~ ("(" ~> pathElem <~ ")") ^^ { it => AggregationElem(it._1, it._2) }
  def filterElem: Parser[FilterElem] = ("(" ~> filterElem <~ ")") | ((pathElem | valueType) ~ infixPredicateOperatorToken ~ (pathElem | valueType)) ^^ { it => FilterElem(it._1._2, it._1._1, it._2)}

  def conjunctionGroupElem: Parser[ConjunctionElem] =  ("(" ~> conjunctionGroupElem <~ ")") |  (filterElem | conjunctionGroupElem | disjunctionGroupElem) ~ ("and" ~> (filterElem | conjunctionGroupElem | disjunctionGroupElem))  ^^ { it => ConjunctionElem(it._1, it._2)}
  def disjunctionGroupElem: Parser[DisjunctionElem] = ("(" ~> disjunctionGroupElem <~ ")") |  (filterElem | conjunctionGroupElem | disjunctionGroupElem) ~ ("or" ~> (filterElem | conjunctionGroupElem | disjunctionGroupElem))  ^^ { it => DisjunctionElem(it._1, it._2)}

  def selectionExpr: Parser[Seq[SelectableElem]] = ("(" ~> selectionExpr <~ ")" ) | (pathElem ~ rep("," ~> (pathElem | aggregationElem))) ^^ { it => it._1 :: it._2 }
  def filterExpr: Parser[Any] = "(" ~> filterExpr <~ ")" | (conjunctionGroupElem | disjunctionGroupElem | filterElem) ~ rep( logicalOperatorToken ~ filterExpr)



  def jobSpecExpr = selectionExpr ~ opt("where" ~> (filterExpr))
}

object FasJobSpecParser extends FasJobSpecParser {

  def main(args: Array[String]): Unit = {
    println("input: " + args(0))
    println("output: " + parseAll(jobSpecExpr, args(0)))
  }
}