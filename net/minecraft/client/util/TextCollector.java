/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.util;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.StringRenderable;

@Environment(value=EnvType.CLIENT)
public class TextCollector {
    private final List<StringRenderable> field_25260 = Lists.newArrayList();

    public void add(StringRenderable arg) {
        this.field_25260.add(arg);
    }

    @Nullable
    public StringRenderable getRawCombined() {
        if (this.field_25260.isEmpty()) {
            return null;
        }
        if (this.field_25260.size() == 1) {
            return this.field_25260.get(0);
        }
        return StringRenderable.concat(this.field_25260);
    }

    public StringRenderable getCombined() {
        StringRenderable lv = this.getRawCombined();
        return lv != null ? lv : StringRenderable.EMPTY;
    }
}

