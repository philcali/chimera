package com.github.philcali.chimera

import reflect.runtime.universe
import universe.TypeTag

/**
 * Something that has a Chimera trait has the ability to
 * transform into something else
 */
trait Chimera extends Dynamic {
  // Clients should never access this directly
  protected val wrapped: Any

  // Facilitate object traversal
  def selectDynamic(name: String): Chimera

  // Facilitate collection traversal
  def applyDynamic(name: String)(index: Int): Chimera

  // Use a standard option for existence checks
  def pullItem = wrapped match {
    case Some(value) => Some(value)
    case null | None => None
    case other => Some(other)
  }

  // Client entry point to object conversion
  def as[T: TypeTag](implicit reflector: Reflector): T = {
    val initialType = universe.typeOf[T]
    reflector.mirrorFor(initialType).toScala(this).asInstanceOf[T]
  }

  override def toString() = pullItem.toString()
}
