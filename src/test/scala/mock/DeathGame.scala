package mock

import mock.MockRogueState.Started

object DeathGame {
  val firstScreen: String =
    """
      |
      |          -------------
      |          |.@S.=......+
      |          |..%........|
      |          ---------+---
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
      |
      |
      |Level: 1  Gold: 0      Hp: 12(12)   Str: 16(16) Arm: 4  Exp: 1/0
      |""".stripMargin
      .split("\n")
      .map(_.padTo(80, ' '))
      .mkString("\n")

  val firstInventoryScreen: String = MockRogue.makeScreen(
    """                                                a) some food
      |                                                b) +1 ring mail [4] being worn
      |          -------------                         c) a +1,+1 mace in hand
      |          |.@S.=......+                         d) a +1,+0 short bow
      |          |..%........|                         e) 31 +0,+0 arrows
      |          ---------+---                         --press space to continue--
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
      |
      |
      |Level: 1  Gold: 0      Hp: 12(12)   Str: 16(16) Arm: 4  Exp: 1/0
      |""".stripMargin)

  val secondScreen: String = MockRogue.makeScreen(
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
      |                         Killed by a snake with 0 gold
      |
      |""".stripMargin)

  val thirdScreen: String = MockRogue.makeScreen(
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
      |""".stripMargin)

  val deathGame: MockRogue = new MockRogue(MockRogue.Start // TODO Pyramid of doom
    .WaitForCommand(firstScreen, firstInventoryScreen, '.')
    .build(new Started {
      override def getScreen: String = secondScreen

      override def transitions: PartialFunction[Char, MockRogueState] = {
        case ' ' => new Started {
          override def getScreen: String = thirdScreen

          override def transitions: PartialFunction[Char, MockRogueState] = {
            case ' ' => new MockRogueState {
              override def isStarted: Boolean = false

              override def sendKeypress(keypress: Char): MockRogueState = fail("sendKeypress called after Rogue process ends")

              override def getScreen: String = fail("getScreen called after Rogue process ends")

              override def start(): MockRogueState = fail("start() called after Rogue process ends")
            }
          }
        }
      }
    }))
}
