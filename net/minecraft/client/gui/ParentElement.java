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

    default public Optional<Element> hoveredElement(double mouseX, double mouseY) {
        for (Element element : this.children()) {
            if (!element.isMouseOver(mouseX, mouseY)) continue;
            return Optional.of(element);
        }
        return Optional.empty();
    }

    @Override
    default public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (Element element : this.children()) {
            if (!element.mouseClicked(mouseX, mouseY, button)) continue;
            this.setFocused(element);
            if (button == 0) {
                this.setDragging(true);
            }
            return true;
        }
        return false;
    }

    @Override
    default public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.setDragging(false);
        return this.hoveredElement(mouseX, mouseY).filter(arg -> arg.mouseReleased(mouseX, mouseY, button)).isPresent();
    }

    @Override
    default public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.getFocused() != null && this.isDragging() && button == 0) {
            return this.getFocused().mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }
        return false;
    }

    public boolean isDragging();

    public void setDragging(boolean var1);

    @Override
    default public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return this.hoveredElement(mouseX, mouseY).filter(arg -> arg.mouseScrolled(mouseX, mouseY, amount)).isPresent();
    }

    @Override
    default public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return this.getFocused() != null && this.getFocused().keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    default public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return this.getFocused() != null && this.getFocused().keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    default public boolean charTyped(char chr, int keyCode) {
        return this.getFocused() != null && this.getFocused().charTyped(chr, keyCode);
    }

    @Nullable
    public Element getFocused();

    public void setFocused(@Nullable Element var1);

    default public void setInitialFocus(@Nullable Element element) {
        this.setFocused(element);
        element.changeFocus(true);
    }

    default public void focusOn(@Nullable Element element) {
        this.setFocused(element);
    }

    @Override
    default public boolean changeFocus(boolean lookForwards) {
        Supplier<Element> supplier;
        BooleanSupplier booleanSupplier;
        int l;
        boolean bl2;
        Element lv = this.getFocused();
        boolean bl = bl2 = lv != null;
        if (bl2 && lv.changeFocus(lookForwards)) {
            return true;
        }
        List<? extends Element> list = this.children();
        int i = list.indexOf(lv);
        if (bl2 && i >= 0) {
            int j = i + (lookForwards ? 1 : 0);
        } else if (lookForwards) {
            boolean k = false;
        } else {
            l = list.size();
        }
        ListIterator<? extends Element> listIterator = list.listIterator(l);
        BooleanSupplier booleanSupplier2 = lookForwards ? listIterator::hasNext : (booleanSupplier = listIterator::hasPrevious);
        Supplier<Element> supplier2 = lookForwards ? listIterator::next : (supplier = listIterator::previous);
        while (booleanSupplier.getAsBoolean()) {
            Element lv2 = supplier.get();
            if (!lv2.changeFocus(lookForwards)) continue;
            this.setFocused(lv2);
            return true;
        }
        this.setFocused(null);
        return false;
    }
}

