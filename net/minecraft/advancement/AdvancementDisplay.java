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

    public AdvancementDisplay(ItemStack arg, Text arg2, Text arg3, @Nullable Identifier arg4, AdvancementFrame arg5, boolean bl, boolean bl2, boolean bl3) {
        this.title = arg2;
        this.description = arg3;
        this.icon = arg;
        this.background = arg4;
        this.frame = arg5;
        this.showToast = bl;
        this.announceToChat = bl2;
        this.hidden = bl3;
    }

    public void setPosition(float f, float g) {
        this.xPos = f;
        this.yPos = g;
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

    public static AdvancementDisplay fromJson(JsonObject jsonObject) {
        MutableText lv = Text.Serializer.fromJson(jsonObject.get("title"));
        MutableText lv2 = Text.Serializer.fromJson(jsonObject.get("description"));
        if (lv == null || lv2 == null) {
            throw new JsonSyntaxException("Both title and description must be set");
        }
        ItemStack lv3 = AdvancementDisplay.iconFromJson(JsonHelper.getObject(jsonObject, "icon"));
        Identifier lv4 = jsonObject.has("background") ? new Identifier(JsonHelper.getString(jsonObject, "background")) : null;
        AdvancementFrame lv5 = jsonObject.has("frame") ? AdvancementFrame.forName(JsonHelper.getString(jsonObject, "frame")) : AdvancementFrame.TASK;
        boolean bl = JsonHelper.getBoolean(jsonObject, "show_toast", true);
        boolean bl2 = JsonHelper.getBoolean(jsonObject, "announce_to_chat", true);
        boolean bl3 = JsonHelper.getBoolean(jsonObject, "hidden", false);
        return new AdvancementDisplay(lv3, lv, lv2, lv4, lv5, bl, bl2, bl3);
    }

    private static ItemStack iconFromJson(JsonObject jsonObject) {
        if (!jsonObject.has("item")) {
            throw new JsonSyntaxException("Unsupported icon type, currently only items are supported (add 'item' key)");
        }
        Item lv = JsonHelper.getItem(jsonObject, "item");
        if (jsonObject.has("data")) {
            throw new JsonParseException("Disallowed data tag found");
        }
        ItemStack lv2 = new ItemStack(lv);
        if (jsonObject.has("nbt")) {
            try {
                CompoundTag lv3 = StringNbtReader.parse(JsonHelper.asString(jsonObject.get("nbt"), "nbt"));
                lv2.setTag(lv3);
            }
            catch (CommandSyntaxException commandSyntaxException) {
                throw new JsonSyntaxException("Invalid nbt tag: " + commandSyntaxException.getMessage());
            }
        }
        return lv2;
    }

    public void toPacket(PacketByteBuf arg) {
        arg.writeText(this.title);
        arg.writeText(this.description);
        arg.writeItemStack(this.icon);
        arg.writeEnumConstant(this.frame);
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
        arg.writeInt(i);
        if (this.background != null) {
            arg.writeIdentifier(this.background);
        }
        arg.writeFloat(this.xPos);
        arg.writeFloat(this.yPos);
    }

    public static AdvancementDisplay fromPacket(PacketByteBuf arg) {
        Text lv = arg.readText();
        Text lv2 = arg.readText();
        ItemStack lv3 = arg.readItemStack();
        AdvancementFrame lv4 = arg.readEnumConstant(AdvancementFrame.class);
        int i = arg.readInt();
        Identifier lv5 = (i & 1) != 0 ? arg.readIdentifier() : null;
        boolean bl = (i & 2) != 0;
        boolean bl2 = (i & 4) != 0;
        AdvancementDisplay lv6 = new AdvancementDisplay(lv3, lv, lv2, lv5, lv4, bl, false, bl2);
        lv6.setPosition(arg.readFloat(), arg.readFloat());
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

