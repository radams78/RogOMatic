package mock

import view.IView

/** A mock implementation of [[IView]] */
object MockView extends IView {
  var lastScreen: Option[String] = None

  def hasDisplayed(screen: String): Boolean = lastScreen contains screen

  override def displayScreen(screen: String): Unit = lastScreen = Some(screen)
}
