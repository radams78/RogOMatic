package expert

import rogue.{Command, RoguePlayer}
import view.IView

import scala.io.StdIn

/** Expert for playing the game in transparent mode, i.e. interactively, getting moves from the user one by one */
class Transparent(player: RoguePlayer, view: IView) {
  /** Play a game of rogue */
  def playRogue(): Unit = {
    player.start()
    while (!player.gameOver) {
      view.displayScreen(player.getScreen)
      view.displayInventory(player.getInventory)
      var cmd: Char = StdIn.readChar()
      cmd match {
        case 'b' => player.sendCommand(Command.DOWNLEFT)
        case 'h' => player.sendCommand(Command.LEFT)
        case 'j' => player.sendCommand(Command.UP)
        case 'k' => player.sendCommand(Command.DOWN)
        case 'l' => player.sendCommand(Command.RIGHT)
        case 'n' => player.sendCommand(Command.DOWNRIGHT)
        case 'u' => player.sendCommand(Command.UPRIGHT)
        case 'y' => player.sendCommand(Command.UPLEFT)
        case '.' => player.sendCommand(Command.REST)
      }
    }
  }
}
