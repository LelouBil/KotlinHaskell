package net.leloubil.impls

import net.leloubil.hk.Hk
import net.leloubil.hk.Witness
import net.leloubil.typeclasses.Monad


@ConsistentCopyVisibility
data class State<S, A> @PublishedApi internal constructor(val runState: (S) -> Pair<A, S>) : Monad<Hk<State.W, S>, A> {
    @Suppress("UNCHECKED_CAST")
    override val monad: Monad.MonadCompanion<Hk<W, S>> = returner()


    fun evalState(s: S): A {
        return runState(s).first
    }

    fun execState(s: S): S {
        return runState(s).second
    }


    override fun <B> flatMap(f: (A) -> Monad<Hk<W, S>, B>): Monad<Hk<W, S>, B> {
        return State { s0: S ->
            val (x, s1) = runState(s0)
            f(x).fix().runState(s1)
        }
    }

    object W : Witness

    class Returner<S> : Monad.MonadCompanion<Hk<W, S>> {
        override fun <A> `return`(x: A): Monad<Hk<W, S>, A> = State { s ->
            Pair(x, s)
        }

        fun put(s: S): State<S, Unit> = State { _ ->
            Unit to s
        }

        fun get(): State<S, S> = State { s ->
            s to s
        }
    }

    companion object {
        fun <S> returner() = Returner<S>()
    }
}

inline fun <S, A> Hk<Hk<State.W, S>, A>.fix(): State<S, A> = this as State<S, A>

