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

  val modelLookUpPackageName = "com.jpmc.fas.model"

  sealed trait Cardinality
  case object ONE extends Cardinality
  case object MANY extends Cardinality

  sealed trait AggregateFunction
  case object MAX extends AggregateFunction
  case object COUNT extends AggregateFunction
  case object MIN extends AggregateFunction
  case object AVERAGE extends AggregateFunction


  case class PathFragment(attributeClass: Class[_], attributeName: String, attributeCardinality: Cardinality)
  case class PathElement(rootClass: Class[_], path: Seq[PathFragment])


  def resolveTypeAndCardinality(field:Field): Tuple2[Class[_], Cardinality] = {
    if((classOf[util.Collection[_]]).isAssignableFrom(field.getType)) {
      (Class.forName(field.getGenericType.asInstanceOf[ParameterizedType].getActualTypeArguments.head.getTypeName), MANY)
    } else (field.getType, ONE)
  }

  object PathElement{
    def apply(className: String, pathAsString:Seq[String]) : PathElement = {
      val rootClass: Class[_] = Class.forName(s"$modelLookUpPackageName.$className")
      val pathFrags = pathAsString.foldLeft(Seq[PathFragment]()){ (a,b) =>
        val fieldMayBe = if(a.isEmpty) Option(rootClass.getDeclaredField(b)) else Option(a.last.attributeClass.getDeclaredField(b))
        fieldMayBe match {
          case Some(field) => {
            val typeAndCardinality = resolveTypeAndCardinality(field)
            a :+ PathFragment(typeAndCardinality._1, b, typeAndCardinality._2)
          }
          case None => throw new NoSuchFieldException(s"The field $b was not found in the path")
        }
      }
      new PathElement(rootClass, pathFrags)
    }
  }


  case class AggregateElement(aggregateFunction: AggregateFunction, pathElement: PathElement)
  object AggregateElement {
    def apply[T](aggregateFunctionName: String, pathElement: PathElement): AggregateElement = {
      aggregateFunctionName match {
        case "max" => new AggregateElement(MAX, pathElement)
        case "count" => new AggregateElement(COUNT, pathElement)
        case "min" => new AggregateElement(MIN, pathElement)
        case "average" => new AggregateElement(AVERAGE, pathElement)
        case _ => throw new IllegalArgumentException(s"Unsupported aggregate function $aggregateFunctionName")
      }
    }
  }





  def simpleAggregateFunctionToken: Parser[String] = "max" | "count" | "min" | "avg"
  def infixPredicateOperatorToken: Parser[String] = "==" | "!=" | ">" | "<" | ">=" | "<="
  def logicalOperatorToken: Parser[String] = "and" | "or"
  def valueType: Parser[Any] = stringLiteral | floatingPointNumber | decimalNumber | wholeNumber


  trait SelectableElem
  case class PathElem(root: String, path: Seq[String]) extends SelectableElem
  case class AggregationElem(aggregateFunction: String, pathElem: PathElem) extends SelectableElem
  case class FilterElem(predicateOperator: String, operandL: Any, operandR: Any)
  case class ConjunctionElem(operandL: FilterElem, operandR: FilterElem)
  case class DisjunctionElem(operandL: FilterElem, operandR: FilterElem)

  def pathElem: Parser[PathElem] = ident ~ rep1("." ~> ident) ^^ { it => PathElem(it._1, it._2) }
  def aggregationElem: Parser[AggregationElem] = simpleAggregateFunctionToken ~ ("(" ~> pathElem <~ ")") ^^ { it => AggregationElem(it._1, it._2) }
  def filterElem: Parser[FilterElem] = (pathElem | valueType) ~ infixPredicateOperatorToken ~ (pathElem | valueType) ^^ { it => FilterElem(it._1._2, it._1._1, it._2)}
  def conjunctionGroupElem: Parser[ConjunctionElem] = filterElem ~ "and" ~ filterElem ^^ { it => ConjunctionElem(it._1._1, it._2)}
  def disjunctionGroupElem: Parser[DisjunctionElem] = filterElem ~ "or" ~ filterElem ^^ { it => DisjunctionElem(it._1._1, it._2)}

  def selectionExpr: Parser[Seq[SelectableElem]] = ("(" ~> selectionExpr <~ ")" ) | (pathElem ~ rep("," ~> (pathElem | aggregationElem))) ^^ { it => it._1 :: it._2 }
  def filterExpr: Parser[Any] = "(" ~> filterExpr <~ ")" | rep(conjunctionGroupElem) | rep(disjunctionGroupElem)



  def jobSpecExpr = selectionExpr ~ opt("where" ~> filterExpr)
}

object FasJobSpecParser extends FasJobSpecParser {

  def main(args: Array[String]): Unit = {
    println("input: " + args(0))
    println("output: " + parseAll(jobSpecExpr, args(0)))
  }
}