package com.diogo.battlebots.domain.repository

import com.diogo.battlebots.data.core.GameBoard
import com.diogo.battlebots.data.repository.GameBoardRepository
import javax.inject.Inject

class GameBoardRepositoryImpl @Inject constructor(
    private val gameBoard: GameBoard
) : GameBoardRepository {

    override fun moveRobot(robot: GameBoard.CellType) = gameBoard.moveRobot(robot)

    override fun initializeGame() = gameBoard.initializeGame()
}