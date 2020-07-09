/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  javax.annotation.Nullable
 */
package net.minecraft.structure.processor;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nullable;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.WorldView;

public class GravityStructureProcessor
extends StructureProcessor {
    public static final Codec<GravityStructureProcessor> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Heightmap.Type.field_24772.fieldOf("heightmap").orElse((Object)Heightmap.Type.WORLD_SURFACE_WG).forGetter(arg -> arg.heightmap), (App)Codec.INT.fieldOf("offset").orElse((Object)0).forGetter(arg -> arg.offset)).apply((Applicative)instance, GravityStructureProcessor::new));
    private final Heightmap.Type heightmap;
    private final int offset;

    public GravityStructureProcessor(Heightmap.Type arg, int i) {
        this.heightmap = arg;
        this.offset = i;
    }

    @Override
    @Nullable
    public Structure.StructureBlockInfo process(WorldView arg, BlockPos arg2, BlockPos arg3, Structure.StructureBlockInfo arg4, Structure.StructureBlockInfo arg5, StructurePlacementData arg6) {
        Heightmap.Type lv4;
        if (arg instanceof ServerWorld) {
            if (this.heightmap == Heightmap.Type.WORLD_SURFACE_WG) {
                Heightmap.Type lv = Heightmap.Type.WORLD_SURFACE;
            } else if (this.heightmap == Heightmap.Type.OCEAN_FLOOR_WG) {
                Heightmap.Type lv2 = Heightmap.Type.OCEAN_FLOOR;
            } else {
                Heightmap.Type lv3 = this.heightmap;
            }
        } else {
            lv4 = this.heightmap;
        }
        int i = arg.getTopY(lv4, arg5.pos.getX(), arg5.pos.getZ()) + this.offset;
        int j = arg4.pos.getY();
        return new Structure.StructureBlockInfo(new BlockPos(arg5.pos.getX(), i + j, arg5.pos.getZ()), arg5.state, arg5.tag);
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return StructureProcessorType.GRAVITY;
    }
}

