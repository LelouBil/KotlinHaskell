package net.leloubil.impls.monads

import net.leloubil.hk.Hk
import net.leloubil.typeclasses.Monad

sealed class Either<L, R> : Monad<Either<L, *>, R> {
    data class Left<L, R>(val value: L) : Either<L, R>() {
        override fun <B> flatMap(f: (R) -> Monad<Either<L, *>, B>): Monad<Either<L, *>, B> {
            return Left(value)
        }
    }

    data class Right<L, R>(val value: R) : Either<L, R>() {
        override fun <B> flatMap(f: (R) -> Monad<Either<L, *>, B>): Monad<Either<L, *>, B> {
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

    override val monad: Monad.MonadCompanion<Either<L, *>> = companion()

    class Returner<L> : Monad.MonadCompanion<Either<L, *>> {
        override fun <A> `return`(x: A): Monad<Either<L, *>, A> = Right(x)
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
fun <L, R> Hk<Either<L, *>, R>.fix(): Either<L, R> =
    this as Either<L, R>
