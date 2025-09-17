package com.example.raahi.ui.screens

import android.Manifest
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn 
import androidx.compose.material.icons.filled.Place 
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.raahi.ui.theme.RaahiTheme
import com.example.raahi.ui.viewmodels.SafetyViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SafetyScreen(
      appNavController: NavController? = null,
    safetyViewModel: SafetyViewModel = viewModel()
) {
    val uiState by safetyViewModel.uiState.collectAsState()
    val context = LocalContext.current

    val locationPermissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    )
    LaunchedEffect(Unit) {
        if (!locationPermissionsState.allPermissionsGranted) {
            locationPermissionsState.launchMultiplePermissionRequest()
        }
    }

    RaahiTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Column(modifier = Modifier.fillMaxSize()) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.6f)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    if (locationPermissionsState.allPermissionsGranted) {
                        if (uiState.isMapReady) {
                            Text(
                                "Google Maps View Placeholder\nUser Location: ${uiState.userLocation ?: "Fetching..."}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )

                            Column(
                                horizontalAlignment = Alignment.End, 
                                verticalArrangement = Arrangement.Bottom, 
                                modifier = Modifier.fillMaxSize().padding(16.dp)
                            ) {
                                Icon(
                                    Icons.Filled.LocationOn, 
                                    contentDescription = "Nearest Police Station", 
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(36.dp).padding(4.dp)
                                )
                                Icon(
                                    Icons.Filled.Place, 
                                    contentDescription = "Nearest Monument", 
                                    tint = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.size(36.dp).padding(4.dp)
                                )
                            }
                        } else {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary)
                        }
                    } else {
                        Button(
                            onClick = { locationPermissionsState.launchMultiplePermissionRequest() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp, pressedElevation = 8.dp)
                        ) {
                            Text("Request Location Permissions", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }


                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.4f)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {
                            safetyViewModel.triggerPanicAlert()
                            Toast.makeText(context, "Panic Alert Triggered! Sending location to authorities.", Toast.LENGTH_LONG).show()
                        },
                        shape = CircleShape,
                        modifier = Modifier.size(180.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp, pressedElevation = 8.dp)
                    ) {
                        Text(
                            "SOS",
                            style = MaterialTheme.typography.displayLarge.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onError
                        )
                    }
                }
            }
        }
    }


    if (uiState.panicAlertSent) {

    }
    uiState.error?.let {
        Toast.makeText(context, "Error: $it", Toast.LENGTH_LONG).show()

    }
}

@Preview(showBackground = true)
@Composable
fun SafetyScreenPreview() {
    RaahiTheme {
        SafetyScreen(appNavController = rememberNavController())
    }
}
