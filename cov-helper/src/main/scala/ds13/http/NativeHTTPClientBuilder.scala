package ds13.http

import org.apache.http.HttpHost
import org.apache.http.HttpRequest
import org.apache.http.HttpResponse
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.DefaultRedirectStrategy
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.protocol.HttpContext

import ds13.logger.Logger
import ds13.logger.LoggerSupported
import org.apache.http.impl.client.BasicCredentialsProvider
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.impl.client.BasicAuthCache
import org.apache.http.impl.auth.BasicScheme
import org.apache.http.protocol.BasicHttpContext
import org.apache.http.client.protocol.HttpClientContext
import org.apache.http.impl.client.HttpClients

class NativeHTTPClientBuilder(val logger: Logger, proxyPropsOpt: Option[TraitProxyProperties]) extends LoggerSupported {

  def build: CloseableHttpClient = {
    val builder: HttpClientBuilder = HttpClientBuilder.create

    debug("HTTP Client: Set redirect strategy...")
    builder setRedirectStrategy (new DefaultRedirectStrategy() {

      override def isRedirected(request: HttpRequest, response: HttpResponse, context: HttpContext): Boolean =
        super.isRedirected(request, response, context) && response.getFirstHeader("location") != null

    });

    builder setMaxConnPerRoute 6

    proxyPropsOpt match {
      case Some(proxyProps) =>
        debug("HTTP client: try to set proxy...")
        debug("HTTP client: proxy IP: " + proxyProps.getIP)
        val proxyHost =
          new HttpHost(proxyProps.getIP,
            proxyProps.getPort match {
              case Some(port) =>
                debug("HTTP client: proxy port defined: " + port)
                port
              case _ =>
                debug("HTTP client: proxy port udnefined, set to default: " + 80)
                80
            })
        builder.setProxy(proxyHost);
        (proxyProps.getLogin, proxyProps.getPass) match {
          case (Some(login), Some(pass)) =>
            debug("Setup proxy auth...")
            val credsProvider = new BasicCredentialsProvider
            credsProvider.setCredentials(
              new AuthScope(proxyHost.getHostName, proxyHost.getPort),
              new UsernamePasswordCredentials(login, pass))
            val authCache = new BasicAuthCache
            val basicAuth = new BasicScheme
            authCache.put(proxyHost, basicAuth)
            builder.setDefaultCredentialsProvider(credsProvider)
            debug("Proxy auth has been prepared successfully with login and password: " + login + ", " + pass)
          case _ =>
        }
        debug("HTTP client: proxy prepared.")
      case _ =>
    }

    builder.build
  }

}