package jp.co.soramitsu.staking.impl.data.network.blockhain.bindings

import jp.co.soramitsu.common.data.network.runtime.binding.HelperBinding
import jp.co.soramitsu.common.data.network.runtime.binding.cast
import jp.co.soramitsu.common.data.network.runtime.binding.incompatible
import jp.co.soramitsu.common.data.network.runtime.binding.requireType
import jp.co.soramitsu.fearless_utils.extensions.toHexString
import jp.co.soramitsu.fearless_utils.runtime.definitions.types.composite.DictEnum
import jp.co.soramitsu.fearless_utils.runtime.definitions.types.generics.Data as DataType

sealed class Data {
    abstract fun asString(): String?

    object None : Data() {
        override fun asString(): String? = null
    }

    class Raw(val value: ByteArray) : Data() {

        override fun asString() = String(value)
    }

    class Hash(val value: ByteArray, val type: Type) : Data() {

        enum class Type {
            BLAKE_2B_256, SHA_256, KECCAK_256, SHA_3_256
        }

        override fun asString() = value.toHexString(withPrefix = true)
    }
}

@HelperBinding
fun bindData(dynamicInstance: Any?): Data {
    requireType<DictEnum.Entry<Any?>>(dynamicInstance)

    return when {
        dynamicInstance.name == DataType.NONE -> Data.None
        dynamicInstance.name.startsWith(DataType.RAW) -> {
            val value = dynamicInstance.value
            val byteArray = when {
                value is ArrayList<*> && value.getOrNull(0) is Number -> {
                    value.map { (it as Number).toByte() }.toByteArray()
                }
                value is ArrayList<*> -> incompatible()
                else -> value
            }

            Data.Raw(byteArray.cast())
        }
        dynamicInstance.name == DataType.BLAKE_2B_256 -> Data.Hash(dynamicInstance.value.cast(), Data.Hash.Type.BLAKE_2B_256)
        dynamicInstance.name == DataType.SHA_256 -> Data.Hash(dynamicInstance.value.cast(), Data.Hash.Type.SHA_256)
        dynamicInstance.name == DataType.KECCAK_256 -> Data.Hash(dynamicInstance.value.cast(), Data.Hash.Type.KECCAK_256)
        dynamicInstance.name == DataType.SHA_3_256 -> Data.Hash(dynamicInstance.value.cast(), Data.Hash.Type.SHA_3_256)
        else -> incompatible()
    }
}
