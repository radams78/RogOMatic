package gamedata

trait Command {
  def keypresses: Seq[Char]
}

object Command {

  object QUIT extends Command {
    override def keypresses: Seq[Char] = Seq('Q', 'y', ' ')
  }

  object LEFT extends Command {
    override def keypresses: Seq[Char] = Seq('h')
  }

}