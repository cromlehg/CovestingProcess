package ds13.http

import java.io.IOException

import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpUriRequest

import ds13.logger.LoggerSupported
import java.net.URLEncoder

abstract class AbstractHTTPRequest(
    val httpClient: HTTPClient,
    val url: String,
    val args: Seq[(String, String)],
    val headers: Seq[(String, String)]) extends LoggerSupported {

  override val logger = httpClient.logger

  protected def createNativeRequest: HttpUriRequest

  protected def performArgs: String =
    if (args.isEmpty) url else url + "?" + args.map(entry => entry._1 + "=" + URLEncoder.encode(entry._2, "UTF-8")).mkString("&")

  def withHeaders(headers: (String, String)*): AbstractHTTPRequest

  def withArgs(args: (String, String)*): AbstractHTTPRequest

  def execute[T](onSuccess: CloseableHttpResponse => T): T = {
    debug("Preparing request to: " + url)
    if (args.length > 0) {
      debug("With args: ")
      debug(args.map(arg => arg._1 + " -> " + arg._2).mkString("\n"))
    }
    val request = createNativeRequest
    debug("Prepared url: " + request.getURI.toURL.toString)
    headers foreach (header => request.addHeader(header._1, header._2))
    var response: CloseableHttpResponse = null
    try {
      var response = httpClient.nativeClient.execute(request, httpClient.httpContext)
      onSuccess(response)
    } finally if (response != null) response.close
  }

}