package net.leloubil

import net.leloubil.impls.monads.HList
import net.leloubil.impls.monoids.Sum
import net.leloubil.typeclasses.mconcat
import kotlin.test.Test

class MonoidTests {

    @Test
    fun sumTest() {
        val list = listOf(1, 2, 3)
        val l = Sum.mconcat(list)
        assert(l == Sum(6)) { "Expected Sum(6), but got $l" }
    }

    @Test
    fun hListTest(){
        val hList = HList(1, 2, 3)
        val hList2 = HList(4, 5, 6)
        val par = listOf(hList, hList2)
        val result = HList.monoid<Int>().mconcat(par)
        val result2 = hList + hList2
        assert(result == HList(1, 2, 3, 4, 5, 6)) { "Expected HList(1, 2, 3, 4, 5, 6), but got $result" }
        assert(result2 == HList(1, 2, 3, 4, 5, 6)) { "Expected HList(1, 2, 3, 4, 5, 6), but got $result2" }
        assert(result == result2) { "Expected both results to be equal, but they are not." }
    }
}
