package com.diogo.battlebots.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Snackbar
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.diogo.battlebots.data.core.CurrentGame
import com.diogo.battlebots.data.core.GameBoard
import com.diogo.battlebots.data.core.GameBoard.CellType
import com.diogo.battlebots.ui.theme.BackgroundColor
import com.diogo.battlebots.ui.theme.EmptyCellColor
import com.diogo.battlebots.ui.theme.PrizeColor
import com.diogo.battlebots.ui.theme.Robot1Color
import com.diogo.battlebots.ui.theme.Robot2Color
import com.diogo.battlebots.ui.theme.Trail1Color
import com.diogo.battlebots.ui.theme.Trail2Color

@Composable
fun GameBoardScreen(viewModel: GameBoardViewModel = hiltViewModel()) {
    val gameState by viewModel.gameViewState.collectAsState()
    var selectedRobot by remember { mutableStateOf<CellType?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        when (gameState) {
            is GameBoardViewState.GameIdle -> {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = "Game is Idle, press Start to begin!",
                    color = Color.White
                )
                Button(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    onClick = { viewModel.initializeGame() }
                ) {
                    Text(
                        text = "Start Game",
                        color = Color.White
                    )
                }
            }

            is GameBoardViewState.GameUpdated -> {
                val gameUpdated = gameState as GameBoardViewState.GameUpdated
                DisplayGameBoard(
                    currentGame = gameUpdated.currentGame,
                    onRobotSelected = { selectedRobot = it }
                )
            }

            is GameBoardViewState.GameStarted -> {
                val gameStarted = gameState as GameBoardViewState.GameStarted
                DisplayGameBoard(
                    currentGame = gameStarted.currentGame,
                    onRobotSelected = { selectedRobot = it }
                )
            }

            is GameBoardViewState.GameOver -> {
                val gameOver = gameState as GameBoardViewState.GameOver
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = "Game Over! Winner: ${gameOver.winner}",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = if (gameOver.winner == CellType.ROBOT1) Robot1Color else Robot2Color,
                )
                Button(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    onClick = { viewModel.initializeGame() }
                ) {
                    Text(
                        text = "Start New Game",
                        color = Color.White
                    )
                }
            }

            is GameBoardViewState.InvalidMove -> {
                val invalidMove = gameState as GameBoardViewState.InvalidMove
                DisplayGameBoard(
                    currentGame = invalidMove.currentGame,
                    onRobotSelected = { selectedRobot = it }
                )
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    backgroundColor = Color.Red,
                    content = {
                        Text(
                            text = "Invalid move!",
                            color = Color.White
                        )
                    }
                )
            }
        }

        selectedRobot?.let { robot ->
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                DisplayDirectionOptions { direction ->
                    viewModel.moveRobot(robot, direction)
                    selectedRobot = null
                }
            }
        }
    }
}

@Composable
fun DisplayGameBoard(
    onRobotSelected: (CellType) -> Unit,
    currentGame: CurrentGame
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(top = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            RobotOneScoreDisplay(score = currentGame.robot1Score)
            Text(
                text = "Turn: ${currentGame.currentRobotTurn}",
                color = if (currentGame.currentRobotTurn == CellType.ROBOT1) Robot1Color else Robot2Color,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            RobotTwoScoreDisplay(score = currentGame.robot2Score)
        }
        val board = currentGame.board
        LazyVerticalGrid(
            columns = GridCells.Fixed(board[0].size),
            contentPadding = PaddingValues(20.dp)
        ) {
            items(board.size * board[0].size) { index ->
                val rowIndex = index / board[0].size
                val columnIndex = index % board[0].size
                val cell = board[rowIndex][columnIndex]
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .padding(1.dp)
                        .clip(CircleShape)
                        .background(getCellColor(cell))
                        .clickable {
                            if (cell == currentGame.currentRobotTurn) {
                                onRobotSelected(cell)
                            }
                        }
                )
            }
        }
    }
}

@Composable
fun RobotOneScoreDisplay(score: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(Robot1Color)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$score",
            color = Robot1Color,
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
fun RobotTwoScoreDisplay(score: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "$score",
            color = Robot2Color,
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(Robot2Color)
        )
    }
}

@Composable
fun DisplayDirectionOptions(onDirectionSelected: (GameBoard.Direction) -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Button(
                onClick = { onDirectionSelected(GameBoard.Direction.UP) }
            ) {
                Text("UP")
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { onDirectionSelected(GameBoard.Direction.LEFT) }
                ) {
                    Text("LEFT")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { onDirectionSelected(GameBoard.Direction.RIGHT) }
                ) {
                    Text("RIGHT")
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Button(
                onClick = { onDirectionSelected(GameBoard.Direction.DOWN) }
            ) {
                Text("DOWN")
            }
        }
    }
}

fun getCellColor(cell: CellType): Color =
    when (cell) {
        CellType.EMPTY -> EmptyCellColor
        CellType.ROBOT1 -> Robot1Color
        CellType.ROBOT2 -> Robot2Color
        CellType.PRIZE -> PrizeColor
        CellType.TRAIL1 -> Trail1Color
        CellType.TRAIL2 -> Trail2Color
    }