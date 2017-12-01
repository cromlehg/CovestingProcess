package ds13.http

import scala.collection.JavaConverters.seqAsJavaListConverter

import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.message.BasicNameValuePair

class PostRequest(
    httpClient: HTTPClient,
    url: String,
    args: Seq[(String, String)],
    formArgs: Seq[(String, String)],
    headers: Seq[(String, String)]) extends GetRequest(httpClient, url, args, headers) {

  def withMod(mod: ((String, String)*) => Seq[(String, String)]): PostRequest =
    new PostRequest(httpClient, url, args, mod(formArgs: _*), headers)

  protected override def createNativeRequest: HttpUriRequest =
    new HttpPost(performArgs) {
      debug("Preparing POST entity: ")
      debug(formArgs.map(entry => entry._1 + " -> " + entry._2).mkString("\n"))
      setEntity(new UrlEncodedFormEntity(formArgs map (entry => new BasicNameValuePair(entry._1, entry._2)) asJava, "UTF-8"))
    }

  override def withHeaders(headers: (String, String)*): PostRequest =
    new PostRequest(httpClient, url, args, formArgs, headers)

  override def withArgs(args: (String, String)*): PostRequest =
    new PostRequest(httpClient, url, args, formArgs, headers)

  def withFormArgs(formArgs: (String, String)*): PostRequest =
    new PostRequest(httpClient, url, args, formArgs, headers)

}