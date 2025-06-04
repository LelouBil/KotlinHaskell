package net.leloubil.impls.monads

import net.leloubil.hk.Hk
import net.leloubil.hk.Witness
import net.leloubil.typeclasses.Monad


sealed class Maybe<T>: Monad<Maybe<*>,T> {
    data class Just<T>(val value: T) : Maybe<T>()

    @ConsistentCopyVisibility
    data class Nothing<T> private constructor(val unit: Unit) : Maybe<T>() {
        private constructor() : this(Unit)

        override fun toString() = "Nothing"

        companion object {
            private val nothingInstance = Nothing<Any>()
            @Suppress("UNCHECKED_CAST")
            operator fun <T> invoke(): Nothing<T> = nothingInstance as Nothing<T>
        }
    }

    override fun <B> flatMap(f: (T) -> Monad<Maybe<*>, B>): Monad<Maybe<*>, B> = when (this) {
        is Just -> f(value)
        is Nothing -> Nothing()
    }


    override val monad = Companion
    companion object: Monad.MonadCompanion<Maybe<*>>  {
        override fun <A> `return`(x: A): Monad<Maybe<*>, A> = Just(x)
    }
}

fun <T> Hk<Maybe<*>, T>.fix(): Maybe<T> = this as Maybe<T>
