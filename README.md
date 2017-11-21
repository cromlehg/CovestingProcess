![CovestingTest](logo.png "CovestingTest")

# Covesting smart contract test

* _Standart_        : ERC20
* _Name_            : Covesting
* _Ticket_          : COV
* _Decimals_        : 18
* _Emission_        : Mintable
* _Crowdsales_      : 2
* _Fiat dependency_ : No
* _Tokens locked_   : No

## Smart-contracts description

Contract mint bounty and founders tokens after main sale stage finished. 
Crowdsale contracts have special function to retrieve transferred in errors tokens.
Also crowdsale contracts have special function to direct mint tokens in wei value (featue implemneted to support external pay gateway).

### Contracts contains
1. _CovestingToken_ - Token contract
2. _Presale_ - Presale contract
3. _Mainsale_ - ICO contract
4. _Configurator_ - contract with main configuration for production

### How to manage contract
To start working with contract you should follow next steps:
1. Compile it in Remix with enamble optimization flag and compiler 0.4.18
2. Deploy bytecode with MyEtherWallet. Gas 5100000 (actually 5073514).
3. Call 'deploy' function on addres from (3). Gas 4000000 (actually 3979551). 

Contract manager must call finishMinting after each crowdsale milestone!
To support external mint service manager should specify address by calling _setDirectMintAgent_. After that specified address can direct mint VST tokens by calling _directMint_.

### How to invest
To purchase tokens investor should send ETH (more than minimum 0.1 ETH) to corresponding crowdsale contract.
Recommended GAS: 200000, GAS PRICE - 21 Gwei.

### Wallets with ERC20 support
1. MyEtherWallet - https://www.myetherwallet.com/
2. Parity 
3. Mist/Ethereum wallet

EXODUS not support ERC20, but have way to export key into MyEtherWallet - http://support.exodus.io/article/128-how-do-i-receive-unsupported-erc20-tokens

Investor must not use other wallets, coinmarkets or stocks. Can lose money.

## Token counts

Maximum tokens can mint - 20 000 000 VST 
* on all crowdsales : 82% or 16 500 000 VST 
* on presale : 7% or 1 500 000 VST 
* on mainsale : 75% or 15 000 000 VST
* to founders : 13% or 2 500 000 VST
* to bounty : 5% or 1 000 000 VST

## Main network configuration

* _Minimal insvested limit_     : 0.1 ETH
* _Bounty tokens percent_       : 5% of Presale tokens
* _Founders tokens percent_     : 13% of Presale tokens
* _For sale tokens percent_     : 82% of Presale tokens
* _Founders tokens wallet_      : 0x25ED4f0D260D5e5218D95390036bc8815Ff38262
* _Bounty tokens wallet_        : 0x717bfD30f039424B049D918F935DEdD069B66810
* _Founders tokens lock period_ : 90 days

### Links
1. _Token_ - https://etherscan.io/token/0x21a16c2b16c3f0dc75d7ddf0bdd0f76ab26d148b
2. _Presale_ - https://etherscan.io/address/0x313ef35a7d18633038c8c5f0c8df1a662991d1f1
3. _Mainsale_ - https://etherscan.io/address/0x1c42086ac06a976d0b347b86320b86fe7859f8af

### Crowdsale stages

#### Presale
* _Hardcap_                    : 5000 ETH
* _Price_                      : 300 COV
* _Period_                     : 30 days
* _Start_                      : Fri, 20 Oct 2017 13:00:00 GMT
* _Wallet_                     : 0x6245C05a6fc205d249d0775769cfE73CB596e57D
* _Contract owner_             : 0xEA15Adb66DC92a4BbCcC8Bf32fd25E2e86a2A770 

#### ICO
* _Hardcap_                    : 100000 ETH
* _Period_                     : 30 days
* _Start_                      : Tue, 21 Nov 2017 00:00:00 GMT
* _Wallet_                     : 0x15A071B83396577cCbd86A979Af7d2aBa9e18970
* _Contract owner_             : 0xEA15Adb66DC92a4BbCcC8Bf32fd25E2e86a2A770

_Milestones_
1. hardcap  5000 ETH, price 200 COV per ETH
2. hardcap  5000 ETH, price 180 COV per ETH
3. hardcap 10000 ETH, price 170 COV per ETH
4. hardcap 20000 ETH, price 160 COV per ETH
5. hardcap 20000 ETH, price 150 COV per ETH
6. hardcap 40000 ETH, price 130 COV per ETH



