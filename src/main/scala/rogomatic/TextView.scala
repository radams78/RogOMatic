package rogomatic

import view.IView

class TextView extends IView {
  override def displayScreen(screen: String): Unit = println(screen)
}
