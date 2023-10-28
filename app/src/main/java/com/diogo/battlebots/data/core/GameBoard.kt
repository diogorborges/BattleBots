package com.diogo.battlebots.data.core

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameBoard @Inject constructor(
    private val gameBoardStream: GameBoardStream
) {
    private var board: Array<Array<CellType>> = Array(BOARD_SIZE) {
        Array(BOARD_SIZE) { CellType.EMPTY }
    }

    private val robot1Position = Position(0, 0)
    private val robot2Position = Position(BOARD_SIZE - 1, BOARD_SIZE - 1)
    private var prizePosition: Position = Position(0, 0)

    fun initializeGame() {
        resetBoard()
        placePrize()
        gameBoardStream.boardStream(GameBoardState.GameStarted(board))
    }

    private fun resetBoard() {
        board = Array(BOARD_SIZE) { Array(BOARD_SIZE) { CellType.EMPTY } }
        board[robot1Position.row][robot1Position.col] = CellType.ROBOT1
        board[robot2Position.row][robot2Position.col] = CellType.ROBOT2
    }

    private fun placePrize() {
        prizePosition = getRandomEmptyPosition()
        board[prizePosition.row][prizePosition.col] = CellType.PRIZE
    }

    private fun getRandomEmptyPosition(): Position {
        var position: Position
        do {
            position = Position((0 until BOARD_SIZE).random(), (0 until BOARD_SIZE).random())
        } while (board[position.row][position.col] != CellType.EMPTY)
        return position
    }

    fun moveRobot(robot: CellType, direction: Direction) {
        val currentPosition = if (robot == CellType.ROBOT1) robot1Position else robot2Position
        val nextPosition = getNextMove(currentPosition, direction)

        if (canMove(nextPosition, robot)) {
            if (isPrizePosition(nextPosition)) {
                gameBoardStream.boardStream(GameBoardState.GameOver(robot, prizePosition))
                resetBoard()
                return
            }

            board[currentPosition.row][currentPosition.col] = if (robot == CellType.ROBOT1) CellType.TRAIL1 else CellType.TRAIL2
            board[nextPosition.row][nextPosition.col] = robot

            if (robot == CellType.ROBOT1) robot1Position.apply {
                row = nextPosition.row; col = nextPosition.col
            } else {
                robot2Position.apply { row = nextPosition.row; col = nextPosition.col }
            }

            gameBoardStream.boardStream(GameBoardState.GameUpdated(board))
        } else {
            gameBoardStream.boardStream(GameBoardState.InvalidMove(board))
        }
    }

    private fun getNextMove(currentPosition: Position, direction: Direction): Position {
        return when(direction) {
            Direction.UP -> Position(currentPosition.row - 1, currentPosition.col)
            Direction.DOWN -> Position(currentPosition.row + 1, currentPosition.col)
            Direction.LEFT -> Position(currentPosition.row, currentPosition.col - 1)
            Direction.RIGHT -> Position(currentPosition.row, currentPosition.col + 1)
        }
    }

    private fun canMove(position: Position, robot: CellType): Boolean {
        if (position.row !in 0 until BOARD_SIZE || position.col !in 0 until BOARD_SIZE) {
            return false
        }

        val cell = board[position.row][position.col]
        return when {
            cell == CellType.PRIZE -> true
            cell == CellType.EMPTY -> true
            (robot == CellType.ROBOT1 && cell in listOf(CellType.TRAIL2, CellType.ROBOT2)) -> false
            (robot == CellType.ROBOT2 && cell in listOf(CellType.TRAIL1, CellType.ROBOT1)) -> false
            else -> false
        }
    }

    private fun isPrizePosition(nextPosition: Position) = board[nextPosition.row][nextPosition.col] == CellType.PRIZE

    enum class CellType {
        EMPTY, ROBOT1, ROBOT2, PRIZE, TRAIL1, TRAIL2
    }

    enum class Direction {
        UP, DOWN, LEFT, RIGHT
    }

    data class Position(var row: Int, var col: Int)

    companion object {
        private const val BOARD_SIZE = 7
    }
}