package com.example.raahi.ui.screens
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sos
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.raahi.R
import com.example.raahi.ui.theme.RaahiTheme
import com.example.raahi.ui.theme.NegativeRed
import com.example.raahi.ui.viewmodels.SOSViewModel
import com.example.raahi.ui.viewmodels.SafetyScoreHistoryItem
import com.example.raahi.ui.viewmodels.SafetyScoreUiState
import com.example.raahi.ui.viewmodels.SafetyScoreViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import android.location.Location

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SafetyScoreScreen(
    appNavController: NavController? = null,
    safetyScoreViewModel: SafetyScoreViewModel = viewModel(),
    sosViewModel: SOSViewModel = viewModel()
) {
    val uiState by safetyScoreViewModel.uiState.collectAsState()
    val sosUiState by sosViewModel.uiState.collectAsState()
    val context = LocalContext.current

    var showInitialSosConfirmDialog by remember { mutableStateOf(false) }
    var showFinalSosSentDialog by remember { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions: Map<String, Boolean> ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (fineLocationGranted || coarseLocationGranted) {
            Toast.makeText(context, "Location permission granted. Proceeding with SOS.", Toast.LENGTH_LONG).show()

            getAndSendCurrentLocation(context, sosViewModel)
        } else {
            Toast.makeText(context, "Location permission denied. SOS will be sent without precise location.", Toast.LENGTH_LONG).show()
            sosViewModel.triggerSOS(0.0, 0.0) // Send SOS without precise location
        }
    }


    LaunchedEffect(sosUiState.sosSuccessMessage) {

        if (sosUiState.sosSuccessMessage != null && sosUiState.sosSuccessMessage != "SOS procedure canceled by user.") { 
            showFinalSosSentDialog = true
        }
    }

    LaunchedEffect(sosUiState.sosError) {
        sosUiState.sosError?.let {
            Toast.makeText(context, "SOS Error: $it", Toast.LENGTH_LONG).show()
            sosViewModel.clearSosMessages()
        }
    }


    if (showInitialSosConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showInitialSosConfirmDialog = false },
            title = { Text("Confirm Emergency") },
            text = { Text("Are you sure you want to activate SOS?") },
            confirmButton = {
                Button(
                    onClick = {
                        showInitialSosConfirmDialog = false

                        when {
                            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                                getAndSendCurrentLocation(context, sosViewModel)
                            }
                            else -> {
                                locationPermissionLauncher.launch(
                                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                                )
                            }
                        }
                    }
                ) { Text("Yes, Activate SOS") }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showInitialSosConfirmDialog = false
                        sosViewModel.cancelSosSignal()
                        Toast.makeText(context, "SOS procedure canceled.", Toast.LENGTH_SHORT).show()
                    }
                ) { Text("No, Cancel") }
            }
        )
    }


    if (showFinalSosSentDialog) {
        AlertDialog(
            onDismissRequest = {
                showFinalSosSentDialog = false
                sosViewModel.clearSosMessages()
            },
            title = { Text("SOS Activated") },
            text = { Text(sosUiState.sosSuccessMessage ?: "Authorities are being alerted. Help is on the way.") },
            confirmButton = {
                Button(onClick = {
                    showFinalSosSentDialog = false
                    sosViewModel.clearSosMessages()
                }) { Text("OK") }
            }
        )
    }

    RaahiTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.app_logo),
                                contentDescription = "Raahi App Logo",
                                modifier = Modifier.size(32.dp).padding(end = 8.dp)
                            )
                            Text("My ID & SOS", style = MaterialTheme.typography.titleLarge)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            when {
                uiState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary)
                    }
                }
                uiState.error != null -> {
                    Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                        Text(
                            "Error loading score: ${uiState.error}",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                else -> {
                    SafetyScoreContent(
                        uiState = uiState,
                        sosInProgress = sosUiState.isSendingSos,
                        modifier = Modifier.padding(paddingValues),
                        onSosClicked = {
                            showInitialSosConfirmDialog = true
                        }
                    )
                }
            }
        }
    }
}

@SuppressLint("MissingPermission")
fun getAndSendCurrentLocation(context: Context, sosViewModel: SOSViewModel) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
    ) {
        Toast.makeText(context, "Location permission not available for SOS. Sending without precise location.", Toast.LENGTH_LONG).show()
        Log.d("getAndSendCurrentLocation", "Permission check failed unexpectedly before location request.")
        sosViewModel.triggerSOS(0.0, 0.0)
        return
    }

    Log.d("getAndSendCurrentLocation", "Requesting current location.")
    fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
        .addOnSuccessListener { locationResult: Location? ->
            if (locationResult != null) {
                Log.d("getAndSendCurrentLocation", "Location obtained: ${locationResult.latitude}, ${locationResult.longitude}")
                sosViewModel.triggerSOS(locationResult.latitude, locationResult.longitude)
            } else {
                Log.w("getAndSendCurrentLocation", "Failed to get current location (location is null).")
                Toast.makeText(context, "Could not get current location. Sending SOS without location.", Toast.LENGTH_LONG).show()
                sosViewModel.triggerSOS(0.0, 0.0)
            }
        }
        .addOnFailureListener { exception: Exception ->
            Log.e("getAndSendCurrentLocation", "Failed to get location", exception)
            Toast.makeText(context, "Failed to get location: ${exception.message}. Sending SOS without location.", Toast.LENGTH_LONG).show()
            sosViewModel.triggerSOS(0.0, 0.0)
        }
}


@Composable
fun SafetyScoreContent(
    uiState: SafetyScoreUiState,
    sosInProgress: Boolean,
    modifier: Modifier = Modifier,
    onSosClicked: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SosButton(
            onClick = onSosClicked,
            isLoading = sosInProgress
        )
        Spacer(modifier = Modifier.height(24.dp))
        QrNfcIdSection(qrCodeData = "YOUR_QR_CODE_DATA_HERE", nfcId = "NFC_ID: XYZ123ABC")
        Spacer(modifier = Modifier.height(24.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Your Current Safety Score",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                CircularScoreIndicator(
                    score = uiState.currentScore,
                    maxScore = uiState.maxScore,
                    primaryColor = MaterialTheme.colorScheme.primary,
                    backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                    textColor = MaterialTheme.colorScheme.primary,
                    subTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun SosButton(
    onClick: () -> Unit,
    isLoading: Boolean
) {
    Button(
        onClick = onClick,
        enabled = !isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
            disabledContainerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp, pressedElevation = 8.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(36.dp),
                color = MaterialTheme.colorScheme.onErrorContainer,
                strokeWidth = 3.dp
            )
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Sos,
                    contentDescription = "SOS",
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text("SOS", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}

@Composable
fun QrNfcIdSection(qrCodeData: String, nfcId: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Text("My Digital ID", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
        Card(
            modifier = Modifier
                .size(180.dp)
                .clip(RoundedCornerShape(12.dp)),
            elevation = CardDefaults.cardElevation(2.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Box(modifier = Modifier.fillMaxSize().padding(8.dp), contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "QR Code: $qrCodeData",
                    modifier = Modifier
                        .size(160.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)),
                    contentScale = ContentScale.Fit
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("NFC Tag ID: $nfcId", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun CircularScoreIndicator(
    score: Int,
    maxScore: Int = 100,
    primaryColor: Color,
    backgroundColor: Color,
    textColor: Color,
    subTextColor: Color,
    strokeWidth: Dp = 10.dp,
    size: Dp = 150.dp,
    modifier: Modifier = Modifier
) {
    val animatedScore by animateFloatAsState(targetValue = score.toFloat(), label = "scoreAnimation")
    val progress = if (maxScore > 0) animatedScore / maxScore else 0f

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawArc(
                color = backgroundColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
            drawArc(
                color = primaryColor,
                startAngle = -90f,
                sweepAngle = 360 * progress,
                useCenter = false,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$score",
                style = MaterialTheme.typography.displayMedium.copy(fontSize = 40.sp),
                color = textColor
            )
            Text(
                text = "/ $maxScore",
                style = MaterialTheme.typography.bodySmall,
                color = subTextColor
            )
        }
    }
}

@Composable
fun ScoreHistoryItemView(item: SafetyScoreHistoryItem) {
    val itemColor = if (item.points >= 0) MaterialTheme.colorScheme.primary else NegativeRed
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = if (item.points >= 0) "Positive point" else "Negative point",
            tint = itemColor,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = item.description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = (if (item.points > 0) "+" else "") + "${item.points} pts",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            color = itemColor
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SafetyScoreScreenPreview() {
    RaahiTheme {
        SafetyScoreScreen(appNavController = rememberNavController())
    }
}

@Preview(showBackground = true)
@Composable
fun SosButtonPreview() {
    RaahiTheme {
        Column {
            SosButton(onClick = {}, isLoading = false)
            Spacer(Modifier.height(10.dp))
            SosButton(onClick = {}, isLoading = true)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QrNfcIdSectionPreview() {
    RaahiTheme {
        QrNfcIdSection(qrCodeData = "PREVIEW_QR_DATA", nfcId = "NFC_PREVIEW_123")
    }
}
