package mock

import gamedata._
import gamedata.item.armor.{Armor, ArmorType}
import gamedata.item.weapon.{MeleeType, WeaponType}
import gamedata.item.{weapon, _}

object ZeroMoveGame {
  val firstScreen: String =
    """
      |
      |
      |
      |
      |
      |
      |
      |         ---------+--
      |         |..........|
      |         |.S........|
      |         |.....@....|
      |         |.K........|
      |         |..........|
      |         ------------
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

  val emptyInventoryScreen: String =
    """                                                --press space to continue--
      |
      |
      |
      |
      |
      |
      |
      |         ---------+--
      |         |..........|
      |         |.S........|
      |         |.....@....|
      |         |.K........|
      |         |..........|
      |         ------------
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

  val firstInventoryScreen: String =
    """                                                a) some food
      |                                                b) +1 ring mail [4] being worn
      |                                                c) a +1,+1 mace in hand
      |                                                d) a +1,+0 short bow
      |                                                e) 31 +0,+0 arrows
      |                                                --press space to continue--
      |
      |
      |         ---------+--
      |         |..........|
      |         |.S........|
      |         |.....@....|
      |         |.K........|
      |         |..........|
      |         ------------
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

  /** The PC's inventory at the start of [[zeroMoveGame]] */
  val firstInventory: pInventory = pInventory(
    items = Map(
      Slot.A -> Food(1),
      Slot.B -> Armor(ArmorType.RING_MAIL, +1),
      Slot.C -> weapon.pWeapon(MeleeType.MACE, +1, +1),
      Slot.D -> weapon.pWeapon(WeaponType.SHORT_BOW, +1, +0),
      Slot.E -> weapon.pWeapon(31, WeaponType.ARROW, +0, +0)
    ),
    wearing = Some(Slot.B),
    wielding = Some(Slot.C)
  )

  /** Game of Rogue which does not allow any moves to be made */
  val zeroMoveGame: MockRogue = MockRogue.Start.Terminal("zeroMoveGame", firstScreen, firstInventoryScreen)

  /** Game of Rogue with an empty inventory */
  val emptyInventoryGame: MockRogue = MockRogue.Start.Terminal("emptyInventoryGame", firstScreen, emptyInventoryScreen)
}
