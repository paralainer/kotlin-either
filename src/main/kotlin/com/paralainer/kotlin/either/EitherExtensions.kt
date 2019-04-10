package com.paralainer.kotlin.either

fun <A, B> A.left(): Either<A, B> = Either.left(this)
fun <A, B> B.right(): Either<A, B> = Either.right(this)