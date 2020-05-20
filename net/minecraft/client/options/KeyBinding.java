/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.options;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class KeyBinding
implements Comparable<KeyBinding> {
    private static final Map<String, KeyBinding> keysById = Maps.newHashMap();
    private static final Map<InputUtil.Key, KeyBinding> keyToBindings = Maps.newHashMap();
    private static final Set<String> keyCategories = Sets.newHashSet();
    private static final Map<String, Integer> categoryOrderMap = Util.make(Maps.newHashMap(), hashMap -> {
        hashMap.put("key.categories.movement", 1);
        hashMap.put("key.categories.gameplay", 2);
        hashMap.put("key.categories.inventory", 3);
        hashMap.put("key.categories.creative", 4);
        hashMap.put("key.categories.multiplayer", 5);
        hashMap.put("key.categories.ui", 6);
        hashMap.put("key.categories.misc", 7);
    });
    private final String translationKey;
    private final InputUtil.Key defaultKey;
    private final String category;
    private InputUtil.Key boundKey;
    private boolean pressed;
    private int timesPressed;

    public static void onKeyPressed(InputUtil.Key arg) {
        KeyBinding lv = keyToBindings.get(arg);
        if (lv != null) {
            ++lv.timesPressed;
        }
    }

    public static void setKeyPressed(InputUtil.Key arg, boolean bl) {
        KeyBinding lv = keyToBindings.get(arg);
        if (lv != null) {
            lv.setPressed(bl);
        }
    }

    public static void updatePressedStates() {
        for (KeyBinding lv : keysById.values()) {
            if (lv.boundKey.getCategory() != InputUtil.Type.KEYSYM || lv.boundKey.getCode() == InputUtil.UNKNOWN_KEY.getCode()) continue;
            lv.setPressed(InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), lv.boundKey.getCode()));
        }
    }

    public static void unpressAll() {
        for (KeyBinding lv : keysById.values()) {
            lv.reset();
        }
    }

    public static void updateKeysByCode() {
        keyToBindings.clear();
        for (KeyBinding lv : keysById.values()) {
            keyToBindings.put(lv.boundKey, lv);
        }
    }

    public KeyBinding(String string, int i, String string2) {
        this(string, InputUtil.Type.KEYSYM, i, string2);
    }

    public KeyBinding(String string, InputUtil.Type arg, int i, String string2) {
        this.translationKey = string;
        this.defaultKey = this.boundKey = arg.createFromCode(i);
        this.category = string2;
        keysById.put(string, this);
        keyToBindings.put(this.boundKey, this);
        keyCategories.add(string2);
    }

    public boolean isPressed() {
        return this.pressed;
    }

    public String getCategory() {
        return this.category;
    }

    public boolean wasPressed() {
        if (this.timesPressed == 0) {
            return false;
        }
        --this.timesPressed;
        return true;
    }

    private void reset() {
        this.timesPressed = 0;
        this.setPressed(false);
    }

    public String getTranslationKey() {
        return this.translationKey;
    }

    public InputUtil.Key getDefaultKey() {
        return this.defaultKey;
    }

    public void setBoundKey(InputUtil.Key arg) {
        this.boundKey = arg;
    }

    @Override
    public int compareTo(KeyBinding arg) {
        if (this.category.equals(arg.category)) {
            return I18n.translate(this.translationKey, new Object[0]).compareTo(I18n.translate(arg.translationKey, new Object[0]));
        }
        return categoryOrderMap.get(this.category).compareTo(categoryOrderMap.get(arg.category));
    }

    public static Supplier<Text> getLocalizedName(String string) {
        KeyBinding lv = keysById.get(string);
        if (lv == null) {
            return () -> new TranslatableText(string);
        }
        return lv::getBoundKeyLocalizedText;
    }

    public boolean equals(KeyBinding arg) {
        return this.boundKey.equals(arg.boundKey);
    }

    public boolean isUnbound() {
        return this.boundKey.equals(InputUtil.UNKNOWN_KEY);
    }

    public boolean matchesKey(int i, int j) {
        if (i == InputUtil.UNKNOWN_KEY.getCode()) {
            return this.boundKey.getCategory() == InputUtil.Type.SCANCODE && this.boundKey.getCode() == j;
        }
        return this.boundKey.getCategory() == InputUtil.Type.KEYSYM && this.boundKey.getCode() == i;
    }

    public boolean matchesMouse(int i) {
        return this.boundKey.getCategory() == InputUtil.Type.MOUSE && this.boundKey.getCode() == i;
    }

    public Text getBoundKeyLocalizedText() {
        return this.boundKey.getLocalizedText();
    }

    public boolean isDefault() {
        return this.boundKey.equals(this.defaultKey);
    }

    public String getBoundKeyTranslationKey() {
        return this.boundKey.getTranslationKey();
    }

    public void setPressed(boolean bl) {
        this.pressed = bl;
    }

    @Override
    public /* synthetic */ int compareTo(Object object) {
        return this.compareTo((KeyBinding)object);
    }
}

