package jp.co.soramitsu.polkaswap.impl.data.network.blockchain.bindings

import java.math.BigInteger
import jp.co.soramitsu.common.data.network.runtime.binding.UseCaseBinding
import jp.co.soramitsu.common.data.network.runtime.binding.fromHexOrIncompatible
import jp.co.soramitsu.common.data.network.runtime.binding.incompatible
import jp.co.soramitsu.common.data.network.runtime.binding.storageReturnType
import jp.co.soramitsu.fearless_utils.extensions.toHexString
import jp.co.soramitsu.fearless_utils.runtime.RuntimeSnapshot
import jp.co.soramitsu.fearless_utils.runtime.definitions.types.composite.Struct

@UseCaseBinding
fun bindDexInfos(
    scale: String,
    runtime: RuntimeSnapshot
): String {
    val returnType = runtime.metadata.storageReturnType("DEXManager", "DEXInfos")

    val rawData = returnType.fromHexOrIncompatible(scale, runtime) as? Struct.Instance
    val baseAssetId = rawData?.get<Struct.Instance>("baseAssetId")
    return baseAssetId?.get<List<*>>("code")?.map { (it as BigInteger).toByte() }?.toByteArray()?.toHexString(true)
        ?: incompatible("Can't bind DEXManager.DEXInfos")
}
