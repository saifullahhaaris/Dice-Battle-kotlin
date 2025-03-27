// Saifullah Haaris - w1902235 | 20212163

package com.example.dicegame

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlin.random.Random

@Composable
fun DiceGameScreen(navController: NavController) {
    // State variables for the game
    var playerDice by rememberSaveable { mutableStateOf(List(5) { Random.nextInt(1, 7) }) }
    var computerDice by rememberSaveable { mutableStateOf(List(5) { Random.nextInt(1, 7) }) }
    var selectedDice by rememberSaveable { mutableStateOf(mutableSetOf<Int>()) }
    var playerScore by rememberSaveable { mutableStateOf(0) }
    var computerScore by rememberSaveable { mutableStateOf(0) }
    var rerollsLeft by rememberSaveable { mutableStateOf(2) }
    var computerRerollsLeft by rememberSaveable { mutableStateOf(2) }
    var targetScore by rememberSaveable { mutableStateOf(101) }
    var winnerMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var humanWins by rememberSaveable { mutableStateOf(0) }
    var computerWins by rememberSaveable { mutableStateOf(0) }
    var turnCompleted by rememberSaveable { mutableStateOf(false) }

    //computer reroll logic
    LaunchedEffect(computerRerollsLeft) {
        if (computerRerollsLeft > 0 && winnerMessage == null) {
            kotlinx.coroutines.delay(5000L) //5sec deleay
            computerDice = computerDice.map { if (it == 1 || it == 2) Random.nextInt(1, 7) else it }
            computerRerollsLeft--
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Scoreboard & total wins
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 80.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.Start) {
                Text("Total Wins", style = MaterialTheme.typography.titleMedium)
                Text("You: $humanWins", style = MaterialTheme.typography.bodyMedium)
                Text("Computer: $computerWins", style = MaterialTheme.typography.bodyMedium)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Current Scores", style = MaterialTheme.typography.titleMedium)
                Text("You: $playerScore", style = MaterialTheme.typography.bodyMedium)
                Text("Computer: $computerScore", style = MaterialTheme.typography.bodyMedium)
            }
        }

        //Spacer(modifier = Modifier.height(10.dp))

        // target score & text field
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Target: ", style = MaterialTheme.typography.titleMedium)
            BasicTextField(
                value = targetScore.toString(),
                onValueChange = { newValue ->
                    if (winnerMessage == null) {
                        targetScore = newValue.toIntOrNull()?.coerceAtLeast(1) ?: targetScore
                    }
                },
                modifier = Modifier.width(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(25.dp))

        // Displaying dices of human and computer
        Text(text = "Computer", style = MaterialTheme.typography.titleMedium)
        DiceRow(diceValues = computerDice, isHuman = false)
        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "You", style = MaterialTheme.typography.titleMedium)
        DiceRow(diceValues = playerDice, selectedDice = selectedDice, isHuman = true)
        //Spacer(modifier = Modifier.height(20.dp))

        // Reroll checkboxes for human player
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            (0 until 5).forEach { index ->
                Checkbox(
                    checked = selectedDice.contains(index),
                    onCheckedChange = {
                        if (winnerMessage == null) {
                            if (index in selectedDice) selectedDice.remove(index) else selectedDice.add(index)
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Game buttons and the logics
        //Throw button
        Button(onClick = {
            if (winnerMessage == null) {
                playerDice = List(5) { Random.nextInt(1, 7) }
                computerDice = List(5) { Random.nextInt(1, 7) }
                selectedDice.clear()
                rerollsLeft = 2
                computerRerollsLeft = 2
                turnCompleted = false
            }
        }, colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
            enabled = winnerMessage == null) {
            Text("Throw")
        }

        // Reroll Button
        Button(onClick = {
            if (winnerMessage == null && rerollsLeft > 0 && !turnCompleted) {
                playerDice = playerDice.mapIndexed { index, value ->
                    if (selectedDice.contains(index)) Random.nextInt(1, 7) else value
                }
                rerollsLeft--

                if (rerollsLeft == 0) {
                    playerScore += playerDice.sum()
                    computerScore += computerDice.sum()
                    checkForWinner(playerScore, computerScore, targetScore, {
                        winnerMessage = "You Win!"
                        humanWins++
                    }, {
                        winnerMessage = "Computer Wins!"
                        computerWins++
                    }, {
                        playerDice = List(5) { Random.nextInt(1, 7) }
                        computerDice = List(5) { Random.nextInt(1, 7) }
                        selectedDice.clear()
                        rerollsLeft = 2
                        computerRerollsLeft = 2
                        turnCompleted = false
                    })
                    turnCompleted = true
                }
            }
            selectedDice.clear()
        }, colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
            enabled = rerollsLeft > 0 && winnerMessage == null && !turnCompleted) {
            Text("Reroll ($rerollsLeft left)")
        }

        //Score Button
        Button(onClick = {
            if (winnerMessage == null && !turnCompleted) {
                playerScore += playerDice.sum()
                computerScore += computerDice.sum()
                turnCompleted = true //onetimeclickable to avoid duplicate updates

                // Check for winner and update accordingly
                checkForWinner(playerScore, computerScore, targetScore, {
                    winnerMessage = "You Win!"
                    humanWins++
                }, {
                    winnerMessage = "Computer Wins!"
                    computerWins++
                }, {
                    winnerMessage = "It's a tie!"
                })
            }
        }, colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
            enabled = winnerMessage == null && !turnCompleted) {
            Text("Score")
        }
        Spacer(modifier = Modifier.height(80.dp))


        //  pop-up dialog at the end
        if (winnerMessage != null) {
            AlertDialog(
                onDismissRequest = { winnerMessage = null },
                title = { Text("Game Result") },
                text = {
                    Text(
                        text = if (winnerMessage == "You Win!") "You won!" else "You lost!",
                        color = if (winnerMessage == "You Win!") Color.Green else Color.Red,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                confirmButton = {
                    Button(onClick = {
                        winnerMessage = null
                        playerScore = 0
                        computerScore = 0
                        navController.navigate("home")
                    }) {
                        Text("Go to Home Screen")
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        winnerMessage = null
                        playerScore = 0
                        computerScore = 0
                    }) {
                        Text("Play Again")
                    }
                }
            )
        }
    }
}


//dice raws
@Composable
fun DiceRow(diceValues: List<Int>, isHuman: Boolean, selectedDice: MutableSet<Int>? = null) {
    Row(horizontalArrangement = Arrangement.Center) {
        diceValues.forEachIndexed { index, value ->
            DiceImage(
                diceValue = value,
                isHuman = isHuman,
                isSelected = selectedDice?.contains(index) == true
            ) {
                selectedDice?.let {
                    if (index in it) it.remove(index) else it.add(index)
                }
            }
        }
    }
}

//dice logic
@Composable
fun DiceImage(diceValue: Int, isHuman: Boolean, isSelected: Boolean, onClick: () -> Unit) {
    val diceImage = if (isHuman) {
        when (diceValue) {
            1 -> R.drawable.h1
            2 -> R.drawable.h2
            3 -> R.drawable.h3
            4 -> R.drawable.h4
            5 -> R.drawable.h5
            6 -> R.drawable.h6
            else -> R.drawable.h1
        }
    } else {
        when (diceValue) {
            1 -> R.drawable.c1
            2 -> R.drawable.c2
            3 -> R.drawable.c3
            4 -> R.drawable.c4
            5 -> R.drawable.c5
            6 -> R.drawable.c6
            else -> R.drawable.c1
        }
    }

    //Single Dice sixe and the feature
    Image(
        painter = painterResource(id = diceImage),
        contentDescription = "Dice $diceValue",
        modifier = Modifier
            .size(50.dp)
            .padding(4.dp)
            .clickable(onClick = onClick)
            .then(if (isSelected) Modifier.border(2.dp, MaterialTheme.colorScheme.secondary) else Modifier)
    )
}

//winner/loser logic(at the end)
fun checkForWinner(
    playerScore: Int,
    computerScore: Int,
    targetScore: Int,
    onPlayerWin: () -> Unit,
    onComputerWin: () -> Unit,
    onTie: () -> Unit
) {
    when {
        //human wins (higher than target and higher than computer)
        playerScore >= targetScore && playerScore > computerScore -> {
            onPlayerWin()
        }

        // Computer wins (higher than target and higher than player)
        computerScore >= targetScore && computerScore > playerScore -> {
            onComputerWin()
        }

        // Tie(both exceed target with equal scores)
        playerScore >= targetScore && computerScore >= targetScore && playerScore == computerScore -> {
            onTie()
        }
    }
}
