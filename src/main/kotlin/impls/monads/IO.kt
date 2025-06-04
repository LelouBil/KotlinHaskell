package net.leloubil.impls.monads

import net.leloubil.hk.Hk
import net.leloubil.hk.Witness
import net.leloubil.typeclasses.Monad

sealed class IO<T> : Monad<IO<*>, T> {

    private data class Pure<A>(val value: A) : IO<A>() {
        override fun <B> flatMap(f: (A) -> Monad<IO<*>, B>): Monad<IO<*>, B> = f(value)
    }

    private data class Continuation<A>(val next: IORunner.() -> IO<A>) : IO<A>() {
        override fun <B> flatMap(f: (A) -> Monad<IO<*>, B>): Monad<IO<*>, B> = Continuation {
            next().flatMap(f).fix()
        }
    }

    // run function that returns the actual result
    fun runIO(runner: IORunner): T {
        return when (this@IO) {
            is Pure -> value
            is Continuation<T> -> {
                val next = this.next(runner)
                next.runIO(runner)
            }
        }
    }

    override val monad = Companion

    companion object : Monad.MonadCompanion<IO<*>> {
        override fun <A> `return`(x: A): Monad<IO<*>, A> = Pure(x)

        // getChar, putChar, putStr, putStrLn, getLine
        fun getChar(): IO<Char> = Continuation { Pure(getChar()) }
        fun putChar(c: Char): IO<Unit> = Continuation { Pure(putChar(c)) }
        fun putStr(s: String): IO<Unit> = s.fold<IO<Unit>>(Pure(Unit)) { acc, c ->
            acc.flatMap { putChar(c) }.fix()
        }

        fun putStrLn(s: String): IO<Unit> = putStr(s) then putChar('\n') fix Unit
        private fun _getLine(acc: String = ""): Monad<IO<*>, String> {
            return getChar() flatMap { char ->
                when (char) {
                    '\n' -> Pure(acc)
                    else -> _getLine(acc + char)
                }
            }
        }

        val getLine: IO<String> = _getLine().fix()
    }
}

interface IORunner {
    fun getChar(): Char

    fun putChar(c: Char)
}

fun <T> Hk<IO<*>, T>.fix(): IO<T> = this as IO<T>
infix fun <T> Hk<IO<*>, T>.fix(unit: Unit): IO<T> = this as IO<T>
