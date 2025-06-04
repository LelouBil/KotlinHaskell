package net.leloubil.impls.monads

import net.leloubil.hk.Hk
import net.leloubil.hk.Witness
import net.leloubil.typeclasses.Monad

sealed class Either<L, R> : Monad<Hk<Either.W, L>, R> {
    object W : Witness
    data class Left<L, R>(val value: L) : Either<L, R>() {
        override fun <B> flatMap(f: (R) -> Monad<Hk<W, L>, B>): Monad<Hk<W, L>, B> {
            return Left(value)
        }
    }

    data class Right<L, R>(val value: R) : Either<L, R>() {
        override fun <B> flatMap(f: (R) -> Monad<Hk<W, L>, B>): Monad<Hk<W, L>, B> {
            return f(value)
        }
    }

    fun <T> either(
        ifLeft: (L) -> T,
        ifRight: (R) -> T
    ): T = when (this) {
        is Left -> ifLeft(value)
        is Right -> ifRight(value)
    }

    fun isLeft(): Boolean = this is Left<L, R>
    fun isRight(): Boolean = this is Right<L, R>

    fun fromLeft(default: L): L = either({ it }, { default })
    fun fromRight(default: R): R = either({ default }, { it })

    override val monad: Monad.MonadCompanion<Hk<W, L>> = companion()

    class Returner<L> : Monad.MonadCompanion<Hk<W, L>> {
        override fun <A> `return`(x: A): Monad<Hk<W, L>, A> = Right(x)
    }

    companion object {
        fun <L> companion(): Returner<L> = Returner()
    }
}


fun <L, R> List<Either<L, R>>.lefts(): List<L> =
    filterIsInstance<Either.Left<L, R>>().map { it.value }

fun <L, R> List<Either<L, R>>.rights(): List<R> =
    filterIsInstance<Either.Right<L, R>>().map { it.value }

@Suppress("UNCHECKED_CAST")
fun <L, R> Hk<Hk<Either.W, L>, R>.fix(): Either<L, R> =
    this as Either<L, R>
