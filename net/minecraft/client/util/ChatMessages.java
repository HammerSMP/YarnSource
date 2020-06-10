/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.util;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.TextCollector;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;

@Environment(value=EnvType.CLIENT)
public class ChatMessages {
    private static final StringRenderable field_25263 = StringRenderable.plain(" ");

    private static String getRenderedChatMessage(String string) {
        return MinecraftClient.getInstance().options.chatColors ? string : Formatting.strip(string);
    }

    public static List<StringRenderable> breakRenderedChatMessageLines(StringRenderable arg, int i, TextRenderer arg22) {
        TextCollector lv = new TextCollector();
        arg.visit((arg2, string) -> {
            lv.add(StringRenderable.styled(ChatMessages.getRenderedChatMessage(string), arg2));
            return Optional.empty();
        }, Style.EMPTY);
        List<StringRenderable> list = arg22.getTextHandler().method_29971(lv.getCombined(), i, Style.EMPTY, field_25263);
        if (list.isEmpty()) {
            return Lists.newArrayList((Object[])new StringRenderable[]{StringRenderable.EMPTY});
        }
        return list;
    }
}

