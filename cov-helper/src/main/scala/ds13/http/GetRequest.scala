package ds13.http

import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpUriRequest
import java.net.URLEncoder

class GetRequest(
    httpClient: HTTPClient,
    url: String,
    args: Seq[(String, String)],
    headers: Seq[(String, String)]) extends AbstractHTTPRequest(httpClient, url, args, headers) {

  protected override def createNativeRequest: HttpUriRequest =
    new HttpGet(performArgs)

  override def withHeaders(headers: (String, String)*): GetRequest =
    new GetRequest(httpClient, url, args, headers)

  override def withArgs(args: (String, String)*): GetRequest =
    new GetRequest(httpClient, url, args, headers)

}