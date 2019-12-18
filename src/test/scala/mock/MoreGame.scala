package mock

object MoreGame {
  val firstScreen: String = MockRogue.makeScreen(
    """
      |
      |
      |
      |
      |
      |
      |
      |
      |
      |
      |
      |
      |
      |
      |
      |                                                             ---------+--------
      |                                                             |................|
      |                                                             |.............)..|
      |                                                             +......@*H.......|
      |                                                             |.............B..|
      |                                                             ------------------
      |
      |Level: 1  Gold: 0      Hp: 12(12)   Str: 16(16) Arm: 4  Exp: 1/0
      |""".stripMargin)

  val firstInventoryScreen: String = MockRogue.makeScreen(
    """                                                a) some food
      |                                                b) +1 ring mail [4] being worn
      |                                                c) a +1,+1 mace in hand
      |                                                d) a +1,+0 short bow
      |                                                e) 28 +0,+0 arrows
      |                                                f) a plaid potion
      |                                                --press space to continue--
      |
      |
      |
      |
      |
      |
      |
      |
      |
      |                                                             ---------+--------
      |                                                             |................|
      |                                                             |.............)..|
      |                                                             +......@*H.......|
      |                                                             |.............B..|
      |                                                             ------------------
      |
      |Level: 1  Gold: 8      Hp: 12(12)   Str: 16(16) Arm: 4  Exp: 1/0
      |""".stripMargin)

  val secondScreen: String = MockRogue.makeScreen(
    """8 pieces of gold-more-
      |
      |
      |
      |
      |
      |
      |
      |
      |
      |
      |
      |
      |
      |
      |
      |                                                             ---------+--------
      |                                                             |................|
      |                                                             |.............)..|
      |                                                             +.......@H.......|
      |                                                             |.............B..|
      |                                                             ------------------
      |
      |Level: 1  Gold: 8      Hp: 12(12)   Str: 16(16) Arm: 4  Exp: 1/0
      |""".stripMargin)

  val thirdScreen: String = MockRogue.makeScreen(
    """the hobgoblin misses 
      |
      |
      |
      |
      |
      |
      |
      |
      |
      |
      |
      |
      |
      |
      |
      |                                                             ---------+--------
      |                                                             |................|
      |                                                             |.............)..|
      |                                                             +.......@H.......|
      |                                                             |.............B..|
      |                                                             ------------------
      |
      |Level: 1  Gold: 8      Hp: 12(12)   Str: 16(16) Arm: 4  Exp: 1/0
      |""".stripMargin)

  val thirdInventoryScreen: String = MockRogue.makeScreen(
    """                                                a) some food
      |                                                b) +1 ring mail [4] being worn
      |                                                c) a +1,+1 mace in hand
      |                                                d) a +1,+0 short bow
      |                                                e) 28 +0,+0 arrows
      |                                                f) a plaid potion
      |                                                --press space to continue--
      |
      |
      |
      |
      |
      |
      |
      |
      |
      |                                                             ---------+--------
      |                                                             |................|
      |                                                             |.............)..|
      |                                                             +.......@H.......|
      |                                                             |.............B..|
      |                                                             ------------------
      |
      |Level: 1  Gold: 8      Hp: 12(12)   Str: 16(16) Arm: 4  Exp: 1/0
      |""".stripMargin
  )

  val moreGame: MockRogue =
    MockRogue.Build
      .WaitForCommand(firstScreen, firstInventoryScreen, 'l')
      .Wait(secondScreen, ' ')
      .End(thirdScreen, thirdInventoryScreen)
}
