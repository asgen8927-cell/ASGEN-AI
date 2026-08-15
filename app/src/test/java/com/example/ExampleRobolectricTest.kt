package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.MessageStatus
import com.example.model.MessageType
import com.example.model.PresetSamples
import com.example.ui.MockupViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("WhatsApp Mockup Studio", appName)
    }

    @Test
    fun `test initial state and presets`() {
        val viewModel = MockupViewModel()
        assertNotNull(viewModel.linkData.value)
        assertNotNull(viewModel.headerState.value)
        assertNotNull(viewModel.messageState.value)

        // Apply a preset
        val youtubePreset = PresetSamples.presets.first()
        viewModel.applyPreset(youtubePreset)

        assertEquals(youtubePreset.url, viewModel.linkData.value.url)
        assertEquals(youtubePreset.previewTitle, viewModel.linkData.value.title)
        assertEquals(youtubePreset.domain, viewModel.linkData.value.domain)

        // Update message state
        viewModel.updateMessageType(MessageType.RECEIVED)
        assertEquals(MessageType.RECEIVED, viewModel.messageState.value.messageType)

        viewModel.updateMessageStatus(MessageStatus.READ_DOUBLE_BLUE)
        assertEquals(MessageStatus.READ_DOUBLE_BLUE, viewModel.messageState.value.status)
    }
}

