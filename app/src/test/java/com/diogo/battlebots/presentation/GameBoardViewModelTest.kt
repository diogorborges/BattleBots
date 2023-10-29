package com.diogo.battlebots.presentation

import com.diogo.battlebots.data.core.CurrentGame
import com.diogo.battlebots.data.core.GameBoardEngine
import com.diogo.battlebots.data.core.GameBoardState
import com.diogo.battlebots.data.core.GameBoardStream
import com.diogo.battlebots.domain.usecase.InitializeGameUseCase
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GameBoardViewModelTest {
    private lateinit var viewModel: GameBoardViewModel
    private val mockGameBoardStreamStateFlow = MutableStateFlow<GameBoardState?>(null)

    private val mockInitializeGameUseCase: InitializeGameUseCase = mockk(relaxed = true)
    private val mockGameBoardStream: GameBoardStream = mockk()

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        clearMocks(mockGameBoardStream)
        coEvery { mockGameBoardStream.gameBoardStream } returns mockGameBoardStreamStateFlow
        viewModel = GameBoardViewModel(mockInitializeGameUseCase, mockGameBoardStream)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cancel()
    }

    @Test
    fun `test collects GameBoardStream on initialization`() = runTest {
        verify { mockGameBoardStream.gameBoardStream }
    }

    @Test
    fun `test collects state should receive game started state`() = runTest {
        val mockGame = mockk<CurrentGame>()

        mockGameBoardStreamStateFlow.value = GameBoardState.GameStarted(mockGame)

        advanceUntilIdle()

        assert(viewModel.gameViewState.value == GameBoardViewState.GameStarted(mockGame))
    }

    @Test
    fun `test collects state should receive game updated state`() = runTest {
        val mockGame = mockk<CurrentGame>()

        mockGameBoardStreamStateFlow.value = GameBoardState.GameUpdated(mockGame)

        advanceUntilIdle()

        assert(viewModel.gameViewState.value == GameBoardViewState.GameUpdated(mockGame))
    }

    @Test
    fun `test collects state should receive game over state`() = runTest {
        val mockGame = mockk<CurrentGame>()
        val winner = GameBoardEngine.CellType.ROBOT1

        mockGameBoardStreamStateFlow.value = GameBoardState.GameOver(winner, mockGame)

        advanceUntilIdle()

        assert(viewModel.gameViewState.value == GameBoardViewState.GameOver(winner))
    }

    @Test
    fun `test collects state should receive game idle state`() = runTest {
        assert(viewModel.gameViewState.value == GameBoardViewState.GameIdle)
    }

    @Test
    fun testInitializeGame() = runTest {
        viewModel.initializeGame()
        verify { mockInitializeGameUseCase.execute() }
    }
}