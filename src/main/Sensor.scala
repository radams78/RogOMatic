package main

import rogue.IRogue
import view.IView

/** An object that reads and interprets information received from the game of Rogue.
 * 
 *  This class uses the observer pattern. Any number of [[SensorObserver]]s may subscribe to this, and will
 *  be notified of the current screen.
 *  
 *  @param rogue The game of Rogue
 * */
class Sensor(rogue : IRogue) {
  private var observers : Set[SensorObserver] = Set()

  /** Read the current screen from Rogue and notify all observers */
  def broadcastAll(): Unit = for (observer <- observers) observer.notify(rogue.readScreen)

  /** Add a new observer
   * 
   * @param view The observer to be added
   * */  
  def subscribe(view: IView): Unit = observers = observers + view
}
