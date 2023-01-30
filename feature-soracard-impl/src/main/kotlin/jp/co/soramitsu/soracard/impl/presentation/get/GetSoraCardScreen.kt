package jp.co.soramitsu.soracard.impl.presentation.get

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import jp.co.soramitsu.common.compose.component.MarginVertical
import jp.co.soramitsu.common.compose.component.ShapeButton
import jp.co.soramitsu.common.compose.component.Toolbar
import jp.co.soramitsu.common.compose.component.ToolbarViewState
import jp.co.soramitsu.common.compose.theme.colorAccentDark
import jp.co.soramitsu.feature_soracard_impl.R
import jp.co.soramitsu.ui_core.component.button.properties.Size
import jp.co.soramitsu.ui_core.resources.Dimens
import jp.co.soramitsu.ui_core.theme.borderRadius
import jp.co.soramitsu.ui_core.theme.customColors
import jp.co.soramitsu.ui_core.theme.customTypography
import jp.co.soramitsu.ui_core.theme.elevation

@Composable
fun GetSoraCardScreenWithToolbar(
    scrollState: ScrollState,
    onEnableCard: () -> Unit,
    onNavigationClick: () -> Unit
) {
    val toolbarViewState = ToolbarViewState(
        title = stringResource(id = R.string.profile_soracard_title),
        navigationIcon = R.drawable.ic_arrow_left_24
    )

    Column(
        Modifier
            .fillMaxSize()
            .background(color = Color.Black)
    ) {
        Toolbar(
            modifier = Modifier.height(62.dp),
            state = toolbarViewState,
            onNavigationClick = onNavigationClick
        )
        MarginVertical(margin = 8.dp)
        GetSoraCardScreen(scrollState = scrollState, onEnableCard = onEnableCard)
    }
}

@Composable
fun GetSoraCardScreen(
    scrollState: ScrollState,
    onEnableCard: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = Dimens.x2)
            .padding(bottom = Dimens.x5)
    ) {
        Card(
            modifier = Modifier.fillMaxSize(),
            cornerRadius = 12.dp,
            elevation = 0.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color(0xFF1D1D1F))
                    .padding(Dimens.x2)
            ) {
                Image(
                    modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                    painter = painterResource(R.drawable.ic_sora_card),
                    contentDescription = null
                )

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Dimens.x2, start = Dimens.x1, end = Dimens.x1),
                    text = stringResource(R.string.sora_card_title),
                    style = MaterialTheme.customTypography.headline2,
                    color = Color.White
                )

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Dimens.x2, start = Dimens.x1, end = Dimens.x1),
                    text = stringResource(R.string.sora_card_description),
                    style = MaterialTheme.customTypography.paragraphM,
                    color = Color.White
                )

                AnnualFee()

                FreeCardIssuance()

                ShapeButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = Dimens.x2, horizontal = Dimens.x1)
                        .height(Size.Large),
                    shape = RoundedCornerShape(12.dp),
                    onClick = onEnableCard,
                    backgroundColor = colorAccentDark
                ) {
                    Text(
                        text = stringResource(R.string.sora_card_enable_card_title).uppercase(),
                        style = MaterialTheme.customTypography.headline3
                    )
                }
            }
        }
    }
}

@Composable
private fun AnnualFee() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = Dimens.x2, start = Dimens.x1, end = Dimens.x1),
        cornerRadius = 12.dp,
        backgroundColor = Color(0xFF131313),
        elevation = 0.dp
    ) {
        Text(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = Dimens.x2, horizontal = Dimens.x3),
            text = AnnotatedString(
                text = stringResource(R.string.sora_card_annual_service_fee),
                spanStyles = listOf(
                    AnnotatedString.Range(SpanStyle(fontWeight = FontWeight.Bold), 0, 3)
                )
            ),
            style = MaterialTheme.customTypography.textL,
            color = Color.White
        )
    }
}

@Composable
private fun FreeCardIssuance() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = Dimens.x2, start = Dimens.x1, end = Dimens.x1),
        cornerRadius = 12.dp,
        backgroundColor = Color(0xFF131313),
        elevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Dimens.x2, horizontal = Dimens.x3)
        ) {
            Text(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = Dimens.x2),
                text = AnnotatedString(
                    text = stringResource(R.string.sora_card_free_card_issuance),
                    spanStyles = listOf(
                        AnnotatedString.Range(SpanStyle(fontWeight = FontWeight.Bold), 0, 4)
                    )
                ),
                style = MaterialTheme.customTypography.textL,
                color = Color.White
            )

            Text(
                modifier = Modifier
                    .fillMaxSize(),
                text = stringResource(R.string.sora_card_free_card_issuance_conditions_xor),
                style = MaterialTheme.customTypography.paragraphM,
                color = Color.White
            )

            Text(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = Dimens.x2),
                text = stringResource(R.string.sora_card_free_card_issuance_conditions_euro),
                style = MaterialTheme.customTypography.paragraphM,
                color = Color.White
            )
        }
    }
}

@Composable
fun Card(
    modifier: Modifier = Modifier,
    elevation: Dp = MaterialTheme.elevation.l,
    cornerRadius: Dp = MaterialTheme.borderRadius.xl,
    backgroundColor: Color = MaterialTheme.customColors.bgSurface,
    content: @Composable () -> Unit
) {
    androidx.compose.material.Card(
        modifier = modifier.shadow(
            elevation = elevation,
            ambientColor = MaterialTheme.customColors.elevation,
            spotColor = MaterialTheme.customColors.elevation,
            shape = RoundedCornerShape(cornerRadius)
        ),
        shape = RoundedCornerShape(cornerRadius),
        backgroundColor = backgroundColor
    ) {
        content()
    }
}

@Preview
@Composable
private fun PreviewGetSoraCardScreen() {
    GetSoraCardScreenWithToolbar(
        scrollState = rememberScrollState(),
        onEnableCard = {},
        onNavigationClick = {}
    )
}
