package gamedata

import gamestate.ScrollKnowledge

trait ProvidesScrollKnowledge {
  def scrollKnowledge: ScrollKnowledge
}
