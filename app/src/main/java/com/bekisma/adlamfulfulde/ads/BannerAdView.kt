// package com.bekisma.adlamfulfulde.ads

package com.bekisma.adlamfulfulde.ads

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.bekisma.adlamfulfulde.R

@SuppressLint("VisibleForTests")
@Composable
fun BannerAdView() {
    val adUnitId = stringResource(id = R.string.ad_mob_banner_id)

    AndroidView(
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.FULL_BANNER)
                this.adUnitId = adUnitId // Set the adUnitId here
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}
