import com.diogo.battlebots.data.core.GameBoardEngine
import com.diogo.battlebots.data.core.GameBoardStream
import io.mockk.*
import junit.framework.TestCase.assertEquals
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
class GameBoardEngineTest {
    private val mockGameBoardStream = mockk<GameBoardStream>(relaxed = true)
    private lateinit var gameBoardEngine: GameBoardEngine

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        clearMocks(mockGameBoardStream)
        gameBoardEngine = GameBoardEngine(mockGameBoardStream)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cancel()
    }

    @Test
    fun `prize should be placed on the board after game initialization`() = runTest {
        // When
        gameBoardEngine.initializeGame()

        // Then
        val prizeCount =
            gameBoardEngine.board.flatten().count { it == GameBoardEngine.CellType.PRIZE }
        assertEquals(1, prizeCount)
    }

    @Test
    fun `robot should move to a valid position when moveRobot is called`() = runTest {
        // Given
        gameBoardEngine.initializeGame()
        val initialRobot1Position = gameBoardEngine.robot1Position.copy()
        val initialRobot2Position = gameBoardEngine.robot2Position.copy()

        // When
        gameBoardEngine.moveRobot()

        // Then
        val hasRobot1Moved = initialRobot1Position != gameBoardEngine.robot1Position
        val hasRobot2Moved = initialRobot2Position != gameBoardEngine.robot2Position
        assert(hasRobot1Moved xor hasRobot2Moved)  // Check that only one robot moved
    }

    @Test
    fun `board should be of correct size after game initialization`() = runTest {
        // Given
        gameBoardEngine.initializeGame()

        // Then
        assertEquals(GameBoardEngine.BOARD_SIZE, gameBoardEngine.board.size)
        assertEquals(GameBoardEngine.BOARD_SIZE, gameBoardEngine.board[0].size)
    }

    @Test
    fun `prize should be within board boundaries`() = runTest {
        // Given
        gameBoardEngine.initializeGame()
        val prizePosition =
            gameBoardEngine.board.flatten().indexOfFirst { it == GameBoardEngine.CellType.PRIZE }

        // Then
        assert(prizePosition != -1)
    }

    @Test
    fun `robots should be within board boundaries after initialization`() = runTest {
        // Given
        gameBoardEngine.initializeGame()

        // Then
        assert(gameBoardEngine.robot1Position.row in 0 until GameBoardEngine.BOARD_SIZE)
        assert(gameBoardEngine.robot1Position.col in 0 until GameBoardEngine.BOARD_SIZE)
        assert(gameBoardEngine.robot2Position.row in 0 until GameBoardEngine.BOARD_SIZE)
        assert(gameBoardEngine.robot2Position.col in 0 until GameBoardEngine.BOARD_SIZE)
    }

    @Test
    fun `robots should not be on the same position after initialization`() = runTest {
        // Given
        gameBoardEngine.initializeGame()

        // Then
        assert(gameBoardEngine.robot1Position != gameBoardEngine.robot2Position)
    }

    @Test
    fun `robot cannot move outside the board boundaries`() = runTest {
        // Given
        gameBoardEngine.initializeGame()

        gameBoardEngine.robot1Position = GameBoardEngine.Position(0, 0)
        gameBoardEngine.moveRobot()

        // Then
        assert(gameBoardEngine.robot1Position.row >= 0 && gameBoardEngine.robot1Position.col >= 0)

        gameBoardEngine.robot1Position =
            GameBoardEngine.Position(GameBoardEngine.BOARD_SIZE - 1, GameBoardEngine.BOARD_SIZE - 1)
        gameBoardEngine.moveRobot()

        // Then
        assert(gameBoardEngine.robot1Position.row < GameBoardEngine.BOARD_SIZE && gameBoardEngine.robot1Position.col < GameBoardEngine.BOARD_SIZE)
    }

    @Test
    fun `robot should leave a trail when moving`() = runTest {
        // Given
        gameBoardEngine.initializeGame()

        // When
        gameBoardEngine.moveRobot()
        val hasTrail = gameBoardEngine.board.flatten()
            .any { it == GameBoardEngine.CellType.TRAIL1 || it == GameBoardEngine.CellType.TRAIL2 }

        // Then
        assert(hasTrail)
    }

    @Test
    fun `robots should take turns`() = runTest {
        // Given
        gameBoardEngine.initializeGame()

        val initialTurn = gameBoardEngine.currentRobotTurn

        // When
        gameBoardEngine.moveRobot()

        // Then
        assert(gameBoardEngine.currentRobotTurn != initialTurn)
    }
}