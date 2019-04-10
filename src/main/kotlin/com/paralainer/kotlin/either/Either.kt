package com.paralainer.kotlin.either

sealed class Either<out A, out B> {

    companion object {
        fun <A, B> left(value: A): Either<A, B> = Left(value)
        fun <A, B> right(value: B): Either<A, B> = Right(value)
    }

    class Left<out A> internal constructor(val value: A) : Either<A, Nothing>() {
        override val isLeft: Boolean = true
        override val isRight: Boolean = false

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Left<*>

            if (value != other.value) return false

            return true
        }

        override fun hashCode(): Int {
            return value?.hashCode() ?: 0
        }

    }

    class Right<out B> internal constructor(val value: B) : Either<Nothing, B>() {
        override val isLeft: Boolean = false
        override val isRight: Boolean = true

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Left<*>

            if (value != other.value) return false

            return true
        }

        override fun hashCode(): Int {
            return value?.hashCode() ?: 0
        }
    }

    abstract val isLeft: Boolean
    abstract val isRight: Boolean

    fun swap(): Either<B, A> = fold({ Right(it) }, { Left(it) })

    fun toOption(): B? = fold({ null }, { it })

    inline fun <C> fold(ifLeft: (A) -> C, ifRight: (B) -> C): C = when (this) {
        is Left -> ifLeft(this.value)
        is Right -> ifRight(this.value)
    }

    fun <C> map(f: (B) -> C): Either<A, C> = when (this) {
        is Left -> this
        is Right -> Right(f(this.value))
    }

    fun <C> mapLeft(f: (A) -> C): Either<C, B> = when (this) {
        is Left -> Left(f(this.value))
        is Right -> this
    }
}

fun <A, B, C> Either<A, B>.flatMap(f: (B) -> Either<A, C>): Either<A, C> = when (this) {
    is Either.Left ->  this
    is Either.Right -> f(this.value)
}