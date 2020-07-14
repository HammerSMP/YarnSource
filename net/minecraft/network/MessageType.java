/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public enum MessageType {
    CHAT(0, false),
    SYSTEM(1, true),
    GAME_INFO(2, true);

    private final byte id;
    private final boolean interruptsNarration;

    private MessageType(byte id, boolean interruptsNarration) {
        this.id = id;
        this.interruptsNarration = interruptsNarration;
    }

    public byte getId() {
        return this.id;
    }

    public static MessageType byId(byte id) {
        for (MessageType lv : MessageType.values()) {
            if (id != lv.id) continue;
            return lv;
        }
        return CHAT;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean interruptsNarration() {
        return this.interruptsNarration;
    }
}

