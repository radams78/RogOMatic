package expert

import gamedata.fact.ProvidesKnowledge._
import gamestate.History
import rogue.Command
import view.IView

/** Expert for playing the game in transparent mode, i.e. interactively, getting moves from the user one by one. */
class Transparent(view: IView) extends Expert {
  override def advice(history: History.GameOn): Command = {
    view.displayScreen(history.screen)
    view.displayInventory(history.inventory)
    for (fact <- history.implications) view.displayFact(fact)
    view.getCommand
  }
}
