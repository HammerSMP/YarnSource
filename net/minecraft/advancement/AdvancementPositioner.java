/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 */
package net.minecraft.advancement;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancement.Advancement;

public class AdvancementPositioner {
    private final Advancement advancement;
    private final AdvancementPositioner parent;
    private final AdvancementPositioner previousSibling;
    private final int childrenSize;
    private final List<AdvancementPositioner> children = Lists.newArrayList();
    private AdvancementPositioner optionalLast;
    private AdvancementPositioner substituteChild;
    private int depth;
    private float row;
    private float relativeRowInSiblings;
    private float field_1266;
    private float field_1265;

    public AdvancementPositioner(Advancement arg, @Nullable AdvancementPositioner arg2, @Nullable AdvancementPositioner arg3, int i, int j) {
        if (arg.getDisplay() == null) {
            throw new IllegalArgumentException("Can't position an invisible advancement!");
        }
        this.advancement = arg;
        this.parent = arg2;
        this.previousSibling = arg3;
        this.childrenSize = i;
        this.optionalLast = this;
        this.depth = j;
        this.row = -1.0f;
        AdvancementPositioner lv = null;
        for (Advancement lv2 : arg.getChildren()) {
            lv = this.findChildrenRecursively(lv2, lv);
        }
    }

    @Nullable
    private AdvancementPositioner findChildrenRecursively(Advancement arg, @Nullable AdvancementPositioner arg2) {
        if (arg.getDisplay() != null) {
            arg2 = new AdvancementPositioner(arg, this, arg2, this.children.size() + 1, this.depth + 1);
            this.children.add(arg2);
        } else {
            for (Advancement lv : arg.getChildren()) {
                arg2 = this.findChildrenRecursively(lv, arg2);
            }
        }
        return arg2;
    }

    private void calculateRecursively() {
        if (this.children.isEmpty()) {
            this.row = this.previousSibling != null ? this.previousSibling.row + 1.0f : 0.0f;
            return;
        }
        AdvancementPositioner lv = null;
        for (AdvancementPositioner lv2 : this.children) {
            lv2.calculateRecursively();
            lv = lv2.onFinishCalculation(lv == null ? lv2 : lv);
        }
        this.onFinishChildrenCalculation();
        float f = (this.children.get((int)0).row + this.children.get((int)(this.children.size() - 1)).row) / 2.0f;
        if (this.previousSibling != null) {
            this.row = this.previousSibling.row + 1.0f;
            this.relativeRowInSiblings = this.row - f;
        } else {
            this.row = f;
        }
    }

    private float findMinRowRecursively(float f, int i, float g) {
        this.row += f;
        this.depth = i;
        if (this.row < g) {
            g = this.row;
        }
        for (AdvancementPositioner lv : this.children) {
            g = lv.findMinRowRecursively(f + this.relativeRowInSiblings, i + 1, g);
        }
        return g;
    }

    private void increaseRowRecursively(float f) {
        this.row += f;
        for (AdvancementPositioner lv : this.children) {
            lv.increaseRowRecursively(f);
        }
    }

    private void onFinishChildrenCalculation() {
        float f = 0.0f;
        float g = 0.0f;
        for (int i = this.children.size() - 1; i >= 0; --i) {
            AdvancementPositioner lv = this.children.get(i);
            lv.row += f;
            lv.relativeRowInSiblings += f;
            f += lv.field_1265 + (g += lv.field_1266);
        }
    }

    @Nullable
    private AdvancementPositioner getFirstChild() {
        if (this.substituteChild != null) {
            return this.substituteChild;
        }
        if (!this.children.isEmpty()) {
            return this.children.get(0);
        }
        return null;
    }

    @Nullable
    private AdvancementPositioner getLastChild() {
        if (this.substituteChild != null) {
            return this.substituteChild;
        }
        if (!this.children.isEmpty()) {
            return this.children.get(this.children.size() - 1);
        }
        return null;
    }

    private AdvancementPositioner onFinishCalculation(AdvancementPositioner arg) {
        if (this.previousSibling == null) {
            return arg;
        }
        AdvancementPositioner lv = this;
        AdvancementPositioner lv2 = this;
        AdvancementPositioner lv3 = this.previousSibling;
        AdvancementPositioner lv4 = this.parent.children.get(0);
        float f = this.relativeRowInSiblings;
        float g = this.relativeRowInSiblings;
        float h = lv3.relativeRowInSiblings;
        float i = lv4.relativeRowInSiblings;
        while (lv3.getLastChild() != null && lv.getFirstChild() != null) {
            lv3 = lv3.getLastChild();
            lv = lv.getFirstChild();
            lv4 = lv4.getFirstChild();
            lv2 = lv2.getLastChild();
            lv2.optionalLast = this;
            float j = lv3.row + h - (lv.row + f) + 1.0f;
            if (j > 0.0f) {
                lv3.getLast(this, arg).pushDown(this, j);
                f += j;
                g += j;
            }
            h += lv3.relativeRowInSiblings;
            f += lv.relativeRowInSiblings;
            i += lv4.relativeRowInSiblings;
            g += lv2.relativeRowInSiblings;
        }
        if (lv3.getLastChild() != null && lv2.getLastChild() == null) {
            lv2.substituteChild = lv3.getLastChild();
            lv2.relativeRowInSiblings += h - g;
        } else {
            if (lv.getFirstChild() != null && lv4.getFirstChild() == null) {
                lv4.substituteChild = lv.getFirstChild();
                lv4.relativeRowInSiblings += f - i;
            }
            arg = this;
        }
        return arg;
    }

    private void pushDown(AdvancementPositioner arg, float f) {
        float g = arg.childrenSize - this.childrenSize;
        if (g != 0.0f) {
            arg.field_1266 -= f / g;
            this.field_1266 += f / g;
        }
        arg.field_1265 += f;
        arg.row += f;
        arg.relativeRowInSiblings += f;
    }

    private AdvancementPositioner getLast(AdvancementPositioner arg, AdvancementPositioner arg2) {
        if (this.optionalLast != null && arg.parent.children.contains(this.optionalLast)) {
            return this.optionalLast;
        }
        return arg2;
    }

    private void apply() {
        if (this.advancement.getDisplay() != null) {
            this.advancement.getDisplay().setPosition(this.depth, this.row);
        }
        if (!this.children.isEmpty()) {
            for (AdvancementPositioner lv : this.children) {
                lv.apply();
            }
        }
    }

    public static void arrangeForTree(Advancement arg) {
        if (arg.getDisplay() == null) {
            throw new IllegalArgumentException("Can't position children of an invisible root!");
        }
        AdvancementPositioner lv = new AdvancementPositioner(arg, null, null, 1, 0);
        lv.calculateRecursively();
        float f = lv.findMinRowRecursively(0.0f, 0, lv.row);
        if (f < 0.0f) {
            lv.increaseRowRecursively(-f);
        }
        lv.apply();
    }
}

