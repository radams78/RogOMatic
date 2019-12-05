package view

/** Interface for the View component of the MVC architecture. Handles user IO. */
trait IView {

  def displayScreen(screen: String): Unit
}
