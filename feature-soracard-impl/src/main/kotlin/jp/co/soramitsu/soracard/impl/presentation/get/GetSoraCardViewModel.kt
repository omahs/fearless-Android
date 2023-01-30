package jp.co.soramitsu.soracard.impl.presentation.get

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Locale
import javax.inject.Inject
import jp.co.soramitsu.common.base.BaseViewModel
import jp.co.soramitsu.common.utils.Event
import jp.co.soramitsu.oauth.base.sdk.registration.SoraCardEnvironmentType
import jp.co.soramitsu.oauth.base.sdk.registration.SoraCardKycCredentials
import jp.co.soramitsu.oauth.base.sdk.registration.SoraCardRegistrationContractData
import jp.co.soramitsu.soracard.api.presentation.SoraCardRouter

@HiltViewModel
class GetSoraCardViewModel @Inject constructor(
    private val router: SoraCardRouter
) : BaseViewModel() {

    private val _launchSoraCard = MutableLiveData<Event<SoraCardRegistrationContractData>>()
    val launchSoraCard: LiveData<Event<SoraCardRegistrationContractData>> = _launchSoraCard

    fun onEnableCard() {
        // TODO remove hardcoded credentials after security audit
        _launchSoraCard.value = Event(
            SoraCardRegistrationContractData(
                locale = Locale.ENGLISH,
                apiKey = "6974528a-ee11-4509-b549-a8d02c1aec0d",
                domain = "soracard.com",
                kycCredentials = SoraCardKycCredentials(
                    endpointUrl = "https://kyc-test.soracard.com/mobile",
                    username = "BD6C35D9-68C9-437D-AA22-FFDBDAAFD195",
                    password = "39DED46E-2E8D-46BA-902F-CA14CA990DBF"
                ),
                environment = SoraCardEnvironmentType.TEST
            )
        )
    }

    fun onBackClick() {
        router.back()
    }
}
