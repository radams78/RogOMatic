package view

import model.IScreenObserver
import model.rogue.Screen

class ScreenView extends IScreenObserver {
  override def notify(screen: Screen): Unit = println(screen)
}
