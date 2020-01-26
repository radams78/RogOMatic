package acceptance

import mock.{MockRogue, MockUser, TestGame}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import rogomatic.RogOMatic

class TransparentGameSpec extends AnyFunSuite with Matchers {

  test("Play a game of Rogue in transparent mode") {
    val rogue: MockRogue = TestGame.testGame
    val user: MockUser = TestGame.user
    RogOMatic.transparent(rogue, user).playRogue(user)
    assert(user.finished)
  }
}
