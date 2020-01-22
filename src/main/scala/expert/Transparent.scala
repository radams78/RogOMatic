package expert

import gamedata.ProvidesKnowledge._
import gamedata._
import gamestate.History
import view.IView

/** An Expert is an object that takes the current state of the game, including its full history, and chooses the
 * next command to play. */
trait Expert {
  def advice(history: History.GameOn): pCommand
}

/** Expert for playing the game in transparent mode, i.e. interactively, getting moves from the user one by one. */
class Transparent(view: IView) extends Expert {
  override def advice(history: History.GameOn): pCommand = {
    view.displayScreen(history.screen)
    view.displayInventory(history.inventory)
    for (fact <- history.implications) view.displayFact(fact)
    view.getCommand
  }
}
