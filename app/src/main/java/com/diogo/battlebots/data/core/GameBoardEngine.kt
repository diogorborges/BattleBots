package com.diogo.battlebots.data.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class GameBoardEngine @Inject constructor(
    private val gameBoardStream: GameBoardStream
) : CoroutineScope {

    private var startTimeMillis: Long = 0L
    private val elapsedTime: Long
        get() = System.currentTimeMillis() - startTimeMillis

    override val coroutineContext = Dispatchers.Main
    private var gameJob: Job? = null
    private val moveMutex = Mutex()

    var currentRobotTurn = getRandomInitialRobotTurn()
    var robot1Position = Position(0, 0)
    var robot2Position = Position(BOARD_SIZE - 1, BOARD_SIZE - 1)
    var prizePosition: Position = Position(0, 0)
    private var robot1Score = 0
    private var robot2Score = 0

    var board: Array<Array<CellType>> = Array(BOARD_SIZE) {
        Array(BOARD_SIZE) { CellType.EMPTY }
    }

    fun initializeGame() {
        gameJob = Job()

        resetBoard()
        placePrize()

        startTimeMillis = System.currentTimeMillis()

        runGameLoop()
    }

    private fun runGameLoop() {
        gameJob?.let { job ->
            launch(job + Dispatchers.Main) {
                while (isActive) {
                    moveMutex.withLock {
                        moveRobot()
                    }
                    delay(MOVE_DELAY)
                }
            }
            launch(job + Dispatchers.Main) {
                while (isActive) {
                    updateGameTime()
                    delay(TIME_UPDATE_DELAY)
                }
            }
        }
    }

    private fun updateGameTime() {
        sendBoardStream(
            GameBoardState.GameUpdated(
                CurrentGame(
                    robot1Score = robot1Score,
                    robot2Score = robot2Score,
                    board = board,
                    currentRobotTurn = currentRobotTurn,
                    elapsedTime = elapsedTime
                )
            )
        )
    }

    private fun getRandomInitialRobotTurn(): CellType =
        if (Random.nextBoolean()) CellType.ROBOT1 else CellType.ROBOT2

    private fun sendBoardStream(gameBoardState: GameBoardState) =
        gameBoardStream.boardStream(gameBoardState)

    private fun resetBoard() {
        board = Array(BOARD_SIZE) { Array(BOARD_SIZE) { CellType.EMPTY } }

        robot1Position.row = 0
        robot1Position.col = 0
        robot2Position.row = BOARD_SIZE - 1
        robot2Position.col = BOARD_SIZE - 1

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
        } while (board[position.row][position.col] != CellType.EMPTY || position == robot1Position || position == robot2Position)
        return position
    }

    fun moveRobot() {
        val currentPosition =
            if (currentRobotTurn == CellType.ROBOT1) robot1Position else robot2Position
        val currentNextPosition = getNextMove(currentPosition, currentRobotTurn)

        if (currentNextPosition == null) {
            val nextRobot =
                if (currentRobotTurn == CellType.ROBOT1) CellType.ROBOT2 else CellType.ROBOT1
            val nextRobotPosition =
                if (currentRobotTurn == CellType.ROBOT1) robot2Position else robot1Position
            val nextRobotNextPosition = getNextMove(nextRobotPosition, nextRobot)

            if (nextRobotNextPosition == null) {
                sendBoardStream(
                    GameBoardState.GameOver(
                        winner = null,
                        currentGame = CurrentGame(
                            robot1Score,
                            robot2Score,
                            board,
                            currentRobotTurn,
                            elapsedTime
                        )
                    )
                )
                endGame()
                return
            }

            toggleCurrentRobotTurn()
            return
        }

        if (isPrizePosition(currentNextPosition)) {
            if (currentRobotTurn == CellType.ROBOT1) {
                robot1Score++
            } else {
                robot2Score++
            }
            sendBoardStream(
                GameBoardState.GameOver(
                    winner = currentRobotTurn,
                    currentGame = CurrentGame(
                        robot1Score,
                        robot2Score,
                        board,
                        currentRobotTurn,
                        elapsedTime
                    )
                )
            )
            endGame()
            return
        }

        board[currentPosition.row][currentPosition.col] =
            if (currentRobotTurn == CellType.ROBOT1) CellType.TRAIL1 else CellType.TRAIL2
        board[currentNextPosition.row][currentNextPosition.col] = currentRobotTurn

        if (currentRobotTurn == CellType.ROBOT1) {
            robot1Position.apply { row = currentNextPosition.row; col = currentNextPosition.col }
        } else {
            robot2Position.apply { row = currentNextPosition.row; col = currentNextPosition.col }
        }

        sendBoardStream(
            GameBoardState.GameUpdated(
                CurrentGame(
                    robot1Score = robot1Score,
                    robot2Score = robot2Score,
                    board = board,
                    currentRobotTurn = currentRobotTurn,
                    elapsedTime = elapsedTime
                )
            )
        )

        toggleCurrentRobotTurn()
    }

    private fun toggleCurrentRobotTurn() {
        currentRobotTurn =
            if (currentRobotTurn == CellType.ROBOT1) CellType.ROBOT2 else CellType.ROBOT1
    }

    private fun getNextMove(position: Position, robot: CellType): Position? {
        val possibleDirections =
            listOf(Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT).shuffled()

        for (direction in possibleDirections) {
            val potentialPosition = when (direction) {
                Direction.UP -> Position(position.row - 1, position.col)
                Direction.DOWN -> Position(position.row + 1, position.col)
                Direction.LEFT -> Position(position.row, position.col - 1)
                Direction.RIGHT -> Position(position.row, position.col + 1)
            }

            if (canMove(potentialPosition, robot)) {
                return potentialPosition
            }
        }

        return null
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

    private fun endGame() {
        gameJob?.cancel()
    }

    private fun isPrizePosition(nextPosition: Position) =
        board[nextPosition.row][nextPosition.col] == CellType.PRIZE

    enum class CellType {
        EMPTY, ROBOT1, ROBOT2, PRIZE, TRAIL1, TRAIL2
    }

    enum class Direction {
        UP, DOWN, LEFT, RIGHT
    }

    data class Position(var row: Int, var col: Int)

    companion object {
        const val BOARD_SIZE = 7
        const val MOVE_DELAY = 500L
        const val TIME_UPDATE_DELAY = 1000L
    }
}