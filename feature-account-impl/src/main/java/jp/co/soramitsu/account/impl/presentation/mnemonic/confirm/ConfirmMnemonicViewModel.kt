package jp.co.soramitsu.account.impl.presentation.mnemonic.confirm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.soramitsu.common.base.BaseViewModel
import jp.co.soramitsu.common.resources.ResourceManager
import jp.co.soramitsu.common.utils.Event
import jp.co.soramitsu.common.utils.combine
import jp.co.soramitsu.common.utils.map
import jp.co.soramitsu.common.utils.requireException
import jp.co.soramitsu.common.utils.sendEvent
import jp.co.soramitsu.common.vibration.DeviceVibrator
import jp.co.soramitsu.account.api.domain.interfaces.AccountInteractor
import jp.co.soramitsu.feature_account_impl.R
import jp.co.soramitsu.account.impl.presentation.AccountRouter
import jp.co.soramitsu.account.impl.presentation.mnemonic.confirm.ConfirmMnemonicFragment.Companion.KEY_PAYLOAD
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfirmMnemonicViewModel @Inject constructor(
    private val resourceManager: ResourceManager,
    private val interactor: AccountInteractor,
    private val router: AccountRouter,
    private val deviceVibrator: DeviceVibrator,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val payload = savedStateHandle.get<ConfirmMnemonicPayload>(KEY_PAYLOAD)!!

    private val originMnemonic = payload.mnemonic

    val shuffledMnemonic = originMnemonic.shuffled()

    private val confirmationMnemonicWords = MutableLiveData<List<String>>(emptyList())

    private val _resetConfirmationEvent = MutableLiveData<Event<Unit>>()
    val resetConfirmationEvent: LiveData<Event<Unit>> = _resetConfirmationEvent

    private val _removeLastWordFromConfirmationEvent = MutableLiveData<Event<Unit>>()
    val removeLastWordFromConfirmationEvent: LiveData<Event<Unit>> = _removeLastWordFromConfirmationEvent

    private val proceedInProgress = MutableLiveData(false)

    val nextButtonEnableLiveData: LiveData<Boolean> = combine(confirmationMnemonicWords, proceedInProgress) { (words: List<String>, progress: Boolean) ->
        originMnemonic.size == words.size && !progress
    }

    val skipButtonEnableLiveData: LiveData<Boolean> = proceedInProgress.map { !it }

    val skipVisible = payload.createExtras != null

    private val _matchingMnemonicErrorAnimationEvent = MutableLiveData<Event<Unit>>()
    val matchingMnemonicErrorAnimationEvent: LiveData<Event<Unit>> = _matchingMnemonicErrorAnimationEvent

    fun homeButtonClicked() {
        router.backToBackupMnemonicScreen()
    }

    fun resetConfirmationClicked() {
        reset()
    }

    private fun reset() {
        confirmationMnemonicWords.value = mutableListOf()
        _resetConfirmationEvent.sendEvent()
    }

    fun addWordToConfirmMnemonic(word: String) {
        confirmationMnemonicWords.value?.let {
            val wordList = mutableListOf<String>().apply {
                addAll(it)
                add(word)
            }
            confirmationMnemonicWords.value = wordList
        }
    }

    fun removeLastWordFromConfirmation() {
        confirmationMnemonicWords.value?.let {
            if (it.isEmpty()) {
                return
            }
            val wordList = mutableListOf<String>().apply {
                addAll(it.subList(0, it.size - 1))
            }
            confirmationMnemonicWords.value = wordList
        }

        _removeLastWordFromConfirmationEvent.sendEvent()
    }

    fun nextButtonClicked() {
        confirmationMnemonicWords.value?.let { enteredWords ->
            if (originMnemonic == enteredWords) {
                proceed()
            } else {
                deviceVibrator.makeShortVibration()
                _matchingMnemonicErrorAnimationEvent.sendEvent()
            }
        }
    }

    private fun proceed() {
        if (proceedInProgress.value == true) return
        proceedInProgress.value = true
        when (val createExtras = payload.createExtras) {
            null -> finishConfirmGame()
            is ConfirmMnemonicPayload.CreateChainExtras -> createChainAccount(createExtras)
            else -> createAccount(createExtras)
        }
    }

    private fun finishConfirmGame() {
        router.finishExportFlow()
        showMessage("Success")
    }

    private fun createAccount(extras: ConfirmMnemonicPayload.CreateExtras) {
        viewModelScope.launch {
            val mnemonicString = originMnemonic.joinToString(" ")

            with(extras) {
                val result = interactor.createAccount(accountName, mnemonicString, cryptoType, substrateDerivationPath, ethereumDerivationPath)

                if (result.isSuccess) {
                    continueBasedOnCodeStatus()
                } else {
                    showError(result.requireException())
                }
            }
        }
    }

    private fun createChainAccount(extras: ConfirmMnemonicPayload.CreateChainExtras) {
        viewModelScope.launch {
            val mnemonicString = originMnemonic.joinToString(" ")

            with(extras) {
                val result =
                    interactor.createChainAccount(metaId, chainId, accountName, mnemonicString, cryptoType, substrateDerivationPath, ethereumDerivationPath)

                if (result.isSuccess) {
                    continueBasedOnCodeStatus()
                } else {
                    proceedInProgress.value = false
                    showError(result.requireException())
                }
            }
        }
    }

    fun matchingErrorAnimationCompleted() {
        showError(resourceManager.getString(R.string.confirm_mnemonic_mismatch_error_message_2_0))
        reset()
    }

    private suspend fun continueBasedOnCodeStatus() {
        if (interactor.isCodeSet()) {
            router.openMain()
        } else {
            router.openCreatePincode()
        }
    }

    fun skipClicked() {
        proceed()
    }
}
