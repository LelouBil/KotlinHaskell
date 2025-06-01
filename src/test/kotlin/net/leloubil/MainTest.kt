package net.leloubil

import net.leloubil.impls.IO
import net.leloubil.impls.IO.Companion.getChar
import net.leloubil.impls.IO.Companion.getLine
import net.leloubil.impls.IO.Companion.putChar
import net.leloubil.impls.IO.Companion.putStr
import net.leloubil.impls.IO.Companion.putStrLn
import net.leloubil.impls.IORunner
import net.leloubil.typeclasses.*
import net.leloubil.impls.ListMonad
import net.leloubil.impls.Maybe
import net.leloubil.impls.State
import net.leloubil.impls.fix
import kotlin.test.Test
import kotlin.test.assertTrue

class MainTest {

    @Test
    fun testList() {
        println("List Monad Example")
        println("==================================================")

        // Functor examples
        println("\nFunctor Examples:")
        val listW = ListMonad(listOf(1, 2, 3))

        // Create a simple function to double a number
        val double: (Int) -> Int = { it * 2 }

        // Use the functor instance to map over the list
        val mappedList = listW.fmap(double)

        println("Original list: ${listW.inner}")
        println("Mapped list (doubled): ${(mappedList as ListMonad<Int>).inner}")

        // Assert that the mapped list contains the expected values
        assertTrue((mappedList.inner == listOf(2, 4, 6)), "Mapped list should contain doubled values")

        // Using lift to transform a function
        val addOne: (Int) -> Int = { it + 1 }
        val liftedAddOne = addOne.lift<ListMonad.W, _, _>()
        val resultLift = liftedAddOne(listW)
        println("Lifted function result (add one): ${(resultLift as ListMonad<Int>).inner}")

        // Assert that the lifted function produces the expected result
        assertTrue((resultLift.inner == listOf(2, 3, 4)), "Lifted function should add one to each value")

        // Applicative examples
        println("\nApplicative Examples:")
        val listApp = ListMonad(listOf(1, 2, 3))

        // Create a pure value
        val pureValue = ListMonad.pure(42)
        println("Pure value: ${(pureValue as ListMonad<Int>).inner}")

        // Assert that pure value contains the expected value
        assertTrue((pureValue.inner == listOf(42)), "Pure value should contain only the provided value")

        // Create a list of functions
        val addTen: (Int) -> Int = { it + 10 }
        val multiplyByTwo: (Int) -> Int = { it * 2 }
        val functionList = ListMonad(listOf(addTen, multiplyByTwo))

        // Apply the functions to the values
        val apResult = listApp.apl(functionList).fix()

        println("Apply functions to values: ${apResult.inner}")

        // Assert that applying functions produces the expected result
        assertTrue(apResult.inner.containsAll(listOf(11, 12, 13, 2, 4, 6)), "Applied functions should produce expected results")

        // Using product to combine two applicatives
        val list1 = ListMonad(listOf(1, 2))
        val list2 = ListMonad(listOf("A", "B"))
        val product = list1.product(list2).fix()

        println("Product of two lists: ${product.inner}")

        // Assert that product contains all expected pairs
        assertTrue(product.inner.containsAll(listOf(Pair(1, "A"), Pair(1, "B"), Pair(2, "A"), Pair(2, "B"))),
            "Product should contain all combinations of elements")

        // Monad examples
        println("\nMonad Examples:")
        val listMonad = ListMonad(listOf(1, 2, 3))

        // Create a function that duplicates each value
        val duplicate: (Int) -> ListMonad<Int> = { n -> ListMonad(listOf(n, n)) }

        // Use flatMap to apply the function
        val flatMappedList = listMonad.flatMap { n -> duplicate(n) }.fix()

        println("Original list: ${listMonad.inner}")
        println("FlatMapped list (duplicated): ${flatMappedList.inner}")

        // Assert that flatMap produces the expected result
        assertTrue(flatMappedList.inner == listOf(1, 1, 2, 2, 3, 3), "FlatMap should duplicate each value")

        // Create a nested list and flatten it
        val nestedList = ListMonad(
            listOf(
                ListMonad(listOf(1, 2)), ListMonad(listOf(3, 4))
            )
        )

        val flattened = nestedList.flatten()

        println("Nested list flattened: ${(flattened as ListMonad<Int>).inner}")

        // Assert that flattened list contains all expected values
        assertTrue(flattened.inner == listOf(1, 2, 3, 4), "Flattened list should contain all values from nested lists")

        // Composition example
        val addOneM: (Int) -> ListMonad<Int> = { n -> ListMonad(listOf(n + 1)) }
        val doubleM: (Int) -> ListMonad<Int> = { n -> ListMonad(listOf(n * 2)) }

        val composedResult = addOneM(5).flatMap(doubleM).fix()
        println("Composed functions result (5+1)*2: ${composedResult.inner}")

        // Assert that composition produces the expected result
        assertTrue(composedResult.inner == listOf(12), "Composed functions should produce (5+1)*2 = 12")
    }

    @Test
    fun testMaybe() {
        println("Maybe Monad Example")
        println("====================")

        // Functor example
        val maybeValue = Maybe.Just(5)
        val maybeMapped = maybeValue.fmap { it * 2 }.fix()
        println("Maybe mapped value: $maybeMapped")

        // Assert that the mapped value is correct
        assertTrue(maybeMapped is Maybe.Just && maybeMapped.value == 10, "Mapped Maybe should contain doubled value")

        // Applicative example
        val maybeFunc = Maybe.Just({ x: Int -> x + 10 })
        val maybeApplied = maybeValue.apl(maybeFunc).fix()
        println("Maybe applied value: $maybeApplied")

        // Assert that the applied value is correct
        assertTrue(maybeApplied is Maybe.Just && maybeApplied.value == 15, "Applied Maybe should contain value + 10")

        // Monad example
        val flatMapped = maybeValue.flatMap { Maybe.Just(it + 1) }.fix()
        println("Maybe flatMapped value: $flatMapped")

        // Assert that the flatMapped value is correct
        assertTrue(flatMapped is Maybe.Just && flatMapped.value == 6, "FlatMapped Maybe should contain value + 1")

        // Nothing examples
        val nothingValue = Maybe.Nothing<Int>()
        val nothingMapped = nothingValue.fmap { it * 2 }.fix()
        println("Nothing mapped value: $nothingMapped")

        // Assert that mapping Nothing results in Nothing
        assertTrue(nothingMapped is Maybe.Nothing, "Mapping Nothing should result in Nothing")

        val nothingApplied = nothingValue.apl(maybeFunc).fix()
        println("Nothing applied value: $nothingApplied")

        // Assert that applying to Nothing results in Nothing
        assertTrue(nothingApplied is Maybe.Nothing, "Applying to Nothing should result in Nothing")

        val nothingFlatMapped = nothingValue.flatMap { Maybe.Just(it + 1) }.fix()
        println("Nothing flatMapped value: $nothingFlatMapped")

        // Assert that flatMapping Nothing results in Nothing
        assertTrue(nothingFlatMapped is Maybe.Nothing, "FlatMapping Nothing should result in Nothing")

        // Nested Maybe example
        val nestedMaybe = Maybe.Just(Maybe.Just(10))
        val flattenedNested = nestedMaybe.flatten().fix()
        println("Flattened nested Maybe: $flattenedNested")

        // Assert that flattening nested Maybe works correctly
        assertTrue(flattenedNested is Maybe.Just && flattenedNested.value == 10, "Flattened nested Maybe should contain inner value")

        // Composing functions with Maybe
        val addOne: (Int) -> Maybe<Int> = { Maybe.Just(it + 1) }
        val double: (Int) -> Maybe<Int> = { Maybe.Just(it * 2) }
        val composed = addOne(5).flatMap(double).fix()
        println("Composed Maybe result (5+1)*2: $composed")

        // Assert that composition works correctly
        assertTrue(composed is Maybe.Just && composed.value == 12, "Composed functions should produce (5+1)*2 = 12")

        // Using Maybe with ListW
        val listMonad = ListMonad(listOf(1, 2, 3))
        val maybeListMapped: ListMonad<Maybe<Int>> = listMonad.fmap { Maybe.Just(it * 2) }.fix()
        println("ListW with Maybe mapped: $maybeListMapped")

        // Assert that mapping list to Maybe works correctly
        assertTrue(maybeListMapped.inner.all { it is Maybe.Just }, "All elements should be Just instances")
        assertTrue(maybeListMapped.inner.map { (it as Maybe.Just<Int>).value } == listOf(2, 4, 6),
            "Mapped values should be doubled")

        // Using Maybe with ListW's product
        val list1 = ListMonad(listOf(1, 2))
        val list2 = ListMonad(listOf("A", "B"))
        val maybeProduct = list1.product(list2).fix()
        println("Product of ListW with Maybe: ${maybeProduct.inner}")

        // Assert that product contains all expected pairs
        assertTrue(maybeProduct.inner.containsAll(listOf(Pair(1, "A"), Pair(1, "B"), Pair(2, "A"), Pair(2, "B"))),
            "Product should contain all combinations of elements")
    }

    @Test
    fun testIO() {
        // Create a mock IORunner for testing
        val mockRunner = object : IORunner {
            val output = StringBuilder()
            val inputQueue = mutableListOf("test input")
            var currentInputIndex = 0

            override fun getChar(): Char {
                return if (currentInputIndex < inputQueue[0].length) {
                    inputQueue[0][currentInputIndex++]
                } else {
                    currentInputIndex = 0
                    '\n'
                }
            }

            override fun putChar(c: Char) {
                output.append(c)
            }
        }

        val otherIo: IO<Unit> =
            IO.`return`(Unit).then(putStrLn("Salut")).then(getLine).flatMap { putStrLn("Salut: $it") }.then(putStrLn("truc"))
                .fix()

        // Execute the IO operation with our mock runner
        otherIo.runIO(mockRunner)

        // Verify the output contains the expected strings
        val expectedOutput = "Salut\nSalut: test input\ntruc\n"
        assertTrue(mockRunner.output.toString() == expectedOutput,
            "IO execution should produce the expected output")

        // Create a simple IO for testing
        val simpleIO = IO.`return`(42)
        assertTrue(simpleIO is IO<Int>, "Should create a valid IO instance with the correct type")

        // Execute the simple IO and verify the result
        val simpleResult = simpleIO.runIO(mockRunner)
        assertTrue(simpleResult == 42, "Running simple IO should return the value")

        // Test that we can chain operations
        val chainedIO = simpleIO.then(IO.`return`("test")).fix()

        // Execute the chained IO and verify the result
        val chainedResult = chainedIO.runIO(mockRunner)
        assertTrue(chainedResult == "test", "Running chained IO should return the final value")

        // Test a more complex IO operation with do-notation
        val complexIO = doReturning(IO.Companion) {
            putStrLn("Enter your name:").bind()
            val name = getLine.bind()
            putStrLn("Hello, $name!").bind()
            `return`(name.length)
        }.fix()

        // Reset the mock runner's output
        mockRunner.output.clear()

        // Execute the complex IO and verify the result
        val nameLength = complexIO.runIO(mockRunner)

        // Verify the output and result
        val complexExpectedOutput = "Enter your name:\nHello, test input!\n"
        assertTrue(mockRunner.output.toString() == complexExpectedOutput,
            "Complex IO execution should produce the expected output")
        assertTrue(nameLength == "test input".length,
            "Complex IO should return the length of the input string")
    }

    @Test
    fun testListDo() {
        val listDo = doReturning(ListMonad.Companion) {
            val c: Int = ListMonad(0, 1, 2, 3, 4).bind()
            val b: Int = ListMonad(0, 1, 2, 3, 4).bind()
            val bind = ListMonad(listOf(c, b)).bind()
            `return`(bind)
        }.fix()

        // Verify the structure of the result
        assertTrue(listDo.inner.isNotEmpty(), "Result list should not be empty")

        // The result should contain 5 * 5 * 2 = 50 combinations
        assertTrue(listDo.inner.size == 50, "Result list should contain 50 combinations of c and b")
    }

    sealed interface TurnstileState {
        data object Locked : TurnstileState
        data object Unlocked : TurnstileState
    }

    sealed interface TurnstileOutput {
        data object Thank : TurnstileOutput
        data object Open : TurnstileOutput
        data object Tut : TurnstileOutput
    }

    fun coin(@Suppress("unused") s: TurnstileState) = TurnstileOutput.Thank to TurnstileState.Unlocked
    fun push(s: TurnstileState) = when (s) {
        TurnstileState.Locked -> TurnstileOutput.Tut to TurnstileState.Locked
        TurnstileState.Unlocked -> TurnstileOutput.Open to TurnstileState.Locked
    }

    @Test
    fun testTurnstileState() {
        val coinS = State<TurnstileState, TurnstileOutput>(::coin)
        val pushS = State<TurnstileState, TurnstileOutput>(::push)
        val mondayS = State.returner<TurnstileState>().sequence(listOf(coinS, pushS, pushS, coinS, pushS)).fix()

        val pushSDo = doReturning(State.returner()) {
            with(State.returner<TurnstileState>()) {
                val s = get().bind()
                put(TurnstileState.Locked).bind()
                when (s) {
                    TurnstileState.Locked -> TurnstileOutput.Tut
                    TurnstileState.Unlocked -> TurnstileOutput.Open
                }.let(::`return`)
            }
        }.fix()

        val testTurnstile = doReturning(State.returner()) {
            with(State.returner()) {
                put(TurnstileState.Locked).bind()
                val check1 = pushS.bind()
                put(TurnstileState.Unlocked).bind()
                val check2 = pushSDo.bind()
                `return`(check1 == TurnstileOutput.Tut && check2 == TurnstileOutput.Open)
            }
        }.fix()

        val testTurnstileFlatMap = with(State.returner<TurnstileState>()) {
            put(TurnstileState.Locked).flatMap { _ ->
                pushS.flatMap { check1 ->
                    put(TurnstileState.Unlocked).flatMap {
                        pushS.flatMap { check2 ->
                            `return`(check1 == TurnstileOutput.Tut && check2 == TurnstileOutput.Open)
                        }
                    }
                }
            }
        }.fix()

        // Execute the tests and verify the results
        assertTrue(testTurnstile.evalState(TurnstileState.Locked))
        assertTrue(testTurnstileFlatMap.evalState(TurnstileState.Locked))
        val mondaySResult = mondayS.execState(TurnstileState.Locked)
        assertTrue(mondaySResult== TurnstileState.Locked, "Final state should be Locked")
    }
}
