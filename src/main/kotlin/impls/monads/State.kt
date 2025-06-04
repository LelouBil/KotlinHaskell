package net.leloubil.impls.monads

import net.leloubil.hk.Hk
import net.leloubil.typeclasses.Monad


@ConsistentCopyVisibility
data class State<S, A> @PublishedApi internal constructor(val runState: (S) -> Pair<A, S>) :
    Monad<State<S, *>, A> {
    @Suppress("UNCHECKED_CAST")
    override val monad: Monad.MonadCompanion<State<S, *>> = State()


    fun evalState(s: S): A {
        return runState(s).first
    }

    fun execState(s: S): S {
        return runState(s).second
    }


    override fun <B> flatMap(f: (A) -> Monad<State<S, *>, B>): Monad<State<S, *>, B> {
        return State { s0: S ->
            val (x, s1) = runState(s0)
            f(x).fix().runState(s1)
        }
    }


    class StateCompanion<S> : Monad.MonadCompanion<State<S, *>> {
        override fun <A> `return`(x: A): Monad<State<S, *>, A> = State { s ->
            Pair(x, s)
        }

        fun put(s: S): State<S, Unit> = State { _ ->
            Unit to s
        }

        fun get(): State<S, S> = State { s ->
            s to s
        }
    }


    companion object{
        operator fun <S> invoke() = StateCompanion<S>()
    }
}

inline fun <S, A> Hk<State<S, *>, A>.fix(): State<S, A> = this as State<S, A>

