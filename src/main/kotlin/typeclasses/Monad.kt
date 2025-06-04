package net.leloubil.typeclasses

import net.leloubil.doReturning

interface Monad<out W : Monad<W, *>, A> : Applicative<W, A> {
    val monad: MonadCompanion<W>
    override val applicative get() = monad

    override fun <B> fmap(f: (A) -> B): Functor<W, B> = with(monad) { liftM(f)(this@Monad) }

    infix fun <B> flatMap(f: (A) -> Monad<@UnsafeVariance W, B>): Monad<W, B>

    infix fun <B> then(m: Monad<@UnsafeVariance W, B>): Monad<W, B> = flatMap { m }

    infix fun <B> ap(m: Monad<@UnsafeVariance W, (A) -> B>): Monad<W, B> = doReturning(monad) {
        val x1 = this@Monad.bind()
        val x2 = m.bind()
        `return`(x2(x1))
    }

    override fun <R> apl(ff: Applicative<@UnsafeVariance W, (A) -> R>): Applicative<W, R> = ap(ff as Monad<W, (A) -> R>)



    interface MonadCompanion<out W : Monad<W,*>> : Applicative.ApplicativeCompanion<W> {

        override fun <A> pure(a: A): Applicative<W, A> = `return`(a)

        fun <A> `return`(x: A): Monad<W, A>
        fun <A, B> liftM(f: (A) -> B): (Monad<@UnsafeVariance W, A>) -> Monad<W, B> = { m1 ->
            doReturning(this@MonadCompanion) {
                val x1 = m1.bind()
                `return`(f(x1))
            }
        }

        fun <A, B> mapM(f: (A) -> Monad<@UnsafeVariance W, B>, ls: List<A>): Monad<W, List<B>> {
            fun k(a: A, r: Monad<W, List<B>>) = doReturning(this@MonadCompanion) {
                val x = f(a).bind()
                val xs = r.bind()
                `return`(listOf(x) + xs)
            }
            return ls.foldRight<A, Monad<W, List<B>>>(`return`(listOf()), ::k)
        }

        fun <T> sequence(ls: List<Monad<@UnsafeVariance W, T>>): Monad<W, List<T>> = mapM({ it }, ls)
    }

}

fun <W : Monad<W,*>, T> Monad<W, out Monad<W, T>>.join() = flatMap { it }

inline fun <W : Monad<W,*>, A, B> Monad<W, A>.product(mb: Monad<W, B>): Monad<W, Pair<A, B>> =
    this.flatMap { a -> mb.fmap { b -> Pair(a, b) } as Monad<W, Pair<A, B>> }

// sequence



