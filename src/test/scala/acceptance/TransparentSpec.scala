package acceptance

import gamedata.Inventory
import mock._
import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.AnyFeatureSpec
import rogomatic.Controller

/** Acceptance tests for playing Rogue in transparent mode */
class TransparentSpec extends AnyFeatureSpec with GivenWhenThen {
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

  Feature("Play a game of Rogue in transparent mode") {
    Scenario("User starts a game of Rogue in transparent mode") {
      Given("an instance of Rog-O-Matic")
      val rogue: MockRogue = new MockRogue(firstScreen, firstInventoryScreen)
      val controller: Controller = Controller(rogue, MockView)

      When("the user starts the game in transparent mode")
      controller.startTransparent()

      Then("the first screen should be displayed")
      assert(MockView.hasDisplayed(firstScreen))

      And("the first inventory should be displayed")
      assert(MockView.hasDisplayedInventory(firstInventory))
    }
  }
}

