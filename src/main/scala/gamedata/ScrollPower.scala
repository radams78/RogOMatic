package gamedata

/** The set of powers that a scroll can have */
object ScrollPower extends Enumeration {

  type ScrollPower = Value

  implicit def valueToScrollPowertVal(x: Value): Val = x.asInstanceOf[Val]

  val REMOVE_CURSE: ScrollPower = Val("you feel as though someone is watching over you")

  protected case class Val(effect: String) extends super.Val

  implicit def scrollPowerDomain: Domain[ScrollPower] = Domain.flatDomain
}
