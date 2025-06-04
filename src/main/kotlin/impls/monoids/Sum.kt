package net.leloubil.impls.monoids

import net.leloubil.typeclasses.WrappingMonoid

data class Sum(val value: Int) : WrappingMonoid<Sum, Int> {

    override fun mappend(y: Sum) = Sum(this.value + y.value)

    override val wrappingMonoid = Companion

    companion object : WrappingMonoid.WrappingMonoidCompanion<Sum,Int> {
        override fun mempty() = Sum(0)
        override fun mwrap(x: Int) = Sum(x)
    }

    override fun toString(): String {
        return "Sum($value)"
    }
}
