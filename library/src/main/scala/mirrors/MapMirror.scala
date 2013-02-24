package com.github.philcali.chimera
package mirrors

import scala.language.existentials
import reflect.runtime.universe
import universe.Type

object MapMirror extends GenericTypeMirrorFilter[Map[_, _]] {
  def createMirror(reflector: Reflector, arg: Type) =
    new MapMirror(reflector, arg)
}

class MapMirror(val reflector: Reflector, val initialType: Type) extends Mirror[Map[_, _]] {
  lazy val typeArgs = MapMirror.typeArgs(initialType)
  lazy val List(argK, argV) = typeArgs.map(reflector.mirrorFor)

  def toScala(chimera: Chimera) = {
    chimera.pullItem.map(_.asInstanceOf[Map[_,_]].map {
      case (k, v) => (
        argK.toScala(new CollectionChimera(k)),
        argV.toScala(new CollectionChimera(v))
      )
    }).getOrElse(Map())
  }

  def toChimera(any: Any) = {
    new CollectionChimera(any.asInstanceOf[Map[_, _]].map {
      case (k, v) => (
        argK.toChimera(k).pullItem.getOrElse(null),
        argV.toChimera(v).pullItem.getOrElse(null)
      )
    })
  }
}
