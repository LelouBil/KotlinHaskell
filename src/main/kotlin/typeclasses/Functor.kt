package net.leloubil.typeclasses

import net.leloubil.hk.Hk
import net.leloubil.hk.Witness

interface Functor<out W : Witness,A>: Hk<W,A> {
    fun <B> fmap(f: (A) -> B): Functor<W, B>

    fun void(): Functor<W, Unit> = this.fmap { _ -> Unit }
}

inline fun <W : Witness, A, B> ((A) -> B).lift(): (Functor<W, A>) -> Functor<W, B> =
    { fa -> fa.fmap(this) }
