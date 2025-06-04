# Haskell Monads in Kotlin

[![](https://jitpack.io/v/LelouBil/KotlinMonads.svg)](https://jitpack.io/#LelouBil/KotlinMonads)



Small exercise in Kotlin tinkering to implement a few Haskell monads.

Thanks to this repository for the very hacky tricks with suspend functions, that allow do notation
and also for the trick of using a star-projection as a witness for higher-kinded types:
:
https://github.com/h0tk3y/kotlin-monads

# Installation

Available on Jitpack (https://jitpack.io/LelouBil/KotlinMonads)

Add it in your settings.gradle.kts at the end of repositories:

	dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url = uri("https://jitpack.io") }
		}
	}

Add the dependency

	dependencies {
	        implementation("com.github.LelouBil:KotlinMonads:0.1.0")
	}



