package jp.co.soramitsu.wallet.impl.data.buyToken

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import jp.co.soramitsu.common.utils.showBrowser
import jp.co.soramitsu.feature_wallet_impl.R
import jp.co.soramitsu.runtime.multiNetwork.chain.model.Chain
import jp.co.soramitsu.wallet.impl.domain.model.BuyTokenRegistry

private const val RAMP_APP_NAME = "Fearless Wallet"
private const val RAMP_APP_LOGO =
    "https://raw.githubusercontent.com/soramitsu/fearless-Android/dff3ebbed4a125621732ee039f2bc74c74f5b58f/common/src/main/res/drawable-xxxhdpi/ic_wallet.png"

class RampProvider(
    private val host: String,
    private val apiToken: String
) : ExternalProvider {

    override val name: String = "Ramp"

    override val icon: Int = R.drawable.ic_ramp

    override fun createIntegrator(chainAsset: Chain.Asset, address: String): BuyTokenRegistry.Integrator<Context> {
        if (!isTokenSupported(chainAsset)) {
            throw BuyTokenRegistry.Provider.UnsupportedTokenException()
        }

        return RampIntegrator(host, apiToken, chainAsset, address)
    }

    class RampIntegrator(
        private val host: String,
        private val apiToken: String,
        private val chainAsset: Chain.Asset,
        private val address: String
    ) : BuyTokenRegistry.Integrator<Context> {

        @SuppressLint("SetJavaScriptEnabled")
        override fun integrate(using: Context) {
            using.showBrowser(createPurchaseLink())
        }

        private fun createPurchaseLink(): String {
            return Uri.Builder()
                .scheme("https")
                .authority(host)
                .appendQueryParameter("swapAsset", chainAsset.symbol.uppercase())
                .appendQueryParameter("userAddress", address)
                .appendQueryParameter("hostApiKey", apiToken)
                .appendQueryParameter("hostAppName", RAMP_APP_NAME)
                .appendQueryParameter("hostLogoUrl", RAMP_APP_LOGO)
                .appendQueryParameter("finalUrl", ExternalProvider.REDIRECT_URL_BASE)
                .build()
                .toString()
        }
    }
}
