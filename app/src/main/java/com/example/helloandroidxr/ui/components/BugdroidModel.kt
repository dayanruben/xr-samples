/*
 * Copyright 2025 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.helloandroidxr.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.xr.compose.platform.LocalSession
import androidx.xr.compose.spatial.Subspace
import androidx.xr.compose.subspace.SceneCoreEntity
import androidx.xr.compose.subspace.SceneCoreEntitySizeAdapter
import androidx.xr.compose.subspace.layout.SubspaceModifier
import androidx.xr.compose.subspace.layout.scale
import androidx.xr.compose.unit.Meter
import androidx.xr.scenecore.GltfModel
import androidx.xr.scenecore.GltfModelEntity
import com.example.helloandroidxr.R
import java.io.InputStream

// Bugdroid glb height in meters
private const val bugdroidHeight = 2.08f
// The desired amount of the available layout height to use for the bugdroid
private const val fillRatio = 0.5f

@Composable
fun BugdroidModel(showBugdroid: Boolean, modifier: SubspaceModifier = SubspaceModifier) {
    if (showBugdroid) {
        val xrSession = checkNotNull(LocalSession.current)
        // Load the GltfModel data before creating the entity.
        var gltfModel by remember { mutableStateOf<GltfModel?>(null) }
        val context = LocalContext.current

        LaunchedEffect(Unit) {
            if (gltfModel == null) {
                gltfModel = BugdroidGltfModelCache.getOrLoadModel(xrSession, context)
            }
        }
        gltfModel?.let { gltfModel ->
            Subspace {
                val density = LocalDensity.current
                var scale by remember { mutableFloatStateOf(1f) }
                SceneCoreEntity(
                    factory = {
                        GltfModelEntity.create(xrSession, gltfModel).also { entity ->
                            entity.startAnimation(
                                loop = true, animationName = "Armature|Take 001|BaseLayer"
                            )
                        }
                    },
                    sizeAdapter = SceneCoreEntitySizeAdapter(onLayoutSizeChanged = { size ->
                        // Calculate the scale we should use for the entity based on the size the
                        // layout is setting on the SceneCoreEntity
                        val scaleToFillLayoutHeight = Meter
                            .fromPixel(size.height.toFloat(), density).toM() / bugdroidHeight
                        //Limit the scale to a ratio of the available space
                        scale = scaleToFillLayoutHeight * fillRatio
                    }),
                    modifier = modifier.scale(scale)
                )
            }
        }
    }
    // Clean up the cache when the composable leaves the composition.
    DisposableEffect(Unit) {
        onDispose {
            BugdroidGltfModelCache.clearCache()
        }
    }
}

/**
 * Singleton object to cache the GltfModel.
 */
private object BugdroidGltfModelCache {
    private var cachedModel: GltfModel? = null

    @SuppressLint("RestrictedApi")
    suspend fun getOrLoadModel(
        xrCoreSession: androidx.xr.runtime.Session, context: Context
    ): GltfModel? {
        return if (cachedModel == null) {
            try {
                val inputStream: InputStream =
                    context.resources.openRawResource(R.raw.bugdroid_animated_wave)
                cachedModel = GltfModel.create(
                    xrCoreSession, inputStream.readBytes(), "BUGDROID"
                )
                cachedModel
            } catch (e: Exception) {
                Log.e(TAG, "Error loading GLTF model", e)
                null
            }
        } else {
            cachedModel
        }
    }

    fun clearCache() {
        cachedModel = null
    }

    const val TAG = "BugdroidGltfModelCache"
}