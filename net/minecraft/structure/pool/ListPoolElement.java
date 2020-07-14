/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.structure.pool;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePoolElementType;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class ListPoolElement
extends StructurePoolElement {
    public static final Codec<ListPoolElement> field_24950 = RecordCodecBuilder.create(instance -> instance.group((App)StructurePoolElement.field_24953.listOf().fieldOf("elements").forGetter(arg -> arg.elements), ListPoolElement.method_28883()).apply((Applicative)instance, ListPoolElement::new));
    private final List<StructurePoolElement> elements;

    public ListPoolElement(List<StructurePoolElement> list, StructurePool.Projection arg) {
        super(arg);
        if (list.isEmpty()) {
            throw new IllegalArgumentException("Elements are empty");
        }
        this.elements = list;
        this.setAllElementsProjection(arg);
    }

    @Override
    public List<Structure.StructureBlockInfo> getStructureBlockInfos(StructureManager structureManager, BlockPos pos, BlockRotation rotation, Random random) {
        return this.elements.get(0).getStructureBlockInfos(structureManager, pos, rotation, random);
    }

    @Override
    public BlockBox getBoundingBox(StructureManager structureManager, BlockPos pos, BlockRotation rotation) {
        BlockBox lv = BlockBox.empty();
        for (StructurePoolElement lv2 : this.elements) {
            BlockBox lv3 = lv2.getBoundingBox(structureManager, pos, rotation);
            lv.encompass(lv3);
        }
        return lv;
    }

    @Override
    public boolean generate(StructureManager structureManager, ServerWorldAccess arg2, StructureAccessor arg3, ChunkGenerator arg4, BlockPos arg5, BlockPos arg6, BlockRotation arg7, BlockBox arg8, Random random, boolean keepJigsaws) {
        for (StructurePoolElement lv : this.elements) {
            if (lv.generate(structureManager, arg2, arg3, arg4, arg5, arg6, arg7, arg8, random, keepJigsaws)) continue;
            return false;
        }
        return true;
    }

    @Override
    public StructurePoolElementType<?> getType() {
        return StructurePoolElementType.LIST_POOL_ELEMENT;
    }

    @Override
    public StructurePoolElement setProjection(StructurePool.Projection projection) {
        super.setProjection(projection);
        this.setAllElementsProjection(projection);
        return this;
    }

    public String toString() {
        return "List[" + this.elements.stream().map(Object::toString).collect(Collectors.joining(", ")) + "]";
    }

    private void setAllElementsProjection(StructurePool.Projection arg) {
        this.elements.forEach(arg2 -> arg2.setProjection(arg));
    }
}

