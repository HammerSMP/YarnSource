/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.structure;

import com.google.common.collect.Lists;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.VineBlock;
import net.minecraft.class_5425;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.structure.SimpleStructurePiece;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.BlackstoneReplacementStructureProcessor;
import net.minecraft.structure.processor.BlockAgeStructureProcessor;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.structure.processor.LavaSubmergedBlockStructureProcessor;
import net.minecraft.structure.processor.RuleStructureProcessor;
import net.minecraft.structure.processor.StructureProcessorRule;
import net.minecraft.structure.rule.AlwaysTrueRuleTest;
import net.minecraft.structure.rule.BlockMatchRuleTest;
import net.minecraft.structure.rule.RandomBlockMatchRuleTest;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RuinedPortalStructurePiece
extends SimpleStructurePiece {
    private static final Logger field_24992 = LogManager.getLogger();
    private final Identifier template;
    private final BlockRotation rotation;
    private final BlockMirror mirror;
    private final VerticalPlacement verticalPlacement;
    private final Properties properties;

    public RuinedPortalStructurePiece(BlockPos pos, VerticalPlacement verticalPlacement, Properties properties, Identifier template, Structure structure, BlockRotation rotation, BlockMirror mirror, BlockPos center) {
        super(StructurePieceType.RUINED_PORTAL, 0);
        this.pos = pos;
        this.template = template;
        this.rotation = rotation;
        this.mirror = mirror;
        this.verticalPlacement = verticalPlacement;
        this.properties = properties;
        this.processProperties(structure, center);
    }

    public RuinedPortalStructurePiece(StructureManager manager, CompoundTag tag) {
        super(StructurePieceType.RUINED_PORTAL, tag);
        this.template = new Identifier(tag.getString("Template"));
        this.rotation = BlockRotation.valueOf(tag.getString("Rotation"));
        this.mirror = BlockMirror.valueOf(tag.getString("Mirror"));
        this.verticalPlacement = VerticalPlacement.getFromId(tag.getString("VerticalPlacement"));
        this.properties = (Properties)Properties.CODEC.parse(new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)tag.get("Properties"))).getOrThrow(true, ((Logger)field_24992)::error);
        Structure lv = manager.getStructureOrBlank(this.template);
        this.processProperties(lv, new BlockPos(lv.getSize().getX() / 2, 0, lv.getSize().getZ() / 2));
    }

    @Override
    protected void toNbt(CompoundTag tag) {
        super.toNbt(tag);
        tag.putString("Template", this.template.toString());
        tag.putString("Rotation", this.rotation.name());
        tag.putString("Mirror", this.mirror.name());
        tag.putString("VerticalPlacement", this.verticalPlacement.getId());
        Properties.CODEC.encodeStart((DynamicOps)NbtOps.INSTANCE, (Object)this.properties).resultOrPartial(((Logger)field_24992)::error).ifPresent(arg2 -> tag.put("Properties", (Tag)arg2));
    }

    private void processProperties(Structure structure, BlockPos center) {
        BlockIgnoreStructureProcessor lv = this.properties.airPocket ? BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS : BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS;
        ArrayList list = Lists.newArrayList();
        list.add(RuinedPortalStructurePiece.createReplacementRule(Blocks.GOLD_BLOCK, 0.3f, Blocks.AIR));
        list.add(this.createLavaReplacementRule());
        if (!this.properties.cold) {
            list.add(RuinedPortalStructurePiece.createReplacementRule(Blocks.NETHERRACK, 0.07f, Blocks.MAGMA_BLOCK));
        }
        StructurePlacementData lv2 = new StructurePlacementData().setRotation(this.rotation).setMirror(this.mirror).setPosition(center).addProcessor(lv).addProcessor(new RuleStructureProcessor(list)).addProcessor(new BlockAgeStructureProcessor(this.properties.mossiness)).addProcessor(new LavaSubmergedBlockStructureProcessor());
        if (this.properties.replaceWithBlackstone) {
            lv2.addProcessor(BlackstoneReplacementStructureProcessor.INSTANCE);
        }
        this.setStructureData(structure, this.pos, lv2);
    }

    private StructureProcessorRule createLavaReplacementRule() {
        if (this.verticalPlacement == VerticalPlacement.ON_OCEAN_FLOOR) {
            return RuinedPortalStructurePiece.createReplacementRule(Blocks.LAVA, Blocks.MAGMA_BLOCK);
        }
        if (this.properties.cold) {
            return RuinedPortalStructurePiece.createReplacementRule(Blocks.LAVA, Blocks.NETHERRACK);
        }
        return RuinedPortalStructurePiece.createReplacementRule(Blocks.LAVA, 0.2f, Blocks.MAGMA_BLOCK);
    }

    @Override
    public boolean generate(ServerWorldAccess arg, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox boundingBox, ChunkPos arg5, BlockPos arg6) {
        if (!boundingBox.contains(this.pos)) {
            return true;
        }
        boundingBox.encompass(this.structure.calculateBoundingBox(this.placementData, this.pos));
        boolean bl = super.generate(arg, structureAccessor, chunkGenerator, random, boundingBox, arg5, arg6);
        this.placeNetherrackBase(random, arg);
        this.updateNetherracksInBound(random, arg);
        if (this.properties.vines || this.properties.overgrown) {
            BlockPos.stream(this.getBoundingBox()).forEach(arg2 -> {
                if (this.properties.vines) {
                    this.generateVines(random, arg, (BlockPos)arg2);
                }
                if (this.properties.overgrown) {
                    this.generateOvergrownLeaves(random, arg, (BlockPos)arg2);
                }
            });
        }
        return bl;
    }

    @Override
    protected void handleMetadata(String metadata, BlockPos pos, class_5425 arg2, Random random, BlockBox boundingBox) {
    }

    private void generateVines(Random random, WorldAccess world, BlockPos pos) {
        BlockState lv = world.getBlockState(pos);
        if (lv.isAir() || lv.isOf(Blocks.VINE)) {
            return;
        }
        Direction lv2 = Direction.Type.HORIZONTAL.random(random);
        BlockPos lv3 = pos.offset(lv2);
        BlockState lv4 = world.getBlockState(lv3);
        if (!lv4.isAir()) {
            return;
        }
        if (!Block.isFaceFullSquare(lv.getCollisionShape(world, pos), lv2)) {
            return;
        }
        BooleanProperty lv5 = VineBlock.getFacingProperty(lv2.getOpposite());
        world.setBlockState(lv3, (BlockState)Blocks.VINE.getDefaultState().with(lv5, true), 3);
    }

    private void generateOvergrownLeaves(Random random, WorldAccess world, BlockPos pos) {
        if (random.nextFloat() < 0.5f && world.getBlockState(pos).isOf(Blocks.NETHERRACK) && world.getBlockState(pos.up()).isAir()) {
            world.setBlockState(pos.up(), (BlockState)Blocks.JUNGLE_LEAVES.getDefaultState().with(LeavesBlock.PERSISTENT, true), 3);
        }
    }

    private void updateNetherracksInBound(Random random, WorldAccess world) {
        for (int i = this.boundingBox.minX + 1; i < this.boundingBox.maxX; ++i) {
            for (int j = this.boundingBox.minZ + 1; j < this.boundingBox.maxZ; ++j) {
                BlockPos lv = new BlockPos(i, this.boundingBox.minY, j);
                if (!world.getBlockState(lv).isOf(Blocks.NETHERRACK)) continue;
                this.updateNetherracks(random, world, lv.down());
            }
        }
    }

    private void updateNetherracks(Random random, WorldAccess world, BlockPos pos) {
        BlockPos.Mutable lv = pos.mutableCopy();
        this.placeNetherrackBottom(random, world, lv);
        for (int i = 8; i > 0 && random.nextFloat() < 0.5f; --i) {
            lv.move(Direction.DOWN);
            this.placeNetherrackBottom(random, world, lv);
        }
    }

    private void placeNetherrackBase(Random random, WorldAccess world) {
        boolean bl = this.verticalPlacement == VerticalPlacement.ON_LAND_SURFACE || this.verticalPlacement == VerticalPlacement.ON_OCEAN_FLOOR;
        Vec3i lv = this.boundingBox.getCenter();
        int i = lv.getX();
        int j = lv.getZ();
        float[] fs = new float[]{1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.9f, 0.9f, 0.8f, 0.7f, 0.6f, 0.4f, 0.2f};
        int k = fs.length;
        int l = (this.boundingBox.getBlockCountX() + this.boundingBox.getBlockCountZ()) / 2;
        int m = random.nextInt(Math.max(1, 8 - l / 2));
        int n = 3;
        BlockPos.Mutable lv2 = BlockPos.ORIGIN.mutableCopy();
        for (int o = i - k; o <= i + k; ++o) {
            for (int p = j - k; p <= j + k; ++p) {
                int q = Math.abs(o - i) + Math.abs(p - j);
                int r = Math.max(0, q + m);
                if (r >= k) continue;
                float f = fs[r];
                if (!(random.nextDouble() < (double)f)) continue;
                int s = RuinedPortalStructurePiece.getBaseHeight(world, o, p, this.verticalPlacement);
                int t = bl ? s : Math.min(this.boundingBox.minY, s);
                lv2.set(o, t, p);
                if (Math.abs(t - this.boundingBox.minY) > 3 || !this.canFillNetherrack(world, lv2)) continue;
                this.placeNetherrackBottom(random, world, lv2);
                if (this.properties.overgrown) {
                    this.generateOvergrownLeaves(random, world, lv2);
                }
                this.updateNetherracks(random, world, (BlockPos)lv2.down());
            }
        }
    }

    private boolean canFillNetherrack(WorldAccess world, BlockPos pos) {
        BlockState lv = world.getBlockState(pos);
        return !lv.isOf(Blocks.AIR) && !lv.isOf(Blocks.OBSIDIAN) && !lv.isOf(Blocks.CHEST) && (this.verticalPlacement == VerticalPlacement.IN_NETHER || !lv.isOf(Blocks.LAVA));
    }

    private void placeNetherrackBottom(Random random, WorldAccess world, BlockPos pos) {
        if (!this.properties.cold && random.nextFloat() < 0.07f) {
            world.setBlockState(pos, Blocks.MAGMA_BLOCK.getDefaultState(), 3);
        } else {
            world.setBlockState(pos, Blocks.NETHERRACK.getDefaultState(), 3);
        }
    }

    private static int getBaseHeight(WorldAccess world, int x, int y, VerticalPlacement verticalPlacement) {
        return world.getTopY(RuinedPortalStructurePiece.getHeightmapType(verticalPlacement), x, y) - 1;
    }

    public static Heightmap.Type getHeightmapType(VerticalPlacement verticalPlacement) {
        return verticalPlacement == VerticalPlacement.ON_OCEAN_FLOOR ? Heightmap.Type.OCEAN_FLOOR_WG : Heightmap.Type.WORLD_SURFACE_WG;
    }

    private static StructureProcessorRule createReplacementRule(Block old, float chance, Block updated) {
        return new StructureProcessorRule(new RandomBlockMatchRuleTest(old, chance), AlwaysTrueRuleTest.INSTANCE, updated.getDefaultState());
    }

    private static StructureProcessorRule createReplacementRule(Block old, Block updated) {
        return new StructureProcessorRule(new BlockMatchRuleTest(old), AlwaysTrueRuleTest.INSTANCE, updated.getDefaultState());
    }

    public static enum VerticalPlacement {
        ON_LAND_SURFACE("on_land_surface"),
        PARTLY_BURIED("partly_buried"),
        ON_OCEAN_FLOOR("on_ocean_floor"),
        IN_MOUNTAIN("in_mountain"),
        UNDERGROUND("underground"),
        IN_NETHER("in_nether");

        private static final Map<String, VerticalPlacement> VERTICAL_PLACEMENTS;
        private final String id;

        private VerticalPlacement(String id) {
            this.id = id;
        }

        public String getId() {
            return this.id;
        }

        public static VerticalPlacement getFromId(String id) {
            return VERTICAL_PLACEMENTS.get(id);
        }

        static {
            VERTICAL_PLACEMENTS = Arrays.stream(VerticalPlacement.values()).collect(Collectors.toMap(VerticalPlacement::getId, arg -> arg));
        }
    }

    public static class Properties {
        public static final Codec<Properties> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.BOOL.fieldOf("cold").forGetter(arg -> arg.cold), (App)Codec.FLOAT.fieldOf("mossiness").forGetter(arg -> Float.valueOf(arg.mossiness)), (App)Codec.BOOL.fieldOf("air_pocket").forGetter(arg -> arg.airPocket), (App)Codec.BOOL.fieldOf("overgrown").forGetter(arg -> arg.overgrown), (App)Codec.BOOL.fieldOf("vines").forGetter(arg -> arg.vines), (App)Codec.BOOL.fieldOf("replace_with_blackstone").forGetter(arg -> arg.replaceWithBlackstone)).apply((Applicative)instance, Properties::new));
        public boolean cold;
        public float mossiness = 0.2f;
        public boolean airPocket;
        public boolean overgrown;
        public boolean vines;
        public boolean replaceWithBlackstone;

        public Properties() {
        }

        public <T> Properties(boolean cold, float mossiness, boolean airPocket, boolean overgrown, boolean vines, boolean replaceWithBlackstone) {
            this.cold = cold;
            this.mossiness = mossiness;
            this.airPocket = airPocket;
            this.overgrown = overgrown;
            this.vines = vines;
            this.replaceWithBlackstone = replaceWithBlackstone;
        }
    }
}

