/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.server.command;

import java.util.UUID;
import net.minecraft.text.Text;

public interface CommandOutput {
    public static final CommandOutput DUMMY = new CommandOutput(){

        @Override
        public void sendSystemMessage(Text message, UUID senderUuid) {
        }

        @Override
        public boolean shouldReceiveFeedback() {
            return false;
        }

        @Override
        public boolean shouldTrackOutput() {
            return false;
        }

        @Override
        public boolean shouldBroadcastConsoleToOps() {
            return false;
        }
    };

    public void sendSystemMessage(Text var1, UUID var2);

    public boolean shouldReceiveFeedback();

    public boolean shouldTrackOutput();

    public boolean shouldBroadcastConsoleToOps();
}

