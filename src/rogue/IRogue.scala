package rogue

import model.IScreenObserver

trait IRogue {
  def addScreenObserver(observer: IScreenObserver): Unit

  def sendKeypress(keypress: Char): Unit

  def startGame() : Unit
}
