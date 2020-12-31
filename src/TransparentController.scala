import model.Command
import model.rogue.{IRogue, RoguePlayer}

class TransparentController(player: RoguePlayer) extends Runnable {
  override def run(): Unit = while (true) {
    val input: Char = scala.io.StdIn.readChar()
    input match {
      case 'Q' => player.performCommand(Command.QUIT)
      case _ => System.out.println("Unrecognized command")
    }
  }
}
