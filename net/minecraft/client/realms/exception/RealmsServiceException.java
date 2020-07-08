/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.exception;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsError;
import net.minecraft.client.resource.language.I18n;

@Environment(value=EnvType.CLIENT)
public class RealmsServiceException
extends Exception {
    public final int httpResultCode;
    public final String httpResponseContent;
    public final int errorCode;
    public final String errorMsg;

    public RealmsServiceException(int i, String string, RealmsError arg) {
        super(string);
        this.httpResultCode = i;
        this.httpResponseContent = string;
        this.errorCode = arg.getErrorCode();
        this.errorMsg = arg.getErrorMessage();
    }

    public RealmsServiceException(int i, String string, int j, String string2) {
        super(string);
        this.httpResultCode = i;
        this.httpResponseContent = string;
        this.errorCode = j;
        this.errorMsg = string2;
    }

    @Override
    public String toString() {
        if (this.errorCode == -1) {
            return "Realms (" + this.httpResultCode + ") " + this.httpResponseContent;
        }
        String string = "mco.errorMessage." + this.errorCode;
        String string2 = I18n.translate(string, new Object[0]);
        return (string2.equals(string) ? this.errorMsg : string2) + " - " + this.errorCode;
    }
}

