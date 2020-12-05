package view

import model.IRoguePlayerObserver

trait IView extends IRoguePlayerObserver {
  def notify(screen: Seq[String]): Unit
}
