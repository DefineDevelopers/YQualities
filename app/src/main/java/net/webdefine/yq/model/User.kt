package net.webdefine.yq.model

class User(var id: Int,
           var firstName: String,
           var lastName: String,
           var city: String,
           var age: Int,
           var photoSmall: String,
           var photoBig: String,
           var balance: Int,
           var qualities: MutableList<Quality>)