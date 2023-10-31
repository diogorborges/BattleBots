package com.diogo.battlebots.data.core

import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GameBoardStreamTest {

    private lateinit var gameBoardStream: GameBoardStream
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        gameBoardStream = GameBoardStreamImpl()
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cancel()
    }

    @Test
    fun `stream initial state is null`() {
        assertNull(gameBoardStream.gameBoardStream.value)
    }

    @Test
    fun `stream should receive updated state`() = runTest {
        val state = GameBoardState.GameUpdated(mockk())
        assertStateIsUpdated(state)
    }

    @Test
    fun `stream should receive game over state`() = runTest {
        val state = GameBoardState.GameOver(GameBoardEngine.CellType.ROBOT1, mockk())
        assertStateIsUpdated(state)
    }

    private fun assertStateIsUpdated(state: GameBoardState) {
        gameBoardStream.boardStream(state)
        assertEquals(state, gameBoardStream.gameBoardStream.value)
    }
}