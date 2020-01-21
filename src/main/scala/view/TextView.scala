package view

import domain.pLift
import gamedata._

import scala.io.StdIn

/** A simple text input/output */
class TextView extends IView {
  override def displayScreen(screen: String): Unit = println(screen)

  override def displayInventory(inventory: pInventory): Unit = {
    for ((slot, item) <- inventory.items.toList.sortBy(_._1)) println(s"$slot) $item")
    inventory.wielding match {
      case pLift.UNKNOWN => println("pWeapon: UNKNOWN")
      case pLift.Known(None) => ()
      case pLift.Known(Some(weapon)) => println(s"pWeapon: $weapon")
    }
    inventory.wearing match {
      case pLift.UNKNOWN => println("Armor: UNKNOWN")
      case pLift.Known(None) => ()
      case pLift.Known(Some(armor)) => println(s"Armor: $armor")
    }
  }

  override def displayError(s: String): Unit = println(s"ERROR: $s")

  override def displayGameOver(score: Int): Unit = {
    println("===== GAME OVER =====")
    println(s"Score: $score")
  }

  override def getCommand: pCommand = {
    def getCommand0: Either[String, pCommand] = {
      for {c <- getCharacter("No command entered")
           cmd <- c match {
             case 'b' => Right(pCommand.DOWNLEFT)
             case 'h' => Right(pCommand.LEFT)
             case 'j' => Right(pCommand.UP)
             case 'k' => Right(pCommand.DOWN)
             case 'l' => Right(pCommand.RIGHT)
             case 'n' => Right(pCommand.DOWNRIGHT)
             case 'q' => for {
               slot <- getItem
             } yield pCommand.Quaff(slot)
             case 'r' => for {
               slot <- getItem
             } yield pCommand.Read(slot)
             case 't' => for {
               dir <- getDirection
               slot <- getItem
             } yield pCommand.Throw(dir, slot)
             case 'u' => Right(pCommand.UPRIGHT)
             case 'w' => for (slot <- getItem) yield pCommand.Wield(slot)
             case 'y' => Right(pCommand.UPLEFT)
             case '.' => Right(pCommand.REST)
             case '>' => Right(pCommand.DOWNSTAIRS)
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

  override def displayFact(fact: Fact): Unit = println(fact)
}
