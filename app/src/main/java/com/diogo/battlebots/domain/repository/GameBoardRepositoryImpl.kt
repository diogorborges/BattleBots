package com.diogo.battlebots.domain.repository

import com.diogo.battlebots.data.core.GameBoardEngine
import com.diogo.battlebots.data.repository.GameBoardRepository
import javax.inject.Inject

class GameBoardRepositoryImpl @Inject constructor(
    private val gameBoardEngine: GameBoardEngine
) : GameBoardRepository {

    override fun initializeGame() = gameBoardEngine.initializeGame()
}