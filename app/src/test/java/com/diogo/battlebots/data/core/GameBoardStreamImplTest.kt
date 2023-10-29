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
class GameBoardStreamImplTest {

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
    fun testStreamInitialized() {
        assertNull(gameBoardStream.gameBoardStream.value)
    }

    @Test
    fun testGameBoardStreamGameStartedState() = runTest {
        val mockGame = mockk<CurrentGame>()
        val mockState = GameBoardState.GameStarted(mockGame)

        gameBoardStream.boardStream(mockState)

        assertEquals(mockState, gameBoardStream.gameBoardStream.value)
    }

    @Test
    fun testGameBoardStreamGameUpdatedState() = runTest {
        val mockGame = mockk<CurrentGame>()
        val mockState = GameBoardState.GameUpdated(mockGame)

        gameBoardStream.boardStream(mockState)

        assertEquals(mockState, gameBoardStream.gameBoardStream.value)
    }

    @Test
    fun testGameBoardStreamGameOverState() = runTest {
        val mockGame = mockk<CurrentGame>()
        val winner = GameBoard.CellType.ROBOT1
        val mockState = GameBoardState.GameOver(winner, mockGame)

        gameBoardStream.boardStream(mockState)

        assertEquals(mockState, gameBoardStream.gameBoardStream.value)
    }
}