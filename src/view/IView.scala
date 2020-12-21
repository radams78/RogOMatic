package view

import model.IGameOverObserver
import rogue.IScreenObserver

/** A view that displays info to the user */
trait IView extends IScreenObserver with IGameOverObserver {
  def notify(screen: Seq[String]): Unit
}
