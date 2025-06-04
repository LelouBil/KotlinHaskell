package net.leloubil.impls.monoids

import net.leloubil.typeclasses.Monoid

data class Sum(private val value: Int) : Monoid<Sum,Int> {


    override fun mappend(y: Sum) = Sum(this.value + y.value)

    override val monoid: Monoid.MonoidCompanion<Sum,Int> = Companion

    companion object : Monoid.MonoidCompanion<Sum,Int> {
        override fun mempty() = Sum(0)
        override fun mconvert(x: Int) = Sum(x)
    }

    override fun toString(): String {
        return "Sum($value)"
    }
}
