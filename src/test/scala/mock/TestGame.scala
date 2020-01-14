package mock

import gamedata._
import gamedata.item.armor.{Armor, ArmorType}
import gamedata.item.magic.scroll.Scroll
import gamedata.item.magic.wand
import gamedata.item.magic.wand.{Material, Wand, WandShape}
import gamedata.item.weapon.{Missile, Weapon, WeaponType, WieldableType}
import gamedata.item.{weapon, _}

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

  val firstInventory: pInventory = pInventory(
    items = Map(
      Slot.A -> Food(1),
      Slot.B -> Armor(ArmorType.RING_MAIL, +1),
      Slot.C -> Weapon(WieldableType.MACE, +1, +1),
      Slot.D -> weapon.Weapon(WeaponType.SHORT_BOW, +1, +0),
      Slot.E -> Missile(35, WeaponType.ARROW, +0, +0),
      Slot.F -> Scroll(1, "coph rech"),
      Slot.G -> Armor(ArmorType.SCALE_MAIL),
      Slot.H -> wand.Wand(WandShape.STAFF, Material.REDWOOD),
      Slot.I -> weapon.Missile(7, WeaponType.ARROW)
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

  val fourthInventory: pInventory = pInventory(
    items = Map(
      Slot.A -> Food(1),
      Slot.B -> Armor(ArmorType.RING_MAIL, +1),
      Slot.C -> weapon.Weapon(WieldableType.MACE, +1, +1),
      Slot.D -> weapon.Weapon(WeaponType.SHORT_BOW, +1, +0),
      Slot.E -> weapon.Missile(35, WeaponType.ARROW, +0, +0),
      Slot.G -> Armor(ArmorType.SCALE_MAIL),
      Slot.H -> Wand(WandShape.STAFF, Material.REDWOOD),
      Slot.I -> weapon.Missile(7, WeaponType.ARROW)
    ),
    wearing = Some(Slot.B),
    wielding = Some(Slot.C)
  )

  def testGame: MockRogue =
    MockRogue.Start
      .WaitForCommand("testGame state 1", firstScreen, firstInventoryScreen, 'l')
      .WaitForCommand("testGame state 2", secondScreen, secondInventoryScreen, 'r')
      .Wait(thirdScreen, 'f')
      .End("testGame state 4", fourthScreen, fourthInventoryScreen)
}
