package com.xyz.browser.dm.entity

case class Bancor(
                   hash:String ,
                   tfrom:String ,
                   contract:String ,
                   action:String ,
                   ttype:String ,
                   name:String ,
                   tfunction:String ,
                   param:String ,//jsonStr arr
                   input:String
                )extends Record
