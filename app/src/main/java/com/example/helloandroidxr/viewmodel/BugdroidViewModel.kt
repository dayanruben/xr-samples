/*
 * Copyright 2024 The Android Open Source Project
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

package com.example.helloandroidxr.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

private const val DEFAULT_SCALE = 1.0f
private const val DEFAULT_X_ROTATION = 0.0f
private const val DEFAULT_Y_ROTATION = 0.0f
private const val DEFAULT_Z_ROTATION = 0.0f
private const val DEFAULT_W_ROTATION = 1.0f
private const val DEFAULT_X_OFFSET = 0.0f
private const val DEFAULT_Y_OFFSET = 0.0f
private const val DEFAULT_Z_OFFSET = 400.0f
private const val DEFAULT_X_MATERIAL_COLOR = 0.0f
private const val DEFAULT_Y_MATERIAL_COLOR = 1.0f
private const val DEFAULT_Z_MATERIAL_COLOR = 0.0f
private const val DEFAULT_W_MATERIAL_COLOR = 0.0f
private const val DEFAULT_AMBIENT_OCCLUSION = 0.5f
private const val DEFAULT_METALLIC = 0.0f
private const val DEFAULT_ROUGHNESS = 0.0f
private const val MIN_COERCE_VALUE = -1500.0f
private const val MAX_COERCE_VALUE = 1500.0f

// Represents the rotation values for the 3D model
data class ModelRotation(
    val x: Float = DEFAULT_X_ROTATION,
    val y: Float = DEFAULT_Y_ROTATION,
    val z: Float = DEFAULT_Z_ROTATION,
    val w: Float = DEFAULT_W_ROTATION,
)

// Represents the offset values for the 3D model
data class ModelOffset(
    val x: Float = DEFAULT_X_OFFSET,
    val y: Float = DEFAULT_Y_OFFSET,
    val z: Float = DEFAULT_Z_OFFSET,
)

// Represents the material color values for the 3D model
data class ModelMaterialColor(
    val x: Float = DEFAULT_X_MATERIAL_COLOR,
    val y: Float = DEFAULT_Y_MATERIAL_COLOR,
    val z: Float = DEFAULT_Z_MATERIAL_COLOR,
    val w: Float = DEFAULT_W_MATERIAL_COLOR,
)

// Represents the material properties for the 3D model
data class ModelMaterialProperties(
    val ambientOcclusion: Float = DEFAULT_AMBIENT_OCCLUSION,
    val metallic: Float = DEFAULT_METALLIC,
    val roughness: Float = DEFAULT_ROUGHNESS,
)

// Represents the transform values for the 3D model
data class ModelTransform(
    val scale: Float = DEFAULT_SCALE,
    val rotation: ModelRotation = ModelRotation(),
    val offset: ModelOffset = ModelOffset(),
    val materialColor: ModelMaterialColor = ModelMaterialColor(),
    val materialProperties: ModelMaterialProperties = ModelMaterialProperties(),
)

// Enum to represent which slider group is visible.
// This prevents impossible states, like two groups showing at once.
enum class SliderGroup {
    NONE, SCALE, ROTATION, OFFSET, MATERIAL_COLORS, MATERIAL_PROPERTIES
}

// The single state object for the entire screen
data class BugdroidUiState(
    val showBugdroid: Boolean = false,
    val animateBugdroid: Boolean = false,
    val visibleSliderGroup: SliderGroup = SliderGroup.NONE,
    val modelTransform: ModelTransform = ModelTransform(),
)

class BugdroidViewModel : ViewModel() {
    // Private mutable state
    private val _uiState = MutableStateFlow(BugdroidUiState())

    // Public immutable state flow for the UI to observe
    val uiState: StateFlow<BugdroidUiState> = _uiState.asStateFlow()

    fun updateShownSliderGroup(group: SliderGroup) {
        _uiState.update { currentState ->
            currentState.copy(visibleSliderGroup = group)
        }
    }

    fun updateShowBugdroid() {
        _uiState.update { currentState ->
            currentState.copy(showBugdroid = !currentState.showBugdroid)
        }
    }

    fun updateAnimateBugdroid() {
        _uiState.update { currentState ->
            currentState.copy(animateBugdroid = !currentState.animateBugdroid)
        }
    }

    fun updateScale(newScale: Float) {
        _uiState.update { currentState ->
            currentState.copy(
                modelTransform = currentState.modelTransform.copy(
                    scale = newScale.coerceIn(MIN_COERCE_VALUE, MAX_COERCE_VALUE)
                )
            )
        }
    }

    fun updateRotation(newRotation: ModelRotation) {
        _uiState.update { currentState ->
            currentState.copy(
                modelTransform = currentState.modelTransform.copy(
                    rotation = newRotation.copy(
                        x = newRotation.x.coerceIn(MIN_COERCE_VALUE, MAX_COERCE_VALUE),
                        y = newRotation.y.coerceIn(MIN_COERCE_VALUE, MAX_COERCE_VALUE),
                        z = newRotation.z.coerceIn(MIN_COERCE_VALUE, MAX_COERCE_VALUE),
                        w = newRotation.w.coerceIn(MIN_COERCE_VALUE, MAX_COERCE_VALUE)
                    )
                )
            )
        }
    }

    fun updateOffset(newOffset: ModelOffset) {
        _uiState.update { currentState ->
            currentState.copy(
                modelTransform = currentState.modelTransform.copy(
                    offset = currentState.modelTransform.offset.copy(
                        x = newOffset.x.coerceIn(MIN_COERCE_VALUE, MAX_COERCE_VALUE),
                        y = newOffset.y.coerceIn(MIN_COERCE_VALUE, MAX_COERCE_VALUE),
                        z = newOffset.z.coerceIn(MIN_COERCE_VALUE, MAX_COERCE_VALUE)
                    )
                )
            )
        }
    }

    fun updateMaterialColor(newMaterialColor: ModelMaterialColor) {
        _uiState.update { currentState ->
            currentState.copy(
                modelTransform = currentState.modelTransform.copy(
                    materialColor = currentState.modelTransform.materialColor.copy(
                        x = newMaterialColor.x.coerceIn(MIN_COERCE_VALUE, MAX_COERCE_VALUE),
                        y = newMaterialColor.y.coerceIn(MIN_COERCE_VALUE, MAX_COERCE_VALUE),
                        z = newMaterialColor.z.coerceIn(MIN_COERCE_VALUE, MAX_COERCE_VALUE),
                        w = newMaterialColor.w.coerceIn(MIN_COERCE_VALUE, MAX_COERCE_VALUE)
                    )
                )
            )
        }
    }

    fun updateMaterialProperties(newMaterialProperties: ModelMaterialProperties) {
        _uiState.update { currentState ->
            currentState.copy(
                modelTransform = currentState.modelTransform.copy(
                    materialProperties = currentState.modelTransform.materialProperties.copy(
                        ambientOcclusion = newMaterialProperties.ambientOcclusion.coerceIn(
                            MIN_COERCE_VALUE,
                            MAX_COERCE_VALUE
                        ),
                        metallic = newMaterialProperties.metallic.coerceIn(
                            MIN_COERCE_VALUE,
                            MAX_COERCE_VALUE
                        ),
                        roughness = newMaterialProperties.roughness.coerceIn(
                            MIN_COERCE_VALUE,
                            MAX_COERCE_VALUE
                        ),
                    )
                )
            )
        }
    }

    fun resetModel() {
        _uiState.update { currentState ->
            currentState.copy(
                modelTransform = ModelTransform()
            )
        }
    }
}