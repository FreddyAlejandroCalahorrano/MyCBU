package com.example.sistemabus.adaptadoCond

data class Conductores(var Nombre:String ?= null, val Apellido :String ?= null,
                       var CI: String ?= null, var Url :String ?= null,
                       var email :String ?= null, var Teléfono :String ?= null)