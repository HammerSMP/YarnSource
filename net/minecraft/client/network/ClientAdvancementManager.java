/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.network;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementManager;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.toast.AdvancementToast;
import net.minecraft.network.packet.c2s.play.AdvancementTabC2SPacket;
import net.minecraft.network.packet.s2c.play.AdvancementUpdateS2CPacket;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ClientAdvancementManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private final MinecraftClient client;
    private final AdvancementManager manager = new AdvancementManager();
    private final Map<Advancement, AdvancementProgress> advancementProgresses = Maps.newHashMap();
    @Nullable
    private Listener listener;
    @Nullable
    private Advancement selectedTab;

    public ClientAdvancementManager(MinecraftClient client) {
        this.client = client;
    }

    public void onAdvancements(AdvancementUpdateS2CPacket packet) {
        if (packet.shouldClearCurrent()) {
            this.manager.clear();
            this.advancementProgresses.clear();
        }
        this.manager.removeAll(packet.getAdvancementIdsToRemove());
        this.manager.load(packet.getAdvancementsToEarn());
        for (Map.Entry<Identifier, AdvancementProgress> entry : packet.getAdvancementsToProgress().entrySet()) {
            Advancement lv = this.manager.get(entry.getKey());
            if (lv != null) {
                AdvancementProgress lv2 = entry.getValue();
                lv2.init(lv.getCriteria(), lv.getRequirements());
                this.advancementProgresses.put(lv, lv2);
                if (this.listener != null) {
                    this.listener.setProgress(lv, lv2);
                }
                if (packet.shouldClearCurrent() || !lv2.isDone() || lv.getDisplay() == null || !lv.getDisplay().shouldShowToast()) continue;
                this.client.getToastManager().add(new AdvancementToast(lv));
                continue;
            }
            LOGGER.warn("Server informed client about progress for unknown advancement {}", (Object)entry.getKey());
        }
    }

    public AdvancementManager getManager() {
        return this.manager;
    }

    public void selectTab(@Nullable Advancement tab, boolean local) {
        ClientPlayNetworkHandler lv = this.client.getNetworkHandler();
        if (lv != null && tab != null && local) {
            lv.sendPacket(AdvancementTabC2SPacket.open(tab));
        }
        if (this.selectedTab != tab) {
            this.selectedTab = tab;
            if (this.listener != null) {
                this.listener.selectTab(tab);
            }
        }
    }

    public void setListener(@Nullable Listener listener) {
        this.listener = listener;
        this.manager.setListener(listener);
        if (listener != null) {
            for (Map.Entry<Advancement, AdvancementProgress> entry : this.advancementProgresses.entrySet()) {
                listener.setProgress(entry.getKey(), entry.getValue());
            }
            listener.selectTab(this.selectedTab);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static interface Listener
    extends AdvancementManager.Listener {
        public void setProgress(Advancement var1, AdvancementProgress var2);

        public void selectTab(@Nullable Advancement var1);
    }
}

