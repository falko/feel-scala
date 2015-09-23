package org.camunda.feel

import org.camunda.feel.parser.FeelParser
import org.camunda.feel.parser.FeelParser._
import org.camunda.feel.interpreter.FeelInterpreter
import org.camunda.feel.interpreter.Context
import org.camunda.feel.interpreter._
import org.camunda.feel.parser.Exp

/**
 * @author Philipp Ossler
 */
class FeelEngine {

  val interpreter = new FeelInterpreter

  def evalSimpleUnaryTest(expression: String, context: Map[String, Any] = Map()): EvalResult = {
    nonEmpty(expression) { () =>
      FeelParser.parseSimpleUnaryTest(expression) match {
        case Success(exp, _) => {
          interpreter.eval(exp)(Context(context)) match {
            case ValError(cause) => EvalFailure(s"failed to evaluate expression '$expression':\n$cause")
            case value => EvalValue(extractResultValue(value))
          }
        }
        case e: NoSuccess => {
          ParseFailure(s"failed to parse expression '$expression':\n$e")
        }
      }
    }
  }

  private def nonEmpty(expression: String)(f: () => EvalResult): EvalResult = expression match {
    case "" => EvalValue(true)
    case _ => f()
  }

  private def extractResultValue(value: Val): Any = value match {
    case ValBoolean(boolean) => boolean
    case ValNumber(number) => number
    case ValString(string) => string
    case ValDate(date) => date
    case _ => throw new IllegalArgumentException(s"unexpected result value '$value'")
  }

}