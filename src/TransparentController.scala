import model.gamedata.Command
import rogue.IRogue

class TransparentController(rogue: IRogue) extends Runnable {
  override def run(): Unit = while (true) {
    val input: Char = scala.io.StdIn.readChar()
    input match {
      case 'Q' => Command.QUIT.perform(rogue)
      case _ => System.out.println("Unrecognized command")
    }
  }
}
