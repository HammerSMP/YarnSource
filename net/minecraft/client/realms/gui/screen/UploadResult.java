/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public class UploadResult {
    public final int statusCode;
    public final String errorMessage;

    private UploadResult(int i, String string) {
        this.statusCode = i;
        this.errorMessage = string;
    }

    @Environment(value=EnvType.CLIENT)
    public static class Builder {
        private int statusCode = -1;
        private String errorMessage;

        public Builder withStatusCode(int i) {
            this.statusCode = i;
            return this;
        }

        public Builder withErrorMessage(String string) {
            this.errorMessage = string;
            return this;
        }

        public UploadResult build() {
            return new UploadResult(this.statusCode, this.errorMessage);
        }
    }
}

