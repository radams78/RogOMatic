package view

import gamedata.items.ScrollPower.ScrollPower
import gamedata.{Direction, Inventory, Slot}
import rogue.Command

import scala.io.StdIn

/** A simple text input/output */
class TextView extends IView {
  override def displayScreen(screen: String): Unit = println(screen)

  override def displayInventory(inventory: Inventory): Unit = {
    for ((slot, item) <- inventory.items.toList.sortBy(_._1)) println(s"$slot) $item")
    for (slot <- inventory.wielding) println(s"WEAPON: $slot) ${inventory.items(slot)}")
    for (slot <- inventory.wearing) println(s"ARMOR: $slot) ${inventory.items(slot)}")
  }

  override def displayError(s: String): Unit = println(s"ERROR: $s")

  override def displayGameOver(score: Int): Unit = {
    println("===== GAME OVER =====")
    println(s"Score: $score")
  }

  override def getCommand: Command = {
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
             } yield Command.Quaff(slot)
             case 'r' => for {
               slot <- getItem
             } yield Command.Read(slot)
             case 't' => for {
               dir <- getDirection
               slot <- getItem
             } yield Command.Throw(dir, slot)
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
        getCommand
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

  /** Get an item in the PC's inventory */
  private def getItem: Either[String, Slot] = {
    println("Select object")
    for {
      c <- getCharacter("No slot entered")
      s <- Slot.parse(c.toString)
    } yield s
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

  override def displayScrollPower(title: String, power: ScrollPower): Unit = println(s"Scroll $title: $power")
}
