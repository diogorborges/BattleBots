package com.diogo.battlebots.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.diogo.battlebots.R
import com.diogo.battlebots.data.core.CurrentGame
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        when (gameState) {
            is GameBoardViewState.GameIdle -> {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = stringResource(R.string.game_is_idle_press_start_to_begin),
                    color = Color.White
                )
                Button(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    onClick = { viewModel.initializeGame() }
                ) {
                    Text(
                        text = stringResource(R.string.start_game),
                        color = Color.White
                    )
                }
            }

            is GameBoardViewState.GameUpdated -> {
                val gameUpdated = gameState as GameBoardViewState.GameUpdated
                DisplayGameBoard(
                    currentGame = gameUpdated.currentGame
                )
            }

            is GameBoardViewState.GameStarted -> {
                val gameStarted = gameState as GameBoardViewState.GameStarted
                DisplayGameBoard(
                    currentGame = gameStarted.currentGame
                )
            }

            is GameBoardViewState.GameOver -> {
                val gameOver = gameState as GameBoardViewState.GameOver
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = when {
                        gameOver.winner != null -> {
                            stringResource(R.string.game_over_winner, gameOver.winner)
                        }
                        else -> {
                            stringResource(R.string.game_over_no_winners)
                        }
                    },
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = when (gameOver.winner) {
                        CellType.ROBOT1 -> Robot1Color
                        CellType.ROBOT2 -> Robot2Color
                        else -> Color.White
                    },
                )
                Button(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    onClick = { viewModel.initializeGame() }
                ) {
                    Text(
                        text = stringResource(R.string.start_new_game),
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun DisplayGameBoard(currentGame: CurrentGame) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DisplayGameTime(currentGame.elapsedTime)
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
                )
            }
        }
    }
}

@Composable
fun DisplayGameTime(elapsedTime: Long) {
    val seconds = (elapsedTime / 1000) % 60
    val minutes = (elapsedTime / (1000 * 60)) % 60
    val hours = (elapsedTime / (1000 * 60 * 60)) % 24

    val timeString = if (hours > 0) {
        String.format(stringResource(R.string.hour_time), hours, minutes, seconds)
    } else {
        String.format(stringResource(R.string.minute_time), minutes, seconds)
    }

    Text(
        modifier = Modifier.padding(bottom = 8.dp),
        text = timeString,
        color = Color.White,
        style = TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    )
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

fun getCellColor(cell: CellType): Color =
    when (cell) {
        CellType.EMPTY -> EmptyCellColor
        CellType.ROBOT1 -> Robot1Color
        CellType.ROBOT2 -> Robot2Color
        CellType.PRIZE -> PrizeColor
        CellType.TRAIL1 -> Trail1Color
        CellType.TRAIL2 -> Trail2Color
    }