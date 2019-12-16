package gamedata

/** The colours that a potion can have */
object Colour extends ParsableEnum {
  type Colour = Value
  override val setName: String = "colour"
  val BLUE: gamedata.Colour.Value = Value("blue")
  val RED: gamedata.Colour.Value = Value("red")
  val GREEN: gamedata.Colour.Value = Value("green")
  val GREY: gamedata.Colour.Value = Value("grey")
  val BROWN: gamedata.Colour.Value = Value("brown")
  val CLEAR: gamedata.Colour.Value = Value("clear")
  val PINK: gamedata.Colour.Value = Value("pink")
  val WHITE: gamedata.Colour.Value = Value("white")
  val PURPLE: gamedata.Colour.Value = Value("purple")
  val BLACK: gamedata.Colour.Value = Value("black")
  val YELLOW: gamedata.Colour.Value = Value("yellow")
  val PLAID: gamedata.Colour.Value = Value("plaid")
  val BURGUNDY: gamedata.Colour.Value = Value("burgundy")
  val BEIGE: gamedata.Colour.Value = Value("beige")
}
