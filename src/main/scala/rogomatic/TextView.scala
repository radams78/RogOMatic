package rogomatic

import view.IView

/** A simple text input/output */
class TextView extends IView {
  override def displayScreen(screen: String): Unit = println(screen)
}
