package com.github.philcali.chimera
package mirrors

import reflect.runtime.universe
import universe.Type

class AnyMirror(val reflector: Reflector) extends Mirror[Any] {
  val initialType = universe.typeOf[Any]

  def toScala(chimera: Chimera) = chimera.pullItem.getOrElse(null)
  def toChimera(any: Any) = new CollectionChimera(any)
}
