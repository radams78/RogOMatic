package unit

import gamedata._
import gamedata.item.magic.scroll.Scroll
import gamedata.item.pItem
import mock._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import rogue._

/** Unit tests for [[RogueActuator]] class */
class RogueActuatorTest extends AnyFlatSpec with Matchers {

  trait ZeroMoveGame {
    val game: MockRogue = mock.ZeroMoveGame.zeroMoveGame
    val player: IRogueActuator = new RogueActuator(game)
  }

  trait EmptyInventoryGame {
    val player: IRogueActuator = new RogueActuator(ZeroMoveGame.emptyInventoryGame)
  }

  trait TestGame {
    val player: IRogueActuator = new RogueActuator(TestGame.testGame)
  }

  trait DeathGame {
    val player: IRogueActuator = new RogueActuator(DeathGame.deathGame)
  }

  trait MoreGame {
    val player: IRogueActuator = new RogueActuator(MoreGame.moreGame)
  }

  trait ReadScroll {
    val player: IRogueActuator = new RogueActuator(MockRogue.Build
      .WaitForCommand("readScroll state 1", TestGame.secondScreen, TestGame.secondInventoryScreen, 'r')
      .Wait(TestGame.thirdScreen, 'f')
      .Terminal("readScroll state 3", TestGame.fourthScreen, TestGame.fourthInventoryScreen)
    )
  }

  it should "display the first screen of the game" in new ZeroMoveGame {
    player.start() match {
      case Right(report: Report.GameOn) => report.screen should be(ZeroMoveGame.firstScreen)
      case Right(report) => fail(s"Incorrect report type returned: $report")
      case Left(err) => fail(err)
    }
  }

  it should "display the first inventory of the game" in new EmptyInventoryGame {
    player.start() match {
      case Right(report: Report.GameOn) => report.inventory should be(pInventory(Map[Slot, pItem](), None, None))
      case Right(report) => fail(s"Incorrect report type returned: $report")
      case Left(err) => fail(err)
    }
  }

  it should "display the new screen after sending the command" in new TestGame {
    player.start() match {
      case Right(report: Report.GameOn) => ()
      case Right(report) => fail(s"Incorrect report type returned: $report")
      case Left(err) => fail(err)
    }
    player.sendCommand(Command.RIGHT) match {
      case Right(report: Report.GameOn) => report.screen should be(TestGame.secondScreen)
      case Right(report) => fail(s"Incorrect report type returned: $report")
      case Left(err) => fail(err)
    }
  }

  it should "display the inventory after sending the command" in new TestGame {
    player.start() match {
      case Right(report: Report.GameOn) => ()
      case Right(report) => fail(s"Incorrect report type returned: $report")
      case Left(err) => fail(err)
    }
    player.sendCommand(Command.RIGHT) match {
      case Right(report: Report.GameOn) => report.inventory should be(TestGame.firstInventory)
      case Right(report) => fail(s"Incorrect report type returned: $report")
      case Left(err) => fail(err)
    }
  }

  it should "know when the game is over" in new DeathGame {
    player.start() match {
      case Right(report: Report.GameOn) => ()
      case Right(report) => fail(s"Incorrect report type returned: $report")
      case Left(err) => fail(err)
    }
    player.sendCommand(Command.REST) match {
      case Right(report: Report.GameOver) => report.score should be(7)
      case Right(report) => fail(s"Incorrect report type returned: $report")
      case Left(err) => fail(err)
    }
  }

  it should "clear a more screen" in new MoreGame {
    player.sendCommand(Command.RIGHT) match {
      case Right(report: Report.GameOn) => report.screen should be(MoreGame.thirdScreen)
      case Right(report) => fail(s"Incorrect report type returned: $report")
      case Left(err) => fail(err)
    }
  }

  it should "remember scroll powers" in new ReadScroll {
    player.sendCommand(Command.Read(Slot.F, Scroll(1, "coph rech"))) match {
      case Right(report: Report.GameOn) => report.events should contain(Event.REMOVE_CURSE)
      case Right(report) => fail(s"Incorrect report type returned: $report")
      case Left(err) => fail(err)
    }
  }
}
