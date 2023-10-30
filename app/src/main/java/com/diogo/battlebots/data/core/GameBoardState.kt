package com.diogo.battlebots.data.core

sealed class GameBoardState {
    data class GameUpdated(val currentGame: CurrentGame) : GameBoardState()
    data class GameOver(val winner: GameBoardEngine.CellType?, val currentGame: CurrentGame) :
        GameBoardState()
}