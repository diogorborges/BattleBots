package com.diogo.battlebots.presentation

import com.diogo.battlebots.data.core.GameBoard

sealed class GameBoardViewState {
    object GameIdle : GameBoardViewState()
    data class GameStarted(val board: Array<Array<GameBoard.CellType>>) : GameBoardViewState()
    data class RobotMoved(val board: Array<Array<GameBoard.CellType>>) : GameBoardViewState()
    data class GameOver(val winner: GameBoard.CellType) : GameBoardViewState()
}