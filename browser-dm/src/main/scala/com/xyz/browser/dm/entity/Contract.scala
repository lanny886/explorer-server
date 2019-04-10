package com.xyz.browser.dm.entity

case class Contract(
                  total:String,
                  decimal:String,
                  name:String,
                  symbol:String,
                  asset:String,
                  hash:String,
                  blockNumber:String,
                  contract:String,
                  tokenStandard:String,
                  tokenAction:String,
                  tfrom:String,//from
                  tto:String//to
                )extends Record
