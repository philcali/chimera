package com.github.philcali.chimera
package mirrors

import reflect.runtime.universe
import universe.Type

object ClassMirror extends Reflector.MirrorFilter {
  def apply(arg: Type) = arg.typeSymbol.asClass.isCaseClass

  def createMirror(reflector: Reflector, arg: Type) =
    new ClassMirror(reflector, arg)
}

class ClassMirror(val reflector: Reflector, val initialType: Type) extends Mirror[Any] {

  lazy val constructor = {
    val mappedClass = initialType.typeSymbol.asClass
    val classMirror = Reflector.mirror.reflectClass(mappedClass)
    val constructor = initialType.declaration(universe.nme.CONSTRUCTOR).asMethod

    classMirror.reflectConstructor(constructor)
  }

  def findBestArgs(chimera: Chimera) = {
    constructor.symbol.asMethod.paramss
          .find(_.forall(a => chimera.selectDynamic(a.name.toString).pullItem.isDefined))
          .getOrElse(constructor.symbol.asMethod.paramss.head)
  }

  def toScala(chimera: Chimera) = {
    constructor(findBestArgs(chimera).map { arg =>
      val selected = chimera.selectDynamic(arg.name.toString)
      reflector.mirrorFor(arg.typeSignature).toScala(selected)
    }: _*)
  }

  def toChimera(any: Any) = {
    val mirrored = Reflector.mirror.reflect(any)
    val mirroredType = mirrored.symbol.asType.toType

    new CollectionChimera(Map[String, Any](
      constructor.symbol.asMethod.paramss.head.map { arg =>
        val argName = arg.name.toString
        val fieldMirror = mirrored.reflectField(
          mirroredType.declaration(universe.newTermName(argName)).asTerm.accessed.asTerm
        )

        val innerChimera = reflector.mirrorFor(arg.typeSignature).toChimera(fieldMirror.get)

        (argName -> innerChimera.pullItem.getOrElse(null))
      }:_*))
  }
}

