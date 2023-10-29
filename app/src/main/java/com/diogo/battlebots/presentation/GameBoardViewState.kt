package com.diogo.battlebots.presentation

import com.diogo.battlebots.data.core.GameBoard

sealed class GameBoardViewState {
    object GameIdle : GameBoardViewState()
    data class GameStarted(
        val board: Array<Array<GameBoard.CellType>>,
        val robot1Score: Int,
        val robot2Score: Int
    ) : GameBoardViewState()
    data class GameUpdated(
        val board: Array<Array<GameBoard.CellType>>,
        val robot1Score: Int,
        val robot2Score: Int
    ) : GameBoardViewState()
    data class GameOver(val winner: GameBoard.CellType) : GameBoardViewState()
    data class InvalidMove(
        val board: Array<Array<GameBoard.CellType>>,
        val robot1Score: Int,
        val robot2Score: Int
    ) : GameBoardViewState()
}