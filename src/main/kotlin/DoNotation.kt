package net.leloubil

import net.leloubil.typeclasses.Monad
import java.io.Serializable
import kotlin.coroutines.*
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn


fun <W : Monad<W, *>, T, R, MC : Monad.MonadCompanion<W>> Monad<W, T>.bindDo(
    companion: MC,
    c: suspend context(MC) DoController<W>.(T) -> Monad<W, R>
): Monad<W, R> =
    flatMap { t ->
        val controller = DoController(companion)
        val f: suspend DoController<W>.() -> Monad<W, R> = { with(companion) { c(t) } }
        f.startCoroutine(controller, controller)
        val returnedMonad = controller.returnedMonad
        returnedMonad as Monad<W, R>
    }

fun <W : Monad<W, *>, R, MC : Monad.MonadCompanion<W>> doReturning(
    aReturn: MC,
    c: suspend context(MC) DoController<W>.() -> Monad<W, R>
): Monad<W, R> {
    val controller = DoController(aReturn)
    val f: suspend DoController<W>.() -> Monad<W,R> = { with(aReturn) { c() } }
    f.startCoroutine(controller, controller)
    return controller.returnedMonad as Monad<W, R>
}


@RestrictsSuspension
class DoController<W : Monad<W, *>>(private val returning: Monad.MonadCompanion<W>) :
    Serializable, Monad.MonadCompanion<W> by returning, Continuation<Monad<W, *>> {

    override val context = EmptyCoroutineContext

    override fun resumeWith(result: Result<Monad<W, *>>) {
        returnedMonad = result.getOrThrow()
    }

    internal lateinit var returnedMonad: Monad<W, *>

    suspend fun <T> Monad<W, T>.bind(): T = suspendCoroutineUninterceptedOrReturn { c ->
        val stackLabels = c.stackLabels
        returnedMonad = flatMap { x ->
            c.stackLabels = stackLabels
            c.resume(x)
            returnedMonad
        }
        COROUTINE_SUSPENDED
    }
}

//private var <T> Continuation<T>.completion: Continuation<*>?
//    get() = completionField(this::class).get(this) as Continuation<*>
//    set(value) = completionField(this::class).set(this@completion, value)

internal var <T> Continuation<T>.stackLabels: List<Any>
    get() = this.javaClass.declaredFields.map { it.isAccessible = true; it.get(this) }
    set(value) {
        this.javaClass.declaredFields.zip(value).forEach { (field, value) ->
            field.isAccessible = true
            field.set(this, value)
        }
    }

