package com.example.randominsect.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.randominsect.data.model.Insect
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.background

import com.example.randominsect.ui.components.ImageDetailDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsectCard(
    insect: Insect,
    onDelete: (Insect) -> Unit,
    modifier: Modifier = Modifier
) {
    var showImageModal by remember { mutableStateOf(false) }
    var isExpanded by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                onDelete(insect)
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false, // Disables left-to-right swipe
        enableDismissFromEndToStart = true,  // Enables right-to-left swipe
        backgroundContent = {
            val isWillDismiss = dismissState.targetValue == SwipeToDismissBoxValue.EndToStart

            val backgroundColor = if (isWillDismiss) {
                MaterialTheme.colorScheme.errorContainer
            } else {
                MaterialTheme.colorScheme.surface
            }

            val infiniteTransition = rememberInfiniteTransition(label = "HeartbeatPulse")

            val pulseScale by if (isWillDismiss) {
                infiniteTransition.animateFloat(
                    initialValue = 0.85f,
                    targetValue = 1.45f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 180),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "PulseScale"
                )
            } else {
                remember { mutableFloatStateOf(1f) }
            }

            val pulseAlpha by if (isWillDismiss) {
                infiniteTransition.animateFloat(
                    initialValue = 0.6f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 180),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "PulseAlpha"
                )
            } else {
                remember { mutableFloatStateOf(0.5f) }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
                    .clip(RoundedCornerShape(12.dp))
                    .padding(8.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .graphicsLayer(
                            scaleX = pulseScale,
                            scaleY = pulseScale,
                            alpha = pulseAlpha
                        )
                )
            }
        },
        modifier = modifier
    ) {
        // Card layout content...
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
                .clickable { isExpanded = !isExpanded },
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    insect.imageUri?.let { uri ->
                        AsyncImage(
                            model = uri,
                            contentDescription = insect.nombreComun,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { showImageModal = true },
                            contentScale = ContentScale.Crop
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = insect.nombreComun,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = insect.nombreCientifico,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (isExpanded) {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Orden: ${insect.orden}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Hábitat: ${insect.habitat}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        if (showImageModal) {
            ImageDetailDialog(
                imageUri = insect.imageUri,
                contentDescription = insect.nombreComun,
                onDismiss = { showImageModal = false }
            )
        }


    }
}
