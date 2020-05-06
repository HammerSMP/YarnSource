/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.SimpleStructurePiece;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ShipwreckFeatureConfig;

public class ShipwreckGenerator {
    private static final BlockPos field_14536 = new BlockPos(4, 0, 15);
    private static final Identifier[] BEACHED_TEMPLATES = new Identifier[]{new Identifier("shipwreck/with_mast"), new Identifier("shipwreck/sideways_full"), new Identifier("shipwreck/sideways_fronthalf"), new Identifier("shipwreck/sideways_backhalf"), new Identifier("shipwreck/rightsideup_full"), new Identifier("shipwreck/rightsideup_fronthalf"), new Identifier("shipwreck/rightsideup_backhalf"), new Identifier("shipwreck/with_mast_degraded"), new Identifier("shipwreck/rightsideup_full_degraded"), new Identifier("shipwreck/rightsideup_fronthalf_degraded"), new Identifier("shipwreck/rightsideup_backhalf_degraded")};
    private static final Identifier[] REGULAR_TEMPLATES = new Identifier[]{new Identifier("shipwreck/with_mast"), new Identifier("shipwreck/upsidedown_full"), new Identifier("shipwreck/upsidedown_fronthalf"), new Identifier("shipwreck/upsidedown_backhalf"), new Identifier("shipwreck/sideways_full"), new Identifier("shipwreck/sideways_fronthalf"), new Identifier("shipwreck/sideways_backhalf"), new Identifier("shipwreck/rightsideup_full"), new Identifier("shipwreck/rightsideup_fronthalf"), new Identifier("shipwreck/rightsideup_backhalf"), new Identifier("shipwreck/with_mast_degraded"), new Identifier("shipwreck/upsidedown_full_degraded"), new Identifier("shipwreck/upsidedown_fronthalf_degraded"), new Identifier("shipwreck/upsidedown_backhalf_degraded"), new Identifier("shipwreck/sideways_full_degraded"), new Identifier("shipwreck/sideways_fronthalf_degraded"), new Identifier("shipwreck/sideways_backhalf_degraded"), new Identifier("shipwreck/rightsideup_full_degraded"), new Identifier("shipwreck/rightsideup_fronthalf_degraded"), new Identifier("shipwreck/rightsideup_backhalf_degraded")};

    public static void addParts(StructureManager arg, BlockPos arg2, BlockRotation arg3, List<StructurePiece> list, Random random, ShipwreckFeatureConfig arg4) {
        Identifier lv = Util.getRandom(arg4.isBeached ? BEACHED_TEMPLATES : REGULAR_TEMPLATES, random);
        list.add(new Piece(arg, lv, arg2, arg3, arg4.isBeached));
    }

    public static class Piece
    extends SimpleStructurePiece {
        private final BlockRotation rotation;
        private final Identifier template;
        private final boolean grounded;

        public Piece(StructureManager arg, Identifier arg2, BlockPos arg3, BlockRotation arg4, boolean bl) {
            super(StructurePieceType.SHIPWRECK, 0);
            this.pos = arg3;
            this.rotation = arg4;
            this.template = arg2;
            this.grounded = bl;
            this.initializeStructureData(arg);
        }

        public Piece(StructureManager arg, CompoundTag arg2) {
            super(StructurePieceType.SHIPWRECK, arg2);
            this.template = new Identifier(arg2.getString("Template"));
            this.grounded = arg2.getBoolean("isBeached");
            this.rotation = BlockRotation.valueOf(arg2.getString("Rot"));
            this.initializeStructureData(arg);
        }

        @Override
        protected void toNbt(CompoundTag arg) {
            super.toNbt(arg);
            arg.putString("Template", this.template.toString());
            arg.putBoolean("isBeached", this.grounded);
            arg.putString("Rot", this.rotation.name());
        }

        private void initializeStructureData(StructureManager arg) {
            Structure lv = arg.getStructureOrBlank(this.template);
            StructurePlacementData lv2 = new StructurePlacementData().setRotation(this.rotation).setMirror(BlockMirror.NONE).setPosition(field_14536).addProcessor(BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS);
            this.setStructureData(lv, this.pos, lv2);
        }

        @Override
        protected void handleMetadata(String string, BlockPos arg, IWorld arg2, Random random, BlockBox arg3) {
            if ("map_chest".equals(string)) {
                LootableContainerBlockEntity.setLootTable(arg2, random, arg.down(), LootTables.SHIPWRECK_MAP_CHEST);
            } else if ("treasure_chest".equals(string)) {
                LootableContainerBlockEntity.setLootTable(arg2, random, arg.down(), LootTables.SHIPWRECK_TREASURE_CHEST);
            } else if ("supply_chest".equals(string)) {
                LootableContainerBlockEntity.setLootTable(arg2, random, arg.down(), LootTables.SHIPWRECK_SUPPLY_CHEST);
            }
        }

        @Override
        public boolean generate(IWorld arg, StructureAccessor arg2, ChunkGenerator<?> arg3, Random random, BlockBox arg4, ChunkPos arg5, BlockPos arg6) {
            int i = 256;
            int j = 0;
            BlockPos lv = this.structure.getSize();
            Heightmap.Type lv2 = this.grounded ? Heightmap.Type.WORLD_SURFACE_WG : Heightmap.Type.OCEAN_FLOOR_WG;
            int k = lv.getX() * lv.getZ();
            if (k == 0) {
                j = arg.getTopY(lv2, this.pos.getX(), this.pos.getZ());
            } else {
                BlockPos lv3 = this.pos.add(lv.getX() - 1, 0, lv.getZ() - 1);
                for (BlockPos lv4 : BlockPos.iterate(this.pos, lv3)) {
                    int l = arg.getTopY(lv2, lv4.getX(), lv4.getZ());
                    j += l;
                    i = Math.min(i, l);
                }
                j /= k;
            }
            int m = this.grounded ? i - lv.getY() / 2 - random.nextInt(3) : j;
            this.pos = new BlockPos(this.pos.getX(), m, this.pos.getZ());
            return super.generate(arg, arg2, arg3, random, arg4, arg5, arg6);
        }
    }
}

