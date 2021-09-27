package com.example.sistemabus.adaptadorProp

data class Propietarios(var Nombre:String ?= null, val Apellido :String ?= null,
                        var CI: String ?= null, var Url :String ?= null,
                        var email :String ?= null, var Teléfono :String ?= null)