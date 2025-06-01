package net.leloubil.hk

interface Witness

interface Hk<out W: Witness,out T> : Witness {
}
