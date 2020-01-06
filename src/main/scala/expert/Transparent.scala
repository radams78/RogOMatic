package expert

import gamedata._
import rogue._
import view.IView

import scala.annotation.tailrec

/** Expert for playing the game in transparent mode, i.e. interactively, getting moves from the user one by one. */
class Transparent(player: RogueActuator, recorder: Recorder, view: IView) {
  /** Play a game of rogue */
  def playRogue(): Unit = {
    player.start()
    playRogue0()

    @tailrec
    def playRogue0(): Unit = {
      if (recorder.gameOver) {
        view.displayGameOver(recorder.getScore)
      } else {
        view.displayScreen(recorder.getScreen)
        val inventory: Inventory = recorder.getInventory
        view.displayInventory(inventory)
        player.sendCommand(getCommand) match {
          case Left(err) => view.displayError(err)
          case Right(_) => playRogue0()
        }
      }
    }
  }


  /** Get a command from the user */
  private def getCommand: Command = view.getCommand

  /*    def getCommand0: Either[String, Command] = {
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
                 case Some(p: Potion) => Command.Quaff(slot, p)
                 case _ => return Left(s"Invalid potion: $slot")
               }
               case 'r' => for {
                 slot <- getItem
               } yield inventory.items.get(slot) match {
                 case Some(s: Scroll) => Command.Read(slot, s)
                 case _ => return Left(s"Invalid scroll: $slot")
               }
               case 't' => for {
                 dir <- getDirection
                 slot <- getItem
               } yield Command.Throw(dir, slot, inventory.items(slot))
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
    } */
}
