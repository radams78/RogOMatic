package mock

import gamedata._
import org.scalatest.{Assertion, Assertions}
import rogue.IRogue

class MockRogue2 extends IRogue with Assertions {
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
  private var state: State = INITIAL

  def assertMovedRight: Assertion = state match {
    case SCREEN2 | INVENTORY2 => succeed
    case _ => fail(s"Has not yet moved right: state $state")
  }

  override def start(): Unit = state match {
    case INITIAL => state = SCREEN1
    case _ => throw new Error("start() called after game started")
  }

  override def getScreen: String = state match {
    case INITIAL => throw new Error("getScreen called before game started")
    case SCREEN1 => firstScreen
    case INVENTORY1 => firstInventoryScreen
    case SCREEN2 => secondScreen
    case INVENTORY2 => secondInventoryScreen
  }

  override def sendKeypress(keyPress: Char): Unit = (state, keyPress) match {
    case (SCREEN1, 'i') => state = INVENTORY1
    case (SCREEN1, 'l') => state = SCREEN2
    case (INVENTORY1, ' ') => state = SCREEN1
    case (SCREEN2, 'i') => state = INVENTORY2
    case (INVENTORY2, ' ') => state = SCREEN2
    case _ => throw new Error(s"Unexpected keypress: $keyPress in state $state")
  }

  private trait State

  private case object INITIAL extends State

  private case object SCREEN1 extends State

  private case object INVENTORY1 extends State

  private case object SCREEN2 extends State

  private case object INVENTORY2 extends State

} // TODO Duplication with MockRogue
