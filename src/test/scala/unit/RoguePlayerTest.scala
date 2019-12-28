package unit

import gamedata.{ScrollPower, _}
import mock._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import rogue._

/** Unit tests for [[RoguePlayer]] class */
class RoguePlayerTest extends AnyFlatSpec with Matchers {

  trait ZeroMoveGame {
    val player: RoguePlayer.NotStarted = RoguePlayer(ZeroMoveGame.zeroMoveGame)
  }

  trait EmptyInventoryGame {
    val player: RoguePlayer.NotStarted = RoguePlayer(ZeroMoveGame.emptyInventoryGame)
  }

  trait TestGame {
    val player: RoguePlayer.NotStarted = RoguePlayer(TestGame.testGame)
  }

  trait DeathGame {
    val player: RoguePlayer.NotStarted = RoguePlayer(DeathGame.deathGame)
  }

  trait MoreGame {
    val player: RoguePlayer.GameOn = new RoguePlayer.GameOn(MoreGame.moreGame, new GameState())
  }

  trait ReadScroll {
    val player: RoguePlayer.GameOn = new RoguePlayer.GameOn(MockRogue.Build
      .WaitForCommand(TestGame.secondScreen, TestGame.secondInventoryScreen, 'r')
      .Wait(TestGame.thirdScreen, 'f')
      .End(TestGame.fourthScreen, TestGame.fourthInventoryScreen), new GameState()
    )
  }

  it should "display the first screen of the game" in new ZeroMoveGame {
    player.start().getScreen should be(ZeroMoveGame.firstScreen)
  }

  it should "display the first inventory of the game" in new EmptyInventoryGame {
    player.start().getInventory should be(Right(Inventory()))
  }

  it should "display the new screen after sending the command" in new TestGame {
    player.start().sendCommand(Command.RIGHT) match {
      case Right(p: RoguePlayer.GameOn) => p.getScreen should be(TestGame.secondScreen)
      case Right(_: RoguePlayer.GameOver) => fail("Game ended prematurely")
      case Left(s) => fail(s)
    }
  }

  it should "display the inventory after sending the command" in new TestGame {
    player.start().sendCommand(Command.RIGHT) match {
      case Right(p: RoguePlayer.GameOn) => p.getInventory should be(Right(TestGame.firstInventory))
      case Right(_: RoguePlayer.GameOver) => fail("Game ended prematurely")
      case Left(s) => fail(s)

    }
  }

  it should "know when the game is over" in new DeathGame {
    player.start().sendCommand(Command.REST) match {
      case Right(p: RoguePlayer.GameOver) => p.getScore should be(7)
      case Right(_) => fail("Game not ended when it should have")
      case Left(s) => fail(s)
    }
  }

  it should "clear a more screen" in new MoreGame {
    player.sendCommand(Command.RIGHT) match {
      case Right(p: RoguePlayer.GameOn) => p.getScreen should be(MoreGame.thirdScreen)
      case Right(_) => fail("Game ended prematurely")
      case Left(s) => fail(s)
    }
  }

  it should "remember scroll powers" in new ReadScroll {
    player.sendCommand(Command.Read(Slot.F, Scroll(1, "coph rech"))) match {
      case Right(p: RoguePlayer.GameOn) => p.getScrollKnowledge.getPower("coph rech") should contain(ScrollPower.REMOVE_CURSE)
      case Right(_) => fail("Game ended prematurely")
      case Left(s) => fail(s)
    }
  }
}
