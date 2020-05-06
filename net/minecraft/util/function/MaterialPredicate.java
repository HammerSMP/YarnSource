/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.util.function;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;

public class MaterialPredicate
implements Predicate<BlockState> {
    private static final MaterialPredicate IS_AIR = new MaterialPredicate(Material.AIR){

        @Override
        public boolean test(@Nullable BlockState arg) {
            return arg != null && arg.isAir();
        }

        @Override
        public /* synthetic */ boolean test(@Nullable Object object) {
            return this.test((BlockState)object);
        }
    };
    private final Material material;

    private MaterialPredicate(Material arg) {
        this.material = arg;
    }

    public static MaterialPredicate create(Material arg) {
        return arg == Material.AIR ? IS_AIR : new MaterialPredicate(arg);
    }

    @Override
    public boolean test(@Nullable BlockState arg) {
        return arg != null && arg.getMaterial() == this.material;
    }

    @Override
    public /* synthetic */ boolean test(@Nullable Object object) {
        return this.test((BlockState)object);
    }
}

