package jp.co.soramitsu.common.compose.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jp.co.soramitsu.common.R
import jp.co.soramitsu.common.compose.theme.FearlessTheme
import jp.co.soramitsu.common.compose.theme.customColors
import jp.co.soramitsu.common.compose.theme.customTypography
import jp.co.soramitsu.common.utils.toggleableWithNoIndication

@Composable
fun <T : MultiToggleItem> MultiToggleButton(
    state: MultiToggleButtonState<T>,
    onToggleChange: (T) -> Unit
) {
    val selectedTint = MaterialTheme.customColors.white16
    val unselectedTint = Color.Unspecified

    BackgroundCornered(backgroundColor = MaterialTheme.customColors.white04) {
        Row(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .fillMaxWidth()
        ) {
            state.toggleStates.forEach { toggleState ->
                val onToggleChangeCallback = remember<(Boolean) -> Unit> {
                    { selected ->
                        if (selected) {
                            onToggleChange(toggleState)
                        }
                    }
                }
                val isSelected = state.currentSelection == toggleState
                val backgroundTint = if (isSelected) selectedTint else unselectedTint

                val style = if (isSelected) {
                    MaterialTheme.customTypography.header5
                } else {
                    MaterialTheme.customTypography.body1
                }

                BackgroundCornered(
                    modifier = Modifier
                        .testTag("MultiToggleButton_${stringResource(toggleState.titleResId)}")
                        .weight(1f)
                        .toggleableWithNoIndication(
                            value = isSelected,
                            role = Role.Tab,
                            onValueChange = onToggleChangeCallback
                        ),
                    backgroundColor = backgroundTint
                ) {
                    Text(
                        text = stringResource(toggleState.titleResId),
                        style = style,
                        color = Color.White,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .padding(horizontal = 1.dp)
                    )
                }
            }
        }
    }
}

data class MultiToggleButtonState<T : MultiToggleItem>(
    val currentSelection: T,
    val toggleStates: List<T>
)

interface MultiToggleItem {
    val titleResId: Int
}

@Preview
@Composable
private fun PreviewMultiToggleButton() {
    val currencies = object : MultiToggleItem {
        override val titleResId = R.string.сurrencies_stub_text
    }
    val nfts = object : MultiToggleItem {
        override val titleResId = R.string.nfts_stub
    }
    FearlessTheme {
        Surface(Modifier.background(Color.Black)) {
            MultiToggleButton(
                MultiToggleButtonState(currencies, listOf(currencies, nfts)),
                onToggleChange = {}
            )
        }
    }
}
