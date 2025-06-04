package net.leloubil.impls.monoids

import net.leloubil.then
import net.leloubil.typeclasses.Foldable
import net.leloubil.typeclasses.Monoid

class Endo<T>(val appEndo: (T) -> T) : Monoid<Endo<T>> {

    override val monoid = Endo<T>()

    override fun mappend(y: Endo<T>): Endo<T> = Endo { x: T -> this.appEndo(y.appEndo(x)) }

    class EndoCompanion<T> : Monoid.MonoidCompanion<Endo<T>> {

        override fun mempty(): Endo<T> = Endo { it }
    }

    companion object {
        operator fun <A> invoke(): EndoCompanion<A> = EndoCompanion()
    }

    override fun toString(): String {
        return "Endo($appEndo)"
    }
}






