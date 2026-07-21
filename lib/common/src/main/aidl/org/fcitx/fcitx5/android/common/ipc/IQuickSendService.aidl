/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * SPDX-FileCopyrightText: Copyright 2026 Fcitx5 for Android Contributors
 */
package org.fcitx.fcitx5.android.common.ipc;

/**
 * Quick-send service: allows a signed plugin APK to ask the running IME
 * to commit text or synthesize key events. All methods return false when
 * no InputMethodService is currently active (IME disabled / no focused editor).
 * Callers must hold ${applicationId}.permission.IPC (signature-level).
 */
interface IQuickSendService {

    /** Commit text at the current cursor. cursor=-1 means end of text. */
    boolean commitText(String text, int cursor);

    /** Send a single down+up key pair with an explicit metaState mask. */
    boolean sendKeyDownUpKey(int keyCode, int metaState);

    /** Send a full modifier+key combination (modifiers down, key, modifiers up). */
    boolean sendKeyCombination(int keyCode, boolean alt, boolean ctrl, boolean shift, boolean meta);
}
