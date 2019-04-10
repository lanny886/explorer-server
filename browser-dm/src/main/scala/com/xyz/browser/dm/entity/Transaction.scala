package com.xyz.browser.dm.entity

case class Transaction(
//                  jsonrpc: String,
//                  id: Long,
                  blockHash:String,
                  blockNumber:String,
                  contractAddress:String,
                  cumulativeGasUsed:String,
                  tfrom:String,//from
                  gasUsed:String,
                  logs:String,//jsonStr arr
                  logsBloom:String,
                  status:String,
                  tto:String,//to
                  transactionHash:String,
                  transactionIndex:String
                ) extends Record
