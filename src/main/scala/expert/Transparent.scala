package expert

import gamedata._
import gamedata.items.{Potion, Scroll}
import rogue._
import view.IView

import scala.annotation.tailrec
import scala.io.StdIn

/** Expert for playing the game in transparent mode, i.e. interactively, getting moves from the user one by one */
class Transparent(player: RogueActuator, recorder: Recorder, view: IView) {
  /** Play a game of rogue */
  @tailrec
  final def playRogue(): Unit = {
    if (recorder.gameOver) {
      view.displayGameOver(recorder.getScore)
    } else {
      view.displayScreen(recorder.getScreen)
      val inventory: Inventory = recorder.getInventory
      view.displayInventory(inventory)
      player.sendCommand(getCommand(inventory))
      playRogue()
    }
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

  /** Get an item in the PC's inventory */
  private def getItem: Either[String, Slot] = {
    println("Select object")
    for {
      c <- getCharacter("No slot entered")
      s <- Slot.parse(c.toString)
    } yield s
  }

  /** Get a command from the user */
  @tailrec
  private def getCommand(inventory: Inventory): Command = {
    def getCommand0: Either[String, Command] = {
      for {c <- getCharacter("No command entered")
           cmd <- c match {
             case 'b' => Right(Command.DOWNLEFT)
             case 'h' => Right(Command.LEFT)
             case 'j' => Right(Command.UP)
             case 'k' => Right(Command.DOWN)
             case 'l' => Right(Command.RIGHT)
             case 'n' => Right(Command.DOWNRIGHT)
             case 'q' => for {
               slot <- getItem
             } yield inventory.items.get(slot) match {
               case Some(p: Potion) => Command.Quaff(Some(slot), p)
               case _ => return Left(s"Invalid potion: $slot")
             }
             case 'r' => for {
               slot <- getItem
             } yield inventory.items.get(slot) match {
               case Some(s: Scroll) => Command.Read(Some(slot), s)
               case _ => return Left(s"Invalid scroll: $slot")
             }
             case 't' => for {
               dir <- getDirection
               slot <- getItem
             } yield Command.Throw(dir, slot, inventory.items(slot)) // TODO Better error handling
             case 'u' => Right(Command.UPRIGHT)
             case 'w' => for (slot <- getItem) yield Command.Wield(slot)
             case 'y' => Right(Command.UPLEFT)
             case '.' => Right(Command.REST)
             case '>' => Right(Command.DOWNSTAIRS)
             case _ => Left(s"Unrecognised command: $c")
           }} yield cmd
    }

    getCommand0 match {
      case Right(c) => c
      case Left(s) =>
        println(s)
        getCommand(inventory)
    }
  }
}
