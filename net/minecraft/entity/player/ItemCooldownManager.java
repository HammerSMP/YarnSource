/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.player;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;
import net.minecraft.util.math.MathHelper;

public class ItemCooldownManager {
    private final Map<Item, Entry> entries = Maps.newHashMap();
    private int tick;

    public boolean isCoolingDown(Item arg) {
        return this.getCooldownProgress(arg, 0.0f) > 0.0f;
    }

    public float getCooldownProgress(Item arg, float f) {
        Entry lv = this.entries.get(arg);
        if (lv != null) {
            float g = lv.endTick - lv.startTick;
            float h = (float)lv.endTick - ((float)this.tick + f);
            return MathHelper.clamp(h / g, 0.0f, 1.0f);
        }
        return 0.0f;
    }

    public void update() {
        ++this.tick;
        if (!this.entries.isEmpty()) {
            Iterator<Map.Entry<Item, Entry>> iterator = this.entries.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Item, Entry> entry = iterator.next();
                if (entry.getValue().endTick > this.tick) continue;
                iterator.remove();
                this.onCooldownUpdate(entry.getKey());
            }
        }
    }

    public void set(Item arg, int i) {
        this.entries.put(arg, new Entry(this.tick, this.tick + i));
        this.onCooldownUpdate(arg, i);
    }

    @Environment(value=EnvType.CLIENT)
    public void remove(Item arg) {
        this.entries.remove(arg);
        this.onCooldownUpdate(arg);
    }

    protected void onCooldownUpdate(Item arg, int i) {
    }

    protected void onCooldownUpdate(Item arg) {
    }

    class Entry {
        private final int startTick;
        private final int endTick;

        private Entry(int i, int j) {
            this.startTick = i;
            this.endTick = j;
        }
    }
}

