package unit

import gamedata._
import mock._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import rogue.{Command, RoguePlayer}

/** Unit tests for [[RoguePlayer]] class */
class RoguePlayerTest extends AnyFlatSpec with Matchers {
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

  val firstInventory: Inventory = Inventory(
    items = Map(
      Slot.A -> Food(1),
      Slot.B -> Armor(ArmorType.RING_MAIL, +1),
      Slot.C -> Weapon(WeaponType.MACE, +1, +1),
      Slot.D -> Weapon(WeaponType.SHORT_BOW, +1, +0),
      Slot.E -> Weapon(31, WeaponType.ARROW, +0, +0)
    ),
    wearing = Some(Slot.B),
    wielding = Some(Slot.C)
  )

  "A controller" should "be able to start a game of Rogue" in {
    val rogue: MockRogue = MockRogue.Start.End(firstScreen, firstInventoryScreen)
    val player: RoguePlayer = new RoguePlayer(rogue)
    player.start()
    assert(rogue.isStarted)
  }

  it should "display the first screen of the game" in {
    val rogue: MockRogue = MockRogue.Start.End(firstScreen, firstInventoryScreen)
    val player: RoguePlayer = new RoguePlayer(rogue)
    player.start()
    assertResult(firstScreen) {
      player.getScreen
    }
  }

  it should "display the first inventory of the game" in {
    val inventoryScreen: String =
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
    val rogue: MockRogue = MockRogue.Start.End(firstScreen, inventoryScreen)
    val player: RoguePlayer = new RoguePlayer(rogue)
    player.start()
    player.getInventory should be(Right(Inventory()))
  }

  it should "display the new screen after sending the command" in {
    val firstScreen: String =
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
        |""".stripMargin
        .split("\n")
        .map(_.padTo(80, ' '))
        .mkString("\n")

    val firstInventoryScreen: String =
      """                                                a) some food
        |                                                b) +1 ring mail [4] being worn
        |                                                c) a +1,+1 mace in hand
        |                                                d) a +1,+0 short bow
        |                                                e) 35 +0,+0 arrows
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
        |
        |                                                      ------------+-
        |                                                      |...*..).....|
        |                                                      +............|
        |                                                      |.........@..|
        |                                                      |............|
        |                                                      --------------
        |
        |Level: 1  Gold: 0      Hp: 12(12)   Str: 16(16) Arm: 4  Exp: 1/0
        |""".stripMargin
        .split("\n")
        .map(_.padTo(80, ' '))
        .mkString("\n")
    val firstInventory: Inventory = Inventory(
      items = Map(
        Slot.A -> Food(1),
        Slot.B -> Armor(ArmorType.RING_MAIL, +1),
        Slot.C -> Weapon(WeaponType.MACE, +1, +1),
        Slot.D -> Weapon(WeaponType.SHORT_BOW, +1, +0),
        Slot.E -> Missile(35, WeaponType.ARROW, +0, +0)
      ),
      wearing = Some(Slot.B),
      wielding = Some(Slot.C)
    )
    val secondScreen: String =
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
        |""".stripMargin
        .split("\n")
        .map(_.padTo(80, ' '))
        .mkString("\n")
    val secondInventoryScreen: String =
      """                                                a) some food
        |                                                b) +1 ring mail [4] being worn
        |                                                c) a +1,+1 mace in hand
        |                                                d) a +1,+0 short bow
        |                                                e) 35 +0,+0 arrows
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
        |
        |                                                      ------------+-
        |                                                      |...*..).....|
        |                                                      +............|
        |                                                      |..........@.|
        |                                                      |............|
        |                                                      --------------
        |
        |Level: 1  Gold: 0      Hp: 12(12)   Str: 16(16) Arm: 4  Exp: 1/0
        |""".stripMargin
        .split("\n")
        .map(_.padTo(80, ' '))
        .mkString("\n")

    val rogue: MockRogue =
      MockRogue.Start
        .WaitForCommand(firstScreen, firstInventoryScreen, 'l')
        .End(secondScreen, secondInventoryScreen)
    val player: RoguePlayer = new RoguePlayer(rogue)
    player.start()
    player.sendCommand(Command.RIGHT)
    assertResult(secondScreen) {
      player.getScreen
    }
  }

  it should "display the inventory after sending the command" in {
    val firstScreen: String =
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
        |""".stripMargin
        .split("\n")
        .map(_.padTo(80, ' '))
        .mkString("\n")

    val firstInventoryScreen: String =
      """                                                a) some food
        |                                                b) +1 ring mail [4] being worn
        |                                                c) a +1,+1 mace in hand
        |                                                d) a +1,+0 short bow
        |                                                e) 35 +0,+0 arrows
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
        |
        |                                                      ------------+-
        |                                                      |...*..).....|
        |                                                      +............|
        |                                                      |.........@..|
        |                                                      |............|
        |                                                      --------------
        |
        |Level: 1  Gold: 0      Hp: 12(12)   Str: 16(16) Arm: 4  Exp: 1/0
        |""".stripMargin
        .split("\n")
        .map(_.padTo(80, ' '))
        .mkString("\n")
    val firstInventory: Inventory = Inventory(
      items = Map(
        Slot.A -> Food(1),
        Slot.B -> Armor(ArmorType.RING_MAIL, +1),
        Slot.C -> Weapon(WeaponType.MACE, +1, +1),
        Slot.D -> Weapon(WeaponType.SHORT_BOW, +1, +0),
        Slot.E -> Missile(35, WeaponType.ARROW, +0, +0)
      ),
      wearing = Some(Slot.B),
      wielding = Some(Slot.C)
    )
    val secondScreen: String =
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
        |""".stripMargin
        .split("\n")
        .map(_.padTo(80, ' '))
        .mkString("\n")
    val secondInventoryScreen: String =
      """                                                a) some food
        |                                                b) +1 ring mail [4] being worn
        |                                                c) a +1,+1 mace in hand
        |                                                d) a +1,+0 short bow
        |                                                e) 35 +0,+0 arrows
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
        |
        |                                                      ------------+-
        |                                                      |...*..).....|
        |                                                      +............|
        |                                                      |..........@.|
        |                                                      |............|
        |                                                      --------------
        |
        |Level: 1  Gold: 0      Hp: 12(12)   Str: 16(16) Arm: 4  Exp: 1/0
        |""".stripMargin
        .split("\n")
        .map(_.padTo(80, ' '))
        .mkString("\n")

    val rogue: MockRogue =
      MockRogue.Start
        .WaitForCommand(firstScreen, firstInventoryScreen, 'l')
        .End(secondScreen, secondInventoryScreen)
    val player: RoguePlayer = new RoguePlayer(rogue)
    player.start()
    player.sendCommand(Command.RIGHT)
    player.getInventory should be(Right(firstInventory))
  }

  it should "know that the game is not over after being started" in {
    val rogue: MockRogue = MockRogue.Start.End(firstScreen, firstInventoryScreen)
    val player: RoguePlayer = new RoguePlayer(rogue)
    player.start()
    assert(!player.gameOver)
  }
}
