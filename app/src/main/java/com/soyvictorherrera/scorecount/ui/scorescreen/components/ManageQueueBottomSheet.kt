package com.soyvictorherrera.scorecount.ui.scorescreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.soyvictorherrera.scorecount.data.database.entity.PlayerProfileEntity
import com.soyvictorherrera.scorecount.domain.model.Player

@Suppress("LongParameterList", "LongMethod")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageQueueBottomSheet(
    challengerModeEnabled: Boolean,
    onChallengerModeToggled: (Boolean) -> Unit,
    queue: List<Player>,
    playerProfiles: List<PlayerProfileEntity>,
    onAddPlayer: (String) -> Unit,
    onRemovePlayer: (Int) -> Unit,
    onSkipPlayer: (Int) -> Unit,
    onDismissRequest: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var inputText by remember { mutableStateOf("") }

    val suggestions =
        remember(inputText, playerProfiles) {
            if (inputText.isBlank()) {
                emptyList()
            } else {
                playerProfiles.filter {
                    it.name.contains(inputText, ignoreCase = true)
                }
            }
        }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        modifier = Modifier.fillMaxHeight(0.8f)
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 24.dp)
        ) {
            Text(
                text = "Manage Challenger Queue",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Challenger Mode Toggle
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Challenger Mode",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Enable rotation queue for waiting players",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = challengerModeEnabled,
                    onCheckedChange = onChallengerModeToggled,
                    modifier = Modifier.testTag("challenger_mode_switch")
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (challengerModeEnabled) {
                // Add Player Input Field
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    label = { Text("Add Player to Queue") },
                    placeholder = { Text("Enter player name") },
                    singleLine = true,
                    keyboardOptions =
                        KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Done
                        ),
                    keyboardActions =
                        KeyboardActions(
                            onDone = {
                                if (inputText.isNotBlank()) {
                                    onAddPlayer(inputText)
                                    inputText = ""
                                }
                            }
                        ),
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                if (inputText.isNotBlank()) {
                                    onAddPlayer(inputText)
                                    inputText = ""
                                }
                            },
                            modifier = Modifier.testTag("add_player_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add player")
                        }
                    },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .testTag("queue_player_name_input")
                )

                // Autocomplete Suggestions Row
                if (suggestions.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(suggestions) { suggestion ->
                            SuggestionChip(
                                onClick = {
                                    onAddPlayer(suggestion.name)
                                    inputText = ""
                                },
                                label = { Text(suggestion.name) },
                                modifier = Modifier.testTag("autocomplete_suggestion_${suggestion.name}")
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Queue Title
                Text(
                    text = "Players in Queue (${queue.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Queued Players List
                if (queue.isEmpty()) {
                    Text(
                        text = "No players in the queue. Add players above to start rotation.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier =
                            Modifier
                                .weight(1f)
                                .testTag("queue_list")
                    ) {
                        items(queue, key = { it.id }) { player ->
                            Card(
                                colors =
                                    CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    ),
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .testTag("queue_item_${player.name}")
                            ) {
                                Row(
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = player.name,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Row {
                                        IconButton(
                                            onClick = { onSkipPlayer(player.id) },
                                            modifier = Modifier.testTag("skip_player_${player.name}")
                                        ) {
                                            Icon(
                                                Icons.Default.SkipNext,
                                                contentDescription = "Skip player to end of queue",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        IconButton(
                                            onClick = { onRemovePlayer(player.id) },
                                            modifier = Modifier.testTag("remove_player_${player.name}")
                                        ) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Remove player from queue",
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // If challenger mode is disabled, show a placeholder or spacer
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}
