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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.TextCollector;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Environment(value=EnvType.CLIENT)
public class ChatMessages {
    private static String getRenderedChatMessage(String string) {
        return MinecraftClient.getInstance().options.chatColors ? string : Formatting.strip(string);
    }

    public static List<Text> breakRenderedChatMessageLines(Text arg, int i, TextRenderer arg22) {
        TextCollector lv = new TextCollector();
        arg.visit((arg2, string) -> {
            lv.add(new LiteralText(ChatMessages.getRenderedChatMessage(string)).setStyle(arg2));
            return Optional.empty();
        }, Style.EMPTY);
        List<Text> list = arg22.getTextHandler().wrapLines(lv.getCombined(), i, Style.EMPTY);
        if (list.isEmpty()) {
            return Lists.newArrayList((Object[])new Text[]{LiteralText.EMPTY});
        }
        ArrayList list2 = Lists.newArrayList();
        list2.add(list.get(0));
        for (int j = 1; j < list.size(); ++j) {
            list2.add(new LiteralText(" ").append(list.get(j)));
        }
        return list2;
    }
}

