package com.example.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.R

/**
 * Cityscape Artwork loaded from the user's uploaded design image (untitled_design.png).
 * You can modify or replace this file to update the bottom banner design.
 */
@Composable
fun CityscapeArtwork() {
  Image(
    painter = painterResource(id = R.drawable.untitled_design),
    contentDescription = "Cityscape Banner",
    contentScale = ContentScale.Crop,
    modifier = Modifier
      .fillMaxWidth()
      .height(140.dp)
  )
}
