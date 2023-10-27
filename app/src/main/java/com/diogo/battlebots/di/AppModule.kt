package com.diogo.battlebots.di

import com.diogo.battlebots.data.core.GameBoard
import com.diogo.battlebots.data.core.GameBoardStream
import com.diogo.battlebots.data.core.GameBoardStreamImpl
import com.diogo.battlebots.data.repository.GameBoardRepository
import com.diogo.battlebots.domain.repository.GameBoardRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindGameBoardStream(impl: GameBoardStreamImpl): GameBoardStream

    @Binds
    @Singleton
    abstract fun bindGameBoardRepository(impl: GameBoardRepositoryImpl): GameBoardRepository

    companion object {
        @Provides
        @Singleton
        fun provideGameBoard(boardStream: GameBoardStream): GameBoard = GameBoard(boardStream)
    }
}