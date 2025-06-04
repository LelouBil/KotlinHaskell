package net.leloubil.typeclasses

import net.leloubil.doReturning
import net.leloubil.hk.Hk
import net.leloubil.hk.Witness

interface Applicative<out W : Applicative<W,*>, T> : Functor<W, T> {

    val applicative: ApplicativeCompanion<W>

    // Apply a function wrapped in a context to a value wrapped in a context
    fun <R> apl(ff: Applicative<@UnsafeVariance W, (T) -> R>): Applicative<W, R>

    fun <B, R> liftA2(f: (T, B) -> R): (Applicative<@UnsafeVariance W, B>) -> Applicative<W, R> = { fb ->
        this.apl(fb.fmap { b -> { a: T -> f(a, b) } }.fix())
    }

    private fun <T> Hk<@UnsafeVariance W, T>.fix(): Applicative<W, T> =
        this as Applicative<W, T>

    interface ApplicativeCompanion<out W : Applicative<W,*>> {
        fun <A> pure(a: A): Applicative<W, A>

        fun <A> replicateM(cnt0: Int, f: Applicative<@UnsafeVariance W, A>): Applicative<W, List<A>> {
            val lf = f.liftA2<List<A>, List<A>> { a, b -> listOf(a) + b }
            var orig = pure(emptyList<A>())
            repeat(cnt0) {
                orig = lf(orig)
            }
            return orig
        }
        fun <A> replicateM_(cnt0: Int, f: Applicative<@UnsafeVariance W, A>): Applicative<W, Unit> {
            return replicateM(cnt0, f).void().fix()
        }
        private fun <T> Hk<@UnsafeVariance W, T>.fix(): Applicative<W, T> =
            this as Applicative<W, T>
    }

}

inline fun <A, B, C, W : Applicative<W,*>> ((A, B) -> C).liftA2(): (Applicative<W, A>) -> (Applicative<W, B>) -> Applicative<W, C> =
    { fa ->
        { fb ->
            fa.liftA2(this)(fb)
        }
    }

