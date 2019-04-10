package com.xyz.browser.dm.entity

case class Block(
//                  jsonrpc: String,
//                  id: Long,
                  difficulty:String,
                  extraData:String,
                  gasLimit:String,
                  gasUsed:String,
                  hash:String,
                  logsBloom:String,
                  miner:String,
                  mixHash:String,
                  nonce:String,
                  number:String,
                  parentHash:String,
                  receiptsRoot:String,
                  sha3Uncles:String,
                  size:String,
                  stateRoot:String,
                  timestamp:String,
                  totalDifficulty:String,
                  transactions:String,//jsonStr arr
                  transactionsRoot: String,
                  uncles:String//jsonStr arr
                )extends Record
