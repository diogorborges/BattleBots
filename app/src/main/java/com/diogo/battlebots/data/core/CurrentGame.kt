package com.diogo.battlebots.data.core

data class CurrentGame(
    val robot1Score: Int,
    val robot2Score: Int,
    val board: Array<Array<GameBoard.CellType>>,
    val currentRobotTurn: GameBoard.CellType
)