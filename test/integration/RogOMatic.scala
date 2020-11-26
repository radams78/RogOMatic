package integration

class RogOMatic(rogue : IRogue, view : IView) {
  def startGame(): Unit = view.notify(Seq("The first screen"))

}
