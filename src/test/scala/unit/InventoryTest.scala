package unit

import gamedata._
import gamedata.item.armor.{Armor, ArmorType}
import gamedata.item.weapon.{WeaponType, pWeapon}
import gamedata.item.{weapon, _}
import gamestate.Inventory
import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class InventoryTest extends AnyFlatSpec with Matchers with EitherValues {
  "a displayed inventory" should "be correctly parsed" in {
    Inventory.parseInventoryScreen(
      """                                                a) some food
        |                                                b) +1 ring mail [4] being worn
        |                                                c) a +1,+1 mace in hand
        |                                                d) a +1,+0 short bow
        |                                                e) 31 +0,+0 arrows
        |                                                --press space to continue--
        |
        |
        |
        |
        |                                                          ---------------
        |                                                          |.....S.......|
        |                                                          +............@|
        |                                                          |........*....|
        |                                                          -+-------------
        |
        |
        |
        |
        |
        |
        |
        |
        |Level: 1  Gold: 0      Hp: 12(12)   Str: 16(16) Arm: 4  Exp: 1/0
        |""".stripMargin.split("\n")
        .map(_.padTo(80, ' '))
        .mkString("\n")
    ) should be(Right(Inventory(
      items = Map(
        Slot.A -> Food(1),
        Slot.B -> Armor(ArmorType.RING_MAIL, +1),
        Slot.C -> pWeapon(WeaponType.MACE, +1, +1),
        Slot.D -> weapon.pWeapon(WeaponType.SHORT_BOW, +1, +0),
        Slot.E -> weapon.pWeapon(31, WeaponType.ARROW, +0, +0)
      ),
      wearing = Some(Slot.B),
      wielding = Some(Slot.C)
    )))
  }
}
