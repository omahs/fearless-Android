package jp.co.soramitsu.wallet.impl.presentation.balance.detail.frozen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import jp.co.soramitsu.common.compose.component.B2
import jp.co.soramitsu.common.compose.component.BottomSheetScreen
import jp.co.soramitsu.common.compose.component.Grip
import jp.co.soramitsu.common.compose.component.H3
import jp.co.soramitsu.common.compose.component.H5
import jp.co.soramitsu.common.compose.component.MarginVertical
import jp.co.soramitsu.common.utils.formatAsCurrency
import jp.co.soramitsu.feature_wallet_impl.R

data class FrozenTokensContentViewState(
    val frozenAssetPayload: FrozenAssetPayload
)

@Composable
fun FrozenTokensContent(
    state: FrozenTokensContentViewState
) {
    val payload = state.frozenAssetPayload

    BottomSheetScreen {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
        ) {
            MarginVertical(margin = 2.dp)
            Grip(Modifier.align(Alignment.CenterHorizontally))
            MarginVertical(margin = 8.dp)
            H3(text = stringResource(id = R.string.common_title_frozen_token, payload.assetSymbol.uppercase()))
            MarginVertical(margin = 28.dp)
            payload.locked?.let {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    H5(text = stringResource(id = R.string.common_title_frozen_loked))
                    Spacer(modifier = Modifier.weight(1f))
                    B2(text = it.formatAsCurrency(""))
                }
                MarginVertical(margin = 24.dp)
            }
            payload.staked?.let {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    H5(text = stringResource(id = R.string.common_title_frozen_staked))
                    Spacer(modifier = Modifier.weight(1f))
                    B2(text = it.formatAsCurrency(""))
                }
                MarginVertical(margin = 24.dp)
            }
            payload.reserved?.let {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    H5(text = stringResource(id = R.string.common_title_frozen_reserved))
                    Spacer(modifier = Modifier.weight(1f))
                    B2(text = it.formatAsCurrency(""))
                }
                MarginVertical(margin = 24.dp)
            }
            payload.redeemable?.let {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    H5(text = stringResource(id = R.string.common_title_frozen_redeemable))
                    Spacer(modifier = Modifier.weight(1f))
                    B2(text = it.formatAsCurrency(""))
                }
                MarginVertical(margin = 24.dp)
            }
            payload.unstaking?.let {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    H5(text = stringResource(id = R.string.common_title_frozen_unstaking))
                    Spacer(modifier = Modifier.weight(1f))
                    B2(text = it.formatAsCurrency(""))
                }
                MarginVertical(margin = 12.dp)
            }
        }
    }
}
