package com.papb.petto.ui.servicemap

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.LocationServices
import com.papb.petto.data.location.DefaultLocationRepository
import com.papb.petto.data.repository.ServiceRepositoryImpl

@Composable
fun ServiceMapRoute(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val appContext = context.applicationContext

    val locationRepository = remember(appContext) {
        DefaultLocationRepository(
            context = appContext,
            client = LocationServices.getFusedLocationProviderClient(appContext)
        )
    }
    val serviceRepository = remember { ServiceRepositoryImpl() }

    val viewModel: ServiceMapViewModel = viewModel(
        factory = ServiceMapViewModel.factory(serviceRepository, locationRepository)
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var permissionHandled by remember {
        mutableStateOf(locationRepository.hasLocationPermission())
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val granted = result[Manifest.permission.ACCESS_COARSE_LOCATION] == true ||
                result[Manifest.permission.ACCESS_FINE_LOCATION] == true
        if (!granted) viewModel.onPermissionDenied()
        permissionHandled = true
    }

    if (!permissionHandled) {
        LocationPermissionScreen(
            onAllowClick = {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                )
            },
            onNotNowClick = { permissionHandled = true },
            onBack = onBack
        )
        return
    }

    LaunchedEffect(Unit) {
        viewModel.load()
    }

    ServiceMapScreen(
        uiState = uiState,
        onQueryChange = viewModel::onQueryChange,
        onCategoryChange = viewModel::onCategoryChange,
        onDirectionsClick = { service ->
            val uri = Uri.parse("google.navigation:q=${service.latitude},${service.longitude}")
            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
        },
        onCallClick = { service ->
            service.phone?.let { phone ->
                context.startActivity(
                    Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                )
            }
        },
        onServiceClick = { },
        onBack = onBack
    )
}