package com.example.edutrack.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import com.example.edutrack.util.AvatarHelper

@Composable
fun UserAvatar(
    userName: String,
    avatarPath: String,
    modifier: Modifier = Modifier
) {
    val bitmapState = produceState<Bitmap?>(initialValue = null, avatarPath) {
        value = if (avatarPath.isNotBlank()) {
            AvatarHelper.loadAvatarBitmap(avatarPath)
        } else {
            null
        }
    }

    val initial = userName.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "U"
    val bitmap = bitmapState.value
    val imageBitmap = remember(bitmap) { bitmap?.asImageBitmap() }

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        if (imageBitmap != null) {
            Image(
                bitmap = imageBitmap,
                contentDescription = "User Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text(
                text = initial,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}
