package unit

import expert.{Transparent, pGameState}
import gamedata.items.ScrollPower
import gamedata.items.ScrollPower.ScrollPower
import gamedata.{pInventory, pOption}
import gamestate.ScrollKnowledge
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import rogue.Command
import view.IView

class TransparentTest extends AnyFlatSpec with Matchers {
  "A transparent game of Rogue" should "report the known scroll powers to the user" in {
    val transparent: Transparent = new Transparent(MockView)
    transparent.displayAll(pGameState(Some(""), pInventory(), ScrollKnowledge(Map("abcde" -> ScrollPower.AGGRAVATE_MONSTER)), pOption.UNKNOWN))
    assert(MockView.displayedPower)
  }

  private object MockView extends IView {
    private var _displayedPower: Boolean = false

    def displayedPower: Boolean = _displayedPower

    override def getCommand: Command = fail("User was asked for command")

    override def displayError(err: String): Unit = fail(err)

    override def displayInventory(inventory: pInventory): Unit = ()

    override def displayScreen(screen: String): Unit = ()

    override def displayGameOver(finalScore: Int): Unit = ()

    override def displayScrollPower(title: String, power: ScrollPower): Unit = {
      title should be("abcde")
      power should be(ScrollPower.AGGRAVATE_MONSTER) // TODO Duplication
      _displayedPower = true
    }
  }

}
