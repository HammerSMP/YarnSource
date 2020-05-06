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
    private static final Map<InputUtil.KeyCode, KeyBinding> keysByCode = Maps.newHashMap();
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
    private final String id;
    private final InputUtil.KeyCode defaultKeyCode;
    private final String category;
    private InputUtil.KeyCode keyCode;
    private boolean pressed;
    private int timesPressed;

    public static void onKeyPressed(InputUtil.KeyCode arg) {
        KeyBinding lv = keysByCode.get(arg);
        if (lv != null) {
            ++lv.timesPressed;
        }
    }

    public static void setKeyPressed(InputUtil.KeyCode arg, boolean bl) {
        KeyBinding lv = keysByCode.get(arg);
        if (lv != null) {
            lv.setPressed(bl);
        }
    }

    public static void updatePressedStates() {
        for (KeyBinding lv : keysById.values()) {
            if (lv.keyCode.getCategory() != InputUtil.Type.KEYSYM || lv.keyCode.getKeyCode() == InputUtil.UNKNOWN_KEYCODE.getKeyCode()) continue;
            lv.setPressed(InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), lv.keyCode.getKeyCode()));
        }
    }

    public static void unpressAll() {
        for (KeyBinding lv : keysById.values()) {
            lv.reset();
        }
    }

    public static void updateKeysByCode() {
        keysByCode.clear();
        for (KeyBinding lv : keysById.values()) {
            keysByCode.put(lv.keyCode, lv);
        }
    }

    public KeyBinding(String string, int i, String string2) {
        this(string, InputUtil.Type.KEYSYM, i, string2);
    }

    public KeyBinding(String string, InputUtil.Type arg, int i, String string2) {
        this.id = string;
        this.defaultKeyCode = this.keyCode = arg.createFromCode(i);
        this.category = string2;
        keysById.put(string, this);
        keysByCode.put(this.keyCode, this);
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

    public String getId() {
        return this.id;
    }

    public InputUtil.KeyCode getDefaultKeyCode() {
        return this.defaultKeyCode;
    }

    public void setKeyCode(InputUtil.KeyCode arg) {
        this.keyCode = arg;
    }

    @Override
    public int compareTo(KeyBinding arg) {
        if (this.category.equals(arg.category)) {
            return I18n.translate(this.id, new Object[0]).compareTo(I18n.translate(arg.id, new Object[0]));
        }
        return categoryOrderMap.get(this.category).compareTo(categoryOrderMap.get(arg.category));
    }

    public static Supplier<Text> getLocalizedName(String string) {
        KeyBinding lv = keysById.get(string);
        if (lv == null) {
            return () -> new TranslatableText(string);
        }
        return lv::getLocalizedName;
    }

    public boolean equals(KeyBinding arg) {
        return this.keyCode.equals(arg.keyCode);
    }

    public boolean isNotBound() {
        return this.keyCode.equals(InputUtil.UNKNOWN_KEYCODE);
    }

    public boolean matchesKey(int i, int j) {
        if (i == InputUtil.UNKNOWN_KEYCODE.getKeyCode()) {
            return this.keyCode.getCategory() == InputUtil.Type.SCANCODE && this.keyCode.getKeyCode() == j;
        }
        return this.keyCode.getCategory() == InputUtil.Type.KEYSYM && this.keyCode.getKeyCode() == i;
    }

    public boolean matchesMouse(int i) {
        return this.keyCode.getCategory() == InputUtil.Type.MOUSE && this.keyCode.getKeyCode() == i;
    }

    public Text getLocalizedName() {
        return this.keyCode.method_27445();
    }

    public boolean isDefault() {
        return this.keyCode.equals(this.defaultKeyCode);
    }

    public String getName() {
        return this.keyCode.getName();
    }

    public void setPressed(boolean bl) {
        this.pressed = bl;
    }

    @Override
    public /* synthetic */ int compareTo(Object object) {
        return this.compareTo((KeyBinding)object);
    }
}

