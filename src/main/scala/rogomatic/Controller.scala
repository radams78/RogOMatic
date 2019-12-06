package rogomatic

import gamedata.Inventory
import rogue.{Command, IRogue}
import view.IView

/** The controller class in the MVC architecture. Construct using the companion object. */
class Controller(rogue: IRogue, view: IView) {
  // TODO What should happen if game not started?
  // TODO What should happen if command not valid?
  /** Execute a command when playing in transparent mode */
  def sendCommand(command: Command): Unit = rogue.sendKeypress(command.keypress)

  /** Start a game of Rogue in transparent mode */
  def startTransparent(): Unit = {
    rogue.start()
    view.displayScreen(rogue.getScreen)
    rogue.sendKeypress('i')
    val screen: String = rogue.getScreen
    rogue.sendKeypress(' ')
    Inventory.parseInventoryScreen(screen) match {
      case Some(i) => view.displayInventory(i)
      case None => println(s"ERROR: Could not parse inventory screen:\n$screen")
    }
  }
}

/** Factory object for [[Controller]] class */
object Controller {
  def apply(rogue: IRogue, view: IView): Controller = new Controller(rogue, view)

}