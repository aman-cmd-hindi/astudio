package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private data class DustMote(
    val baseAngle: Float,
    val orbitRadiusDp: Float,
    val sizeDp: Float,
    val speedMultiplier: Float,
    val color: Color,
    val isStarSparkle: Boolean = false
)

@Composable
fun CelestialParticleEffect(
    isActive: Boolean = true,
    burstTriggerKey: Int = 0,
    modifier: Modifier = Modifier
) {
    if (!isActive) return

    val infiniteTransition = rememberInfiniteTransition(label = "stardust_ambient")

    // Ambient floating progress (0 to 1 endlessly)
    val ambientProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ambient_progress"
    )

    // Pulsing shimmer (0.4 to 1.0)
    val shimmerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer_alpha"
    )

    // Burst animation triggered on clean day maintenance / streak advance
    val burstProgress = remember { Animatable(0f) }
    LaunchedEffect(burstTriggerKey) {
        if (burstTriggerKey > 0) {
            burstProgress.snapTo(0f)
            burstProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 950, easing = FastOutSlowInEasing)
            )
        }
    }

    // Static particle definitions (seeded once)
    val motes = remember {
        listOf(
            DustMote(baseAngle = 0.2f, orbitRadiusDp = 42f, sizeDp = 2.4f, speedMultiplier = 1.0f, color = StarGoldPrimary, isStarSparkle = true),
            DustMote(baseAngle = 0.9f, orbitRadiusDp = 58f, sizeDp = 1.8f, speedMultiplier = -0.8f, color = StarGoldPrimaryContainer),
            DustMote(baseAngle = 1.6f, orbitRadiusDp = 48f, sizeDp = 3.0f, speedMultiplier = 1.2f, color = EmeraldTertiary, isStarSparkle = true),
            DustMote(baseAngle = 2.4f, orbitRadiusDp = 64f, sizeDp = 1.5f, speedMultiplier = -1.1f, color = IrisPurpleSecondary),
            DustMote(baseAngle = 3.1f, orbitRadiusDp = 40f, sizeDp = 2.8f, speedMultiplier = 0.9f, color = StarGoldPrimary, isStarSparkle = true),
            DustMote(baseAngle = 3.8f, orbitRadiusDp = 55f, sizeDp = 1.6f, speedMultiplier = -1.3f, color = StarGoldPrimaryContainer),
            DustMote(baseAngle = 4.5f, orbitRadiusDp = 62f, sizeDp = 2.5f, speedMultiplier = 1.1f, color = EmeraldTertiary),
            DustMote(baseAngle = 5.2f, orbitRadiusDp = 46f, sizeDp = 2.0f, speedMultiplier = -0.7f, color = IrisPurpleSecondary),
            DustMote(baseAngle = 5.9f, orbitRadiusDp = 52f, sizeDp = 3.2f, speedMultiplier = 1.0f, color = StarGoldPrimary, isStarSparkle = true),
            DustMote(baseAngle = 0.5f, orbitRadiusDp = 70f, sizeDp = 1.4f, speedMultiplier = -0.9f, color = StarGoldPrimary),
            DustMote(baseAngle = 2.1f, orbitRadiusDp = 68f, sizeDp = 1.6f, speedMultiplier = 0.8f, color = EmeraldTertiary),
            DustMote(baseAngle = 3.5f, orbitRadiusDp = 75f, sizeDp = 2.2f, speedMultiplier = -1.0f, color = IrisPurpleSecondary, isStarSparkle = true)
        )
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f

        // Draw Ambient Orbiting Motes
        motes.forEach { mote ->
            val angle = mote.baseAngle + (ambientProgress * 2f * PI.toFloat() * mote.speedMultiplier)
            val rx = mote.orbitRadiusDp.dp.toPx()
            val ry = (mote.orbitRadiusDp * 0.75f).dp.toPx()

            val x = centerX + cos(angle) * rx
            val y = centerY + sin(angle) * ry

            val alpha = (shimmerAlpha * (0.6f + 0.4f * sin(angle))).coerceIn(0.2f, 1f)

            if (mote.isStarSparkle) {
                drawCelestialSparkle(
                    center = Offset(x, y),
                    size = mote.sizeDp.dp.toPx() * 2.2f,
                    color = mote.color.copy(alpha = alpha),
                    rotationDegrees = (ambientProgress * 360f * mote.speedMultiplier)
                )
            } else {
                drawCircle(
                    color = mote.color.copy(alpha = alpha),
                    radius = mote.sizeDp.dp.toPx(),
                    center = Offset(x, y)
                )
                // Subtle glowing aura around primary particles
                drawCircle(
                    color = mote.color.copy(alpha = alpha * 0.35f),
                    radius = mote.sizeDp.dp.toPx() * 2.5f,
                    center = Offset(x, y)
                )
            }
        }

        // Draw Celebratory Particle Burst (when limit maintained / streak extended)
        val bProgress = burstProgress.value
        if (bProgress > 0f && bProgress < 1f) {
            val burstCount = 14
            val burstAlpha = (1f - bProgress).coerceIn(0f, 1f)
            val burstDistance = bProgress * 95.dp.toPx()

            for (i in 0 until burstCount) {
                val burstAngle = (i.toFloat() / burstCount) * 2f * PI.toFloat()
                val bx = centerX + cos(burstAngle) * burstDistance
                val by = centerY + sin(burstAngle) * (burstDistance * 0.85f)
                val color = if (i % 3 == 0) StarGoldPrimary else if (i % 3 == 1) EmeraldTertiary else IrisPurpleSecondary

                drawCircle(
                    color = color.copy(alpha = burstAlpha),
                    radius = (3.5f * (1f - bProgress * 0.5f)).dp.toPx(),
                    center = Offset(bx, by)
                )
                // Trailing sparkle
                drawCelestialSparkle(
                    center = Offset(bx, by),
                    size = (6f * (1f - bProgress * 0.4f)).dp.toPx(),
                    color = color.copy(alpha = burstAlpha * 0.8f),
                    rotationDegrees = bProgress * 180f + i * 25f
                )
            }
        }
    }
}

private fun DrawScope.drawCelestialSparkle(
    center: Offset,
    size: Float,
    color: Color,
    rotationDegrees: Float
) {
    rotate(degrees = rotationDegrees, pivot = center) {
        // Vertical ray
        drawLine(
            color = color,
            start = Offset(center.x, center.y - size),
            end = Offset(center.x, center.y + size),
            strokeWidth = 1.4f.dp.toPx()
        )
        // Horizontal ray
        drawLine(
            color = color,
            start = Offset(center.x - size, center.y),
            end = Offset(center.x + size, center.y),
            strokeWidth = 1.4f.dp.toPx()
        )
        // Center spark core
        drawCircle(
            color = Color.White.copy(alpha = color.alpha),
            radius = size * 0.3f,
            center = center
        )
    }
}
