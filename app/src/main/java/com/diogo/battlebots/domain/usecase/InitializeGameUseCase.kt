package com.diogo.battlebots.domain.usecase

import com.diogo.battlebots.data.repository.GameBoardRepository
import javax.inject.Inject

class InitializeGameUseCase @Inject constructor(
    private val gameBoardRepository: GameBoardRepository
) {
    fun execute() = gameBoardRepository.initializeGame()
}