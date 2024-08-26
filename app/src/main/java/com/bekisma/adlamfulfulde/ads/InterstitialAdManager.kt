package com.bekisma.adlamfulfulde.ads

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import androidx.activity.ComponentActivity

class InterstitialAdManager(private val context: Context, private val adUnitId: String) {

    private var interstitialAd: InterstitialAd? = null
    private val maxRetries = 3
    private val retryDelay = 5000L // 5 seconds

    init {
        configureAdSdk()
    }

    private fun configureAdSdk() {
        val testDeviceIds = listOf("F3C75E366BF66289B1073F5AFC4209D3") // Replace with actual test device IDs
        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder()
                .setTestDeviceIds(testDeviceIds)
                .build()
        )
    }

    fun loadAd(onAdLoaded: (Boolean) -> Unit) {
        loadAdWithRetry(0, onAdLoaded)
    }

    private fun loadAdWithRetry(retryCount: Int, onAdLoaded: (Boolean) -> Unit) {
        if (retryCount >= maxRetries) {
            Log.e(TAG, "Failed to load ad after $maxRetries attempts")
            onAdLoaded(false)
            return
        }

        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(context, adUnitId, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: InterstitialAd) {
                interstitialAd = ad
                Log.d(TAG, "Interstitial ad loaded successfully")
                onAdLoaded(true)
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                Log.e(TAG, "Interstitial ad failed to load: ${loadAdError.message}")
                retryLoadAd(retryCount, onAdLoaded)
            }
        })
    }

    private fun retryLoadAd(retryCount: Int, onAdLoaded: (Boolean) -> Unit) {
        Handler(Looper.getMainLooper()).postDelayed({
            loadAdWithRetry(retryCount + 1, onAdLoaded)
        }, retryDelay)
    }

    fun showAd(onAdDismissed: () -> Unit) {
        interstitialAd?.let { ad ->
            ad.fullScreenContentCallback = object : com.google.android.gms.ads.FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "Ad was dismissed")
                    interstitialAd = null
                    onAdDismissed()
                    loadAd { } // Reload the ad for the next use
                }

                override fun onAdFailedToShowFullScreenContent(adError: com.google.android.gms.ads.AdError) {
                    Log.e(TAG, "Failed to show ad: ${adError.message}")
                    interstitialAd = null
                    onAdDismissed()
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d(TAG, "Ad showed fullscreen content")
                }
            }

            try {
                (context as? ComponentActivity)?.let { activity ->
                    ad.show(activity)
                } ?: run {
                    Log.e(TAG, "Context is not a ComponentActivity")
                    onAdDismissed()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error showing ad: ${e.message}")
                onAdDismissed()
            }
        } ?: run {
            Log.w(TAG, "Ad wasn't loaded yet")
            onAdDismissed()
        }
    }

    companion object {
        private const val TAG = "InterstitialAdManager"
    }
}