package com.vkolisnichenko.babybirthday.presentation.screen

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vkolisnichenko.babybirthday.R
import com.vkolisnichenko.babybirthday.domain.model.AgeInfo
import com.vkolisnichenko.babybirthday.domain.model.BirthdayScreenVariant
import com.vkolisnichenko.babybirthday.domain.model.ImageSource
import com.vkolisnichenko.babybirthday.domain.usecase.PrepareCameraUseCase
import com.vkolisnichenko.babybirthday.presentation.components.AsyncImageLoader
import com.vkolisnichenko.babybirthday.presentation.mapper.getAgeImageResource
import com.vkolisnichenko.babybirthday.presentation.mapper.getBabyImageResource
import com.vkolisnichenko.babybirthday.presentation.mapper.getPhotoImageResource
import com.vkolisnichenko.babybirthday.presentation.state.BirthdayScreenState
import com.vkolisnichenko.babybirthday.presentation.theme.BabyBirthdayAppTheme
import com.vkolisnichenko.babybirthday.presentation.theme.BlueGray
import com.vkolisnichenko.babybirthday.presentation.theme.getConfig
import com.vkolisnichenko.babybirthday.presentation.viewmodel.BirthdayScreenViewModel
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun BirthdayScreen(
    modifier: Modifier = Modifier,
    onCloseClick: () -> Unit = {},
    viewModel: BirthdayScreenViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val imagePickerState by viewModel.imagePickerState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    BackHandler {
        onCloseClick()
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.onImageSelected(it, ImageSource.GALLERY) }
    }

    var showImageSourceDialog by remember { mutableStateOf(false) }
    var cameraSetup by remember { mutableStateOf<PrepareCameraUseCase.CameraSetup?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        cameraSetup?.let { setup ->
            viewModel.onCameraImageCaptured(success, setup.tempFile)
            cameraSetup = null
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val setup = viewModel.prepareCameraCapture()
            cameraSetup = setup
            cameraLauncher.launch(setup.uri)
        }
    }

    BirthdayScreenContent(
        state = state,
        onCloseClick = onCloseClick,
        onCameraClick = { showImageSourceDialog = true },
        modifier = modifier,
        isProcessing = imagePickerState.isProcessingImage
    )

    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text(stringResource(R.string.select_image_source)) },
            text = { Text(stringResource(R.string.choose_how_you_want_to_add_a_photo)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showImageSourceDialog = false
                        galleryLauncher.launch("image/*")
                    }
                ) {
                    Text(stringResource(R.string.gallery))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showImageSourceDialog = false

                        val hasPermission = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED

                        if (hasPermission) {
                            val setup = viewModel.prepareCameraCapture()
                            cameraSetup = setup
                            cameraLauncher.launch(setup.uri)
                        } else {
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    }
                ) {
                    Text(stringResource(R.string.camera))
                }
            }
        )
    }
}

@Composable
private fun BirthdayScreenContent(
    state: BirthdayScreenState,
    onCloseClick: () -> Unit,
    onCameraClick: () -> Unit,
    modifier: Modifier = Modifier,
    isProcessing: Boolean = false
) {
    val config = state.variant.getConfig()
    val density = LocalDensity.current
    val statusBarHeight = with(density) {
        WindowInsets.statusBars.getTop(this).toDp()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(config.backgroundColor)
    ) {
        config.decorationResource?.let { decorationRes ->
            Image(
                painter = painterResource(id = decorationRes),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )
        }

        CloseButton(
            onClick = onCloseClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .zIndex(10f)
        )

        BirthdayScreenLayout(
            babyName = state.babyName,
            ageInfo = state.ageInfo,
            photoPath = state.photoPath,
            variant = state.variant,
            onCameraClick = onCameraClick,
            statusBarHeight = statusBarHeight,
            isProcessing = isProcessing
        )
    }
}


@Composable
private fun BirthdayScreenLayout(
    babyName: String,
    ageInfo: AgeInfo,
    photoPath: String,
    variant: BirthdayScreenVariant,
    onCameraClick: () -> Unit,
    statusBarHeight: Dp,
    isProcessing: Boolean
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        BirthdayScreenLandscapeLayout(
            babyName = babyName,
            ageInfo = ageInfo,
            photoPath = photoPath,
            variant = variant,
            onCameraClick = onCameraClick,
            isProcessing = isProcessing
        )
    } else {
        BirthdayScreenPortraitLayout(
            babyName = babyName,
            ageInfo = ageInfo,
            photoPath = photoPath,
            variant = variant,
            onCameraClick = onCameraClick,
            statusBarHeight = statusBarHeight,
            isProcessing = isProcessing
        )
    }
}

@Composable
private fun BirthdayScreenPortraitLayout(
    babyName: String,
    ageInfo: AgeInfo,
    photoPath: String,
    variant: BirthdayScreenVariant,
    onCameraClick: () -> Unit,
    statusBarHeight: Dp,
    isProcessing: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(statusBarHeight + 30.dp))

        NameDisplayText(
            babyName = babyName,
            modifier = Modifier.padding(bottom = 13.dp)
        )

        AgeNumberSection(
            ageInfo = ageInfo,
            modifier = Modifier.padding(bottom = 14.dp)
        )

        AgeUnitText(
            ageInfo = ageInfo,
            modifier = Modifier.padding(bottom = 15.dp)
        )

        BabyImageWithCamera(
            photoPath = photoPath,
            variant = variant,
            onCameraClick = onCameraClick,
            isProcessing = isProcessing
        )

        NanitLogo(
            modifier = Modifier.padding(top = 15.dp)
        )
    }
}

@Composable
private fun BirthdayScreenLandscapeLayout(
    babyName: String,
    ageInfo: AgeInfo,
    photoPath: String,
    variant: BirthdayScreenVariant,
    onCameraClick: () -> Unit,
    isProcessing: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .aspectRatio(1f),
            contentAlignment = Alignment.Center
        ) {
            BabyImageWithCamera(
                photoPath = photoPath,
                variant = variant,
                onCameraClick = onCameraClick,
                isProcessing = isProcessing
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            NameDisplayText(
                babyName = babyName,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            AgeNumberSection(
                ageInfo = ageInfo,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            AgeUnitText(
                ageInfo = ageInfo,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            NanitLogo()
        }
    }
}


@Composable
private fun AgeNumberSection(
    ageInfo: AgeInfo,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        DecorativeSwirls(isLeft = true)
        Spacer(modifier = Modifier.width(22.dp))

        Image(
            painter = painterResource(id = getAgeImageResource(ageInfo.value)),
            contentDescription = "Age ${ageInfo.value}",
            modifier = Modifier
                .height(120.dp)
                .wrapContentWidth(),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.width(22.dp))
        DecorativeSwirls(isLeft = false)
    }
}

@Composable
private fun DecorativeSwirls(
    isLeft: Boolean
) {
    val drawableRes = if (isLeft) {
        R.drawable.ic_left_swirls
    } else {
        R.drawable.ic_right_swirls
    }

    Image(
        painter = painterResource(id = drawableRes),
        contentDescription = if (isLeft) {
            stringResource(R.string.left_decorative_swirls)
        } else {
            stringResource(R.string.right_decorative_swirls)
        },
        modifier = Modifier.size(60.dp, 40.dp),
        contentScale = ContentScale.Fit
    )
}


@Composable
private fun AgeUnitText(
    ageInfo: AgeInfo,
    modifier: Modifier = Modifier
) {
    Text(
        text = if (ageInfo.isYears) {
            if (ageInfo.value == 1) {
                stringResource(R.string.year_old)
            } else {
                stringResource(R.string.years_old)
            }
        } else {
            if (ageInfo.value == 1) {
                stringResource(R.string.month_old)
            } else {
                stringResource(R.string.months_old)
            }
        },
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = BlueGray,
        textAlign = TextAlign.Center,
        letterSpacing = 1.sp,
        modifier = modifier
    )
}

@Composable
private fun NameDisplayText(
    babyName: String,
    modifier: Modifier = Modifier
) {
    val upperCaseName = babyName.uppercase()
    val displayText = stringResource(R.string.today_is, upperCaseName)

    Text(
        text = displayText,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = BlueGray,
        textAlign = TextAlign.Center,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
    )
}

@Composable
private fun BabyImageWithCamera(
    photoPath: String,
    variant: BirthdayScreenVariant,
    isProcessing: Boolean,
    onCameraClick: () -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 50.dp)
            .aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {

        BabyImageCircle(
            photoPath = photoPath,
            variant = variant,
            isProcessing = isProcessing,
            onCameraClick = onCameraClick
        )

        CameraIconOnBorder(
            onCameraClick = onCameraClick,
            containerSize = this.maxWidth,
            variant = variant,
            isEnabled = !isProcessing
        )

        if (isProcessing) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }
}

@Composable
private fun BabyImageCircle(
    photoPath: String,
    variant: BirthdayScreenVariant,
    isProcessing: Boolean,
    onCameraClick: () -> Unit
) {
    Image(
        painter = painterResource(getBabyImageResource(variant)),
        contentDescription = "Baby ${variant.name.lowercase()}",
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                enabled = !isProcessing,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onCameraClick() },
        contentScale = ContentScale.Fit
    )

    if (photoPath.isNotEmpty()) {
        AsyncImageLoader(
            imagePath = photoPath,
            fallbackPainter = painterResource(getBabyImageResource(variant)),
            contentDescription = "Baby photo",
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp)
                .clickable(
                    enabled = !isProcessing,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onCameraClick() },
            contentScale = ContentScale.Crop,
            isCircular = true
        )
    }
}

@Composable
private fun CameraIconOnBorder(
    onCameraClick: () -> Unit,
    containerSize: Dp,
    variant: BirthdayScreenVariant,
    isEnabled: Boolean = true
) {
    val radius = containerSize / 2
    val cameraIconSize = 36.dp
    val angle = -45.0
    val angleRad = Math.toRadians(angle)

    val offsetX = (radius.value * cos(angleRad)).dp
    val offsetY = (radius.value * sin(angleRad)).dp

    Box(
        modifier = Modifier
            .offset(x = offsetX, y = offsetY)
            .size(cameraIconSize)
    ) {
        Image(
            painter = painterResource(getPhotoImageResource(variant)),
            contentDescription = "Camera ${variant.name.lowercase()}",
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    enabled = isEnabled,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onCameraClick() },
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
private fun NanitLogo(
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(id = R.drawable.nanit),
        contentDescription = stringResource(R.string.nanit_logo),
        modifier = modifier
            .height(20.dp)
            .width(55.dp),
        contentScale = ContentScale.FillBounds
    )
}

@Composable
private fun CloseButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.size(44.dp),
        shape = CircleShape,
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.close),
                modifier = Modifier.size(24.dp),
                tint = Color(0xFF5A7A6B)
            )
        }
    }
}

@Preview(showBackground = true, name = "Fox Variant")
@Composable
fun BirthdayScreenFoxPreview() {
    BabyBirthdayAppTheme {
        BirthdayScreenContent(
            state = BirthdayScreenState(
                babyName = "Emma",
                ageInfo = AgeInfo(value = 8, isYears = false),
                variant = BirthdayScreenVariant.FOX,
                photoPath = "sample_path"
            ),
            onCloseClick = {},
            onCameraClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Elephant Variant - Long Name")
@Composable
fun BirthdayScreenElephantLongNamePreview() {
    BabyBirthdayAppTheme {
        BirthdayScreenContent(
            state = BirthdayScreenState(
                babyName = "Cristiano Ronaldo",
                ageInfo = AgeInfo(value = 1, isYears = false),
                variant = BirthdayScreenVariant.ELEPHANT,
                photoPath = ""
            ),
            onCloseClick = {},
            onCameraClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Pelican Variant - Years")
@Composable
fun BirthdayScreenPelicanYearsPreview() {
    BabyBirthdayAppTheme {
        BirthdayScreenContent(
            state = BirthdayScreenState(
                babyName = "Alexander",
                ageInfo = AgeInfo(value = 2, isYears = true),
                variant = BirthdayScreenVariant.PELICAN,
                photoPath = ""
            ),
            onCloseClick = {},
            onCameraClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Very Long Name Test")
@Composable
fun BirthdayScreenVeryLongNamePreview() {
    BabyBirthdayAppTheme {
        BirthdayScreenContent(
            state = BirthdayScreenState(
                babyName = "Alexander Christopher Benjamin Jonathan",
                ageInfo = AgeInfo(value = 6, isYears = false),
                variant = BirthdayScreenVariant.FOX,
                photoPath = ""
            ),
            onCloseClick = {},
            onCameraClick = {}
        )
    }
}