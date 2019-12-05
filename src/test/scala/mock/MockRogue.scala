package mock

import rogue.IRogue

/** A mock Rogue process */
object MockRogue extends IRogue {
  val firstScreen: String =
    """
      |
      |
      |
      |
      |
      |
      |
      |                             ------------
      |                             |.......%..|
      |                             |..........+
      |                             |..........|
      |                             |..........|
      |                             +...@......|
      |                             -------+----
      |
      |
      |
      |
      |
      |
      |
      |
      |Level: 1  Gold: 0      Hp: 12(12)   Str: 16(16) Arm: 4  Exp: 1/0
      |""".stripMargin

  /** True if the method [[start()]] has been called */
  var isStarted: Boolean = false

  override def start(): Unit = isStarted = true

  override def getScreen: String = if (isStarted) firstScreen else throw new Error("getScreen() called before game started")
}
