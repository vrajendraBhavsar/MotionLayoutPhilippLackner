package com.example.motionlayoutphilipplackner.ui.composables

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import com.example.motionlayoutphilipplackner.R
import com.example.motionlayoutphilipplackner.data.dummyData.ListPreviewParameterProvider
import com.example.motionlayoutphilipplackner.data.model.Item
import com.example.motionlayoutphilipplackner.ui.management.ToolbarState
import com.example.motionlayoutphilipplackner.ui.scrollflags.ScrollState
import com.example.motionlayoutphilipplackner.ui.theme.MotionLayoutPhilippLacknerTheme

private val MinToolbarHeight = 96.dp
private val MaxToolbarHeight = 176.dp

@Composable
private fun rememberToolbarState(toolbarHeightRange: IntRange): ToolbarState {
    return rememberSaveable(saver = ScrollState.Saver) {
        ScrollState(toolbarHeightRange)
    }
}

@Preview(showBackground = true)
@Composable
fun CatalogPreview(
    @PreviewParameter(ListPreviewParameterProvider::class) list: List<Item>
) {
    MotionLayoutPhilippLacknerTheme() {
        ProductCatalog(
            item = list,
            columns = 2,
            onPrivacyTipButtonClicked = {},
            onSettingsButtonClicked = {},
            modifier = Modifier.fillMaxSize()
        )
    }
}

@OptIn(ExperimentalMotionApi::class)
@Composable
fun ProductCatalog(
    item: List<Item>,
    columns: Int,
    onPrivacyTipButtonClicked: () -> Unit,
    onSettingsButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {

    val toolbarHeightRange = with(LocalDensity.current) {
        MinToolbarHeight.roundToPx()..MaxToolbarHeight.roundToPx()
    }
    val toolbarState = rememberToolbarState(toolbarHeightRange)
    val scrollState = rememberScrollState()

    toolbarState.scrollValue = scrollState.value

    //...Motion layout
    val context = LocalContext.current  //to get the raw file, we need context.
    Log.d("TAG", "ProfileHeader: progress => ${toolbarState.progress}")
    val motionScene = remember {    // To include raw file, the JSON5 script file
        context.resources.openRawResource(R.raw.motion_scene_netflix)
            .readBytes()
            .decodeToString()   //readBytes -> cuz we want motionScene in String
    }

    MotionLayout(
        motionScene = MotionScene(content = motionScene),
        progress = toolbarState.progress,
        modifier = Modifier.fillMaxWidth()
    ) {
        val propertiesOfContentImage = motionProperties(id = "content_img")   //motionProperties -> to get the custom properties

        Box(modifier = modifier) {
            GridItemHandler(
                list = item,
                columns = columns,
                modifier = Modifier
                    .layoutId("data_content"),
                scrollState = scrollState,
                contentPadding = PaddingValues(top = MaxToolbarHeight)
            )
            /*Toolbar(
                progress = toolbarState.progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(with(LocalDensity.current) { toolbarState.height.toDp()})
                    .graphicsLayer { translationY = toolbarState.offset }
            )*/

            CollapsingToolbar(
                backgroundImageResId = R.drawable.ic_starwars,
                progress = toolbarState.progress,
                onPrivacyTipButtonClicked = onPrivacyTipButtonClicked,
                onSettingsButtonClicked = onSettingsButtonClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .layoutId("collapsing_box")
                    .height(with(LocalDensity.current) { toolbarState.height.toDp() })
                    .graphicsLayer { translationY = toolbarState.offset }
            )

           /* Image(
                painter = painterResource(id = R.drawable.ic_baby_yoda),
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(100.dp)
                    .layoutId("content_img"),
                contentDescription = "Content image holder"
            )*/
        }

        Icon(  //Shield icon
            painter = painterResource(id = R.drawable.ic_shield), contentDescription = null,
            modifier = Modifier
                /*.clip(CircleShape)
                .border(
                    width = 2.dp,
//                    color = Color.Green,
                    color = propertiesOfProfilePic.value.color(name = "color"),
                    shape = CircleShape
                )*/
                .layoutId("content_img"),    // Same ID that we have used in motion_scene.json5
            tint = Color.White
        )
    }
}

/*
@Composable
fun ProductCatalog(
    item: List<Item>,
    columns: Int,   //number of grid
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState(),
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {

    val chunkedList = remember(item, columns) {
        item.chunked(columns)
    }

    Column(
        modifier = modifier.verticalScroll(scrollState)
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(contentPadding.calculateTopPadding())
        )

        chunkedList.forEach { chunk ->
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {

                Spacer(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(contentPadding.calculateStartPadding(LocalLayoutDirection.current))
                )

                chunk.forEach { item ->
                    ItemCard(
                        item = item,
                        modifier = Modifier
                            .padding(2.dp)
                            .weight(1f)
                    )
                }

                val emptyCells = columns - chunk.size
                if (emptyCells > 0) {
                    Spacer(modifier = Modifier.weight(emptyCells.toFloat()))
                }

                Spacer(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(contentPadding.calculateEndPadding(LocalLayoutDirection.current))
                )
            }
        }

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(contentPadding.calculateBottomPadding())
        )
    }
}*/
