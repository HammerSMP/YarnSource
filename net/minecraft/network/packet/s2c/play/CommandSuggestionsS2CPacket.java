/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.context.StringRange
 *  com.mojang.brigadier.suggestion.Suggestion
 *  com.mojang.brigadier.suggestion.Suggestions
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Lists;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;

public class CommandSuggestionsS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int completionId;
    private Suggestions suggestions;

    public CommandSuggestionsS2CPacket() {
    }

    public CommandSuggestionsS2CPacket(int i, Suggestions suggestions) {
        this.completionId = i;
        this.suggestions = suggestions;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.completionId = arg.readVarInt();
        int i = arg.readVarInt();
        int j = arg.readVarInt();
        StringRange stringRange = StringRange.between((int)i, (int)(i + j));
        int k = arg.readVarInt();
        ArrayList list = Lists.newArrayListWithCapacity((int)k);
        for (int l = 0; l < k; ++l) {
            String string = arg.readString(32767);
            Text lv = arg.readBoolean() ? arg.readText() : null;
            list.add(new Suggestion(stringRange, string, (Message)lv));
        }
        this.suggestions = new Suggestions(stringRange, (List)list);
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeVarInt(this.completionId);
        arg.writeVarInt(this.suggestions.getRange().getStart());
        arg.writeVarInt(this.suggestions.getRange().getLength());
        arg.writeVarInt(this.suggestions.getList().size());
        for (Suggestion suggestion : this.suggestions.getList()) {
            arg.writeString(suggestion.getText());
            arg.writeBoolean(suggestion.getTooltip() != null);
            if (suggestion.getTooltip() == null) continue;
            arg.writeText(Texts.toText(suggestion.getTooltip()));
        }
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onCommandSuggestions(this);
    }

    @Environment(value=EnvType.CLIENT)
    public int getCompletionId() {
        return this.completionId;
    }

    @Environment(value=EnvType.CLIENT)
    public Suggestions getSuggestions() {
        return this.suggestions;
    }
}

