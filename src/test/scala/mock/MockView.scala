package mock

import gamedata.Inventory
import view.IView

/** A mock implementation of [[IView]] */
object MockView extends IView {
  private var lastInventory: Option[Inventory] = None

  def hasDisplayedInventory(inventory: Inventory): Boolean = lastInventory contains inventory

  private var lastScreen: Option[String] = None

  def hasDisplayed(screen: String): Boolean = lastScreen contains screen

  override def displayScreen(screen: String): Unit = lastScreen = Some(screen)

  override def displayInventory(inventory: Inventory): Unit = lastInventory = Some(inventory)
}
