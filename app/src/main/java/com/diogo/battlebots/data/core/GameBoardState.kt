package com.diogo.battlebots.data.core

sealed class GameBoardState {
    data class GameStarted(val board: Array<Array<GameBoard.CellType>>) : GameBoardState()
    data class GameUpdated(val board: Array<Array<GameBoard.CellType>>) : GameBoardState()
    data class GameOver(val winner: GameBoard.CellType, val prizePosition: GameBoard.Position) : GameBoardState()
}