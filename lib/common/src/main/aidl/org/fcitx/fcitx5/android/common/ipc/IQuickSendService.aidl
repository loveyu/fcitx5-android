/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * SPDX-FileCopyrightText: Copyright 2026 Fcitx5 for Android Contributors
 */
package org.fcitx.fcitx5.android.common.ipc;

import org.fcitx.fcitx5.android.common.ipc.IInputWindowStateListener;

/**
 * Quick-send service: allows a signed plugin APK to ask the running IME
 * to commit text or synthesize key events, and to subscribe to IME window
 * visibility changes. Send methods return false when no InputMethodService is
 * currently active (IME disabled / no focused editor). Callers must hold
 * ${applicationId}.permission.IPC (signature-level).
 */
interface IQuickSendService {

    /** Commit text at the current cursor. cursor=-1 means end of text. */
    boolean commitText(String text, int cursor);

    /** Send a single down+up key pair with an explicit metaState mask. */
    boolean sendKeyDownUpKey(int keyCode, int metaState);

    /** Send a full modifier+key combination (modifiers down, key, modifiers up). */
    boolean sendKeyCombination(int keyCode, boolean alt, boolean ctrl, boolean shift, boolean meta);

    /**
     * Set composing (preedit) text in the currently focused editor. The text is
     * shown underlined while composing; a subsequent [commitText] replaces it.
     * Used by the QuickSend voice plugin to stream partial recognition results.
     */
    boolean setComposingText(String text);

    /** Finish composing, leaving whatever was composed committed in place. */
    boolean finishComposingText();

    /**
     * Subscribe to IME window visibility changes. Notifications are delivered
     * to [listener] for as long as it remains registered and the binder is
     * alive; duplicate registrations of the same listener are ignored.
     */
    void registerInputWindowStateListener(IInputWindowStateListener listener);

    /** Remove a previously registered [IInputWindowStateListener]. */
    void unregisterInputWindowStateListener(IInputWindowStateListener listener);
}
