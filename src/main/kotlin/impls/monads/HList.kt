package net.leloubil.impls.monads

import net.leloubil.hk.Hk
import net.leloubil.hk.Witness
import net.leloubil.typeclasses.Monad
import net.leloubil.typeclasses.Monoid

// Implement Monad for ListW
data class HList<T>(val inner: List<T>) : Monad<HList<*>, T>, Monoid<HList<T>, HList<T>> {

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
    }

    fun head(): Pair<Maybe<T>, HList<T>> {
        return if (inner.isNotEmpty()) {
            Maybe.Just(inner.first()) to HList(inner.drop(1))
        } else {
            Maybe.Nothing<T>() to this
        }
    }

    class MonoidCompanion<T> : Monoid.MonoidCompanion<HList<T>, HList<T>> {
        override fun mempty() = HList<T>()

        override fun mconvert(x: HList<T>) = x
    }

    override val monad = Companion
    override fun mappend(y: HList<T>): HList<T> = HList(this.inner + y.inner)

    override val monoid = monoid<T>()
}

fun <T> Hk<HList<*>, T>.fix() = this as HList<T>
