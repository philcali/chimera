package com.github.philcali.chimera

import mirrors._
import reflect.runtime.universe
import universe.{ Type, TypeTag }

// @todo: make this nicer
object Reflector { self =>
  type Build = Reflector => Mirror[_]

  trait MirrorFilter extends (Type => Boolean) {
    def build(arg: Type): Build = createMirror(_, arg)

    def createMirror(reflector: Reflector, arg: Type): Mirror[_]
  }

  val mirror = universe.runtimeMirror(getClass.getClassLoader)

  implicit object Default extends ReflectorBuilder(
    List(PrimitiveMirror, OptionMirror, ListMirror, MapMirror, ClassMirror),
    Map((universe.typeOf[Any], (reflector) => new AnyMirror(reflector)))
  )

  implicit class AnyToChimera(any: Any) {
    val mirroredType = mirror.reflect(any).symbol.asType.toType

    def toChimera(implicit reflector: Reflector) = {
      reflector.mirrorFor(mirroredType).toChimera(any)
    }
  }
}

trait Reflector {
  def mirrorFor(arg: Type): Mirror[_]
}

trait FilterMirror {
  self: Reflector =>
  val mirrorFilters: List[Reflector.MirrorFilter]
}

trait MirrorMap {
  self: Reflector =>

  val mirrorMap: Map[Type, Reflector.Build]
}

trait ComplexReflector extends Reflector with FilterMirror with MirrorMap {
  def filterMirror(filter: Reflector.MirrorFilter): ComplexReflector

  def mapMirror(argMirror: (Type, Reflector.Build)): ComplexReflector

  def mirrorFor(arg: Type) =
    mirrorMap.get(arg).map(_.apply(this))
             .getOrElse(
    mirrorFilters.find(_.apply(arg)).map(_.build(arg)(this))
             .getOrElse(
    throw new IllegalArgumentException(s"Type ${arg} was an invalid type")
    ))
}

case class ReflectorBuilder(
  mirrorFilters: List[Reflector.MirrorFilter] = List(),
  mirrorMap: Map[Type, Reflector.Build] = Map()) extends ComplexReflector {

  def filterMirror(filter: Reflector.MirrorFilter) =
    this.copy(mirrorFilters = filter :: mirrorFilters)

  def mapMirror(arg: (Type, Reflector.Build)) =
    this.copy(mirrorMap = this.mirrorMap + arg)
}
