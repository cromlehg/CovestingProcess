package covesting

import ds13.logger.PrintAllLogger
import ds13.http.HTTPClient
import java.io.PrintWriter
import scala.io.Source

object TokenHoldersParser extends App {
  
    // ROPSTEN NET TEST
  //val node = "http://localhost:8545"
  val node = "http://localhost:8545"
  
  val URL = "https://ropsten.etherscan.io/"

  // Token contract address
  val COV_TOKEN_ADDRESS = "0x08Af19675879d4baDF6056cF140a32b83a985eD6"

  // Token processor contract address
  val contractAddress = "0x39490A2A76a603aB69103458611981a0ABc08c63"

  // Authorized in token processor contract address
  val accountAddr = "0x25803D4325EbC33CCF779FF16a23d6CF9543e559"

  // Authorized in token processor account password
  val accountPassword = ".........."
  
  /*
  // MAIN NET TEST
  //val node = "http://localhost:8545"
  val node = "http://localhost:8545"
  
  val URL = "https://etherscan.io/"

  // Token contract address
  val COV_TOKEN_ADDRESS = "0x21A16c2B16c3F0dC75D7dDf0BDD0F76AB26d148B"

  // Token processor contract address
  val contractAddress = ""

  // Authorized in token processor contract address
  val accountAddr = "0x772215cCF488031991f7DCC65e80A7C1FD497E75"

  // Authorized in token processor account password
  val accountPassword = "................."
  */
  
/*
  // KOVAN
  val node = "http://localhost:8545"
  //val node = "https://kovan.infura.io/8rldhike5t6EoXQpanE5"
  
  val URL = "https://kovan.etherscan.io/"

  // Token contract address
  val COV_TOKEN_ADDRESS = "0x9804cc353A155D6d3526047da0fCbA73eeabA73B"

  // Token processor contract address
  val contractAddress = "0x836F796Fa93B5ebDA8CB904304660d2Ea6479120"

  // Authorized in token processor contract address
  val accountAddr = "0x772215cCF488031991f7DCC65e80A7C1FD497E75"

  // Authorized in token processor account password
  val accountPassword = "................."
*/
  /*
  // PRODUCTION
  val node = "http://localhost:8545"
  
  val URL = "https://etherscan.io/"

  // Token contract address
  val COV_TOKEN_ADDRESS = "0xe2fb6529ef566a080e6d23de0bd351311087d567"

  // Token processor contract address
  val contractAddress = ""

  // Authorized in token processor contract address
  val accountAddr = ""

  // Authorized in token processor account password
  val accountPassword = ""*/

  val tempFolder = "temp"

  val sourceFile = "source"

  val sourceFilePath = tempFolder + "/" + sourceFile

  val sourceFilePathScript = tempFolder + "/" + "script.js"

  val gropSize = 20

  val client = new EtherscanClient(URL, PrintAllLogger(), None);

  client.getTokenHolders(COV_TOKEN_ADDRESS).foreach { tokenHoldersNative =>
    println(tokenHoldersNative.length)
    Some(new PrintWriter(sourceFilePath)).foreach { p =>
      p.write(tokenHoldersNative.mkString("\n"))
      p.close
    }

    val tokenHolders: List[TokenHolder] = Source.fromFile(sourceFilePath).getLines.map(t => new TokenHolder(t.trim())).toList

    val script = """
console.log('Start')
Web3 = require('web3')
console.log('Connecting to node.')
web3 = new Web3(new Web3.providers.HttpProvider("""" + node +  """"))
if(web3.isConnected()) {
  console.log('Unlock account')
  web3.personal.unlockAccount("""" + accountAddr + """", """" + accountPassword + """")
  console.log('Connected to node.')
  abiDefinition = JSON.parse('[{"constant":false,"inputs":[{"name":"newToken","type":"address"}],"name":"setToken","outputs":[],"payable":false,"stateMutability":"nonpayable","type":"function"},{"constant":false,"inputs":[{"name":"tokenHolder","type":"address"}],"name":"extraMint","outputs":[],"payable":false,"stateMutability":"nonpayable","type":"function"},{"constant":true,"inputs":[],"name":"owner","outputs":[{"name":"","type":"address"}],"payable":false,"stateMutability":"view","type":"function"},{"constant":false,"inputs":[{"name":"newIncreaseK","type":"uint256"}],"name":"setIncreaseK","outputs":[],"payable":false,"stateMutability":"nonpayable","type":"function"},{"constant":false,"inputs":[{"name":"to","type":"address"}],"name":"authorize","outputs":[],"payable":false,"stateMutability":"nonpayable","type":"function"},{"constant":true,"inputs":[{"name":"","type":"address"}],"name":"authorized","outputs":[{"name":"","type":"bool"}],"payable":false,"stateMutability":"view","type":"function"},{"constant":true,"inputs":[],"name":"increaseK","outputs":[{"name":"","type":"uint256"}],"payable":false,"stateMutability":"view","type":"function"},{"constant":false,"inputs":[{"name":"to","type":"address"}],"name":"unauthorize","outputs":[],"payable":false,"stateMutability":"nonpayable","type":"function"},{"constant":false,"inputs":[{"name":"newOwner","type":"address"}],"name":"transferOwnership","outputs":[],"payable":false,"stateMutability":"nonpayable","type":"function"},{"constant":false,"inputs":[{"name":"tokenHolders","type":"address[]"}],"name":"extraMintArray","outputs":[],"payable":false,"stateMutability":"nonpayable","type":"function"},{"constant":true,"inputs":[],"name":"token","outputs":[{"name":"","type":"address"}],"payable":false,"stateMutability":"view","type":"function"}]')
  contract = web3.eth.contract(abiDefinition)
  console.log('Connect to contract.')
  contractInstance = contract.at("""" + contractAddress + """")
""" +
      (tokenHolders.grouped(gropSize).map { thl =>
        "console.log('Mint extra tokens part.')\n" +
          "contractInstance.extraMintArray([" + thl.map("\"" + _ + "\"").mkString(",") + "], {from: \"" + accountAddr + "\"})"
      }).mkString("\n") +
      """
}
"""
println(script)
    Some(new PrintWriter(sourceFilePathScript)).foreach { p =>
      p.write(script)
      p.close
    }

  }

}
