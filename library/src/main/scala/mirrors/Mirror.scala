package com.github.philcali.chimera
package mirrors

import reflect.runtime.universe.Type

trait Mirror[T] {
  val reflector: Reflector
  val initialType: Type

  def toScala(chimera: Chimera): T
  def toChimera(any: Any): Chimera
}
