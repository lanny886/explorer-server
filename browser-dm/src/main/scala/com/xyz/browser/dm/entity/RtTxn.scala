package com.xyz.browser.dm.entity

case class RtTxn(
                  hash:String,
                  block_number:Long,
                  from:String,
                  to:String,
                  value:String,
                  t:String,
                  txn_fee:String,
                  var status:String,
                  block_hash:String
                )extends Record{

}
