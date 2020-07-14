/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonPrimitive
 *  com.google.gson.JsonSyntaxException
 */
package net.minecraft.advancement.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.minecraft.network.PacketByteBuf;

public class CriterionProgress {
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
    private Date obtained;

    public boolean isObtained() {
        return this.obtained != null;
    }

    public void obtain() {
        this.obtained = new Date();
    }

    public void reset() {
        this.obtained = null;
    }

    public Date getObtainedDate() {
        return this.obtained;
    }

    public String toString() {
        return "CriterionProgress{obtained=" + (this.obtained == null ? "false" : this.obtained) + '}';
    }

    public void toPacket(PacketByteBuf buf) {
        buf.writeBoolean(this.obtained != null);
        if (this.obtained != null) {
            buf.writeDate(this.obtained);
        }
    }

    public JsonElement toJson() {
        if (this.obtained != null) {
            return new JsonPrimitive(FORMAT.format(this.obtained));
        }
        return JsonNull.INSTANCE;
    }

    public static CriterionProgress fromPacket(PacketByteBuf buf) {
        CriterionProgress lv = new CriterionProgress();
        if (buf.readBoolean()) {
            lv.obtained = buf.readDate();
        }
        return lv;
    }

    public static CriterionProgress obtainedAt(String datetime) {
        CriterionProgress lv = new CriterionProgress();
        try {
            lv.obtained = FORMAT.parse(datetime);
        }
        catch (ParseException parseException) {
            throw new JsonSyntaxException("Invalid datetime: " + datetime, (Throwable)parseException);
        }
        return lv;
    }
}

