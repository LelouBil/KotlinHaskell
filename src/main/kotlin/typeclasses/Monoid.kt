package net.leloubil.typeclasses

import net.leloubil.impls.monads.HList
import net.leloubil.impls.monoids.Sum

/**
 * A Monoid is a type class that represents an associative binary operation with an identity element.
 * Instances should satisfy the following properties:
 * @sample monoidTest
 */
interface Monoid<S : Monoid<S, T>, T> : Semigroup<S> {

    infix fun mappend(y: S): S

    override fun assoc(y: S): Semigroup<S> = mappend(y)

    //    fun mconcat(xs: HList<Monoid<S>>) : Monoid<S> = xs.foldr(monoid.mempty(),::mappend)
    fun mconcat(xs: List<S>): S = xs.fold(monoid.mempty(), Monoid<S, T>::mappend)

    operator fun plus(y: S): S = mappend(y)

    val monoid: MonoidCompanion<S, T>

    interface MonoidCompanion<S : Monoid<S, T>, T> {
        fun mempty(): S
        fun mconvert(x: T): S
        fun mconcat(xs: List<S>): S = mempty().mconcat(xs)
    }
}

private fun <T : HList<T>> monoidTest(x: T, y: T, z: T, companion: Monoid.MonoidCompanion<HList<T>, HList<T>>) {
    // Identity
    assert((x mappend companion.mempty()) == x)
    assert((companion.mempty() mappend x) == x)

    // Associativity (Semigroup property)
    assert((x mappend (y mappend z)) == ((x mappend y) mappend z))
}

fun <S : Monoid<S, T>, T> Monoid.MonoidCompanion<S, T>.mconcat(xs: List<T>): Monoid<S, T> =
    mconcat(xs.map { mconvert(it) })
