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

    public static void onKeyPressed(InputUtil.Key key) {
        KeyBinding lv = keyToBindings.get(key);
        if (lv != null) {
            ++lv.timesPressed;
        }
    }

    public static void setKeyPressed(InputUtil.Key key, boolean pressed) {
        KeyBinding lv = keyToBindings.get(key);
        if (lv != null) {
            lv.setPressed(pressed);
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

    public KeyBinding(String translationKey, int code, String category) {
        this(translationKey, InputUtil.Type.KEYSYM, code, category);
    }

    public KeyBinding(String translationKey, InputUtil.Type type, int code, String category) {
        this.translationKey = translationKey;
        this.defaultKey = this.boundKey = type.createFromCode(code);
        this.category = category;
        keysById.put(translationKey, this);
        keyToBindings.put(this.boundKey, this);
        keyCategories.add(category);
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

    public void setBoundKey(InputUtil.Key boundKey) {
        this.boundKey = boundKey;
    }

    @Override
    public int compareTo(KeyBinding arg) {
        if (this.category.equals(arg.category)) {
            return I18n.translate(this.translationKey, new Object[0]).compareTo(I18n.translate(arg.translationKey, new Object[0]));
        }
        return categoryOrderMap.get(this.category).compareTo(categoryOrderMap.get(arg.category));
    }

    public static Supplier<Text> getLocalizedName(String id) {
        KeyBinding lv = keysById.get(id);
        if (lv == null) {
            return () -> new TranslatableText(id);
        }
        return lv::getBoundKeyLocalizedText;
    }

    public boolean equals(KeyBinding other) {
        return this.boundKey.equals(other.boundKey);
    }

    public boolean isUnbound() {
        return this.boundKey.equals(InputUtil.UNKNOWN_KEY);
    }

    public boolean matchesKey(int keyCode, int scanCode) {
        if (keyCode == InputUtil.UNKNOWN_KEY.getCode()) {
            return this.boundKey.getCategory() == InputUtil.Type.SCANCODE && this.boundKey.getCode() == scanCode;
        }
        return this.boundKey.getCategory() == InputUtil.Type.KEYSYM && this.boundKey.getCode() == keyCode;
    }

    public boolean matchesMouse(int code) {
        return this.boundKey.getCategory() == InputUtil.Type.MOUSE && this.boundKey.getCode() == code;
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

    public void setPressed(boolean pressed) {
        this.pressed = pressed;
    }

    @Override
    public /* synthetic */ int compareTo(Object other) {
        return this.compareTo((KeyBinding)other);
    }
}

