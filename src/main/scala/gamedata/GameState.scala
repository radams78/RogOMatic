package gamedata

import gamedata.Colour.Colour
import gamedata.Domain._
import gamedata.PotionPower.PotionPower
import gamedata.ScrollPower.ScrollPower
import rogue.Command

import scala.util.matching.Regex

/** Partial information about the state of the game */
class GameState(val scrollKnowledge: ScrollKnowledge = ScrollKnowledge(),
                val potionKnowledge: PotionKnowledge = PotionKnowledge(),
                val lastCommand: Option[Command] = None) {
  /** Deduce all information possible */
  private def complete: Either[String, GameState] =
    lastCommand.fold[Either[String, GameState]](Right(this))((cmd: Command) => for {
      sp <- scrollKnowledge.infer(cmd)
      pp <- potionKnowledge.infer(cmd)
      command <- cmd.infer(scrollKnowledge)
      command2 <- command.infer(potionKnowledge)
    } yield new GameState(sp, pp, Some(command2)))
}

object GameState {
  def apply(lastCommand: Command): GameState = new GameState(ScrollKnowledge(), PotionKnowledge(), Some(lastCommand))

  private val removeCurseMessage: Regex = """you feel as though someone is watching over you""".r.unanchored
  private val healingMessage: Regex = """you begin to feel better""".r.unanchored
  private val blankLine: String = " " * 80

  def interpretMessage(messageLine: String): GameState = messageLine match {
    case removeCurseMessage() => new GameState(new ScrollKnowledge(Map()), new PotionKnowledge(Map()), Some(Command.Read(None, Scroll(None, None, Some(ScrollPower.REMOVE_CURSE)))))
    case healingMessage() => new GameState(new ScrollKnowledge(Map()), new PotionKnowledge(Map()), Some(Command.Quaff(None, Potion(None, None, Some(PotionPower.HEALING)))))
    case blankLine => new GameState()
    case _ => throw new Error(s"Unrecognised message: $messageLine")
  }

  implicit def domain: Domain[GameState] = (x: GameState, y: GameState) => for {
    scrollKnowledge <- x.scrollKnowledge.merge(y.scrollKnowledge)
    potionKnowledge <- y.potionKnowledge.merge(y.potionKnowledge)
    lastCommand <- x.lastCommand.merge(y.lastCommand)
    gameState <- new GameState(scrollKnowledge, potionKnowledge, lastCommand).complete
  } yield gameState
}

class ScrollKnowledge(private val powers: Map[String, ScrollPower]) {
  def getTitle(p: ScrollPower): Option[String] = powers.find(_._2 == p).map(_._1)

  def getPower(title: String): Option[ScrollPower] = powers.get(title)

  def infer(command: Command): Either[String, ScrollKnowledge] = command match {
    case Command.Read(_, scroll) => infer(scroll)
    case Command.Throw(_, _, scroll: Scroll) => infer(scroll)
    case _ => Right(this)
  }


  def infer(scroll: Scroll): Either[String, ScrollKnowledge] = scroll.title match {
    case Some(t) => for {
      power <- powers.get(t).merge(scroll.power)
    } yield power match {
      case Some(p) => new ScrollKnowledge(powers.updated(t, p))
      case None => this
    }
    case None => Right(this)
  }
}

object ScrollKnowledge {
  def apply(): ScrollKnowledge = new ScrollKnowledge(Map())

  implicit def domain: Domain[ScrollKnowledge] = (x: ScrollKnowledge, y: ScrollKnowledge) => x.powers.merge(y.powers).map(new ScrollKnowledge(_))
}

class PotionKnowledge(private val powers: Map[Colour, PotionPower]) {
  def getColour(p: PotionPower): Option[Colour] = powers.find(_._2 == p).map(_._1)

  def getPower(c: Colour): Option[PotionPower] = powers.get(c)

  def infer(cmd: Command): Either[String, PotionKnowledge] = cmd match {
    case Command.Quaff(_, potion) => infer(potion)
    case Command.Throw(_, _, potion: Potion) => infer(potion)
    case _ => Right(this)
  }

  def infer(potion: Potion): Either[String, PotionKnowledge] = potion.colour match {
    case Some(c) => for {power <- powers.get(c).merge(potion.power)} yield new PotionKnowledge(powers ++ power.map((c, _)))
    case None => Right(this)
  }
}

object PotionKnowledge {
  def apply(): PotionKnowledge = new PotionKnowledge(Map())
  
  implicit def domain: Domain[PotionKnowledge] = (x: PotionKnowledge, y: PotionKnowledge) => x.powers.merge(y.powers).map(new PotionKnowledge(_))
}