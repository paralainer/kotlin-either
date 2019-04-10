package com.paralainer.kotlin.either

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.lang.Exception
import java.lang.RuntimeException

internal class EitherTest {
    data class TestError(val value: String)
    data class TestResult(val value: String)

    private fun left(value: String): Either<TestError, TestResult> = Either.left(TestError(value))
    private fun right(value: String): Either<TestError, TestResult> = Either.right(TestResult(value))

    @Test
    fun `left produces Left instance`() {
        val value = "test"
        val leftValue = left(value)
        assertEquals((leftValue as Either.Left<TestError>).value.value, value)
    }

    @Test
    fun `right produces Right instance`() {
        val value = "test"
        val rightValue = right(value)
        assertEquals((rightValue as Either.Right<TestResult>).value.value, value)
    }

    @Test
    fun `swap from Left produces Right`() {
        val value = "test"
        val leftValue = left(value)
        val swappedValue = leftValue.swap()
        assertTrue(swappedValue.isRight)
        assertEquals(
            (swappedValue as Either.Right<TestError>).value.value,
            value
        )
    }

    @Test
    fun `swap from Right produces Left`() {
        val value = "test"
        val rightValue = right(value)
        val swappedValue = rightValue.swap()
        assertTrue(swappedValue.isLeft)
        assertEquals(
            (swappedValue as Either.Left<TestResult>).value.value,
            value
        )
    }

    @Test
    fun `toOptional returns Right value`() {
        val value = "test"
        val rightValue = right(value)
        assertEquals(
            rightValue.toOption()?.value,
            value
        )
    }

    @Test
    fun `toOptional returns null for Left`() {
        val value = "test"
        val leftValue = left(value)
        assertNull(leftValue.toOption())
    }

    @Test
    fun `fold acts on correct Left value`() {
        val value = "test"
        val leftValue = left(value)
        val result = leftValue.fold({
            assertEquals(it.value, value)
            value + "abc"
        }, {
            throw Exception("this should not be called")
        })

        assertEquals(result, value + "abc")
    }

    @Test
    fun `fold acts on correct Right value`() {
        val value = "test"
        val rightValue = right(value)
        val result = rightValue.fold({
            throw Exception("this should not be called")
        }, {
            assertEquals(it.value, value)
            value + "abc"
        })

        assertEquals(result, value + "abc")
    }

    @Test
    fun `map should act on a Right value`() {
        val value = "test"
        val rightValue = right(value)
        val mappedValue = rightValue.map { it.copy(value = it.value + "abc") }
        assertTrue(mappedValue.isRight)
        assertEquals(
            (mappedValue as Either.Right<TestResult>).value.value,
            value + "abc"
        )
    }


    @Test
    fun `map should not act on a Left value`() {
        val value = "test"
        val leftValue = left(value)
        val mappedValue = leftValue.map { throw Exception("this should not be called") }
        assertTrue(mappedValue.isLeft)
        assertEquals(
            (mappedValue as Either.Left<TestError>).value.value,
            value
        )
    }

    @Test
    fun `mapLeft should act on a Left value`() {
        val value = "test"
        val leftValue = left(value)
        val mappedValue = leftValue.mapLeft { TestError(it.value + "abc") }
        assertTrue(mappedValue.isLeft)
        assertEquals(
            (mappedValue as Either.Left<TestError>).value.value,
            value + "abc"
        )
    }

    @Test
    fun `mapLeft should not act on a Right value`() {
        val value = "test"
        val rightValue = right(value)
        val mappedValue = rightValue.mapLeft { throw Exception("this should not be called") }
        assertTrue(mappedValue.isRight)
        assertEquals(
            (mappedValue as Either.Right<TestResult>).value.value,
            value
        )
    }

    @Test
    fun `flatMap for Left should act on Left Either value`() {
        val value = "test"
        val anotherValue = "abc"
        val rightValue = right(value)
        val anotherRightValue = Either.right<Exception, String>(anotherValue)
        val mappedValue = rightValue.flatMap { v -> anotherRightValue.map { TestResult(it + v.value) } }
        assertTrue(mappedValue.isRight)
        assertEquals(
            (mappedValue as Either.Right<TestResult>).value.value,
            anotherValue + value
        )
    }

    @Test
    fun `flatMap for Left should act on Right Either`() {
        val value = "test"
        val errorValue = "error"
        val rightValue = right(value)
        val errorLeftValue = Either.left<Exception, String>(RuntimeException(errorValue))
        val mappedValue = rightValue.flatMap { errorLeftValue.mapLeft { TestError(it.message!!) } }
        assertTrue(mappedValue.isLeft)
        assertEquals(
            (mappedValue as Either.Left<TestError>).value.value,
            errorValue
        )
    }

    @Test
    fun `flatMap should not act on Left`() {
        val value = "error"
        val leftValue = left(value)
        val mappedValue =
            leftValue.flatMap<TestError, TestResult, TestResult> { throw Exception("this should not be called") }
        assertTrue(mappedValue.isLeft)
        assertEquals(
            (mappedValue as Either.Left<TestError>).value.value,
            value
        )
    }

    @Test
    fun `flatMap can chain`() {
        val value1 = "a"
        val value2 = "b"
        val value3 = "c"
        val rightValue = right(value1)
        val rightValue2 = right(value2)
        val rightValue3 = right(value3)
        val mappedValue = rightValue.flatMap { v -> rightValue2.map { TestResult(v.value + it.value) } }
            .flatMap { v -> rightValue3.map { TestResult(v.value + it.value) } }
        assertTrue(mappedValue.isRight)
        assertEquals(
            (mappedValue as Either.Right<TestResult>).value.value,
            "abc"
        )
    }
}