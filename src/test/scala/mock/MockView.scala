package mock

import gamedata.Inventory
import org.scalatest.{Assertion, Assertions}
import view.IView

/** A mock implementation of [[IView]] */
object MockView extends IView with Assertions {
  def assertDisplayed(screen: String): Assertion = assertResult(screen) {
    lastScreen.getOrElse(fail("No screen displayed yet"))
  }

  def assertDisplayedInventory(inventory: Inventory): Assertion = assertResult(inventory) {
    lastInventory.getOrElse(fail("No inventory displayed yet"))
  }

  private var lastInventory: Option[Inventory] = None

  private var lastScreen: Option[String] = None

  override def displayScreen(screen: String): Unit = lastScreen = Some(screen)

  override def displayInventory(inventory: Inventory): Unit = lastInventory = Some(inventory)
}
