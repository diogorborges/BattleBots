package com.diogo.battlebots.presentation

import com.diogo.battlebots.data.core.CurrentGame
import com.diogo.battlebots.data.core.GameBoardEngine

sealed class GameBoardViewState {
    object GameIdle : GameBoardViewState()
    data class GameUpdated(val currentGame: CurrentGame) : GameBoardViewState()
    data class GameOver(val winner: GameBoardEngine.CellType?) : GameBoardViewState()
}