package unit

import expert.Transparent
import gamedata.Inventory
import gamedata.items.ScrollPower
import gamedata.items.ScrollPower.ScrollPower
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import rogue.{Command, IRecorder, IRogueActuator}
import view.IView

class TransparentTest extends AnyFlatSpec with Matchers {
  "A transparent game of Rogue" should "report the known scroll powers to the user" in {
    val transparent: Transparent = new Transparent(MockActuator, MockRecorder, MockView)
    transparent.displayAll()
    assert(MockView.displayedPower)
  }

  object MockActuator extends IRogueActuator {
    override def sendCommand(getCommand: Command): Either[String, Unit] = fail("Command sent to Rogue")

    override def start(): Either[String, Unit] = fail("Start message sent to Rogue")
  }

  object MockRecorder extends IRecorder {
    override def getInventory: Inventory = Inventory()

    override def getScreen: String = ""

    override def getScore: Int = 0

    override def gameOver: Boolean = false

    override def getScrollPowers: Map[String, ScrollPower] = Map("abcde" -> ScrollPower.AGGRAVATE_MONSTER)
  }

  object MockView extends IView {
    private var _displayedPower: Boolean = false

    def displayedPower: Boolean = _displayedPower

    override def getCommand: Command = fail("User was asked for command")

    override def displayError(err: String): Unit = fail(err)

    override def displayInventory(inventory: Inventory): Unit = ()

    override def displayScreen(screen: String): Unit = ()

    override def displayGameOver(finalScore: Int): Unit = ()

    override def displayScrollPower(title: String, power: ScrollPower): Unit = {
      title should be("abcde")
      power should be(ScrollPower.AGGRAVATE_MONSTER) // TODO Duplication
      _displayedPower = true
    }
  }

}
