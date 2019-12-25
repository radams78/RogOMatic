package mock

import gamedata._

object TestGame {
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
      |                                                      ------------+-
      |                                                      |...*..).....|
      |                                                      +............|
      |                                                      |.........@..|
      |                                                      |............|
      |                                                      --------------
      |
      |Level: 1  Gold: 0      Hp: 12(12)   Str: 16(16) Arm: 4  Exp: 1/0
      |""".stripMargin)

  val firstInventoryScreen: String = MockRogue.makeScreen(
    """                                                a) some food
      |                                                b) +1 ring mail [4] being worn
      |                                                c) a +1,+1 mace in hand
      |                                                d) a +1,+0 short bow
      |                                                e) 35 +0,+0 arrows
      |                                                f) a scroll entitled: 'coph rech'
      |                                                g) scale mail
      |                                                h) a redwood staff
      |                                                i) 7 arrows
      |                                                --press space to continue--
      |
      |
      |
      |
      |
      |
      |                                                      ------------+-
      |                                                      |...*..).....|
      |                                                      +............|
      |                                                      |.........@..|
      |                                                      |............|
      |                                                      --------------
      |
      |Level: 1  Gold: 0      Hp: 12(12)   Str: 16(16) Arm: 4  Exp: 1/0
      |""".stripMargin)

  val firstInventory: Inventory = Inventory(
    items = Map(
      Slot.A -> Food(1),
      Slot.B -> Armor(ArmorType.RING_MAIL, +1),
      Slot.C -> Weapon(WeaponType.MACE, +1, +1),
      Slot.D -> Weapon(WeaponType.SHORT_BOW, +1, +0),
      Slot.E -> Missile(35, WeaponType.ARROW, +0, +0),
      Slot.F -> Scroll(1, "coph rech"),
      Slot.G -> Armor(ArmorType.SCALE_MAIL),
      Slot.H -> gamedata.Wand(WandType.STAFF, Material.REDWOOD),
      Slot.I -> Missile(7, WeaponType.ARROW)
    ),
    wearing = Some(Slot.B),
    wielding = Some(Slot.C)
  )

  val secondScreen: String = MockRogue.makeScreen(
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
      |                                                      ------------+-
      |                                                      |...*..).....|
      |                                                      +............|
      |                                                      |..........@.|
      |                                                      |............|
      |                                                      --------------
      |
      |Level: 1  Gold: 0      Hp: 12(12)   Str: 16(16) Arm: 4  Exp: 1/0
      |""".stripMargin)

  val secondInventoryScreen: String = MockRogue.makeScreen(
    """                                                a) some food
      |                                                b) +1 ring mail [4] being worn
      |                                                c) a +1,+1 mace in hand
      |                                                d) a +1,+0 short bow
      |                                                e) 35 +0,+0 arrows
      |                                                f) a scroll entitled: 'coph rech'
      |                                                g) scale mail
      |                                                h) a redwood staff
      |                                                i) 7 arrows
      |                                                --press space to continue--
      |
      |
      |
      |
      |
      |
      |                                                      ------------+-
      |                                                      |...*..).....|
      |                                                      +............|
      |                                                      |..........@.|
      |                                                      |............|
      |                                                      --------------
      |
      |Level: 1  Gold: 0      Hp: 12(12)   Str: 16(16) Arm: 4  Exp: 1/0
      |""".stripMargin)

  val thirdScreen: String = MockRogue.makeScreen(
    """read what?
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
      |                                                      ------------+-
      |                                                      |...*..).....|
      |                                                      +............|
      |                                                      |..........@.|
      |                                                      |............|
      |                                                      --------------
      |
      |Level: 1  Gold: 0      Hp: 12(12)   Str: 16(16) Arm: 4  Exp: 1/0
      |""".stripMargin)

  val fourthScreen: String = MockRogue.makeScreen(
    """you feel as though someone is watching over you
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
      |                                                      ------------+-
      |                                                      |...*..).....|
      |                                                      +............|
      |                                                      |..........@.|
      |                                                      |............|
      |                                                      --------------
      |
      |Level: 1  Gold: 0      Hp: 12(12)   Str: 16(16) Arm: 4  Exp: 1/0
      |""".stripMargin)

  val fourthInventoryScreen: String = MockRogue.makeScreen(
    """                                                a) some food
      |                                                b) +1 ring mail [4] being worn
      |                                                c) a +1,+1 mace in hand
      |                                                d) a +1,+0 short bow
      |                                                e) 35 +0,+0 arrows
      |                                                f) a scroll entitled: 'coph rech'
      |                                                g) scale mail
      |                                                h) a redwood staff
      |                                                i) 7 arrows
      |                                                --press space to continue--
      |
      |
      |
      |
      |
      |
      |
      |                                                      ------------+-
      |                                                      |...*..).....|
      |                                                      +............|
      |                                                      |..........@.|
      |                                                      |............|
      |                                                      --------------
      |
      |Level: 1  Gold: 0      Hp: 12(12)   Str: 16(16) Arm: 4  Exp: 1/0
      |""".stripMargin)

  val fourthInventory: Inventory = Inventory(
    items = Map(
      Slot.A -> Food(1),
      Slot.B -> Armor(ArmorType.RING_MAIL, +1),
      Slot.C -> Weapon(WeaponType.MACE, +1, +1),
      Slot.D -> Weapon(WeaponType.SHORT_BOW, +1, +0),
      Slot.E -> Missile(35, WeaponType.ARROW, +0, +0),
      Slot.G -> Armor(ArmorType.SCALE_MAIL),
      Slot.H -> gamedata.Wand(WandType.STAFF, Material.REDWOOD),
      Slot.I -> Missile(7, WeaponType.ARROW)
    ),
    wearing = Some(Slot.B),
    wielding = Some(Slot.C)
  )

  def testGame: MockRogue =
    MockRogue.Start
      .WaitForCommand(firstScreen, firstInventoryScreen, 'l')
      .WaitForCommand(secondScreen, secondInventoryScreen, 'r')
      .Wait(thirdScreen, 'f')
      .End(fourthScreen, fourthInventoryScreen)
}
