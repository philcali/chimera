package com.github.philcali.chimera
package mirrors
package xml

import reflect.runtime.universe
import universe.Type
import scala.xml.{ Null, Node, NodeSeq, Text, Elem, TopScope }

object XmlMirror extends Reflector.MirrorFilter {
  lazy val nodeSeqType = universe.typeOf[NodeSeq]

  def apply(arg: Type) = arg <:< nodeSeqType || arg =:= nodeSeqType

  def createMirror(reflector: Reflector, arg: Type) =
    new XmlMirror(reflector, arg)
}

class XmlMirror(val reflector: Reflector, val initialType: Type) extends Mirror[Any] {
  lazy val scalaMap = reflector.mirrorFor(universe.typeOf[Map[String, NodeSeq]])
  lazy val scalaList = reflector.mirrorFor(universe.typeOf[List[Node]])

  def toScala(chimera: Chimera) = chimera.pullItem.getOrElse(null) match {
    case map: Map[_, _] =>
      Elem(null, "object", Null, TopScope, false, scalaMap.toScala(chimera)
        .asInstanceOf[Map[String, Any]]
        .filter(_._2 != null)
        .map {
          case (k, nodes: NodeSeq) => Elem(null, k, Null, TopScope, false, nodes:_*)
        }.toList:_*)
    case list: List[_] =>
      Elem(null, "objects", Null, TopScope, false,
      NodeSeq.fromSeq(scalaList.toScala(chimera).asInstanceOf[List[Node]]): _*)
    case other if other != null => new Text(other.toString)
    case null => null
  }

  def toChimera(any: Any) = any match {
    case nodes: Node => new CollectionChimera(traverse(nodes))
    case _ => new CollectionChimera(any)
  }

  private def traverse(nodes: Node): Any = nodes match {
    case node if node.label == "objects" =>
      node.child.map(traverse).toList
    case node if node.label == "object" =>
      Map(node.child.map(e => e.label -> traverse(e.child.head)):_*)
    case _ => nodes.text
  }
}
