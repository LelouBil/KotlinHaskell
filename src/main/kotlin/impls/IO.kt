package net.leloubil.impls

import net.leloubil.hk.Hk
import net.leloubil.hk.Witness
import net.leloubil.typeclasses.Monad

sealed class IO<T> : Monad<IO.W, T> {
    object W : Witness

    private data class Pure<A>(val value: A) : IO<A>() {
        override fun <B> flatMap(f: (A) -> Monad<W, B>): Monad<W, B> = f(value)
    }

    private data class Suspend<A>(val thunk: IORunner.() -> IO<A>) : IO<A>() {
        override fun <B> flatMap(f: (A) -> Monad<W, B>): Monad<W, B> = Suspend {
            thunk().flatMap(f).fix()
        }
    }

    // run function that returns the actual result
    fun runIO(runner: IORunner): T {
        return when (this@IO) {
            is Pure -> value
            is Suspend<T> -> {
                val next = this.thunk(runner)
                next.runIO(runner)
            }
        }
    }

    override val monad = Companion

    companion object : Monad.MonadCompanion<W> {
        override fun <A> `return`(x: A): Monad<W, A> = Pure(x)

        // getChar, putChar, putStr, putStrLn, getLine
        fun getChar(): IO<Char> = Suspend { Pure(getChar()) }
        fun putChar(c: Char): IO<Unit> = Suspend { Pure(putChar(c)) }
        fun putStr(s: String): IO<Unit> = s.fold<IO<Unit>>(Pure(Unit)) { acc, c ->
            acc.flatMap { putChar(c) }.fix()
        }

        fun putStrLn(s: String): IO<Unit> = putStr(s) then putChar('\n') fix Unit
        private fun _getLine(acc: String = ""): Monad<W, String> {
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

fun <T> Hk<IO.W, T>.fix(): IO<T> = this as IO<T>
infix fun <T> Hk<IO.W, T>.fix(unit: Unit): IO<T> = this as IO<T>
