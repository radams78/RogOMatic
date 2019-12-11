package expert

import gamedata.{Direction, Slot}
import rogue.RoguePlayer.GameOver
import rogue._
import view.IView

import scala.annotation.tailrec
import scala.io.StdIn

/** Expert for playing the game in transparent mode, i.e. interactively, getting moves from the user one by one */
class Transparent(player: RoguePlayer.NotStarted, view: IView) {
  /** Play a game of rogue */
  def playRogue(): RoguePlayer.GameOver = {
    @tailrec
    def playRogue0(player: RoguePlayer): GameOver = player match {
      case p: RoguePlayer.NotStarted => playRogue0(p.start())
      case p: RoguePlayer.GameOn =>
        view.displayScreen(p.getScreen)
        p.getInventory match {
          case Right(i) => view.displayInventory(i)
          case Left(s) => view.displayError(s)
        }
        playRogue0(p.sendCommand(getCommand))
      case p: GameOver => p
    }

    playRogue0(player)
  }

  /** Get a character from the user.
   *
   * Returns the first character in the next line that the user enters, or 'error' if the next input line is empty. */
  private def getCharacter(error: String): Either[String, Char] = {
    try {
      Right(StdIn.readChar())
    } catch {
      case _: StringIndexOutOfBoundsException => Left(error)
    }
  }

  /** Get a direction from the user */
  private def getDirection: Either[String, Direction] = {
    println("Select direction")
    for (d <- getCharacter("No direction entered")) yield d match {
      case 'b' => Direction.DOWNLEFT
      case 'h' => Direction.LEFT
      case 'j' => Direction.UP
      case 'k' => Direction.DOWN
      case 'l' => Direction.RIGHT
      case 'n' => Direction.DOWNRIGHT
      case 'u' => Direction.UPRIGHT
      case 'y' => Direction.UPLEFT
      case c => return Left(s"Unrecognised direction: $c")
    }
  }

  /** Get a command from the user */
  @tailrec
  private def getCommand: Command = {
    def getCommand0: Either[String, Command] = {
      for {c <- getCharacter("No command entered")
           cmd <- c match {
             case 'b' => Right(Command.DOWNLEFT)
             case 'h' => Right(Command.LEFT)
             case 'j' => Right(Command.UP)
             case 'k' => Right(Command.DOWN)
             case 'l' => Right(Command.RIGHT)
             case 'n' => Right(Command.DOWNRIGHT)
             case 't' => for {
               dir <- getDirection
               slot <- Slot.parse(StdIn.readChar().toString)
             } yield Command.Throw(dir, slot)
             case 'u' => Right(Command.UPRIGHT)
             case 'w' =>
               println("Select weapon")
               for (slot <- Slot.parse(StdIn.readChar().toString)) yield Command.Wield(slot)
             case 'y' => Right(Command.UPLEFT)
             case '.' => Right(Command.REST)
             case _ => Left(s"Unrecognised command: $c")
           }} yield cmd
    }

    getCommand0 match {
      case Right(c) => c
      case Left(s) =>
        println(s)
        getCommand
    }
  }
}
