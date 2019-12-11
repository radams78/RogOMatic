package unit

import gamedata._
import mock._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import rogue._

/** Unit tests for [[RoguePlayer]] class */
class RoguePlayerTest extends AnyFlatSpec with Matchers {

  trait FirstScreen {
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
  }

  trait EmptyInventory {
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
  }

  trait FullInventory {
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
  }

  trait ZeroMoveGame extends FirstScreen with FullInventory {
    val rogue: MockRogue = MockRogue.Start.End(firstScreen, firstInventoryScreen)
    val player: RoguePlayer.NotStarted = RoguePlayer(rogue)
  }

  trait OneMoveGame {
    val player: RoguePlayer.NotStarted = RoguePlayer(OneMoveGame.oneMoveGame)
  }

  trait DeathGame {
    val player: RoguePlayer.NotStarted = RoguePlayer(DeathGame.deathGame)
  }

  trait MoreGame {
    val player: RoguePlayer.GameOn = new RoguePlayer.GameOn(MoreGame.moreGame)
  }

  "A controller" should "be able to start a game of Rogue" in new ZeroMoveGame {
    player.start()
    assert(rogue.isStarted)
  }

  it should "display the first screen of the game" in new ZeroMoveGame {
    player.start().getScreen should be(firstScreen)
  }

  it should "know that the game is not over after being started" in new ZeroMoveGame {
    val p: RoguePlayer.GameOn = player.start()
    assert(!p.gameOver)
  }

  it should "display the first inventory of the game" in new FirstScreen with EmptyInventory {
    val rogue: MockRogue = MockRogue.Start.End(firstScreen, inventoryScreen)
    val player: RoguePlayer.NotStarted = RoguePlayer(rogue)
    player.start().getInventory should be(Right(Inventory()))
  }

  it should "display the new screen after sending the command" in new OneMoveGame {
    player.start().sendCommand(Command.RIGHT) match {
      case p: RoguePlayer.GameOn => p.getScreen should be(OneMoveGame.secondScreen)
      case _: RoguePlayer.GameOver => fail("Game ended prematurely")
    }
  }

  it should "display the inventory after sending the command" in new OneMoveGame {
    player.start().sendCommand(Command.RIGHT) match {
      case p: RoguePlayer.GameOn => p.getInventory should be(Right(OneMoveGame.firstInventory))
      case _: RoguePlayer.GameOver => fail("Game ended prematurely")

    }
  }

  it should "know when the game is over" in new DeathGame {
    player.start().sendCommand(Command.REST).gameOver should be(true)
  }

  it should "clear a more screen" in new MoreGame {
    player.sendCommand(Command.RIGHT)
    player.getScreen should be(MoreGame.thirdScreen)
  }
}
