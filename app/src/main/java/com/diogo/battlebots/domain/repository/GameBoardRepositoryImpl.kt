package com.diogo.battlebots.domain.repository

import com.diogo.battlebots.data.core.GameBoard
import com.diogo.battlebots.data.repository.GameBoardRepository
import javax.inject.Inject

class GameBoardRepositoryImpl @Inject constructor(
    private val gameBoard: GameBoard
) : GameBoardRepository {

    override fun initializeGame() = gameBoard.initializeGame()
}