package com.xyz.browser.dm.entity

case class Err(
              topic:String,
              partition:String,
              koffset:String,
              k:String,
              v:String,
              msg:String,
              t:String
                )extends Record
