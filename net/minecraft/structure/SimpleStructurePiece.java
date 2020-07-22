/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.structure;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.class_5425;
import net.minecraft.command.argument.BlockArgumentParser;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class SimpleStructurePiece
extends StructurePiece {
    private static final Logger LOGGER = LogManager.getLogger();
    protected Structure structure;
    protected StructurePlacementData placementData;
    protected BlockPos pos;

    public SimpleStructurePiece(StructurePieceType arg, int i) {
        super(arg, i);
    }

    public SimpleStructurePiece(StructurePieceType arg, CompoundTag arg2) {
        super(arg, arg2);
        this.pos = new BlockPos(arg2.getInt("TPX"), arg2.getInt("TPY"), arg2.getInt("TPZ"));
    }

    protected void setStructureData(Structure structure, BlockPos pos, StructurePlacementData placementData) {
        this.structure = structure;
        this.setOrientation(Direction.NORTH);
        this.pos = pos;
        this.placementData = placementData;
        this.boundingBox = structure.calculateBoundingBox(placementData, pos);
    }

    @Override
    protected void toNbt(CompoundTag tag) {
        tag.putInt("TPX", this.pos.getX());
        tag.putInt("TPY", this.pos.getY());
        tag.putInt("TPZ", this.pos.getZ());
    }

    @Override
    public boolean generate(ServerWorldAccess arg, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox boundingBox, ChunkPos arg5, BlockPos arg6) {
        this.placementData.setBoundingBox(boundingBox);
        this.boundingBox = this.structure.calculateBoundingBox(this.placementData, this.pos);
        if (this.structure.place(arg, this.pos, arg6, this.placementData, random, 2)) {
            List<Structure.StructureBlockInfo> list = this.structure.getInfosForBlock(this.pos, this.placementData, Blocks.STRUCTURE_BLOCK);
            for (Structure.StructureBlockInfo lv : list) {
                StructureBlockMode lv2;
                if (lv.tag == null || (lv2 = StructureBlockMode.valueOf(lv.tag.getString("mode"))) != StructureBlockMode.DATA) continue;
                this.handleMetadata(lv.tag.getString("metadata"), lv.pos, arg, random, boundingBox);
            }
            List<Structure.StructureBlockInfo> list2 = this.structure.getInfosForBlock(this.pos, this.placementData, Blocks.JIGSAW);
            for (Structure.StructureBlockInfo lv3 : list2) {
                if (lv3.tag == null) continue;
                String string = lv3.tag.getString("final_state");
                BlockArgumentParser lv4 = new BlockArgumentParser(new StringReader(string), false);
                BlockState lv5 = Blocks.AIR.getDefaultState();
                try {
                    lv4.parse(true);
                    BlockState lv6 = lv4.getBlockState();
                    if (lv6 != null) {
                        lv5 = lv6;
                    } else {
                        LOGGER.error("Error while parsing blockstate {} in jigsaw block @ {}", (Object)string, (Object)lv3.pos);
                    }
                }
                catch (CommandSyntaxException commandSyntaxException) {
                    LOGGER.error("Error while parsing blockstate {} in jigsaw block @ {}", (Object)string, (Object)lv3.pos);
                }
                arg.setBlockState(lv3.pos, lv5, 3);
            }
        }
        return true;
    }

    protected abstract void handleMetadata(String var1, BlockPos var2, class_5425 var3, Random var4, BlockBox var5);

    @Override
    public void translate(int x, int y, int z) {
        super.translate(x, y, z);
        this.pos = this.pos.add(x, y, z);
    }

    @Override
    public BlockRotation getRotation() {
        return this.placementData.getRotation();
    }
}

