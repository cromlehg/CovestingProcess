package ds13.logger

import org.json.JSONObject

trait TraitJSON {

  def toJSON: Option[JSONObject]

  def toJSONString: Option[String] =
    toJSON.map(_.toString)

  override def toString = toJSONString match {
    case Some(string) => string
    case _            => super.toString
  }

}