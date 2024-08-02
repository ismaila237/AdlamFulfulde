// package com.bekisma.adlamfulfulde.ads

package com.bekisma.adlamfulfulde.ads

import android.content.Context
import androidx.compose.runtime.*
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class InterstitialAdManager(private val context: Context, private val adUnitId: String) {

    private var interstitialAd: InterstitialAd? = null

    fun loadAd(onAdLoaded: (InterstitialAd?) -> Unit) {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(context, adUnitId, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: InterstitialAd) {
                interstitialAd = ad
                onAdLoaded(ad)
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                interstitialAd = null
                onAdLoaded(null)
            }
        })
    }

    fun showAd(onAdDismissed: () -> Unit) {
        interstitialAd?.let { ad ->
            ad.fullScreenContentCallback = object : com.google.android.gms.ads.FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    onAdDismissed()
                    loadAd {} // Reload the ad for the next use
                }

                override fun onAdFailedToShowFullScreenContent(adError: com.google.android.gms.ads.AdError) {
                    onAdDismissed()
                }
            }
            ad.show(context as androidx.activity.ComponentActivity)
        } ?: run {
            onAdDismissed()
        }
    }
}
