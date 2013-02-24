package com.github.philcali.chimera
package mirrors

import reflect.runtime.universe
import universe.Type

object OptionMirror extends GenericTypeMirrorFilter[Option[_]] {
  def createMirror(reflector: Reflector, arg: Type) =
    new OptionMirror(reflector, arg)
}

class OptionMirror(val reflector: Reflector, val initialType: Type) extends Mirror[Option[_]] {
  val typeArg = OptionMirror.typeArgs(initialType).head

  def toScala(chimera: Chimera) = if (chimera.pullItem.isEmpty) None else {
    Some(reflector.mirrorFor(typeArg).toScala(chimera))
  }

  def toChimera(any: Any) = any match {
    case Some(value) => reflector.mirrorFor(typeArg).toChimera(value)
    case None => new CollectionChimera(null)
  }
}
