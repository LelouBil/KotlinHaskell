package net.leloubil.impls

import net.leloubil.hk.Hk
import net.leloubil.hk.Witness
import net.leloubil.typeclasses.Monad

// Implement Monad for ListW
data class ListMonad<T> (val inner: List<T>) : Monad<ListMonad.W, T> {

    constructor(vararg elements: T) : this(elements.toList())

    override fun <B> flatMap(f: (T) -> Monad<W, B>): Monad<W, B> {
        val xs = this.inner
        val res = mutableListOf<B>()
        for (x in xs) {
            val m = f(x).fix()
            for (y in m.inner){
                res.add(y)
            }
        }
        return ListMonad(res)
    }


    companion object: Monad.MonadCompanion<W> {
        override fun <A> `return`(x: A): Monad<W, A> = ListMonad(listOf(x))
    }

    fun head(): Pair<Maybe<T>,ListMonad<T>> {
        return if (inner.isNotEmpty()) {
            Maybe.Just(inner.first()) to ListMonad(inner.drop(1))
        } else {
            Maybe.Nothing<T>() to this
        }
    }

    override val monad = Companion

    object W : Witness
}

fun <T> Hk<ListMonad.W, T>.fix() = this as ListMonad<T>
