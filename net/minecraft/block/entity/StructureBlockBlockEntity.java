/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.block.entity;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StructureBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.BlockRotStructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class StructureBlockBlockEntity
extends BlockEntity {
    private Identifier structureName;
    private String author = "";
    private String metadata = "";
    private BlockPos offset = new BlockPos(0, 1, 0);
    private BlockPos size = BlockPos.ORIGIN;
    private BlockMirror mirror = BlockMirror.NONE;
    private BlockRotation rotation = BlockRotation.NONE;
    private StructureBlockMode mode = StructureBlockMode.DATA;
    private boolean ignoreEntities = true;
    private boolean powered;
    private boolean showAir;
    private boolean showBoundingBox = true;
    private float integrity = 1.0f;
    private long seed;

    public StructureBlockBlockEntity() {
        super(BlockEntityType.STRUCTURE_BLOCK);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public double getSquaredRenderDistance() {
        return 96.0;
    }

    @Override
    public CompoundTag toTag(CompoundTag arg) {
        super.toTag(arg);
        arg.putString("name", this.getStructureName());
        arg.putString("author", this.author);
        arg.putString("metadata", this.metadata);
        arg.putInt("posX", this.offset.getX());
        arg.putInt("posY", this.offset.getY());
        arg.putInt("posZ", this.offset.getZ());
        arg.putInt("sizeX", this.size.getX());
        arg.putInt("sizeY", this.size.getY());
        arg.putInt("sizeZ", this.size.getZ());
        arg.putString("rotation", this.rotation.toString());
        arg.putString("mirror", this.mirror.toString());
        arg.putString("mode", this.mode.toString());
        arg.putBoolean("ignoreEntities", this.ignoreEntities);
        arg.putBoolean("powered", this.powered);
        arg.putBoolean("showair", this.showAir);
        arg.putBoolean("showboundingbox", this.showBoundingBox);
        arg.putFloat("integrity", this.integrity);
        arg.putLong("seed", this.seed);
        return arg;
    }

    @Override
    public void fromTag(BlockState arg, CompoundTag arg2) {
        super.fromTag(arg, arg2);
        this.setStructureName(arg2.getString("name"));
        this.author = arg2.getString("author");
        this.metadata = arg2.getString("metadata");
        int i = MathHelper.clamp(arg2.getInt("posX"), -48, 48);
        int j = MathHelper.clamp(arg2.getInt("posY"), -48, 48);
        int k = MathHelper.clamp(arg2.getInt("posZ"), -48, 48);
        this.offset = new BlockPos(i, j, k);
        int l = MathHelper.clamp(arg2.getInt("sizeX"), 0, 48);
        int m = MathHelper.clamp(arg2.getInt("sizeY"), 0, 48);
        int n = MathHelper.clamp(arg2.getInt("sizeZ"), 0, 48);
        this.size = new BlockPos(l, m, n);
        try {
            this.rotation = BlockRotation.valueOf(arg2.getString("rotation"));
        }
        catch (IllegalArgumentException illegalArgumentException) {
            this.rotation = BlockRotation.NONE;
        }
        try {
            this.mirror = BlockMirror.valueOf(arg2.getString("mirror"));
        }
        catch (IllegalArgumentException illegalArgumentException2) {
            this.mirror = BlockMirror.NONE;
        }
        try {
            this.mode = StructureBlockMode.valueOf(arg2.getString("mode"));
        }
        catch (IllegalArgumentException illegalArgumentException3) {
            this.mode = StructureBlockMode.DATA;
        }
        this.ignoreEntities = arg2.getBoolean("ignoreEntities");
        this.powered = arg2.getBoolean("powered");
        this.showAir = arg2.getBoolean("showair");
        this.showBoundingBox = arg2.getBoolean("showboundingbox");
        this.integrity = arg2.contains("integrity") ? arg2.getFloat("integrity") : 1.0f;
        this.seed = arg2.getLong("seed");
        this.updateBlockMode();
    }

    private void updateBlockMode() {
        if (this.world == null) {
            return;
        }
        BlockPos lv = this.getPos();
        BlockState lv2 = this.world.getBlockState(lv);
        if (lv2.isOf(Blocks.STRUCTURE_BLOCK)) {
            this.world.setBlockState(lv, (BlockState)lv2.with(StructureBlock.MODE, this.mode), 2);
        }
    }

    @Override
    @Nullable
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return new BlockEntityUpdateS2CPacket(this.pos, 7, this.toInitialChunkDataTag());
    }

    @Override
    public CompoundTag toInitialChunkDataTag() {
        return this.toTag(new CompoundTag());
    }

    public boolean openScreen(PlayerEntity arg) {
        if (!arg.isCreativeLevelTwoOp()) {
            return false;
        }
        if (arg.getEntityWorld().isClient) {
            arg.openStructureBlockScreen(this);
        }
        return true;
    }

    public String getStructureName() {
        return this.structureName == null ? "" : this.structureName.toString();
    }

    public String getStructurePath() {
        return this.structureName == null ? "" : this.structureName.getPath();
    }

    public boolean hasStructureName() {
        return this.structureName != null;
    }

    public void setStructureName(@Nullable String string) {
        this.setStructureName(ChatUtil.isEmpty(string) ? null : Identifier.tryParse(string));
    }

    public void setStructureName(@Nullable Identifier arg) {
        this.structureName = arg;
    }

    public void setAuthor(LivingEntity arg) {
        this.author = arg.getName().getString();
    }

    @Environment(value=EnvType.CLIENT)
    public BlockPos getOffset() {
        return this.offset;
    }

    public void setOffset(BlockPos arg) {
        this.offset = arg;
    }

    public BlockPos getSize() {
        return this.size;
    }

    public void setSize(BlockPos arg) {
        this.size = arg;
    }

    @Environment(value=EnvType.CLIENT)
    public BlockMirror getMirror() {
        return this.mirror;
    }

    public void setMirror(BlockMirror arg) {
        this.mirror = arg;
    }

    public BlockRotation getRotation() {
        return this.rotation;
    }

    public void setRotation(BlockRotation arg) {
        this.rotation = arg;
    }

    @Environment(value=EnvType.CLIENT)
    public String getMetadata() {
        return this.metadata;
    }

    public void setMetadata(String string) {
        this.metadata = string;
    }

    public StructureBlockMode getMode() {
        return this.mode;
    }

    public void setMode(StructureBlockMode arg) {
        this.mode = arg;
        BlockState lv = this.world.getBlockState(this.getPos());
        if (lv.isOf(Blocks.STRUCTURE_BLOCK)) {
            this.world.setBlockState(this.getPos(), (BlockState)lv.with(StructureBlock.MODE, arg), 2);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public void cycleMode() {
        switch (this.getMode()) {
            case SAVE: {
                this.setMode(StructureBlockMode.LOAD);
                break;
            }
            case LOAD: {
                this.setMode(StructureBlockMode.CORNER);
                break;
            }
            case CORNER: {
                this.setMode(StructureBlockMode.DATA);
                break;
            }
            case DATA: {
                this.setMode(StructureBlockMode.SAVE);
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    public boolean shouldIgnoreEntities() {
        return this.ignoreEntities;
    }

    public void setIgnoreEntities(boolean bl) {
        this.ignoreEntities = bl;
    }

    @Environment(value=EnvType.CLIENT)
    public float getIntegrity() {
        return this.integrity;
    }

    public void setIntegrity(float f) {
        this.integrity = f;
    }

    @Environment(value=EnvType.CLIENT)
    public long getSeed() {
        return this.seed;
    }

    public void setSeed(long l) {
        this.seed = l;
    }

    public boolean detectStructureSize() {
        BlockPos lv3;
        if (this.mode != StructureBlockMode.SAVE) {
            return false;
        }
        BlockPos lv = this.getPos();
        int i = 80;
        BlockPos lv2 = new BlockPos(lv.getX() - 80, 0, lv.getZ() - 80);
        List<StructureBlockBlockEntity> list = this.findStructureBlockEntities(lv2, lv3 = new BlockPos(lv.getX() + 80, 255, lv.getZ() + 80));
        List<StructureBlockBlockEntity> list2 = this.findCorners(list);
        if (list2.size() < 1) {
            return false;
        }
        BlockBox lv4 = this.makeBoundingBox(lv, list2);
        if (lv4.maxX - lv4.minX > 1 && lv4.maxY - lv4.minY > 1 && lv4.maxZ - lv4.minZ > 1) {
            this.offset = new BlockPos(lv4.minX - lv.getX() + 1, lv4.minY - lv.getY() + 1, lv4.minZ - lv.getZ() + 1);
            this.size = new BlockPos(lv4.maxX - lv4.minX - 1, lv4.maxY - lv4.minY - 1, lv4.maxZ - lv4.minZ - 1);
            this.markDirty();
            BlockState lv5 = this.world.getBlockState(lv);
            this.world.updateListeners(lv, lv5, lv5, 3);
            return true;
        }
        return false;
    }

    private List<StructureBlockBlockEntity> findCorners(List<StructureBlockBlockEntity> list) {
        Predicate<StructureBlockBlockEntity> predicate = arg -> arg.mode == StructureBlockMode.CORNER && Objects.equals(this.structureName, arg.structureName);
        return list.stream().filter(predicate).collect(Collectors.toList());
    }

    private List<StructureBlockBlockEntity> findStructureBlockEntities(BlockPos arg, BlockPos arg2) {
        ArrayList list = Lists.newArrayList();
        for (BlockPos lv : BlockPos.iterate(arg, arg2)) {
            BlockEntity lv3;
            BlockState lv2 = this.world.getBlockState(lv);
            if (!lv2.isOf(Blocks.STRUCTURE_BLOCK) || (lv3 = this.world.getBlockEntity(lv)) == null || !(lv3 instanceof StructureBlockBlockEntity)) continue;
            list.add((StructureBlockBlockEntity)lv3);
        }
        return list;
    }

    private BlockBox makeBoundingBox(BlockPos arg, List<StructureBlockBlockEntity> list) {
        BlockBox lv3;
        if (list.size() > 1) {
            BlockPos lv = list.get(0).getPos();
            BlockBox lv2 = new BlockBox(lv, lv);
        } else {
            lv3 = new BlockBox(arg, arg);
        }
        for (StructureBlockBlockEntity lv4 : list) {
            BlockPos lv5 = lv4.getPos();
            if (lv5.getX() < lv3.minX) {
                lv3.minX = lv5.getX();
            } else if (lv5.getX() > lv3.maxX) {
                lv3.maxX = lv5.getX();
            }
            if (lv5.getY() < lv3.minY) {
                lv3.minY = lv5.getY();
            } else if (lv5.getY() > lv3.maxY) {
                lv3.maxY = lv5.getY();
            }
            if (lv5.getZ() < lv3.minZ) {
                lv3.minZ = lv5.getZ();
                continue;
            }
            if (lv5.getZ() <= lv3.maxZ) continue;
            lv3.maxZ = lv5.getZ();
        }
        return lv3;
    }

    public boolean saveStructure() {
        return this.saveStructure(true);
    }

    /*
     * WARNING - void declaration
     */
    public boolean saveStructure(boolean bl) {
        void lv6;
        if (this.mode != StructureBlockMode.SAVE || this.world.isClient || this.structureName == null) {
            return false;
        }
        BlockPos lv = this.getPos().add(this.offset);
        ServerWorld lv2 = (ServerWorld)this.world;
        StructureManager lv3 = lv2.getStructureManager();
        try {
            Structure lv4 = lv3.getStructureOrBlank(this.structureName);
        }
        catch (InvalidIdentifierException lv5) {
            return false;
        }
        lv6.saveFromWorld(this.world, lv, this.size, !this.ignoreEntities, Blocks.STRUCTURE_VOID);
        lv6.setAuthor(this.author);
        if (bl) {
            try {
                return lv3.saveStructure(this.structureName);
            }
            catch (InvalidIdentifierException lv7) {
                return false;
            }
        }
        return true;
    }

    public boolean loadStructure() {
        return this.loadStructure(true);
    }

    private static Random createRandom(long l) {
        if (l == 0L) {
            return new Random(Util.getMeasuringTimeMs());
        }
        return new Random(l);
    }

    /*
     * WARNING - void declaration
     */
    public boolean loadStructure(boolean bl) {
        void lv5;
        if (this.mode != StructureBlockMode.LOAD || this.world.isClient || this.structureName == null) {
            return false;
        }
        ServerWorld lv = (ServerWorld)this.world;
        StructureManager lv2 = lv.getStructureManager();
        try {
            Structure lv3 = lv2.getStructure(this.structureName);
        }
        catch (InvalidIdentifierException lv4) {
            return false;
        }
        if (lv5 == null) {
            return false;
        }
        return this.place(bl, (Structure)lv5);
    }

    public boolean place(boolean bl, Structure arg) {
        BlockPos lv2;
        boolean bl2;
        BlockPos lv = this.getPos();
        if (!ChatUtil.isEmpty(arg.getAuthor())) {
            this.author = arg.getAuthor();
        }
        if (!(bl2 = this.size.equals(lv2 = arg.getSize()))) {
            this.size = lv2;
            this.markDirty();
            BlockState lv3 = this.world.getBlockState(lv);
            this.world.updateListeners(lv, lv3, lv3, 3);
        }
        if (!bl || bl2) {
            StructurePlacementData lv4 = new StructurePlacementData().setMirror(this.mirror).setRotation(this.rotation).setIgnoreEntities(this.ignoreEntities).setChunkPosition(null);
            if (this.integrity < 1.0f) {
                lv4.clearProcessors().addProcessor(new BlockRotStructureProcessor(MathHelper.clamp(this.integrity, 0.0f, 1.0f))).setRandom(StructureBlockBlockEntity.createRandom(this.seed));
            }
            BlockPos lv5 = lv.add(this.offset);
            arg.place(this.world, lv5, lv4, StructureBlockBlockEntity.createRandom(this.seed));
            return true;
        }
        return false;
    }

    public void unloadStructure() {
        if (this.structureName == null) {
            return;
        }
        ServerWorld lv = (ServerWorld)this.world;
        StructureManager lv2 = lv.getStructureManager();
        lv2.unloadStructure(this.structureName);
    }

    public boolean isStructureAvailable() {
        if (this.mode != StructureBlockMode.LOAD || this.world.isClient || this.structureName == null) {
            return false;
        }
        ServerWorld lv = (ServerWorld)this.world;
        StructureManager lv2 = lv.getStructureManager();
        try {
            return lv2.getStructure(this.structureName) != null;
        }
        catch (InvalidIdentifierException lv3) {
            return false;
        }
    }

    public boolean isPowered() {
        return this.powered;
    }

    public void setPowered(boolean bl) {
        this.powered = bl;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean shouldShowAir() {
        return this.showAir;
    }

    public void setShowAir(boolean bl) {
        this.showAir = bl;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean shouldShowBoundingBox() {
        return this.showBoundingBox;
    }

    public void setShowBoundingBox(boolean bl) {
        this.showBoundingBox = bl;
    }

    public static enum Action {
        UPDATE_DATA,
        SAVE_AREA,
        LOAD_AREA,
        SCAN_AREA;

    }
}

