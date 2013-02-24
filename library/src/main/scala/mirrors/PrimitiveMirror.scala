package com.github.philcali.chimera
package mirrors

import reflect.runtime.universe
import universe.{ Type, Symbol }

object PrimitiveMirror extends Reflector.MirrorFilter {
  lazy val stringType = universe.typeOf[String]

  def apply(arg: Type) =
    stringType =:= arg || arg.typeSymbol.asClass.isPrimitive

  def createMirror(reflector: Reflector, arg: Type) =
    new PrimitiveMirror(reflector, arg)
}

class PrimitiveMirror(val reflector: Reflector, val initialType: Type) extends Mirror[Any] {

  lazy val suitable: Map[Type, Any => Any] = Map(
    universe.definitions.IntTpe -> (_.toString.toInt),
    universe.definitions.DoubleTpe -> (_.toString.toDouble),
    universe.definitions.LongTpe -> (_.toString.toLong)
  )

  def convertJavaType(jMirror: Symbol) = jMirror.fullName match {
    case "java.lang.Integer" => universe.definitions.IntClass
    case "java.lang.Double" => universe.definitions.DoubleClass
    case "java.lang.Long" => universe.definitions.LongClass
    case "java.lang.Byte" => universe.definitions.ByteClass
    case "java.lang.Short" => universe.definitions.ShortClass
    case "java.lang.Float" => universe.definitions.FloatClass
    case "java.lang.Character" => universe.definitions.CharClass
    case "java.lang.String" => universe.definitions.StringClass
    case _ => jMirror
  }

  def toScala(chimera: Chimera): Any = {
    chimera.pullItem.map { item =>
      val mirrored = Reflector.mirror.reflect(item).symbol
      val mirroredType = mirrored.map(convertJavaType).asType.toType
      if (mirroredType =:= initialType) item
      else suitable.get(initialType).getOrElse((_: Any).toString)(item)
    }.getOrElse(null)
  }

  def toChimera(any: Any) = new CollectionChimera(any)
}

