package com.papb.petto.ui.missing

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

/** Carousel foto. */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PetPhotoPager(
    photoUrls: List<String>,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { photoUrls.size })
    val scope = rememberCoroutineScope()
    val colors = MaterialTheme.colorScheme

    Box(modifier = modifier.clip(RoundedCornerShape(16.dp)).background(colors.surfaceVariant)) {
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
            AsyncImage(
                model = photoUrls[page],
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        if (photoUrls.size > 1) {
            if (pagerState.currentPage > 0) {
                ArrowButton(
                    icon = { Icon(Icons.Filled.KeyboardArrowLeft, "Previous photo", tint = Color.White) },
                    onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) } },
                    modifier = Modifier.align(Alignment.CenterStart).padding(8.dp)
                )
            }
            if (pagerState.currentPage < photoUrls.lastIndex) {
                ArrowButton(
                    icon = { Icon(Icons.Filled.KeyboardArrowRight, "Next photo", tint = Color.White) },
                    onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } },
                    modifier = Modifier.align(Alignment.CenterEnd).padding(8.dp)
                )
            }
            Row(
                modifier = Modifier.align(Alignment.BottomEnd).padding(12.dp),
            ) {
                repeat(photoUrls.size) { index ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 2.dp)
                            .size(if (index == pagerState.currentPage) 8.dp else 6.dp)
                            .clip(CircleShape)
                            .background(if (index == pagerState.currentPage) Color.White else Color(0x99FFFFFF))
                    )
                }
            }
        }
    }
}

@Composable
private fun ArrowButton(
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = Color(0x66000000),
        modifier = modifier.size(32.dp)
    ) {
        Box(contentAlignment = Alignment.Center) { icon() }
    }
}
