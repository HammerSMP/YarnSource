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
import net.minecraft.class_5348;

@Environment(value=EnvType.CLIENT)
public class TextCollector {
    private final List<class_5348> field_25260 = Lists.newArrayList();

    public void add(class_5348 arg) {
        this.field_25260.add(arg);
    }

    @Nullable
    public class_5348 getRawCombined() {
        if (this.field_25260.isEmpty()) {
            return null;
        }
        if (this.field_25260.size() == 1) {
            return this.field_25260.get(0);
        }
        return class_5348.method_29432(this.field_25260);
    }

    public class_5348 getCombined() {
        class_5348 lv = this.getRawCombined();
        return lv != null ? lv : class_5348.field_25310;
    }
}

