package net.leloubil.typeclasses

import net.leloubil.hk.Hk
import net.leloubil.impls.monoids.Endo
import net.leloubil.then

interface Foldable<T : Foldable<T, *>, A> : Hk<T, A> {

    val foldable: FoldableCompanion<T>


    fun <B> foldr(f: (A) -> (B) -> B, z: B): B = foldable.foldr(
        f, z, this
    )

    fun <B> foldr(f: (A, B) -> B, z: B): B =
        foldr({ a: A -> { b: B -> f(a, b) } }, z)

    fun <B> foldl(f: (B) -> (A) -> B, z: B): B =
        foldable.foldr({ a: A -> { b: B -> f(b)(a) } }, z, this)

    fun <M : Monoid<M>> Monoid.MonoidCompanion<M>.foldMap(f: (A) -> M): M {
        with(foldable) {
            return this@foldMap.foldMap(f, this@Foldable)
        }
    }

    interface FoldableCompanion<F : Foldable<F, *>> {
        fun <M : Monoid<M>, A> Monoid.MonoidCompanion<M>.foldMap(f: (A) -> M, xs: Foldable<F, A>): M
        fun <A, B> foldr(f: (A) -> (B) -> B, z: B, xs: Foldable<F, A>): B
        fun <A, B> foldr(f: (A, B) -> B, z: B, xs: Foldable<F, A>): B =
            foldr({ a: A -> { b: B -> f(a, b) } }, z, xs)
    }

    interface FoldableCompanionMap<F : Foldable<F, *>> : FoldableCompanion<F> {
        override fun <A, B> foldr(f: (A) -> (B) -> B, z: B, xs: Foldable<F, A>): B =
            foldComposing(f, xs).appEndo(z)

        private fun <A, B> foldComposing(f: (A) -> (B) -> B, xs: Foldable<F, A>): Endo<B> {
            return Endo<B>().foldMap(f then ::Endo, xs)
        }

    }

    interface FoldableCompanionR<F : Foldable<F, *>> : FoldableCompanion<F> {
        override fun <M : Monoid<M>, A> Monoid.MonoidCompanion<M>.foldMap(f: (A) -> M, xs: Foldable<F, A>): M {
            return foldr({ a, b -> b.mappend(f(a)) }, mempty(), xs)
        }
    }
}

context(r: Monoid.MonoidCompanion<M>)
fun <M, F> F.fold(): M
        where M : Monoid<M>, F : Foldable<*, M> {
    return r.foldMap { it }
}


fun <M, F> F.fold(c: Monoid.MonoidCompanion<M>): M
        where M : Monoid<M>, F : Foldable<*, M> = with(c) { fold() }

fun <M, F, T> F.fold(c: WrappingMonoid.WrappingMonoidCompanion<M, T>): M
        where M : WrappingMonoid<M, T>, F : Foldable<*, T> = with(c) {
    foldMap(c::mwrap)
}

