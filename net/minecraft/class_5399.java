/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  javax.annotation.Nullable
 */
package net.minecraft;

import com.mojang.serialization.Codec;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public class class_5399
extends StructureProcessor {
    public static final Codec<class_5399> field_25618 = Codec.unit(() -> field_25619);
    public static final class_5399 field_25619 = new class_5399();

    @Override
    @Nullable
    public Structure.StructureBlockInfo process(WorldView arg, BlockPos arg2, BlockPos arg3, Structure.StructureBlockInfo arg4, Structure.StructureBlockInfo arg5, StructurePlacementData arg6) {
        BlockPos lv = arg5.pos;
        boolean bl = arg.getBlockState(lv).isOf(Blocks.LAVA);
        if (bl && !Block.isShapeFullCube(arg5.state.getOutlineShape(arg, lv))) {
            return new Structure.StructureBlockInfo(lv, Blocks.LAVA.getDefaultState(), arg5.tag);
        }
        return arg5;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return StructureProcessorType.LAVA_SUBMERGED_BLOCK;
    }
}

