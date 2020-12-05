package model

import gamedata.Command
import rogue.IRogue

/** Handle input and output with Rogue 
 * 
 * @param rogue The Rogue process */
// TODO Split into two?


class RoguePlayer(rogue: IRogue) extends IRoguePlayer {
  private var observers : Set[IRoguePlayerObserver] = Set()
  
  override def startGame(): Unit =
    for (observer <- observers) observer.notify(rogue.readScreen)

  override def addObserver(observer: IRoguePlayerObserver): Unit =
    observers = observers + observer

  override def performCommand(command: Command): Unit = ()
}
