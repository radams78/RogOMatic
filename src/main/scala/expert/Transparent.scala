package expert

import gamedata.Slot
import rogue.{Command, RoguePlayer}
import view.IView

import scala.annotation.tailrec
import scala.io.StdIn

/** Expert for playing the game in transparent mode, i.e. interactively, getting moves from the user one by one */
class Transparent(player: RoguePlayer, view: IView) {
  /** Play a game of rogue */
  def playRogue(): Unit = {
    player.start()
    while (!player.gameOver) {
      view.displayScreen(player.getScreen)
      player.getInventory match {
        case Right(i) => view.displayInventory(i)
        case Left(s) => view.displayError(s)
      }
      player.sendCommand(getCommand)
    }
  }

  @tailrec
  private def getCommand: Command = {
    def getCommand0: Either[String, Command] = {
      val cmd: Char = StdIn.readChar()
      cmd match {
        case 'b' => Right(Command.DOWNLEFT)
        case 'h' => Right(Command.LEFT)
        case 'j' => Right(Command.UP)
        case 'k' => Right(Command.DOWN)
        case 'l' => Right(Command.RIGHT)
        case 'n' => Right(Command.DOWNRIGHT)
        case 'u' => Right(Command.UPRIGHT)
        case 'w' =>
          println("Select weapon")
          val slot: Slot = Slot.parse(StdIn.readChar().toString)
          Right(Command.Wield(slot))
        case 'y' => Right(Command.UPLEFT)
        case '.' => Right(Command.REST)
        case _ => Left(s"Unrecognised command: $cmd")
      }
    }

    getCommand0 match {
      case Right(c) => c
      case Left(s) =>
        println(s)
        getCommand
    }
  }
}
