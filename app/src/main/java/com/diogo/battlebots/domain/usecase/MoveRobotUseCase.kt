package com.diogo.battlebots.domain.usecase

import com.diogo.battlebots.data.core.GameBoard
import com.diogo.battlebots.data.repository.GameBoardRepository
import javax.inject.Inject

class MoveRobotUseCase @Inject constructor(
    private val gameBoardRepository: GameBoardRepository
) {
    fun execute(robot: GameBoard.CellType) = gameBoardRepository.moveRobot(robot)
}