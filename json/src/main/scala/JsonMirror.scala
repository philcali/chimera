package com.github.philcali.chimera
package mirrors
package json

import util.parsing.json.{ JSONType, JSONObject, JSONArray }
import util.parsing.combinator.RegexParsers

import reflect.runtime.universe
import universe.Type

object JsonMirror extends Reflector.MirrorFilter {
  lazy val jsonType = universe.typeOf[JSONType]

  def apply(arg: Type) = arg <:< jsonType || arg =:= jsonType

  def createMirror(reflector: Reflector, arg: Type) =
    new JsonMirror(reflector, arg)
}

class JsonMirror(val reflector: Reflector, val initialType: Type) extends Mirror[Any] {
  lazy val scalaMap = reflector.mirrorFor(universe.typeOf[Map[String, JSONType]])
  lazy val scalaList = reflector.mirrorFor(universe.typeOf[List[JSONType]])

  // @todo: this is kind of terrible ... fix it
  def toScala(chimera: Chimera) = chimera.pullItem.getOrElse(null) match {
    case map: Map[_, _] =>
      JSONObject(scalaMap.toScala(chimera).asInstanceOf[Map[String, Any]].filter {
        _._2 != null
      })
    case list: List[_] =>
      JSONArray(scalaList.toScala(chimera).asInstanceOf[List[Any]].filter {
        _ != null
      })
    case other => other
  }

  def toChimera(any: Any) = new CollectionChimera(traverse(any))

  private def traverse(any: Any): Any = any match {
    case JSONObject(map) =>
      map.map { case (k, v) => k -> traverse(v) }
    case JSONArray(list) => list.map(traverse)
    case _ => any
  }
}
