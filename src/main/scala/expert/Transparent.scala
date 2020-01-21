package expert

import domain.pLift
import gamedata.ProvidesKnowledge._
import gamedata._
import gamestate.History
import view.IView

/** An Expert is an object that takes the current state of the game, including its full history, and chooses the
 * next command to play. */
trait Expert {
  def advice(history: History.GameOn): Either[String, pCommand]
}

/** Expert for playing the game in transparent mode, i.e. interactively, getting moves from the user one by one. */
class Transparent(view: IView) extends Expert {
  /*    def getCommand0: Either[String, pCommand] = {
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
               } yield inventory.item.get(slot) match {
                 case Some(p: Potion) => pCommand.Quaff(slot, p)
                 case _ => return Left(s"Invalid potion: $slot")
               }
               case 'r' => for {
                 slot <- getItem
               } yield inventory.item.get(slot) match {
                 case Some(s: Scroll) => pCommand.Read(slot, s)
                 case _ => return Left(s"Invalid scroll: $slot")
               }
               case 't' => for {
                 dir <- getDirection
                 slot <- getItem
               } yield pCommand.Throw(dir, slot, inventory.item(slot))
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
          getCommand(inventory)
      }
    } */
  override def advice(history: History.GameOn): Either[String, pCommand] = for {gs <- history.gameState} yield {
    displayAll(gs)
    getCommand
  }

  /** Get a command from the user */
  private def getCommand: pCommand = view.getCommand

  /** Play a game of rogue */
  /*  def playRogue(): Unit = {
      actuator.start()
      playRogue0()
  
      @tailrec
      def playRogue0(): Unit = {
        if (recorder.gameOver) {
          view.displayGameOver(recorder.getScore)
        } else {
          displayAll()
          actuator.sendCommand(getCommand) match {
            case Left(err) => view.displayError(err)
            case Right(_) => playRogue0()
          }
        }
      }
    } */


  def displayAll(gameState: pGameState): Unit = {
    gameState.screen match {
      case pLift.UNKNOWN => view.displayError("Missing screen")
      case pLift.Known(screen) => view.displayScreen(screen)
    }
    val inventory: pInventory = gameState.inventory
    view.displayInventory(inventory)
    for (fact <- gameState.implications) view.displayFact(fact)
  }
}
