package com.github.philcali.chimera

class CollectionChimera(any: Any) extends Chimera {
  protected val wrapped = any

  def selectDynamic(name: String) = wrapped match {
    case obj: Map[_, _] =>
      new CollectionChimera(obj.asInstanceOf[Map[String, Any]].get(name).getOrElse(null))
    case _ => new CollectionChimera(wrapped)
  }

  def applyDynamic(name: String)(index: Int) = wrapped match {
    case obj: Map[_, _] => selectDynamic(name).applyDynamic(name)(index)
    case list: List[_] => new CollectionChimera(list.lift(index).getOrElse(null))
    case _ => new CollectionChimera(wrapped)
  }
}
