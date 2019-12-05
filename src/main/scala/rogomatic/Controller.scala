package rogomatic

import gamedata.Inventory
import rogue.IRogue
import view.IView

/** The controller class in the MVC architecture. Construct using the companion object. */
class Controller(rogue: IRogue, view: IView) {
  /** Start a game of Rogue in transparent mode */
  def startTransparent(): Unit = {
    rogue.start()
    view.displayScreen(rogue.getScreen)
    view.displayInventory(Inventory())
  }

}

/** Factory object for [[Controller]] class */
object Controller {
  def apply(rogue: IRogue, view: IView): Controller = new Controller(rogue, view)

}