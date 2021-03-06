/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonSyntaxException
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.advancement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

public class AdvancementDisplay {
    private final Text title;
    private final Text description;
    private final ItemStack icon;
    private final Identifier background;
    private final AdvancementFrame frame;
    private final boolean showToast;
    private final boolean announceToChat;
    private final boolean hidden;
    private float xPos;
    private float yPos;

    public AdvancementDisplay(ItemStack icon, Text title, Text description, @Nullable Identifier background, AdvancementFrame frame, boolean showToast, boolean announceToChat, boolean hidden) {
        this.title = title;
        this.description = description;
        this.icon = icon;
        this.background = background;
        this.frame = frame;
        this.showToast = showToast;
        this.announceToChat = announceToChat;
        this.hidden = hidden;
    }

    public void setPosition(float xPos, float yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
    }

    public Text getTitle() {
        return this.title;
    }

    public Text getDescription() {
        return this.description;
    }

    @Environment(value=EnvType.CLIENT)
    public ItemStack getIcon() {
        return this.icon;
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public Identifier getBackground() {
        return this.background;
    }

    public AdvancementFrame getFrame() {
        return this.frame;
    }

    @Environment(value=EnvType.CLIENT)
    public float getX() {
        return this.xPos;
    }

    @Environment(value=EnvType.CLIENT)
    public float getY() {
        return this.yPos;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean shouldShowToast() {
        return this.showToast;
    }

    public boolean shouldAnnounceToChat() {
        return this.announceToChat;
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public static AdvancementDisplay fromJson(JsonObject obj) {
        MutableText lv = Text.Serializer.fromJson(obj.get("title"));
        MutableText lv2 = Text.Serializer.fromJson(obj.get("description"));
        if (lv == null || lv2 == null) {
            throw new JsonSyntaxException("Both title and description must be set");
        }
        ItemStack lv3 = AdvancementDisplay.iconFromJson(JsonHelper.getObject(obj, "icon"));
        Identifier lv4 = obj.has("background") ? new Identifier(JsonHelper.getString(obj, "background")) : null;
        AdvancementFrame lv5 = obj.has("frame") ? AdvancementFrame.forName(JsonHelper.getString(obj, "frame")) : AdvancementFrame.TASK;
        boolean bl = JsonHelper.getBoolean(obj, "show_toast", true);
        boolean bl2 = JsonHelper.getBoolean(obj, "announce_to_chat", true);
        boolean bl3 = JsonHelper.getBoolean(obj, "hidden", false);
        return new AdvancementDisplay(lv3, lv, lv2, lv4, lv5, bl, bl2, bl3);
    }

    private static ItemStack iconFromJson(JsonObject json) {
        if (!json.has("item")) {
            throw new JsonSyntaxException("Unsupported icon type, currently only items are supported (add 'item' key)");
        }
        Item lv = JsonHelper.getItem(json, "item");
        if (json.has("data")) {
            throw new JsonParseException("Disallowed data tag found");
        }
        ItemStack lv2 = new ItemStack(lv);
        if (json.has("nbt")) {
            try {
                CompoundTag lv3 = StringNbtReader.parse(JsonHelper.asString(json.get("nbt"), "nbt"));
                lv2.setTag(lv3);
            }
            catch (CommandSyntaxException commandSyntaxException) {
                throw new JsonSyntaxException("Invalid nbt tag: " + commandSyntaxException.getMessage());
            }
        }
        return lv2;
    }

    public void toPacket(PacketByteBuf buf) {
        buf.writeText(this.title);
        buf.writeText(this.description);
        buf.writeItemStack(this.icon);
        buf.writeEnumConstant(this.frame);
        int i = 0;
        if (this.background != null) {
            i |= 1;
        }
        if (this.showToast) {
            i |= 2;
        }
        if (this.hidden) {
            i |= 4;
        }
        buf.writeInt(i);
        if (this.background != null) {
            buf.writeIdentifier(this.background);
        }
        buf.writeFloat(this.xPos);
        buf.writeFloat(this.yPos);
    }

    public static AdvancementDisplay fromPacket(PacketByteBuf buf) {
        Text lv = buf.readText();
        Text lv2 = buf.readText();
        ItemStack lv3 = buf.readItemStack();
        AdvancementFrame lv4 = buf.readEnumConstant(AdvancementFrame.class);
        int i = buf.readInt();
        Identifier lv5 = (i & 1) != 0 ? buf.readIdentifier() : null;
        boolean bl = (i & 2) != 0;
        boolean bl2 = (i & 4) != 0;
        AdvancementDisplay lv6 = new AdvancementDisplay(lv3, lv, lv2, lv5, lv4, bl, false, bl2);
        lv6.setPosition(buf.readFloat(), buf.readFloat());
        return lv6;
    }

    public JsonElement toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("icon", (JsonElement)this.iconToJson());
        jsonObject.add("title", Text.Serializer.toJsonTree(this.title));
        jsonObject.add("description", Text.Serializer.toJsonTree(this.description));
        jsonObject.addProperty("frame", this.frame.getId());
        jsonObject.addProperty("show_toast", Boolean.valueOf(this.showToast));
        jsonObject.addProperty("announce_to_chat", Boolean.valueOf(this.announceToChat));
        jsonObject.addProperty("hidden", Boolean.valueOf(this.hidden));
        if (this.background != null) {
            jsonObject.addProperty("background", this.background.toString());
        }
        return jsonObject;
    }

    private JsonObject iconToJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("item", Registry.ITEM.getId(this.icon.getItem()).toString());
        if (this.icon.hasTag()) {
            jsonObject.addProperty("nbt", this.icon.getTag().toString());
        }
        return jsonObject;
    }
}

