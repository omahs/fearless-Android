package jp.co.soramitsu.common.compose.component

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import jp.co.soramitsu.common.R
import jp.co.soramitsu.common.compose.theme.FearlessTheme
import jp.co.soramitsu.common.compose.theme.backgroundBlurColor
import jp.co.soramitsu.common.compose.theme.colorAccent
import jp.co.soramitsu.common.compose.theme.customTypography
import jp.co.soramitsu.common.compose.theme.white

data class MainToolbarViewState(
    val title: String,
    val homeIconState: ToolbarHomeIconState = ToolbarHomeIconState(),
    val selectorViewState: ChainSelectorViewState
)

data class ToolbarHomeIconState(
    val walletIcon: Drawable? = null,
    @DrawableRes val navigationIcon: Int? = null
)

data class MenuIconItem(
    @DrawableRes val icon: Int,
    val onClick: () -> Unit
)

@Composable
fun MainToolbar(
    state: MainToolbarViewState,
    onChangeChainClick: () -> Unit,
    onNavigationClick: () -> Unit = {},
    menuItems: List<MenuIconItem>? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier.weight(1f)
        ) {
            ToolbarHomeIcon(
                state = state.homeIconState,
                onClick = onNavigationClick
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(2f),
            horizontalAlignment = CenterHorizontally
        ) {
            Text(
                text = state.title,
                style = MaterialTheme.customTypography.header4,
                maxLines = 1
            )

            MarginVertical(margin = 4.dp)

            ChainSelector(
                selectorViewState = state.selectorViewState,
                onChangeChainClick = onChangeChainClick
            )
        }
        Row(
            verticalAlignment = CenterVertically,
            horizontalArrangement = spacedBy(8.dp, End),
            modifier = Modifier.weight(1f)
        ) {
            menuItems?.forEach { menuItem ->
                IconButton(
                    onClick = menuItem.onClick,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(backgroundBlurColor)
                        .size(32.dp)
                ) {
                    Icon(
                        painter = painterResource(id = menuItem.icon),
                        tint = white,
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Composable
private fun ToolbarHomeIcon(state: ToolbarHomeIconState, onClick: () -> Unit) {
    when {
        state.navigationIcon != null -> {
            IconButton(
                painter = painterResource(id = state.navigationIcon),
                tint = white,
                onClick = onClick
            )
        }
        state.walletIcon != null -> {
            IconButton(
                painter = rememberAsyncImagePainter(model = state.walletIcon),
                onClick = onClick
            )
        }
    }
}

@Composable
private fun IconButton(
    painter: Painter,
    tint: Color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current),
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .clip(CircleShape)
            .background(backgroundBlurColor)
            .size(40.dp)
    ) {
        Icon(
            painter = painter,
            tint = tint,
            contentDescription = null
        )
    }
}

@Preview
@Composable
private fun MainToolbarPreview() {
    FearlessTheme {
        MainToolbar(
            state = MainToolbarViewState(
                title = "My main wallet",
                homeIconState = ToolbarHomeIconState(navigationIcon = R.drawable.ic_arrow_back_24dp),
                selectorViewState = ChainSelectorViewState(
                    selectedChainId = "id",
                    selectedChainName = "Kusama",
                    selectedChainStatusColor = colorAccent
                )
            ),
            menuItems = listOf(
                MenuIconItem(icon = R.drawable.ic_scan, {}),
                MenuIconItem(icon = R.drawable.ic_search, {})
            ),
            onChangeChainClick = {},
            onNavigationClick = {}
        )
    }
}
