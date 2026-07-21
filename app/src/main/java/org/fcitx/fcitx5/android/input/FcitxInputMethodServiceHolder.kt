/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * SPDX-FileCopyrightText: Copyright 2026 Fcitx5 for Android Contributors
 */
package org.fcitx.fcitx5.android.input

import java.util.concurrent.atomic.AtomicReference

/**
 * Holds the currently-active [FcitxInputMethodService] instance so that
 * app-internal IPC services (e.g. QuickSendService) can reach it without
 * coupling to the IMS lifecycle owner. The IMS self-registers in
 * [FcitxInputMethodService.onCreate] and self-unregisters in
 * [FcitxInputMethodService.onDestroy].
 *
 * App-internal: not part of any published API surface.
 */
object FcitxInputMethodServiceHolder {

    private val ref = AtomicReference<FcitxInputMethodService?>(null)

    val instance: FcitxInputMethodService?
        get() = ref.get()

    internal fun attach(service: FcitxInputMethodService) {
        ref.set(service)
    }

    internal fun detach(service: FcitxInputMethodService) {
        ref.compareAndSet(service, null)
    }
}
