package gamedata.item.magic.potion

import gamedata.ParsableEnum

/** The colours that a potion can have */
object Colour extends ParsableEnum {
  type Colour = Value
  override val name: String = "colour"
  val BLUE: Colour = Val("blue")
  val RED: Colour = Val("red")
  val GREEN: Colour = Val("green")
  val GREY: Colour = Val("grey")
  val BROWN: Colour = Val("brown")
  val CLEAR: Colour = Val("clear")
  val PINK: Colour = Val("pink")
  val WHITE: Colour = Val("white")
  val PURPLE: Colour = Val("purple")
  val BLACK: Colour = Val("black")
  val YELLOW: Colour = Val("yellow")
  val PLAID: Colour = Val("plaid")
  val BURGUNDY: Colour = Val("burgundy")
  val BEIGE: Colour = Val("beige")

  protected case class Val(override val name: String) extends super.Val(name)

}
