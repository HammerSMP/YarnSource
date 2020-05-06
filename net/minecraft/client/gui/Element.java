/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public interface Element {
    default public void mouseMoved(double d, double e) {
    }

    default public boolean mouseClicked(double d, double e, int i) {
        return false;
    }

    default public boolean mouseReleased(double d, double e, int i) {
        return false;
    }

    default public boolean mouseDragged(double d, double e, int i, double f, double g) {
        return false;
    }

    default public boolean mouseScrolled(double d, double e, double f) {
        return false;
    }

    default public boolean keyPressed(int i, int j, int k) {
        return false;
    }

    default public boolean keyReleased(int i, int j, int k) {
        return false;
    }

    default public boolean charTyped(char c, int i) {
        return false;
    }

    default public boolean changeFocus(boolean bl) {
        return false;
    }

    default public boolean isMouseOver(double d, double e) {
        return false;
    }
}

