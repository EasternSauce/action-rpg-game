package com.easternsauce.game.area

class CurrentAreaHolder {
  var currentArea: Area = _
}

object CurrentAreaHolder {
  def apply(): CurrentAreaHolder = new CurrentAreaHolder()
}
