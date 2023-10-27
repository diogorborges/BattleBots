package com.diogo.battlebots.data.repository

import com.diogo.battlebots.data.core.GameBoard

interface GameBoardRepository {
    fun moveRobot(robot: GameBoard.CellType)
    fun initializeGame()
}