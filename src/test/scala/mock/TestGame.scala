package mock

import gamedata._
import gamedata.fact.ProvidesKnowledge._
import gamedata.item.armor.{Armor, ArmorType}
import gamedata.item.magic.scroll.Scroll.ScrollKnowledge
import gamedata.item.magic.scroll.{Scroll, ScrollPower}
import gamedata.item.magic.wand
import gamedata.item.magic.wand.{Material, Wand, WandShape}
import gamedata.item.weapon.{MeleeType, WeaponType, pMissile, pWeapon}
import gamedata.item.{weapon, _}
import gamestate.Inventory
import rogue.Command

/** Sample data for a game of Rogue used in testing. The game lasts three turns, and involves several different
 * inventory items, recognising the effect of a scroll when read, and recognising the end of the game. */
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
      |                                                      |........H@..|
      |                                                      |............|
      |                                                      --------------
      |
      |Level: 1  Gold: 0      Hp: 2(12)    Str: 16(16) Arm: 4  Exp: 1/0
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
      |                                                      |........H@..|
      |                                                      |............|
      |                                                      --------------
      |
      |Level: 1  Gold: 0      Hp: 2(12)    Str: 16(16) Arm: 4  Exp: 1/0
      |""".stripMargin)

  val firstInventory: Inventory = Inventory(
    items = Map(
      Slot.A -> Food(1),
      Slot.B -> Armor(ArmorType.RING_MAIL, +1),
      Slot.C -> pWeapon(MeleeType.MACE, +1, +1),
      Slot.D -> weapon.pWeapon(WeaponType.SHORT_BOW, +1, +0),
      Slot.E -> pMissile(35, WeaponType.ARROW, +0, +0),
      Slot.F -> Scroll(1, "coph rech"),
      Slot.G -> Armor(ArmorType.SCALE_MAIL),
      Slot.H -> wand.Wand(WandShape.STAFF, Material.REDWOOD),
      Slot.I -> weapon.pMissile(7, WeaponType.ARROW)
    ),
    wearingSlot = Some(Slot.B),
    wieldingSlot = Some(Slot.C)
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
      |                                                      |.........H@.|
      |                                                      |............|
      |                                                      --------------
      |
      |Level: 1  Gold: 0      Hp: 2(12)    Str: 16(16) Arm: 4  Exp: 1/0
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
      |                                                      |.........H@.|
      |                                                      |............|
      |                                                      --------------
      |
      |Level: 1  Gold: 0      Hp: 2(12)    Str: 16(16) Arm: 4  Exp: 1/0
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
      |                                                      |.........H@.|
      |                                                      |............|
      |                                                      --------------
      |
      |Level: 1  Gold: 0      Hp: 2(12)   Str: 16(16) Arm: 4  Exp: 1/0
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
      |                                                      |.........H@.|
      |                                                      |............|
      |                                                      --------------
      |
      |Level: 1  Gold: 0      Hp: 2(12)    Str: 16(16) Arm: 4  Exp: 1/0
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
      |                                                      |.........H@.|
      |                                                      |............|
      |                                                      --------------
      |
      |Level: 1  Gold: 0      Hp: 2(12)    Str: 16(16) Arm: 4  Exp: 1/0
      |""".stripMargin)

  val fourthInventory: Inventory = Inventory(
    items = Map(
      Slot.A -> Food(1),
      Slot.B -> Armor(ArmorType.RING_MAIL, +1),
      Slot.C -> weapon.pWeapon(MeleeType.MACE, +1, +1),
      Slot.D -> weapon.pWeapon(WeaponType.SHORT_BOW, +1, +0),
      Slot.E -> weapon.pMissile(35, WeaponType.ARROW, +0, +0),
      Slot.G -> Armor(ArmorType.SCALE_MAIL),
      Slot.H -> Wand(WandShape.STAFF, Material.REDWOOD),
      Slot.I -> weapon.pMissile(7, WeaponType.ARROW)
    ),
    wearingSlot = Some(Slot.B),
    wieldingSlot = Some(Slot.C)
  )

  val deathScreen: String = MockRogue.makeScreen(
    """             -more-
      |
      |
      |
      |                                __---------__
      |                              _~             ~_
      |                             /                 \
      |                            ~                   ~
      |                           /                     \
      |                           |    XXXX     XXXX    |
      |                           |    XXXX     XXXX    |
      |                           |    XXX       XXX    |
      |                            \         @         /
      |                             --\     @@@     /--
      |                              | |    @@@    | |
      |                              | |           | |
      |                              | vvVvvvvvvvVvv |
      |                              |  ^^^^^^^^^^^  |
      |                               \_           _/
      |                                 ~---------~
      |
      |                                     robin
      |                         Killed by a hobgoblin with 0 gold
      |
      |""".stripMargin
  )

  val finalScreen: String = MockRogue.makeScreen(
    """-more-
      |
      |
      |                              Top  Ten  Rogueists
      |
      |
      |
      |
      |Rank   Score   Name
      |
      | 1      1224   robin: died of starvation on level 11
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
      |""".stripMargin
  )

  /** A mock Rogue process that goes through the above screens in order. */
  def testGame: MockRogue =
    MockRogue.Start
      .WaitForCommand("testGame state 1", firstScreen, firstInventoryScreen, 'l')
      .WaitForCommand("testGame state 2", secondScreen, secondInventoryScreen, 'r')
      .Wait(thirdScreen, 'f')
      .WaitForCommand("testGame state 4", fourthScreen, fourthInventoryScreen, 'h')
      .Wait(deathScreen, ' ')
      .Wait(deathScreen, ' ')
      .End

  /** A mock user who plays this game of Rogue */
  def user: MockUser = MockUser.Start
    .Command(TestGame.firstScreen, TestGame.firstInventory, TestGame.firstInventory.implications, Command.RIGHT)
    .Command(TestGame.secondScreen, TestGame.firstInventory, TestGame.firstInventory.implications, Command.Read(Slot.F))
    .Command(TestGame.fourthScreen, TestGame.fourthInventory,
      TestGame.fourthInventory.implications ++ Set(ScrollKnowledge("coph rech", ScrollPower.REMOVE_CURSE)), Command.LEFT)
    .GameOver(0)
    .End
}
