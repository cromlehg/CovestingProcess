package covesting

import ds13.logger.PrintAllLogger
import ds13.http.HTTPClient
import java.io.PrintWriter
import scala.io.Source

object TokenHoldersParser extends App {

  // Token contract address
  val COV_TOKEN_ADDRESS = "0xe2fb6529ef566a080e6d23de0bd351311087d567"

  // Token processor contract address
  val contractAddress = ""
  
  // Authorized in token processor contract address
  val accountAddr = ""
  
  // Authorized in token processor account password
  val accountPassword = ""
  
  val tempFolder = "temp"

  val sourceFile = "source"

  val sourceFilePath = tempFolder + "/" + sourceFile

  val sourceFilePathScript = tempFolder + "/" + "script.js"

  val gropSize = 10

  val client = new EtherscanClient(PrintAllLogger(), None);
  
  client.getTokenHolders(COV_TOKEN_ADDRESS).foreach { tokenHolders =>
    println(tokenHolders.length)
    Some(new PrintWriter(sourceFilePath)).foreach { p =>
      p.write(tokenHolders.mkString("\n"))
      p.close
    }
  }

  val tokenHolders: List[TokenHolder] = Source.fromFile(TokenHoldersParser.sourceFilePath).getLines.map(t => new TokenHolder(t)).toList

  val script = """
Web3 = require('web3')
console.log('Connecting to node.')
web3 = new Web3(new Web3.providers.HttpProvider("http://localhost:8545"))
if(web3.isConnected()) {
  web3.personal.unlockAccount("""" + accountAddr + """", """" + accountPassword + """")
  console.log('Connected to node.')
  abiDefinition = JSON.parse('[{"constant":false,"inputs":[{"name":"newToken","type":"address"}],"name":"setToken","outputs":[],"payable":false,"stateMutability":"nonpayable","type":"function"},{"constant":false,"inputs":[{"name":"tokenHolder","type":"address"}],"name":"extraMint","outputs":[],"payable":false,"stateMutability":"nonpayable","type":"function"},{"constant":true,"inputs":[],"name":"owner","outputs":[{"name":"","type":"address"}],"payable":false,"stateMutability":"view","type":"function"},{"constant":false,"inputs":[{"name":"newIncreaseK","type":"uint256"}],"name":"setIncreaseK","outputs":[],"payable":false,"stateMutability":"nonpayable","type":"function"},{"constant":false,"inputs":[{"name":"to","type":"address"}],"name":"authorize","outputs":[],"payable":false,"stateMutability":"nonpayable","type":"function"},{"constant":true,"inputs":[{"name":"","type":"address"}],"name":"authorized","outputs":[{"name":"","type":"bool"}],"payable":false,"stateMutability":"view","type":"function"},{"constant":true,"inputs":[],"name":"increaseK","outputs":[{"name":"","type":"uint256"}],"payable":false,"stateMutability":"view","type":"function"},{"constant":false,"inputs":[{"name":"to","type":"address"}],"name":"unauthorize","outputs":[],"payable":false,"stateMutability":"nonpayable","type":"function"},{"constant":false,"inputs":[{"name":"newOwner","type":"address"}],"name":"transferOwnership","outputs":[],"payable":false,"stateMutability":"nonpayable","type":"function"},{"constant":false,"inputs":[{"name":"tokenHolders","type":"address[]"}],"name":"extraMintArray","outputs":[],"payable":false,"stateMutability":"nonpayable","type":"function"},{"constant":true,"inputs":[],"name":"token","outputs":[{"name":"","type":"address"}],"payable":false,"stateMutability":"view","type":"function"}]')
  contract = web3.eth.contract(abiDefinition)
  console.log('Connect to contract.')
  contractInstance = contract.at("""" + contractAddress + """")
""" +
    (tokenHolders.grouped(gropSize).map { thl =>
      "console.log('Mint extra tokens part.')\n" +
        "contractInstance.extraMintArray(" + thl.map("\"" + _ + "\"").mkString(",") + ", {from: \"" + accountAddr + "\"})"
    }).mkString("\n") +
    """
}
"""

  Some(new PrintWriter(TokenHoldersParser.sourceFilePath)).foreach { p =>
    p.write(script.mkString("\n"))
    p.close
  }

}