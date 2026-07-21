/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * SPDX-FileCopyrightText: Copyright 2026 Fcitx5 for Android Contributors
 */
package org.fcitx.fcitx5.android

import android.os.RemoteCallbackList
import org.fcitx.fcitx5.android.common.ipc.IInputWindowStateListener

/**
 * Bridges IME soft-input window visibility events to subscribed QuickSend
 * plugin overlays via [IInputWindowStateListener].
 *
 * [FcitxInputMethodService] pushes state changes here from
 * [org.fcitx.fcitx5.android.input.FcitxInputMethodService.onWindowShown] /
 * [org.fcitx.fcitx5.android.input.FcitxInputMethodService.onWindowHidden];
 * [QuickSendService] registers/unregisters plugin listeners on the binder
 * thread. Using a stand-alone object (mirroring FcitxInputMethodServiceHolder)
 * keeps the IMS free of any plugin-facing coupling, minimising upstream merge
 * friction.
 *
 * App-internal: not part of any published API surface.
 */
object QuickSendStateBroadcaster {

    private val callbacks = RemoteCallbackList<IInputWindowStateListener>()

    fun register(listener: IInputWindowStateListener) {
        callbacks.register(listener)
    }

    fun unregister(listener: IInputWindowStateListener) {
        callbacks.unregister(listener)
    }

    /** Called from FcitxInputMethodService.onWindowShown. */
    fun notifyShown() = broadcast { it.onInputWindowShown() }

    /** Called from FcitxInputMethodService.onWindowHidden. */
    fun notifyHidden() = broadcast { it.onInputWindowHidden() }

    private inline fun broadcast(block: (IInputWindowStateListener) -> Unit) {
        val n = callbacks.beginBroadcast()
        try {
            for (i in 0 until n) block(callbacks.getBroadcastItem(i))
        } finally {
            callbacks.finishBroadcast()
        }
    }
}
