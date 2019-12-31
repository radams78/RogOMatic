package mock

import org.scalatest.Assertions

object EitherAssertion extends Assertions {
  def getEither[T](x: Either[String, T]): T = x match {
    case Right(t) => t
    case Left(err) => fail(err)
  }
}
