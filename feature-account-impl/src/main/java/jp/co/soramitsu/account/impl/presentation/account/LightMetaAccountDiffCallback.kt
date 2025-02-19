package jp.co.soramitsu.account.impl.presentation.account

import androidx.recyclerview.widget.DiffUtil
import jp.co.soramitsu.account.impl.presentation.account.model.LightMetaAccountUi

object LightMetaAccountDiffCallback : DiffUtil.ItemCallback<LightMetaAccountUi>() {

    override fun areItemsTheSame(oldItem: LightMetaAccountUi, newItem: LightMetaAccountUi): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: LightMetaAccountUi, newItem: LightMetaAccountUi): Boolean {
        return oldItem == newItem
    }
}
