package com.diogo.battlebots.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diogo.battlebots.data.core.GameBoard
import com.diogo.battlebots.data.core.GameBoardState
import com.diogo.battlebots.data.core.GameBoardStream
import com.diogo.battlebots.domain.usecase.InitializeGameUseCase
import com.diogo.battlebots.domain.usecase.MoveRobotUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameBoardViewModel @Inject constructor(
    private val initializeGameUseCase: InitializeGameUseCase,
    private val moveRobotUseCase: MoveRobotUseCase,
    private val gameBoardStream: GameBoardStream
) : ViewModel() {

    private val _gameViewState = MutableStateFlow<GameBoardViewState>(GameBoardViewState.GameIdle)
    val gameViewState: StateFlow<GameBoardViewState> = _gameViewState

    init {
        viewModelScope.launch {
            gameBoardStream.gameBoardStream.collect { stream ->
                handleGameBoardStream(stream)
            }
        }
    }

    private fun handleGameBoardStream(stream: GameBoardState?) {
        _gameViewState.value = when (stream) {
            is GameBoardState.GameStarted -> GameBoardViewState.GameStarted(stream.board)
            is GameBoardState.GameUpdated -> GameBoardViewState.GameUpdated(stream.board)
            is GameBoardState.GameOver -> GameBoardViewState.GameOver(stream.winner)
            is GameBoardState.InvalidMove-> GameBoardViewState.InvalidMove(stream.board)
            else -> GameBoardViewState.GameIdle
        }
    }

    fun initializeGame() = initializeGameUseCase.execute()

    fun moveRobot(robot: GameBoard.CellType, direction: GameBoard.Direction) =
        moveRobotUseCase.execute(robot, direction)
}
