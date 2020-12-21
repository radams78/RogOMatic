package view

import rogue.IScreenObserver

/** A view that displays info to the user */
trait IView extends IScreenObserver {
  def notify(screen: Seq[String]): Unit
}
