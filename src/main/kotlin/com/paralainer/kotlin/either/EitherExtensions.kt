package com.paralainer.kotlin.either

fun <A, B> A.left(): Either<A, B> = Either.Left(this)
fun <A, B> B.right(): Either<A, B> = Either.Right(this)