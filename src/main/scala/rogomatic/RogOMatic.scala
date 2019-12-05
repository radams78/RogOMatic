package rogomatic

/** Entry point for the system */
object RogOMatic extends App {
  val rogue = new Rogue
  val view = new TextView
  val controller = new Controller(rogue, view)
  controller.startTransparent()
}
