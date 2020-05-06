/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui;

import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Element;

@Environment(value=EnvType.CLIENT)
public interface ParentElement
extends Element {
    public List<? extends Element> children();

    default public Optional<Element> hoveredElement(double d, double e) {
        for (Element element : this.children()) {
            if (!element.isMouseOver(d, e)) continue;
            return Optional.of(element);
        }
        return Optional.empty();
    }

    @Override
    default public boolean mouseClicked(double d, double e, int i) {
        for (Element element : this.children()) {
            if (!element.mouseClicked(d, e, i)) continue;
            this.setFocused(element);
            if (i == 0) {
                this.setDragging(true);
            }
            return true;
        }
        return false;
    }

    @Override
    default public boolean mouseReleased(double d, double e, int i) {
        this.setDragging(false);
        return this.hoveredElement(d, e).filter(arg -> arg.mouseReleased(d, e, i)).isPresent();
    }

    @Override
    default public boolean mouseDragged(double d, double e, int i, double f, double g) {
        if (this.getFocused() != null && this.isDragging() && i == 0) {
            return this.getFocused().mouseDragged(d, e, i, f, g);
        }
        return false;
    }

    public boolean isDragging();

    public void setDragging(boolean var1);

    @Override
    default public boolean mouseScrolled(double d, double e, double f) {
        return this.hoveredElement(d, e).filter(arg -> arg.mouseScrolled(d, e, f)).isPresent();
    }

    @Override
    default public boolean keyPressed(int i, int j, int k) {
        return this.getFocused() != null && this.getFocused().keyPressed(i, j, k);
    }

    @Override
    default public boolean keyReleased(int i, int j, int k) {
        return this.getFocused() != null && this.getFocused().keyReleased(i, j, k);
    }

    @Override
    default public boolean charTyped(char c, int i) {
        return this.getFocused() != null && this.getFocused().charTyped(c, i);
    }

    @Nullable
    public Element getFocused();

    public void setFocused(@Nullable Element var1);

    default public void setInitialFocus(@Nullable Element arg) {
        this.setFocused(arg);
        arg.changeFocus(true);
    }

    default public void focusOn(@Nullable Element arg) {
        this.setFocused(arg);
    }

    @Override
    default public boolean changeFocus(boolean bl) {
        Supplier<Element> supplier;
        BooleanSupplier booleanSupplier;
        int l;
        boolean bl2;
        Element lv = this.getFocused();
        boolean bl3 = bl2 = lv != null;
        if (bl2 && lv.changeFocus(bl)) {
            return true;
        }
        List<? extends Element> list = this.children();
        int i = list.indexOf(lv);
        if (bl2 && i >= 0) {
            int j = i + (bl ? 1 : 0);
        } else if (bl) {
            boolean k = false;
        } else {
            l = list.size();
        }
        ListIterator<? extends Element> listIterator = list.listIterator(l);
        BooleanSupplier booleanSupplier2 = bl ? listIterator::hasNext : (booleanSupplier = listIterator::hasPrevious);
        Supplier<Element> supplier2 = bl ? listIterator::next : (supplier = listIterator::previous);
        while (booleanSupplier.getAsBoolean()) {
            Element lv2 = supplier.get();
            if (!lv2.changeFocus(bl)) continue;
            this.setFocused(lv2);
            return true;
        }
        this.setFocused(null);
        return false;
    }
}

