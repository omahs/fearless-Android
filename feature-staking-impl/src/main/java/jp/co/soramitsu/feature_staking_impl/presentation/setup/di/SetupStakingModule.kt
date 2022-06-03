package jp.co.soramitsu.feature_staking_impl.presentation.setup.di

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import jp.co.soramitsu.common.address.AddressIconGenerator
import jp.co.soramitsu.common.di.viewmodel.ViewModelKey
import jp.co.soramitsu.common.di.viewmodel.ViewModelModule
import jp.co.soramitsu.common.resources.ResourceManager
import jp.co.soramitsu.common.validation.ValidationExecutor
import jp.co.soramitsu.common.validation.ValidationSystem
import jp.co.soramitsu.feature_staking_impl.domain.StakingInteractor
import jp.co.soramitsu.feature_staking_impl.domain.rewards.RewardCalculatorFactory
import jp.co.soramitsu.feature_staking_impl.domain.setup.SetupStakingInteractor
import jp.co.soramitsu.feature_staking_impl.domain.validations.setup.SetupStakingPayload
import jp.co.soramitsu.feature_staking_impl.domain.validations.setup.SetupStakingValidationFailure
import jp.co.soramitsu.feature_staking_impl.presentation.StakingRouter
import jp.co.soramitsu.feature_staking_impl.presentation.common.SetupStakingProcess
import jp.co.soramitsu.feature_staking_impl.presentation.common.SetupStakingSharedState
import jp.co.soramitsu.feature_staking_impl.presentation.common.rewardDestination.RewardDestinationMixin
import jp.co.soramitsu.feature_staking_impl.presentation.setup.SetupStakingViewModel
import jp.co.soramitsu.feature_staking_impl.scenarios.StakingParachainScenarioInteractor
import jp.co.soramitsu.feature_staking_impl.scenarios.StakingRelayChainScenarioInteractor
import jp.co.soramitsu.feature_staking_impl.scenarios.StakingScenarioInteractor
import jp.co.soramitsu.feature_wallet_api.presentation.mixin.fee.FeeLoaderMixin

@Module(includes = [ViewModelModule::class])
class SetupStakingModule {

    @Provides
    fun provideScenarioInteractor(
        setupStakingSharedState: SetupStakingSharedState,
        stakingParachainScenarioInteractor: StakingParachainScenarioInteractor,
        stakingRelayChainScenarioInteractor: StakingRelayChainScenarioInteractor
    ): StakingScenarioInteractor {
        return when (setupStakingSharedState.get<SetupStakingProcess.SetupStep>()) {
            is SetupStakingProcess.SetupStep.Stash -> stakingRelayChainScenarioInteractor
            is SetupStakingProcess.SetupStep.Parachain -> stakingParachainScenarioInteractor
        }
    }

    @Provides
    @IntoMap
    @ViewModelKey(SetupStakingViewModel::class)
    fun provideViewModel(
        interactor: StakingInteractor,
        stakingScenarioInteractor: StakingScenarioInteractor,
        router: StakingRouter,
        rewardCalculatorFactory: RewardCalculatorFactory,
        resourceManager: ResourceManager,
        setupStakingInteractor: SetupStakingInteractor,
        validationSystem: ValidationSystem<SetupStakingPayload, SetupStakingValidationFailure>,
        validationExecutor: ValidationExecutor,
        setupStakingSharedState: SetupStakingSharedState,
        rewardDestinationMixin: RewardDestinationMixin.Presentation,
        feeLoaderMixin: FeeLoaderMixin.Presentation,
        addressIconGenerator: AddressIconGenerator
    ): ViewModel {
        return SetupStakingViewModel(
            router,
            interactor,
            stakingScenarioInteractor,
            rewardCalculatorFactory,
            resourceManager,
            setupStakingInteractor,
            validationSystem,
            setupStakingSharedState,
            validationExecutor,
            feeLoaderMixin,
            rewardDestinationMixin,
            addressIconGenerator
        )
    }

    @Provides
    fun provideViewModelCreator(
        fragment: Fragment,
        viewModelFactory: ViewModelProvider.Factory
    ): SetupStakingViewModel {
        return ViewModelProvider(fragment, viewModelFactory).get(SetupStakingViewModel::class.java)
    }
}
