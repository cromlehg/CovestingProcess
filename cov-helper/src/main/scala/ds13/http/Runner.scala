package ds13.http

import ds13.logger.PrintAllLogger

object Runner extends App {

  val log = new PrintAllLogger

  val proxyProps = new TraitProxyProperties {

    override val logger = log

    override def getIP = "81.177.157.124"

    override def getPort = Some(65233)

    override def getLogin = Some("cromlehg")

    override def getPass = Some("erygtfghgh")

  }

  val client = new HTTPClient(log, Some(proxyProps))

  client.get("http://ya.ru").execute { response =>
    println(response)
    println(response)
    Some(true)
  }

}