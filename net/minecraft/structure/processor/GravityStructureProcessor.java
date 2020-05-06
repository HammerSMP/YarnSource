/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 *  javax.annotation.Nullable
 */
package net.minecraft.structure.processor;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Map;
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
    private final Heightmap.Type heightmap;
    private final int offset;

    public GravityStructureProcessor(Heightmap.Type arg, int i) {
        this.heightmap = arg;
        this.offset = i;
    }

    public GravityStructureProcessor(Dynamic<?> dynamic) {
        this(Heightmap.Type.byName(dynamic.get("heightmap").asString(Heightmap.Type.WORLD_SURFACE_WG.getName())), dynamic.get("offset").asInt(0));
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
    protected StructureProcessorType getType() {
        return StructureProcessorType.GRAVITY;
    }

    @Override
    protected <T> Dynamic<T> rawToDynamic(DynamicOps<T> dynamicOps) {
        return new Dynamic(dynamicOps, dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("heightmap"), (Object)dynamicOps.createString(this.heightmap.getName()), (Object)dynamicOps.createString("offset"), (Object)dynamicOps.createInt(this.offset))));
    }
}

