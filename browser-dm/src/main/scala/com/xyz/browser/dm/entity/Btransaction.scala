package com.xyz.browser.dm.entity

case class Btransaction(
                         blockHash:String,
                         blockNumber:String,
                         tfrom:String,//from
                         gas:String,
                         gasPrice:String,
                         hash:String,
                         input:String,
                         nonce:String,
                         tto:String,//to
                         transactionIndex:String,
                         tvalue:String, //value
                         v:String,
                         r:String,
                         s:String,
                         timestamp:String
                ) extends Record