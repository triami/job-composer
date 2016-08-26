package com.jpmc.fas.parser

import java.lang.reflect.{Field, ParameterizedType}
import java.util
import java.util.Date

import sun.tools.tree.AssignShiftLeftExpression

import scala.util.{Success, Try}

/**
  * Created by amit on 8/19/16.
  */
object FasJobSpec {


  val modelLookUpPackageName = "com.jpmc.fas.model"

  sealed trait Cardinality
  case object ONE extends Cardinality
  case object MANY extends Cardinality

  sealed trait AggregateFunction
  case object MAX extends AggregateFunction
  case object COUNT extends AggregateFunction
  case object MIN extends AggregateFunction
  case object AVERAGE extends AggregateFunction

  sealed trait PredicateOperator
  case object == extends PredicateOperator
  case object != extends PredicateOperator
  case object <= extends PredicateOperator
  case object >= extends PredicateOperator
  case object <  extends PredicateOperator
  case object >  extends PredicateOperator
  case object like extends PredicateOperator
  case object iLike extends PredicateOperator

  trait Value
  case class StringValue(value: String) extends Value
  case class NumberValue(value: Number) extends Value
  case class BooleanValue(value: Boolean) extends Value
  object BooleanValue{
    def apply(s:String) : PartialFunction[String, BooleanValue] = {
      case "true" => BooleanValue(true)
      case "false" => BooleanValue(false)
    }
  }

  case class PathNode(from: Class[_], to: Class[_], name: String, cardinality: Cardinality)
  case class PathElement(rootClass: Class[_], path: Seq[PathNode])

  object PathElement{

    /*def apply(pathAsString:Seq[String]) : PartialFunction[Seq[String], PathElement] = {
      findEntityClass(pathAsString.head) match {
        case Success(clazz) => {
          val pathNodes = pathAsString.foldLeft(Seq[PathNode]()){ (a,b) =>
            val fieldMayBe = if(a.isEmpty) Option(clazz.getDeclaredField(b)) else Option(a.last.from.getDeclaredField(b))
            fieldMayBe match {
              case Some(field) => {
                val typeAndCardinality = resolveTypeAndCardinality(field)
                a :+ PathNode(field.getDeclaringClass, typeAndCardinality._1, b, typeAndCardinality._2)
              }
              case None => throw new NoSuchFieldException(s"The field $b was not found in the path")
            }
          }
          new PathElement(clazz, pathNodes)
        }
      }

    }*/

    def findEntityClass(className: String) : Try[Class[_]] = Try(Class.forName(s"$modelLookUpPackageName.$className"))



    def resolveTypeAndCardinality(field:Field): Tuple2[Class[_], Cardinality] = {
      if((classOf[util.Collection[_]]).isAssignableFrom(field.getType)) {
        (Class.forName(field.getGenericType.asInstanceOf[ParameterizedType].getActualTypeArguments.head.getTypeName), MANY)
      } else (field.getType, ONE)
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


  trait LogicallyOperable
  case class Predicate(leftExpression: Either[Value, PathElement], predicateOperator: PredicateOperator,  rightExpression: Either[Value, PathElement]) extends LogicallyOperable
  case class ConjunctionExpression(le: LogicallyOperable, re: LogicallyOperable) extends LogicallyOperable
  case class DisjunctionExpression(le: LogicallyOperable, re: LogicallyOperable) extends LogicallyOperable















}
