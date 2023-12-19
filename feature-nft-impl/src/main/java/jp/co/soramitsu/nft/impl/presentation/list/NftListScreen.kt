package jp.co.soramitsu.nft.impl.presentation.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import jp.co.soramitsu.common.compose.component.MarginHorizontal
import jp.co.soramitsu.common.compose.component.MarginVertical
import jp.co.soramitsu.common.compose.theme.FearlessAppTheme

data class NftScreenState(
    val filtersSelected: Boolean,
    val listState: ListState
) {
    sealed class ListState {
        data class Content(
            val items: List<NftCollectionListItem>
        ) : ListState()

        object Loading : ListState()
        object Empty : ListState()
    }
}

data class NftCollectionListItem(
    val id: String,
    val image: String,
    val chain: String,
    val title: String,
    val quantity: Int,
    val collectionSize: Int
)

interface NftListScreenInterface {
    fun filtersClicked()
}

@Composable
fun NftList(state: NftScreenState, screenInterface: NftListScreenInterface) {
    val appearanceType = remember { mutableStateOf(NftAppearanceType.Grid) }

    Column {
        MarginVertical(margin = 8.dp)
        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
            NftSettingsBar(
                NftSettingsState(appearanceType.value, state.filtersSelected),
                appearanceSelected = {
                    appearanceType.value = it
                },
                filtersClicked = screenInterface::filtersClicked
            )
            MarginHorizontal(margin = 21.dp)
        }
        MarginVertical(margin = 6.dp)
        NftList(
            state = state.listState,
            appearanceType = appearanceType.value
        )
    }
}

@Preview
@Composable
fun NftListScreenPreview() {
    val items = listOf(
        NftCollectionListItem(
            "1",
            "https://public.nftstatic.com/static/nft/res/nft-cex/S3/1681135249863_5vfn4v8dfmche8vzqlhcotiwj2z8vn2g.png",
            "BNB Chain",
            "BORED MARIO v2 #120",
            1,
            290
        ),
        NftCollectionListItem(
            "1",
            "https://public.nftstatic.com/static/nft/res/nft-cex/S3/1681135249863_5vfn4v8dfmche8vzqlhcotiwj2z8vn2g.png",
            "BNB Chain",
            "BORED MARIO v2 #120",
            1,
            290
        ),
        NftCollectionListItem(
            "1",
            "https://public.nftstatic.com/static/nft/res/nft-cex/S3/1681135249863_5vfn4v8dfmche8vzqlhcotiwj2z8vn2g.png",
            "BNB Chain",
            "BORED MARIO v2 #120",
            1,
            290
        )
    )
    val state = NftScreenState.ListState.Content(items)
    val previewState =
        NftScreenState(true, listState = state)
    FearlessAppTheme {
        NftList(previewState, screenInterface = object : NftListScreenInterface {
            override fun filtersClicked() = Unit
        })
    }
}