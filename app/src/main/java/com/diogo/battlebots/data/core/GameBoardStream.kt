package com.diogo.battlebots.data.core

import kotlinx.coroutines.flow.StateFlow

interface GameBoardStream {
    val gameBoardStream: StateFlow<GameBoardState?>
    fun boardStream(state: GameBoardState)
}