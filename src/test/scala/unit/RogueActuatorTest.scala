package unit

import gamedata._
import gamedata.item.Item
import gamedata.item.magic.scroll.Scroll._
import gamedata.item.magic.scroll.{Scroll, ScrollPower}
import mock._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import rogue._

/** Unit tests for [[RogueActuator]] class */
class RogueActuatorTest extends AnyFlatSpec with Matchers {

  trait Fixture {
    val recorder: Recorder = new Recorder
  }

  trait ZeroMoveGameF extends Fixture {
    val game: MockRogue = mock.ZeroMoveGame.zeroMoveGame
    val player: RogueActuator = new RogueActuator(game, recorder)
  }

  trait EmptyInventoryGame extends Fixture {
    val player: RogueActuator = new RogueActuator(ZeroMoveGame.emptyInventoryGame, recorder)
  }

  trait TestGame extends Fixture {
    val player: RogueActuator = new RogueActuator(TestGame.testGame, recorder)
  }

  trait DeathGame extends Fixture {
    val player: RogueActuator = new RogueActuator(DeathGame.deathGame, recorder)
  }

  trait MoreGame extends Fixture {
    val player: RogueActuator = new RogueActuator(MoreGame.moreGame, recorder)
  }

  trait ReadScroll extends Fixture {
    val player: RogueActuator = new RogueActuator(MockRogue.Build
      .WaitForCommand("readScroll state 1", TestGame.secondScreen, TestGame.secondInventoryScreen, 'r')
      .Wait(TestGame.thirdScreen, 'f')
      .End("readScroll state 3", TestGame.fourthScreen, TestGame.fourthInventoryScreen), recorder
    )
  }

  it should "display the first screen of the game" in new ZeroMoveGameF {
    player.start()
    recorder.getScreen should be(ZeroMoveGame.firstScreen)
  }

  it should "display the first inventory of the game" in new EmptyInventoryGame {
    player.start() match {
      case Right(_) => recorder.getInventory should be(pInventory(Map[Slot, Item](), None, None))
      case Left(err) => fail(err)
    }
  }

  it should "display the new screen after sending the command" in new TestGame {
    player.start()
    player.sendCommand(Command.RIGHT)
    recorder.getScreen should be(TestGame.secondScreen)
  }

  it should "display the inventory after sending the command" in new TestGame {
    player.start()
    player.sendCommand(Command.RIGHT)
    recorder.getInventory should be(TestGame.firstInventory)
  }

  it should "know when the game is over" in new DeathGame {
    player.start()
    player.sendCommand(Command.REST)
    recorder.getScore should be(7)
  }

  it should "clear a more screen" in new MoreGame {
    player.sendCommand(Command.RIGHT)
    recorder.getScreen should be(MoreGame.thirdScreen)
  }

  it should "remember scroll powers" in new ReadScroll {
    player.sendCommand(Command.Read(Slot.F, Scroll(1, "coph rech")))
    recorder.getScrollKnowledge should contain(ScrollKnowledge("coph rech", ScrollPower.REMOVE_CURSE))
  }
}
