package com.vkolisnichenko.babybirthday.presentation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vkolisnichenko.babybirthday.R
import com.vkolisnichenko.babybirthday.presentation.components.DatePickerDialog
import com.vkolisnichenko.babybirthday.presentation.state.DetailsScreenState
import com.vkolisnichenko.babybirthday.presentation.theme.BabyBirthdayAppTheme
import com.vkolisnichenko.babybirthday.presentation.viewmodel.DetailsScreenViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DetailsScreen(
    modifier: Modifier = Modifier,
    onShowBirthdayScreen: () -> Unit = {},
    viewModel: DetailsScreenViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    DetailsScreenContent(
        state = state,
        onNameChange = viewModel::updateName,
        onBirthdayChange = viewModel::updateBirthday,
        onPhotoChange = viewModel::updatePhotoPath,
        onShowBirthdayScreen = onShowBirthdayScreen,
        modifier = modifier
    )
}

@Composable
private fun DetailsScreenContent(
    state: DetailsScreenState,
    onNameChange: (String) -> Unit,
    onBirthdayChange: (LocalDate) -> Unit,
    onPhotoChange: (String?) -> Unit,
    onShowBirthdayScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val isLandscape =
        configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        AppTitleSection(
            modifier = Modifier.padding(bottom = if (isLandscape) 16.dp else 24.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = if (isLandscape) 32.dp else 0.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                NameInputField(
                    value = state.name,
                    onValueChange = onNameChange,
                    modifier = Modifier.fillMaxWidth()
                )

                BirthdayInputField(
                    modifier = Modifier.fillMaxWidth(),
                    birthday = state.birthday,
                    birthdayErrorMessage = state.birthdayErrorMessage ?: "",
                    onBirthdaySelected = onBirthdayChange
                )

                PhotoSection(
                    hasPhoto = state.hasPhoto,
                    onPhotoClick = {
                        val newPath = if (state.hasPhoto) null else "dummy_path"
                        onPhotoChange(newPath)
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                ShowBirthdayButton(
                    enabled = state.isFormValid && !state.isLoading,
                    isLoading = state.isSaving,
                    onClick = onShowBirthdayScreen,
                    modifier = Modifier.fillMaxWidth()
                )

                if (!state.isFormValid) {
                    FormValidationHint()
                }
            }
        }

        if (state.isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun AppTitleSection(
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_baby_face),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Text(
                text = stringResource(R.string.baby_birthday),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Text(
            text = stringResource(R.string.create_your_baby_birthday_celebration),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun NameInputField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.baby_name),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                val filteredValue = newValue.filter { char ->
                    char.isLetter() || char.isWhitespace()
                }
                onValueChange(filteredValue)
            },
            placeholder = {
                Text(
                    text = stringResource(R.string.enter_baby_name),
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 2
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Name icon",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            maxLines = 2,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            textStyle = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun BirthdayInputField(
    modifier: Modifier = Modifier,
    birthday: LocalDate?,
    birthdayErrorMessage: String = "",
    onBirthdaySelected: (LocalDate) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.birthday),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Box {
            OutlinedTextField(
                value = birthday?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: "",
                onValueChange = { },
                placeholder = {
                    Text(
                        text = stringResource(R.string.select_birthday_date),
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Calendar icon",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                readOnly = true,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { showDatePicker = true }
            )
        }
        birthdayErrorMessage.takeIf { it.isNotBlank() }.apply {
            Text(
                text = this ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDateSelected = { selectedDate ->
                onBirthdaySelected(selectedDate)
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false },
            initialDate = birthday
        )
    }
}

@Composable
private fun PhotoSection(
    hasPhoto: Boolean,
    onPhotoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val isLandscape =
        configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.baby_picture),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            onClick = onPhotoClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(if (isLandscape) 140.dp else 180.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (hasPhoto) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (hasPhoto) {
                    PhotoSelectedContent()
                } else {
                    PhotoPlaceholderContent()
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 2.dp,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Camera",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PhotoSelectedContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(64.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Baby photo",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.photo_selected),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun PhotoPlaceholderContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.DateRange,
            contentDescription = "Add photo",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(32.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.tap_to_add_photo),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = stringResource(R.string.camera_or_gallery),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun ShowBirthdayButton(
    enabled: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            Text(
                text = stringResource(R.string.show_birthday_screen),
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
private fun FormValidationHint() {
    Text(
        text = stringResource(R.string.please_enter_name_and_birthday_to_continue),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true)
@Composable
fun DetailsScreenPreview() {
    BabyBirthdayAppTheme {
        Surface {
            DetailsScreenContent(
                state = DetailsScreenState(
                    name = "Emma",
                    birthday = LocalDate.of(2023, 6, 15),
                    photoPath = "mock_path",
                    isLoading = false,
                    isSaving = false
                ),
                onNameChange = {},
                onBirthdayChange = {},
                onPhotoChange = {},
                onShowBirthdayScreen = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Loading State")
@Composable
fun DetailsScreenLoadingPreview() {
    BabyBirthdayAppTheme {
        Surface {
            DetailsScreenContent(
                state = DetailsScreenState(
                    name = "",
                    birthday = null,
                    photoPath = null,
                    isLoading = true,
                    isSaving = false
                ),
                onNameChange = {},
                onBirthdayChange = {},
                onPhotoChange = {},
                onShowBirthdayScreen = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Empty State")
@Composable
fun DetailsScreenEmptyPreview() {
    BabyBirthdayAppTheme {
        Surface {
            DetailsScreenContent(
                state = DetailsScreenState(),
                onNameChange = {},
                onBirthdayChange = {},
                onPhotoChange = {},
                onShowBirthdayScreen = {}
            )
        }
    }
}