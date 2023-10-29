package com.diogo.battlebots.data.core

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameBoardStreamImpl @Inject constructor() : GameBoardStream {
    private val _gameBoardStream = MutableStateFlow<GameBoardState?>(null)
    override val gameBoardStream: StateFlow<GameBoardState?> get() = _gameBoardStream

    override fun boardStream(state: GameBoardState) {
        _gameBoardStream.value = state
    }
}