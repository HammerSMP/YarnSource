/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.realms;

import java.util.List;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.TickableRealmsButton;

@Environment(value=EnvType.CLIENT)
public abstract class RealmsScreen
extends Screen {
    public RealmsScreen() {
        super(NarratorManager.EMPTY);
    }

    protected static int row(int i) {
        return 40 + i * 13;
    }

    @Override
    public void tick() {
        for (AbstractButtonWidget lv : this.buttons) {
            if (!(lv instanceof TickableRealmsButton)) continue;
            ((TickableRealmsButton)((Object)lv)).tick();
        }
    }

    public void narrateLabels() {
        List<String> list = this.children.stream().filter(RealmsLabel.class::isInstance).map(RealmsLabel.class::cast).map(RealmsLabel::getText).collect(Collectors.toList());
        Realms.narrateNow(list);
    }
}

