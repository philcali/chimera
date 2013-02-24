package com.github.philcali.chimera
package mirrors

import reflect.runtime.universe
import universe.Type

object ListMirror extends GenericTypeMirrorFilter[List[_]] {
  def createMirror(reflector: Reflector, arg: Type) =
    new ListMirror(reflector, arg)
}

class ListMirror(val reflector: Reflector, val initialType: Type) extends Mirror[List[_]] {
  lazy val typeArg = ListMirror.typeArgs(initialType).head

  def toScala(chimera: Chimera) = {
    val result = chimera.pullItem.map(_.asInstanceOf[List[_]]).getOrElse(List())
    val argMirror = reflector.mirrorFor(typeArg)

    result.map(elem => argMirror.toScala(new CollectionChimera(elem)))
  }

  def toChimera(any: Any) = new CollectionChimera(any.asInstanceOf[List[_]].map {
    e => reflector.mirrorFor(typeArg).toChimera(e).pullItem.getOrElse(null)
  })
}
