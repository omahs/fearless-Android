package jp.co.soramitsu.soracard.impl.presentation.get

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.co.soramitsu.common.base.BaseComposeFragment
import jp.co.soramitsu.oauth.base.sdk.registration.SoraCardRegistrationContract
import jp.co.soramitsu.oauth.base.sdk.registration.SoraCardRegistrationResult

@AndroidEntryPoint
class GetSoraCardFragment : BaseComposeFragment<GetSoraCardViewModel>() {

    override val viewModel: GetSoraCardViewModel by viewModels()

    private val soraCard = registerForActivityResult(
        SoraCardRegistrationContract()
    ) { result ->

        // kycStatus: Started, Completed, Failed, Rejected, Successful;

        when (result) {
            is SoraCardRegistrationResult.Failure -> {
                result.kycStatus
            }
            is SoraCardRegistrationResult.Success -> {
                result.kycStatus
            }
            is SoraCardRegistrationResult.Canceled -> {
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.launchSoraCard.observeEvent { contractData ->
            soraCard.launch(contractData)
        }
    }

    @ExperimentalMaterialApi
    @Composable
    override fun Content(padding: PaddingValues, scrollState: ScrollState, modalBottomSheetState: ModalBottomSheetState) {
        GetSoraCardScreenWithToolbar(
            scrollState = scrollState,
            onEnableCard = viewModel::onEnableCard,
            onNavigationClick = viewModel::onBackClick
        )
    }
}
