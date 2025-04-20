package com.bekisma.adlamfulfulde.ads

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.*
import com.bekisma.adlamfulfulde.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("VisibleForTests")
@Composable
fun BannerAdView(
    modifier: Modifier = Modifier,
    adSize: AdSize = AdSize.BANNER,
    maxRetries: Int = 3,
    retryDelay: Long = 5000 // 5 seconds
) {
    val context = LocalContext.current
    val adUnitId = stringResource(id = R.string.ad_mob_banner_id)
    var retryCount by remember { mutableStateOf(0) }
    var adView by remember { mutableStateOf<AdView?>(null) }
    val coroutineScope = rememberCoroutineScope()

    DisposableEffect(Unit) {
        configureAdSdk()
        onDispose {
            adView?.destroy()
        }
    }

    fun retryLoadingAd() {
        coroutineScope.launch {
            retryCount++
            delay(retryDelay)
            adView?.loadAd(AdRequest.Builder().build())
        }
    }

    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { ctx ->
            AdView(ctx).apply {
                setAdSize(adSize)
                this.adUnitId = adUnitId

                adListener = object : AdListener() {
                    override fun onAdFailedToLoad(error: LoadAdError) {
                        Log.e(TAG, "Ad failed to load with code: ${error.code}, message: ${error.message}")
                        if (retryCount < maxRetries) {
                            retryLoadingAd()
                        } else {
                            Log.e(TAG, "Failed to load ad after $maxRetries attempts")
                        }
                    }

                    override fun onAdLoaded() {
                        Log.d(TAG, "Ad loaded successfully")
                        retryCount = 0 // Reset retry count on successful load
                    }

                    override fun onAdClicked() {
                        Log.d(TAG, "Ad clicked")
                    }

                    override fun onAdImpression() {
                        Log.d(TAG, "Ad impression logged")
                    }
                }

                adView = this
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}

private fun configureAdSdk() {
    val testDeviceIds = listOf("F3C75E366BF66289B1073F5AFC4209D3") // Replace with actual test device IDs
    MobileAds.setRequestConfiguration(
        RequestConfiguration.Builder()
            .setTestDeviceIds(testDeviceIds)
            .build()
    )
}

private const val TAG = "BannerAdView"