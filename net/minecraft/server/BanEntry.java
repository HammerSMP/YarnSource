/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  javax.annotation.Nullable
 */
package net.minecraft.server;

import com.google.gson.JsonObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.Nullable;
import net.minecraft.server.ServerConfigEntry;
import net.minecraft.text.Text;

public abstract class BanEntry<T>
extends ServerConfigEntry<T> {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
    protected final Date creationDate;
    protected final String source;
    protected final Date expiryDate;
    protected final String reason;

    public BanEntry(T object, @Nullable Date date, @Nullable String string, @Nullable Date date2, @Nullable String string2) {
        super(object);
        this.creationDate = date == null ? new Date() : date;
        this.source = string == null ? "(Unknown)" : string;
        this.expiryDate = date2;
        this.reason = string2 == null ? "Banned by an operator." : string2;
    }

    protected BanEntry(T object, JsonObject jsonObject) {
        super(object);
        Object date4;
        Date date2;
        try {
            Date date = jsonObject.has("created") ? DATE_FORMAT.parse(jsonObject.get("created").getAsString()) : new Date();
        }
        catch (ParseException parseException) {
            date2 = new Date();
        }
        this.creationDate = date2;
        this.source = jsonObject.has("source") ? jsonObject.get("source").getAsString() : "(Unknown)";
        try {
            Date date3 = jsonObject.has("expires") ? DATE_FORMAT.parse(jsonObject.get("expires").getAsString()) : null;
        }
        catch (ParseException parseException2) {
            date4 = null;
        }
        this.expiryDate = date4;
        this.reason = jsonObject.has("reason") ? jsonObject.get("reason").getAsString() : "Banned by an operator.";
    }

    public String getSource() {
        return this.source;
    }

    public Date getExpiryDate() {
        return this.expiryDate;
    }

    public String getReason() {
        return this.reason;
    }

    public abstract Text toText();

    @Override
    boolean isInvalid() {
        if (this.expiryDate == null) {
            return false;
        }
        return this.expiryDate.before(new Date());
    }

    @Override
    protected void fromJson(JsonObject jsonObject) {
        jsonObject.addProperty("created", DATE_FORMAT.format(this.creationDate));
        jsonObject.addProperty("source", this.source);
        jsonObject.addProperty("expires", this.expiryDate == null ? "forever" : DATE_FORMAT.format(this.expiryDate));
        jsonObject.addProperty("reason", this.reason);
    }
}

