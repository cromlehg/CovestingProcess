package ds13.http

import ds13.logger.LoggerSupported

class RequestURL(httpClient: HTTPClient, url: String) extends LoggerSupported {

  override val logger = httpClient.logger

  def get: GetRequest =
    new GetRequest(httpClient, url, Seq.empty, Seq.empty)

  def post: PostRequest =
    new PostRequest(httpClient, url, Seq.empty, Seq.empty, Seq.empty)

}