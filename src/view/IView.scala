package view

import model.IRoguePlayerObserver

/** A view that displays info to the user */
trait IView extends IRoguePlayerObserver {
  def notify(screen: Seq[String]): Unit
}
