package jp.co.soramitsu.nft.impl.data.model.request

import jp.co.soramitsu.feature_nft_impl.BuildConfig
import okhttp3.MediaType
import okhttp3.internal.commonToMediaTypeOrNull

sealed interface NFTRequest {

    class ContractMetadata: NFTRequest {

        companion object {
            fun requestUrl(alchemyChainId: String?): String {
                return "https://$alchemyChainId.g.alchemy.com/nft/v2/${BuildConfig.ALCHEMY_API_KEY}/getContractMetadataBatch"
            }
        }

        data class Body(
            val mediaType: MediaType = "application/json".commonToMediaTypeOrNull()!!,
            val contractAddresses: List<String>
        )
    }

    class UserOwnedTokens: NFTRequest {
        companion object {
            fun requestUrl(alchemyChainId: String?): String {
                return "https://$alchemyChainId.g.alchemy.com/nft/v2/${BuildConfig.ALCHEMY_API_KEY}/getNFTs"
            }
        }
    }

    class TokensCollection: NFTRequest {
        companion object {
            fun requestUrl(alchemyChainId: String?): String {
                return "https://$alchemyChainId.g.alchemy.com/nft/v2/${BuildConfig.ALCHEMY_API_KEY}/getNFTsForCollection"
            }
        }
    }

    class TokenMetadata: NFTRequest {
        companion object {
            fun requestUrl(alchemyChainId: String?): String {
                return "https://$alchemyChainId.g.alchemy.com/nft/v2/${BuildConfig.ALCHEMY_API_KEY}/getNFTMetadata"
            }
        }
    }

}