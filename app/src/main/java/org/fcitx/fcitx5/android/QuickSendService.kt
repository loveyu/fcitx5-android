/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * SPDX-FileCopyrightText: Copyright 2026 Fcitx5 for Android Contributors
 */
package org.fcitx.fcitx5.android

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import org.fcitx.fcitx5.android.common.ipc.IInputWindowStateListener
import org.fcitx.fcitx5.android.common.ipc.IQuickSendService
import org.fcitx.fcitx5.android.input.FcitxInputMethodServiceHolder
import timber.log.Timber

/**
 * Independent IPC entry point for the QuickSend plugin path. Exposes only
 * send/commit operations, deliberately decoupled from [FcitxRemoteService]
 * so upstream changes to the clipboard/pinyin side do not affect this surface.
 *
 * All binder methods return false when no IMS is currently attached.
 * Key-event methods only touch [android.view.inputmethod.InputConnection.sendKeyEvent]
 * (thread-safe) and are invoked synchronously on the binder thread.
 * [commitText] mutates IMS cursor state, so it is dispatched onto the IMS's
 * lifecycleScope (main dispatcher) with a bounded wait.
 */
class QuickSendService : Service() {

    private val binder = object : IQuickSendService.Stub() {

        override fun commitText(text: String, cursor: Int): Boolean {
            val ims = FcitxInputMethodServiceHolder.instance ?: run {
                Timber.w("QuickSend: no active IMS, drop commitText")
                return false
            }
            return runBlocking {
                withTimeoutOrNull(COMMIT_TIMEOUT_MS) {
                    ims.lifecycleScope.launch { ims.commitText(text, cursor) }.join()
                } != null
            }
        }

        override fun sendKeyDownUpKey(keyCode: Int, metaState: Int): Boolean {
            val ims = FcitxInputMethodServiceHolder.instance ?: return false
            return runCatching { ims.sendKeyDownUpKey(keyCode, metaState) }
                .onFailure { Timber.w(it, "QuickSend sendKeyDownUpKey failed") }
                .map { true }
                .getOrDefault(false)
        }

        override fun sendKeyCombination(
            keyCode: Int,
            alt: Boolean,
            ctrl: Boolean,
            shift: Boolean,
            meta: Boolean
        ): Boolean {
            val ims = FcitxInputMethodServiceHolder.instance ?: return false
            return runCatching { ims.sendKeyCombination(keyCode, alt, ctrl, shift, meta) }
                .onFailure { Timber.w(it, "QuickSend sendKeyCombination failed") }
                .map { true }
                .getOrDefault(false)
        }

        override fun registerInputWindowStateListener(listener: IInputWindowStateListener) {
            QuickSendStateBroadcaster.register(listener)
        }

        override fun unregisterInputWindowStateListener(listener: IInputWindowStateListener) {
            QuickSendStateBroadcaster.unregister(listener)
        }
    }

    override fun onBind(intent: Intent): IBinder = binder

    private companion object {
        const val COMMIT_TIMEOUT_MS = 2000L
    }
}
