package main

import rogue.IRogue
import view.IView

class RogOMatic(rogue : IRogue, view : IView) {
  def startGame(): Unit = view.notify(Seq("The first screen"))

}
