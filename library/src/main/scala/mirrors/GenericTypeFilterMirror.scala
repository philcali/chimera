package com.github.philcali.chimera
package mirrors

import reflect.runtime.universe
import universe.{ Type, TypeTag }

abstract class GenericTypeMirrorFilter[T: TypeTag] extends Reflector.MirrorFilter {
  lazy val parentType = universe.typeOf[T]
  lazy val parentArgs = parentType.typeSymbol.asClass.typeParams.map(_.asType.toType)

  def apply(arg: Type) = arg.typeConstructor =:= parentType.typeConstructor

  def typeArgs(arg: Type) = parentArgs.map(_.asSeenFrom(arg, parentType.typeSymbol.asClass))
}
