package covesting

import ds13.http.TraitProxyProperties
import ds13.logger.Logger
import ds13.http.CommonClient
import org.apache.http.client.methods.CloseableHttpResponse
import scala.util.matching.Regex

class TokenHolder(val address: String) {

  override def toString = address

}

class EtherscanClient(val address: String, override val logger: Logger, proxyPropsOpt: Option[TraitProxyProperties])
  extends CommonClient(logger: Logger, proxyPropsOpt) {

  val addressPattern = """target='_parent'>(0x[\d\w]{40})<""".r

  val pagesCountPattern = """Page\s*<\s*b\s*>\s*\d+\s*<\s*\/b\s*>\s*of\s*<\s*b\s*>\s*(\d+)\s*<\s*\/\s*b\s*>\s*<\s*\/span\s*>""".r

  val ETHERSCAN_URL = address

  val ETHERSCAN_TOKEN_HOLDERS_URL = ETHERSCAN_URL + "token/generic-tokenholders2"

  def getTokenHolders(address: String): Option[List[TokenHolder]] =
    getTokenHoldersPagesCount(address).map { count =>
      (for (i <- 1 to count) yield getTokenHoldersPage(address, i)).flatten.flatten.toList
    }

  def getTokenHoldersPagesCount(address: String): Option[Int] = {
    debug("Loading token pages count for " + address)
    get(ETHERSCAN_TOKEN_HOLDERS_URL, "a" -> address, "p" -> "1")
      .execute(implicit response => processResponseStringContentOk(tokenHoldersPagesCountParser))
  }

  def getTokenHoldersPage(address: String, page: Int): Option[List[TokenHolder]] = {
    debug("Loading token holders page " + page + " from " + address)
    get(ETHERSCAN_TOKEN_HOLDERS_URL, "a" -> address, "p" -> page.toString)
      .execute(implicit response => processResponseStringContentOk(tokenHoldersParser))
  }

  def tokenHoldersParser(content: String): Option[List[TokenHolder]] =
    Some((for (matched <- addressPattern.findAllMatchIn(content)) yield new TokenHolder(matched.group(1))).toList)

  def tokenHoldersPagesCountParser(content: String): Option[Int] =
    pagesCountPattern.findFirstMatchIn(content).map(_.group(1).toInt)

}