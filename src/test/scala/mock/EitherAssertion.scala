package mock

import org.scalatest.Assertions
import rogue.RoguePlayer
import rogue.RoguePlayer.GameOn

object EitherAssertion extends Assertions {
  def getEither[T](x: Either[String, T]): T = x match {
    case Right(t) => t
    case Left(err) => fail(err)
  }

  def getGameOn(x: Either[String, RoguePlayer]): GameOn = x match {
    case Right(p: GameOn) => p
    case Right(_) => fail("Game ended prematurely")
    case Left(err) => fail(err)
  }
}
