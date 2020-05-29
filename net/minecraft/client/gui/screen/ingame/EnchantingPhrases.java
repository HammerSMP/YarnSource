/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.ingame;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5348;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class EnchantingPhrases {
    private static final Identifier field_24283 = new Identifier("minecraft", "alt");
    private static final Style field_24284 = Style.EMPTY.withFont(field_24283);
    private static final EnchantingPhrases INSTANCE = new EnchantingPhrases();
    private final Random random = new Random();
    private final String[] phrases = new String[]{"the", "elder", "scrolls", "klaatu", "berata", "niktu", "xyzzy", "bless", "curse", "light", "darkness", "fire", "air", "earth", "water", "hot", "dry", "cold", "wet", "ignite", "snuff", "embiggen", "twist", "shorten", "stretch", "fiddle", "destroy", "imbue", "galvanize", "enchant", "free", "limited", "range", "of", "towards", "inside", "sphere", "cube", "self", "other", "ball", "mental", "physical", "grow", "shrink", "demon", "elemental", "spirit", "animal", "creature", "beast", "humanoid", "undead", "fresh", "stale", "phnglui", "mglwnafh", "cthulhu", "rlyeh", "wgahnagl", "fhtagn", "baguette"};

    private EnchantingPhrases() {
    }

    public static EnchantingPhrases getInstance() {
        return INSTANCE;
    }

    public class_5348 generatePhrase(TextRenderer arg, int i) {
        StringBuilder stringBuilder = new StringBuilder();
        int j = this.random.nextInt(2) + 3;
        for (int k = 0; k < j; ++k) {
            if (k != 0) {
                stringBuilder.append(" ");
            }
            stringBuilder.append(Util.getRandom(this.phrases, this.random));
        }
        return arg.getTextHandler().trimToWidth(new LiteralText(stringBuilder.toString()).fillStyle(field_24284), i, Style.EMPTY);
    }

    public void setSeed(long l) {
        this.random.setSeed(l);
    }
}

