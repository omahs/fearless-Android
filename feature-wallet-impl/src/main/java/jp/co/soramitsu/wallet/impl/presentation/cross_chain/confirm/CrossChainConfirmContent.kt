package jp.co.soramitsu.wallet.impl.presentation.cross_chain.confirm

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import jp.co.soramitsu.common.compose.component.AccentButton
import jp.co.soramitsu.common.compose.component.BottomSheetScreen
import jp.co.soramitsu.common.compose.component.ButtonViewState
import jp.co.soramitsu.common.compose.component.DoubleGradientIcon
import jp.co.soramitsu.common.compose.component.FullScreenLoading
import jp.co.soramitsu.common.compose.component.GradientIconState
import jp.co.soramitsu.common.compose.component.H1
import jp.co.soramitsu.common.compose.component.H2
import jp.co.soramitsu.common.compose.component.InfoTable
import jp.co.soramitsu.common.compose.component.MarginVertical
import jp.co.soramitsu.common.compose.component.TitleValueViewState
import jp.co.soramitsu.common.compose.component.ToolbarBottomSheet
import jp.co.soramitsu.common.compose.theme.black2
import jp.co.soramitsu.feature_wallet_impl.R

data class CrossChainConfirmViewState(
    val originalChainIcon: GradientIconData?,
    val destinationChainIcon: GradientIconData?,
    val fromInfoItem: TitleValueViewState? = null,
    val toInfoItem: TitleValueViewState? = null,
    val originalNetworkItem: TitleValueViewState? = null,
    val destinationNetworkItem: TitleValueViewState? = null,
    val amountInfoItem: TitleValueViewState? = null,
    val tipInfoItem: TitleValueViewState? = null,
    val originalFeeInfoItem: TitleValueViewState? = null,
    val destinationFeeInfoItem: TitleValueViewState? = null,
    val buttonState: ButtonViewState,
    val isLoading: Boolean = false
) {
    companion object {
        const val CODE_WARNING_CLICK = 3

        val default = CrossChainConfirmViewState(
            originalChainIcon = null,
            destinationChainIcon = null,
            buttonState = ButtonViewState("", false)
        )
    }

    val tableItems = listOf(
        fromInfoItem,
        toInfoItem,
        originalNetworkItem,
        destinationNetworkItem,
        amountInfoItem,
        tipInfoItem,
        originalFeeInfoItem,
        destinationFeeInfoItem
    ).mapNotNull { it }
}

interface CrossChainConfirmScreenInterface {
    fun copyRecipientAddressClicked()
    fun onNextClick()
    fun onNavigationClick()
    fun onItemClick(code: Int)
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CrossChainConfirmContent(
    state: CrossChainConfirmViewState,
    callback: CrossChainConfirmScreenInterface
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    FullScreenLoading(
        isLoading = state.isLoading,
        contentAlignment = Alignment.BottomStart
    ) {
        BottomSheetScreen {
            Box(Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    ToolbarBottomSheet(
                        title = stringResource(id = R.string.preview),
                        onNavigationClick = callback::onNavigationClick
                    )

                    MarginVertical(margin = 24.dp)

                    if (state.originalChainIcon != null && state.destinationChainIcon != null) {
                        DoubleGradientIcon(
                            leftImage = provideGradientIconState(state.originalChainIcon),
                            rightImage = provideGradientIconState(state.destinationChainIcon)
                        )
                    }
                    MarginVertical(margin = 16.dp)
                    H2(
                        text = stringResource(id = R.string.cross_chain_transfer),
                        color = black2,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    MarginVertical(margin = 8.dp)
                    H1(
                        text = state.amountInfoItem?.value.orEmpty(),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    MarginVertical(margin = 24.dp)
                    InfoTable(items = state.tableItems, onItemClick = callback::onItemClick)
                    MarginVertical(margin = 12.dp)

                    AccentButton(
                        state = state.buttonState,
                        onClick = {
                            keyboardController?.hide()
                            callback.onNextClick()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    )

                    MarginVertical(margin = 12.dp)
                }
            }
        }
    }
}

data class GradientIconData(
    val url: String?,
    val color: String?
)

private fun provideGradientIconState(gradientIconData: GradientIconData): GradientIconState {
    return if (gradientIconData.url == null) {
        GradientIconState.Local(
            res = R.drawable.ic_fearless_logo
        )
    } else {
        GradientIconState.Remote(
            url = gradientIconData.url,
            color = gradientIconData.color
        )
    }
}
