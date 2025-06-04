package net.leloubil.typeclasses

/**
 * A Monoid is a type class that represents an associative binary operation with an identity element.
 * Instances should satisfy the following properties:
 * @sample monoidTest
 */
interface Monoid<S : Monoid<S>> : Semigroup<S> {
    infix fun mappend(y: S): S

    override fun assoc(y: S): Semigroup<S> = mappend(y)

//    fun mconcat(xs: Foldable<*,S>): S = xs.foldr({ a, b -> a mappend b }, monoid.mempty()) // doesn't exist, and causes recursion issues
    fun mconcat(xs: List<S>): S = xs.fold(monoid.mempty(), Monoid<S>::mappend)

    operator fun plus(y: S): S = mappend(y)

    val monoid: MonoidCompanion<S>

    interface MonoidCompanion<M : Monoid<M>> {
        fun mempty(): M
        fun mconcat(xs: List<M>): M = mempty().mconcat(xs)
//        fun mconcat(xs: Foldable<*,M>): M = mempty().mconcat(xs) // doesn't exist, and causes recursion issues
    }

}

/**
 * A WrappingMonoid is a Monoid that wraps an external type T.
 * It provides a way to apply monoidal operations to types that are not directly instances of the Monoid.
 * For example, it allows easier use of monoids with external types like Int, String, etc.
 */
interface WrappingMonoid<S : WrappingMonoid<S, T>, T> : Semigroup<S>, Monoid<S> {

    //    fun mconcat(xs: HList<Monoid<S>>) : Monoid<S> = xs.foldr(monoid.mempty(),::mappend)

    val wrappingMonoid: WrappingMonoidCompanion<S, T>

    override val monoid: Monoid.MonoidCompanion<S> get() = wrappingMonoid

    interface WrappingMonoidCompanion<S : WrappingMonoid<S, T>, T> : Monoid.MonoidCompanion<S> {
        fun mwrap(x: T): S // shorthand to apply monoids to external types without manually wrapping them
    }
}


private fun <T : Monoid<T>> monoidTest(x: T, y: T, z: T, companion: Monoid.MonoidCompanion<T>) {
    // Identity
    assert((x mappend companion.mempty()) == x)
    assert((companion.mempty() mappend x) == x)

    // Associativity (Semigroup property)
    assert((x mappend (y mappend z)) == ((x mappend y) mappend z))
}

fun <S : WrappingMonoid<S, T>,T> WrappingMonoid.WrappingMonoidCompanion<S, T>.mconcat(xs: Foldable<*, T>): S =
    with(xs) {
        this@mconcat.foldMap(::mwrap)
    }

