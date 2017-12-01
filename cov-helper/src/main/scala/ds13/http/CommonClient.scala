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
import org.apache.http.HttpStatus

class Error(val code: Int, val descr: Option[String])

class CommonClient(override val logger: Logger, proxyPropsOpt: Option[TraitProxyProperties])
  extends HTTPClient(logger: Logger, proxyPropsOpt) {

  var lastError: Option[Error] = None

  protected final def processResponseStringContentOk[T](onSuccess: String => Option[T])(implicit response: CloseableHttpResponse): Option[T] =
    processResponseOk(response)(processResponseContent(response, getStringContent(response))(onSuccess))

  protected final def processResponseContent[T, C](response: CloseableHttpResponse, bodyParser: Option[C])(onSuccess: C => Option[T]): Option[T] =
    bodyParser flatMap onSuccess

  protected final def processResponseOk[T](response: CloseableHttpResponse)(onSuccess: => Option[T]): Option[T] =
    processResponseByCode(response, HttpStatus.SC_OK)(onSuccess)

  protected final def processResponseMovedTemporary[T](response: CloseableHttpResponse)(onSuccess: => Option[T]): Option[T] =
    processResponseByCode(response, HttpStatus.SC_MOVED_TEMPORARILY)(onSuccess)

  protected final def processResponseByCode[T](response: CloseableHttpResponse, code: Int)(onSuccess: => Option[T]): Option[T] =
    if (response.getStatusLine.getStatusCode == code)
      onSuccess
    else {
      handleError(HTTPClientConsts.ERR_CODE_NOT_EXPECTED, Some(response.getStatusLine.getReasonPhrase))
      None
    }

  override def handleError(code: Int, descrOpt: Option[String]) = {
    super.handleError(code, descrOpt)
    lastError = Some(new Error(code, descrOpt))
  }

}