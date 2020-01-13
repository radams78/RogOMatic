package gamedata.item.magic.potion

import domain.Domain
import gamedata.ParsableEnum

/** The colours that a potion can have */
object Colour extends ParsableEnum {
  type Colour = Value
  override val name: String = "colour"
  val BLUE: Colour = Value("blue")
  val RED: Colour = Value("red")
  val GREEN: Colour = Value("green")
  val GREY: Colour = Value("grey")
  val BROWN: Colour = Value("brown")
  val CLEAR: Colour = Value("clear")
  val PINK: Colour = Value("pink")
  val WHITE: Colour = Value("white")
  val PURPLE: Colour = Value("purple")
  val BLACK: Colour = Value("black")
  val YELLOW: Colour = Value("yellow")
  val PLAID: Colour = Value("plaid")
  val BURGUNDY: Colour = Value("burgundy")
  val BEIGE: Colour = Value("beige")

  implicit def domain: Domain[Colour] = Domain.flatDomain
}
