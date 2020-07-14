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

    public boolean isCoolingDown(Item item) {
        return this.getCooldownProgress(item, 0.0f) > 0.0f;
    }

    public float getCooldownProgress(Item item, float partialTicks) {
        Entry lv = this.entries.get(item);
        if (lv != null) {
            float g = lv.endTick - lv.startTick;
            float h = (float)lv.endTick - ((float)this.tick + partialTicks);
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

    public void set(Item item, int duration) {
        this.entries.put(item, new Entry(this.tick, this.tick + duration));
        this.onCooldownUpdate(item, duration);
    }

    @Environment(value=EnvType.CLIENT)
    public void remove(Item item) {
        this.entries.remove(item);
        this.onCooldownUpdate(item);
    }

    protected void onCooldownUpdate(Item item, int duration) {
    }

    protected void onCooldownUpdate(Item item) {
    }

    class Entry {
        private final int startTick;
        private final int endTick;

        private Entry(int startTick, int endTick) {
            this.startTick = startTick;
            this.endTick = endTick;
        }
    }
}

