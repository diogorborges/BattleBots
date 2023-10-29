import com.diogo.battlebots.data.core.GameBoard
import com.diogo.battlebots.data.core.GameBoardStream
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GameBoardTest {
    private val mockGameBoardStream = mockk<GameBoardStream>(relaxed = true)

    private lateinit var gameBoard: GameBoard

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        clearMocks(mockGameBoardStream) // Clear previous interactions with mock
        gameBoard = GameBoard(mockGameBoardStream)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cancel()
    }

    @Test
    fun testFindPrize() = runTest {
        gameBoard.initializeGame()

        var prizeFound = false
        for (row in 0 until GameBoard.BOARD_SIZE) {
            for (col in 0 until GameBoard.BOARD_SIZE) {
                if (gameBoard.board[row][col] == GameBoard.CellType.PRIZE) {
                    prizeFound = true
                    break
                }
            }
        }

        assert(prizeFound)
    }

    @Test
    fun testRobotsMoveCorrectly() = runTest {
        gameBoard.initializeGame()

        val initialRobot1Position = gameBoard.robot1Position.copy()
        val initialRobot2Position = gameBoard.robot2Position.copy()

        gameBoard.moveRobot()

        val newRobot1Position = gameBoard.robot1Position
        val newRobot2Position = gameBoard.robot2Position

        assert(
            (initialRobot1Position != newRobot1Position && initialRobot2Position == newRobot2Position) || (initialRobot1Position == newRobot1Position && initialRobot2Position != newRobot2Position)
        )
    }
}