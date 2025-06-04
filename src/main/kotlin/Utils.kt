package net.leloubil


operator fun <I, A, T> ((A) -> T).plus(function: (I) -> A): (I) -> T = { this(function(it)) }

// because type inference for generics only works in one direction or the other sometimes
infix fun <I, A, T> ((I) -> A).then(function1: (A) -> T): (I) -> T = { function1(this(it)) }
