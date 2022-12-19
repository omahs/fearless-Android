package jp.co.soramitsu.common.data.network.runtime.calls

import jp.co.soramitsu.fearless_utils.wsrpc.request.runtime.RuntimeRequest

class ExistentialDepositRequest(asset: Any) : RuntimeRequest(
    method = "tokens_queryExistentialDeposit",
    params = listOf(asset)
)
