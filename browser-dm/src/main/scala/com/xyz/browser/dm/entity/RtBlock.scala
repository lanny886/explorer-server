package com.xyz.browser.dm.entity

case class RtBlock(
                    number:String,
                    miner:String,
                    reward:String,
                    t:String,
                    txn_count:String,
                    uncle_count:String,
                    gas_used:String,
                    gas_limit:String,
                    avg_gas_price:String,
                    hash:String
                )extends Record
