package com.diogo.battlebots.data.core

sealed class GameBoardState {
    data class GameStarted(val currentGame: CurrentGame) : GameBoardState()
    data class GameUpdated(val currentGame: CurrentGame) : GameBoardState()
    data class GameOver(val winner: GameBoard.CellType?, val currentGame: CurrentGame) :
        GameBoardState()
}