package com.diogo.battlebots.presentation

import com.diogo.battlebots.data.core.CurrentGame
import com.diogo.battlebots.data.core.GameBoard

sealed class GameBoardViewState {
    object GameIdle : GameBoardViewState()
    data class GameStarted(val currentGame: CurrentGame) : GameBoardViewState()
    data class GameUpdated(val currentGame: CurrentGame) : GameBoardViewState()
    data class GameOver(val winner: GameBoard.CellType) : GameBoardViewState()
    data class InvalidMove(val currentGame: CurrentGame) : GameBoardViewState()
}