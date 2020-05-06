/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  javax.annotation.Nullable
 */
package net.minecraft.server;

import com.google.gson.JsonObject;
import java.util.Date;
import javax.annotation.Nullable;
import net.minecraft.server.BanEntry;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class BannedIpEntry
extends BanEntry<String> {
    public BannedIpEntry(String string) {
        this(string, (Date)null, (String)null, (Date)null, (String)null);
    }

    public BannedIpEntry(String string, @Nullable Date date, @Nullable String string2, @Nullable Date date2, @Nullable String string3) {
        super(string, date, string2, date2, string3);
    }

    @Override
    public Text toText() {
        return new LiteralText((String)this.getKey());
    }

    public BannedIpEntry(JsonObject jsonObject) {
        super(BannedIpEntry.getIp(jsonObject), jsonObject);
    }

    private static String getIp(JsonObject jsonObject) {
        return jsonObject.has("ip") ? jsonObject.get("ip").getAsString() : null;
    }

    @Override
    protected void fromJson(JsonObject jsonObject) {
        if (this.getKey() == null) {
            return;
        }
        jsonObject.addProperty("ip", (String)this.getKey());
        super.fromJson(jsonObject);
    }
}

