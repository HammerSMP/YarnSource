/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.placer;

import com.mojang.serialization.Codec;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.placer.BlockPlacer;
import net.minecraft.world.gen.placer.ColumnPlacer;
import net.minecraft.world.gen.placer.DoublePlantPlacer;
import net.minecraft.world.gen.placer.SimpleBlockPlacer;

public class BlockPlacerType<P extends BlockPlacer> {
    public static final BlockPlacerType<SimpleBlockPlacer> SIMPLE_BLOCK_PLACER = BlockPlacerType.register("simple_block_placer", SimpleBlockPlacer.CODEC);
    public static final BlockPlacerType<DoublePlantPlacer> DOUBLE_PLANT_PLACER = BlockPlacerType.register("double_plant_placer", DoublePlantPlacer.field_24868);
    public static final BlockPlacerType<ColumnPlacer> COLUMN_PLACER = BlockPlacerType.register("column_placer", ColumnPlacer.CODEC);
    private final Codec<P> field_24866;

    private static <P extends BlockPlacer> BlockPlacerType<P> register(String string, Codec<P> codec) {
        return Registry.register(Registry.BLOCK_PLACER_TYPE, string, new BlockPlacerType<P>(codec));
    }

    private BlockPlacerType(Codec<P> codec) {
        this.field_24866 = codec;
    }

    public Codec<P> method_28674() {
        return this.field_24866;
    }
}

