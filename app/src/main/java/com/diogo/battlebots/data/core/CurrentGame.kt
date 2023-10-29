package com.diogo.battlebots.data.core

data class CurrentGame(
    val robot1Score: Int,
    val robot2Score: Int,
    val board: Array<Array<GameBoardEngine.CellType>>,
    val currentRobotTurn: GameBoardEngine.CellType,
    val elapsedTime: Long
)