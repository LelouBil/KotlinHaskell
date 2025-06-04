package net.leloubil.typeclasses

import net.leloubil.hk.Hk

interface Functor<out W : Functor<W,*>,A>: Hk<W,A> {
    fun <B> fmap(f: (A) -> B): Functor<W, B>

    fun void(): Functor<W, Unit> = this.fmap { _ -> Unit }
}

inline fun <S : Functor<S,*>, A, B> ((A) -> B).lift(): (Functor<S, A>) -> Functor<S, B> =
    { fa -> fa.fmap(this) }
