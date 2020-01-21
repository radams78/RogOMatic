package unit

import domain.pLift
import expert.{Transparent, pGameState}
import gamedata.item.magic.scroll.Scroll._
import gamedata.item.magic.scroll.ScrollPower
import gamedata.{Fact, pCommand, pInventory}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import view.IView

class TransparentTest extends AnyFlatSpec with Matchers {
  "The transparent expert" should "report the known scroll powers to the user" in {
    val transparent: Transparent = new Transparent(MockView)
    transparent.displayAll(pGameState(pLift.Known(""), pInventory(), Set(ScrollKnowledge("abcde", ScrollPower.AGGRAVATE_MONSTER)), pLift.UNKNOWN))
    assert(MockView.displayedPower)
  }

  private object MockView extends IView {
    private var _displayedPower: Boolean = false

    def displayedPower: Boolean = _displayedPower

    override def getCommand: pCommand = fail("User was asked for command")

    override def displayError(err: String): Unit = fail(err)

    override def displayInventory(inventory: pInventory): Unit = ()

    override def displayScreen(screen: String): Unit = ()

    override def displayGameOver(finalScore: Int): Unit = ()

    override def displayFact(fact: Fact): Unit = {
      fact should be(ScrollKnowledge("abcde", ScrollPower.AGGRAVATE_MONSTER))
      _displayedPower = true
    }
  }

}
