package net.leloubil.typeclasses

/**
 * A Semigroup is a type class that represents a binary associative operation.
 * Instances should satisfy the associative property:
 *  @sample assocTest
 */
interface Semigroup<A> {
    infix fun assoc(y: A): Semigroup<A>
}

private fun <T: Semigroup<Any>> assocTest(a: T, b: T, c: T){
        (a assoc (b assoc c)) == ((a assoc b) assoc c)
}
