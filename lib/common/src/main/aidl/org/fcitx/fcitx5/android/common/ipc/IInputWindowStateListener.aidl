/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * SPDX-FileCopyrightText: Copyright 2026 Fcitx5 for Android Contributors
 */
package org.fcitx.fcitx5.android.common.ipc;

/**
 * Push-based notification of the IME soft-input window visibility, consumed by
 * the QuickSend plugin overlay so its floating button only appears while the
 * keyboard is actually shown and auto-hides when it is dismissed. All callbacks
 * are one-way and best-effort; listeners are expected to be idempotent.
 */
oneway interface IInputWindowStateListener {

    /** Called by the host IME after its input window becomes visible. */
    void onInputWindowShown();

    /** Called by the host IME after its input window is hidden. */
    void onInputWindowHidden();
}
