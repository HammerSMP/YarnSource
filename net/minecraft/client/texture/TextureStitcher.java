/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.texture;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.TextureStitcherCannotFitException;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class TextureStitcher {
    private static final Comparator<Holder> comparator = Comparator.comparing(arg -> -arg.height).thenComparing(arg -> -arg.width).thenComparing(arg -> arg.sprite.getId());
    private final int mipLevel;
    private final Set<Holder> holders = Sets.newHashSetWithExpectedSize((int)256);
    private final List<Slot> slots = Lists.newArrayListWithCapacity((int)256);
    private int width;
    private int height;
    private final int maxWidth;
    private final int maxHeight;

    public TextureStitcher(int i, int j, int k) {
        this.mipLevel = k;
        this.maxWidth = i;
        this.maxHeight = j;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public void add(Sprite.Info arg) {
        Holder lv = new Holder(arg, this.mipLevel);
        this.holders.add(lv);
    }

    public void stitch() {
        ArrayList list = Lists.newArrayList(this.holders);
        list.sort(comparator);
        for (Holder lv : list) {
            if (this.fit(lv)) continue;
            throw new TextureStitcherCannotFitException(lv.sprite, (Collection)list.stream().map(arg -> arg.sprite).collect(ImmutableList.toImmutableList()));
        }
        this.width = MathHelper.smallestEncompassingPowerOfTwo(this.width);
        this.height = MathHelper.smallestEncompassingPowerOfTwo(this.height);
    }

    public void getStitchedSprites(SpriteConsumer arg) {
        for (Slot lv : this.slots) {
            lv.addAllFilledSlots(arg2 -> {
                Holder lv = arg2.getTexture();
                Sprite.Info lv2 = lv.sprite;
                arg.load(lv2, this.width, this.height, arg2.getX(), arg2.getY());
            });
        }
    }

    private static int applyMipLevel(int i, int j) {
        return (i >> j) + ((i & (1 << j) - 1) == 0 ? 0 : 1) << j;
    }

    private boolean fit(Holder arg) {
        for (Slot lv : this.slots) {
            if (!lv.fit(arg)) continue;
            return true;
        }
        return this.growAndFit(arg);
    }

    private boolean growAndFit(Holder arg) {
        Slot lv2;
        boolean bl6;
        boolean bl4;
        boolean bl2;
        int i = MathHelper.smallestEncompassingPowerOfTwo(this.width);
        int j = MathHelper.smallestEncompassingPowerOfTwo(this.height);
        int k = MathHelper.smallestEncompassingPowerOfTwo(this.width + arg.width);
        int l = MathHelper.smallestEncompassingPowerOfTwo(this.height + arg.height);
        boolean bl = k <= this.maxWidth;
        boolean bl3 = bl2 = l <= this.maxHeight;
        if (!bl && !bl2) {
            return false;
        }
        boolean bl32 = bl && i != k;
        boolean bl5 = bl4 = bl2 && j != l;
        if (bl32 ^ bl4) {
            boolean bl52 = bl32;
        } else {
            boolean bl7 = bl6 = bl && i <= j;
        }
        if (bl6) {
            if (this.height == 0) {
                this.height = arg.height;
            }
            Slot lv = new Slot(this.width, 0, arg.width, this.height);
            this.width += arg.width;
        } else {
            lv2 = new Slot(0, this.height, this.width, arg.height);
            this.height += arg.height;
        }
        lv2.fit(arg);
        this.slots.add(lv2);
        return true;
    }

    @Environment(value=EnvType.CLIENT)
    public static class Slot {
        private final int x;
        private final int y;
        private final int width;
        private final int height;
        private List<Slot> subSlots;
        private Holder texture;

        public Slot(int i, int j, int k, int l) {
            this.x = i;
            this.y = j;
            this.width = k;
            this.height = l;
        }

        public Holder getTexture() {
            return this.texture;
        }

        public int getX() {
            return this.x;
        }

        public int getY() {
            return this.y;
        }

        public boolean fit(Holder arg) {
            if (this.texture != null) {
                return false;
            }
            int i = arg.width;
            int j = arg.height;
            if (i > this.width || j > this.height) {
                return false;
            }
            if (i == this.width && j == this.height) {
                this.texture = arg;
                return true;
            }
            if (this.subSlots == null) {
                this.subSlots = Lists.newArrayListWithCapacity((int)1);
                this.subSlots.add(new Slot(this.x, this.y, i, j));
                int k = this.width - i;
                int l = this.height - j;
                if (l > 0 && k > 0) {
                    int n;
                    int m = Math.max(this.height, k);
                    if (m >= (n = Math.max(this.width, l))) {
                        this.subSlots.add(new Slot(this.x, this.y + j, i, l));
                        this.subSlots.add(new Slot(this.x + i, this.y, k, this.height));
                    } else {
                        this.subSlots.add(new Slot(this.x + i, this.y, k, j));
                        this.subSlots.add(new Slot(this.x, this.y + j, this.width, l));
                    }
                } else if (k == 0) {
                    this.subSlots.add(new Slot(this.x, this.y + j, i, l));
                } else if (l == 0) {
                    this.subSlots.add(new Slot(this.x + i, this.y, k, j));
                }
            }
            for (Slot lv : this.subSlots) {
                if (!lv.fit(arg)) continue;
                return true;
            }
            return false;
        }

        public void addAllFilledSlots(Consumer<Slot> consumer) {
            if (this.texture != null) {
                consumer.accept(this);
            } else if (this.subSlots != null) {
                for (Slot lv : this.subSlots) {
                    lv.addAllFilledSlots(consumer);
                }
            }
        }

        public String toString() {
            return "Slot{originX=" + this.x + ", originY=" + this.y + ", width=" + this.width + ", height=" + this.height + ", texture=" + this.texture + ", subSlots=" + this.subSlots + '}';
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class Holder {
        public final Sprite.Info sprite;
        public final int width;
        public final int height;

        public Holder(Sprite.Info arg, int i) {
            this.sprite = arg;
            this.width = TextureStitcher.applyMipLevel(arg.getWidth(), i);
            this.height = TextureStitcher.applyMipLevel(arg.getHeight(), i);
        }

        public String toString() {
            return "Holder{width=" + this.width + ", height=" + this.height + '}';
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static interface SpriteConsumer {
        public void load(Sprite.Info var1, int var2, int var3, int var4, int var5);
    }
}

