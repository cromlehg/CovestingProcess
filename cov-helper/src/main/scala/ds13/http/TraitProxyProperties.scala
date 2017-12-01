package ds13.http

import ds13.logger.LoggerSupported

trait TraitProxyProperties extends LoggerSupported {

  def getIP: String

  def getPort: Option[Int]

  def getLogin: Option[String]

  def getPass: Option[String]

}