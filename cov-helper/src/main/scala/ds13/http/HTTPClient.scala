package ds13.http

import scala.collection.JavaConverters.asScalaBufferConverter

import org.apache.http.client.CookieStore
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.cookie.Cookie
import org.apache.http.impl.client.BasicCookieStore
import org.apache.http.protocol.BasicHttpContext
import org.apache.http.util.EntityUtils
import org.json.JSONObject

import ds13.logger.Logger
import ds13.logger.LoggerSupported
import org.apache.http.client.protocol.HttpClientContext
import org.apache.http.HttpEntity

class HTTPClient(override val logger: Logger, proxyPropsOpt: Option[TraitProxyProperties]) extends LoggerSupported {

  val httpContext = new BasicHttpContext

  val nativeClient = new NativeHTTPClientBuilder(logger, proxyPropsOpt).build

  var headers: Seq[(String, String)] = Seq.empty

  private def getCookieStore: CookieStore = {
    var cookieStore = httpContext.getAttribute("http.cookie-store").asInstanceOf[CookieStore]
    if (cookieStore == null) {
      cookieStore = new BasicCookieStore()
      httpContext.setAttribute("http.cookie-store", cookieStore)
    }
    cookieStore
  }

  protected def getCookie(name: String): Option[Cookie] =
    (getCookieStore.getCookies asScala).find(_.getName == name)

  protected def getCookieValue(name: String): Option[String] =
    getCookie(name) map (_.getValue)

  def handleError(code: Int, descrOpt: Option[String]) =
    err("ERROR: " + code + (descrOpt match {
      case Some(descr) => " - " + descr
      case _           => ""
    }))

  def addCommonHeaders(addHeaders: (String, String)*) = {
    debug("HTTPCLient: add common headers: ")
    addHeaders foreach (header => debug(header._1 + " -> " + header._2))
    headers = headers ++: addHeaders
  }

  def url(httpUrl: String) = new RequestURL(this, httpUrl)

  def get(httpUrl: String): GetRequest =
    url(httpUrl).get.withHeaders(headers: _*)

  def post(httpUrl: String): PostRequest =
    url(httpUrl).post.withHeaders(headers: _*)

  def get(httpUrl: String, args: (String, String)*): GetRequest =
    get(httpUrl).withArgs(args: _*)

  def post(httpUrl: String, args: (String, String)*): PostRequest =
    post(httpUrl).withArgs(args: _*)

  protected def ifEntity[T](response: CloseableHttpResponse)(f: HttpEntity => Option[T]): Option[T] = {
    val responseEntity = response.getEntity
    if (responseEntity == null) None else f(responseEntity)
  }

  protected def getStringContent(entity: HttpEntity): Option[String] =
    Some(EntityUtils toString entity)

  protected def getByteArrayContent(entity: HttpEntity): Option[Array[Byte]] =
    Some(EntityUtils toByteArray entity)

  protected def getJSONContent(entity: HttpEntity): Option[JSONObject] =
    getStringContent(entity) map (t => new JSONObject(t))

  protected def getStringContent(response: CloseableHttpResponse): Option[String] =
    ifEntity(response)(getStringContent)

  protected def getByteArrayContent(response: CloseableHttpResponse): Option[Array[Byte]] =
    ifEntity(response)(getByteArrayContent)

  protected def getJSONContent(response: CloseableHttpResponse): Option[JSONObject] =
    ifEntity(response)(getJSONContent)

  protected def getContent(response: CloseableHttpResponse): Option[Any] =
    ifEntity(response) { entity =>
      entity.getContentType.getValue match {
        case "application/json" => getJSONContent(entity)
        case "text/html"        => getStringContent(entity)
        case _                  => None
      }
    }

}