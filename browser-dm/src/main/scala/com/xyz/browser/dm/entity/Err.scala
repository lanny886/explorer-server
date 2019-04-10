package com.xyz.browser.dm.entity

case class Err(
              koffset:String,
              k:String,
              v:String,
              msg:String,
              t:String
                )extends Record
