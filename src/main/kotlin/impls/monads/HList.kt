package net.leloubil.impls.monads

import net.leloubil.hk.Hk
import net.leloubil.typeclasses.Foldable
import net.leloubil.typeclasses.Monad
import net.leloubil.typeclasses.Monoid

// Implement Monad for ListW
data class HList<T>(val inner: List<T>) :
    Monad<HList<*>, T>,
    Monoid<HList<T>>,
    Foldable<HList<*>, T> {

    constructor(vararg elements: T) : this(elements.toList())

    override fun <B> flatMap(f: (T) -> Monad<HList<*>, B>): Monad<HList<*>, B> {
        val xs = this.inner
        val res = mutableListOf<B>()
        for (x in xs) {
            val m = f(x).fix()
            for (y in m.inner) {
                res.add(y)
            }
        }
        return HList(res)
    }


    companion object : Monad.MonadCompanion<HList<*>> {
        override fun <A> `return`(x: A): Monad<HList<*>, A> = HList(listOf(x))
        fun <T> monoid(): MonoidCompanion<T> = MonoidCompanion()
        fun <T> foldable(): FoldableCompanion = FoldableCompanion()
    }

    class MonoidCompanion<T> : Monoid.MonoidCompanion<HList<T>> {
        override fun mempty() = HList<T>()
    }

    class FoldableCompanion : Foldable.FoldableCompanionMap<HList<*>> {
        override fun <M : Monoid<M>, A> Monoid.MonoidCompanion<M>.foldMap(
            f: (A) -> M,
            xs: Foldable<HList<*>, A>
        ): M {
            val ls: HList<A> = xs.fix()
            val monoid: Monoid.MonoidCompanion<M> = this@foldMap
            return monoid.mconcat(ls.fmap(f).fix().inner)
        }

    }

    fun head(): Pair<Maybe<T>, HList<T>> {
        return if (inner.isNotEmpty()) {
            Maybe.Just(inner.first()) to HList(inner.drop(1))
        } else {
            Maybe.Nothing<T>() to this
        }
    }

    override val monad = Companion

    override val monoid = monoid<T>()

    override val foldable = foldable<T>()

    override fun mappend(y: HList<T>): HList<T> = HList(this.inner + y.inner)
}

val <T> List<T>.h: HList<T>
    get() = HList(this)

fun <T> Hk<HList<*>, T>.fix() = this as HList<T>
