
import ether from './helpers/ether'
import {advanceBlock} from './helpers/advanceToBlock'
import {increaseTimeTo, duration} from './helpers/increaseTime'
import latestTime from './helpers/latestTime'
import EVMThrow from './helpers/EVMThrow'

const BigNumber = web3.BigNumber

const should = require('chai')
  .use(require('chai-as-promised'))
  .use(require('chai-bignumber')(BigNumber))
  .should()

const Configurator = artifacts.require('Configurator')

const Token = artifacts.require('CovestingToken')

const Mainsale = artifacts.require('Mainsale')

const Presale = artifacts.require('Presale')

contract('Crowdsale', function(wallets) {

  before(async function() {
    await advanceBlock()
  })
  
  beforeEach(async function () {
    this.configurator = await Configurator.new()
    await this.configurator.deploy().should.be.fulfilled
    this.token = Token.at(await this.configurator.token())
    this.presale = Presale.at(await this.configurator.presale())
    this.mainsale = Mainsale.at(await this.configurator.mainsale())
  })	 
  
  it('Integration test', async function () {
  
    const owner = wallets[0]

    const defInvestor = wallets[1]
 
    const extInvestorPresale = wallets[2]

    const defInvestor1 = wallets[3]

    const defInvestor2 = wallets[5]

    const defInvestor3 = wallets[7]

    const defValue = ether(3)

    const jumpValue = ether(5)

    const jumpValue1 = ether(5000)

    var masterWallet = "0x6245C05a6fc205d249d0775769cfE73CB596e57D"

    console.log('Finish presale')
    await this.presale.finishMinting().should.be.fulfilled

    console.log('Increase time to start of ICO')
  //  const mainsaleStart = 1511222400
  //  await increaseTimeTo(mainsaleStart)

    let test1 = await this.token.saleAgent()
    console.log(test1)
    console.log(await this.configurator.mainsale())

    masterWallet = "0x15A071B83396577cCbd86A979Af7d2aBa9e18970"

    console.log('Invest 3 ether')
    await this.mainsale.sendTransaction({from: defInvestor1, value: defValue}).should.be.fulfilled
    
    console.log('Check mainsale investor balance')
    const mainsale1Price = new BigNumber(200)
    var minted = defValue.mul(mainsale1Price)
    var balanceOf = await this.token.balanceOf(defInvestor1)
    balanceOf.should.be.bignumber.equal(minted)

    console.log('Check summary mainsale minted')
    var summaryMinted = minted
    var totalSupply = await this.token.totalSupply()
    totalSupply.should.be.bignumber.equal(summaryMinted)
    
    console.log('Check master wallet')
    var curMasterBalance = await web3.eth.getBalance(masterWallet)
    var localMasterBalance = defValue
    curMasterBalance.should.be.bignumber.equal(curMasterBalance)

    console.log('Invest 5 ether')
    await this.mainsale.sendTransaction({from: defInvestor2, value: jumpValue}).should.be.fulfilled
    
    console.log('Check mainsale investor balance')
    minted = jumpValue.mul(mainsale1Price)
    balanceOf = await this.token.balanceOf(defInvestor2)
    balanceOf.should.be.bignumber.equal(minted)

    console.log('Check summary mainsale minted')
    summaryMinted = summaryMinted.add(minted)
    totalSupply = await this.token.totalSupply()
    totalSupply.should.be.bignumber.equal(summaryMinted)
    
    console.log('Check master wallet')
    curMasterBalance = await web3.eth.getBalance(masterWallet)
    localMasterBalance = localMasterBalance.add(jumpValue)
    curMasterBalance.should.be.bignumber.equal(curMasterBalance)

    console.log("Finishing mainsale")
    await this.mainsale.finishMinting().should.be.fulfilled

    console.log('Check invest rejection after ICO')
    await this.presale.sendTransaction({from: defInvestor, value: defValue}).should.be.rejectedWith(EVMThrow)

    console.log('Check transfer rejection during after ICO')
    await this.token.transfer(defInvestor1, 10, {from: defInvestor2}).should.be.fulfilled

  })

})
