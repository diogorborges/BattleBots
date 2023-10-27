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

    fun moveRobot(robot: CellType) {
        val currentPosition = if (robot == CellType.ROBOT1) robot1Position else robot2Position
        val nextPosition = getNextMove(currentPosition, robot)

        if (nextPosition != currentPosition) {
            board[currentPosition.row][currentPosition.col] =
                if (robot == CellType.ROBOT1) CellType.TRAIL1 else CellType.TRAIL2
            board[nextPosition.row][nextPosition.col] = robot

            if (robot == CellType.ROBOT1) robot1Position.apply {
                row = nextPosition.row; col = nextPosition.col
            }
            else robot2Position.apply { row = nextPosition.row; col = nextPosition.col }
        }

        if (hasRobotWon(robot)) {
            gameBoardStream.boardStream(GameBoardState.GameOver(robot, prizePosition))
            resetBoard()
        } else {
            gameBoardStream.boardStream(GameBoardState.GameUpdated(board))
        }
    }

    private fun getNextMove(currentPosition: Position, robot: CellType): Position {
        val possibleDirections = listOf(
            Position(-1, 0),
            Position(1, 0),
            Position(0, -1),
            Position(0, 1)
        )

        val validMoves = possibleDirections.map {
            Position(
                it.row + currentPosition.row,
                it.col + currentPosition.col
            )
        }.filter { canMove(it, robot) }

        return if (validMoves.isNotEmpty()) validMoves.random() else currentPosition
    }

    private fun canMove(position: Position, robot: CellType): Boolean {
        if (position.row !in 0 until BOARD_SIZE || position.col !in 0 until BOARD_SIZE) {
            return false
        }

        val cell = board[position.row][position.col]
        return when {
            cell == CellType.PRIZE -> true
            cell == CellType.EMPTY -> true
            robot == CellType.ROBOT1 && cell == CellType.TRAIL2 -> false
            robot == CellType.ROBOT2 && cell == CellType.TRAIL1 -> false
            else -> false
        }
    }

    private fun findRobotPosition(robot: CellType): Position? {
        for (i in board.indices) {
            for (j in board[i].indices) {
                if (board[i][j] == robot) {
                    return Position(i, j)
                }
            }
        }
        return null
    }

    private fun hasRobotWon(robot: CellType): Boolean =
        findRobotPosition(robot)?.let {
            return board[it.row][it.col] == CellType.PRIZE
        } ?: kotlin.run {
            return false
        }

    enum class CellType {
        EMPTY, ROBOT1, ROBOT2, PRIZE, TRAIL1, TRAIL2
    }

    data class Position(var row: Int, var col: Int)

    companion object {
        private const val BOARD_SIZE = 7
    }
}