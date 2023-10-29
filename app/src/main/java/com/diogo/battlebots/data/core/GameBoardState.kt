package com.diogo.battlebots.data.core

sealed class GameBoardState {
    data class GameStarted(
        val board: Array<Array<GameBoard.CellType>>,
        val robot1Score: Int,
        val robot2Score: Int
    ) : GameBoardState()
    data class GameUpdated(
        val board: Array<Array<GameBoard.CellType>>,
        val robot1Score: Int,
        val robot2Score: Int
    ) : GameBoardState()

    data class GameOver(
        val winner: GameBoard.CellType,
        val robot1Score: Int,
        val robot2Score: Int
    ) : GameBoardState()

    data class InvalidMove(
        val board: Array<Array<GameBoard.CellType>>,
        val robot1Score: Int,
        val robot2Score: Int
    ) : GameBoardState()
}