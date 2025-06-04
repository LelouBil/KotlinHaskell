# Haskell in Kotlin

Do you want to write haskell-like code in kotlin? No? Well you can do it anyway!

[![](https://jitpack.io/v/LelouBil/KotlinMonads.svg)](https://jitpack.io/#LelouBil/KotlinMonads)

<!-- TOC -->
* [Haskell in Kotlin](#haskell-in-kotlin)
* [Examples](#examples)
  * [Monoids and Foldables](#monoids-and-foldables)
  * [Monads and do notation](#monads-and-do-notation)
  * [IO Monad !](#io-monad-)
  * [And more!](#and-more)
* [Installation](#installation)
<!-- TOC -->

# Examples

## Monoids and Foldables

```kotlin
val xs = HList(1, 2, 3)
val res = xs.foldr({ a, b -> a + b }, 0)
assert(res == 6) { "Expected 6, but got $res" }

val o = HList(1, 2, 3, 4, 5, 6)
val u = o.fold(Sum).value
assert(u == 21) { "Expected 21, but got $u" }
```

## Monads and do notation

```kotlin
val s = Maybe.Just(1)
val r = s.flatMap { a -> Maybe.Just(a + 1) }
assert(r == Maybe.Just(2)) { "Expected Just(2), but got $r" }

val listDo = doReturning(HList.Companion) {
    val c: Int = HList(0, 1, 2, 3, 4).bind()
    val b: Int = HList(0, 1, 2, 3, 4).bind()
    val bind = HList(c, b).bind()
    `return`(bind)
}.fix()
assertTrue(listDo.inner.size == 50, "Result list should contain 50 combinations of c and b")
```

## IO Monad !

```kotlin
val complexIO = doReturning(IO.Companion) {
    putStrLn("Enter your name:").bind()
    val name = getLine.bind()
    putStrLn("Hello, $name!").bind()
    `return`(name.length)
}.fix()
complexIO.runIO(object : IORunner {
    fun getChar(): Char {
        return System.`in`.read().toChar()
    }

    fun putChar(c: Char) {
        print(c)
    }
})
```

## And more!

Current list of monads (not with all the functions)
- `Maybe`
- `Either`
- `IO`
- `HList` (haskell's list)
- `State`

Current list of typeclasses
- `Functor`
- `Applicative`
- `Monad`
- `Semigroup`
- `Monoid`
- `Foldable`

Current monoids
- `Sum` (sum of numbers)
- `Endo` (identity function)


This is mostly a fun project made for absolutely no reason, but it helps me learn more about Haskell's typeclasses and
weird Kotlin tricks to make them work (that could break any time).

Thanks to this repository for the very hacky tricks with suspend functions, that allow do notation
and also for the trick of using a star-projection as a witness for higher-kinded types:

https://github.com/h0tk3y/kotlin-monads

Also most of the code was created using either the GHC source implementation for functions (available when clicking on "
#source" on hackage) or the Haskell wikibook https://en.wikibooks.org/wiki/Haskell/

# Installation

Available on Jitpack (https://jitpack.io/LelouBil/KotlinMonads)

Add it in your settings.gradle.kts at the end of repositories:

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

Add the dependency in your build.gradle.kts file:
```kotlin
dependencies {
        implementation("com.github.LelouBil:KotlinMonads:0.1.0")
}
```



