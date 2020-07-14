/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.block.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Stainable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ContainerLock;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.BeaconScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.Heightmap;

public class BeaconBlockEntity
extends BlockEntity
implements NamedScreenHandlerFactory,
Tickable {
    public static final StatusEffect[][] EFFECTS_BY_LEVEL = new StatusEffect[][]{{StatusEffects.SPEED, StatusEffects.HASTE}, {StatusEffects.RESISTANCE, StatusEffects.JUMP_BOOST}, {StatusEffects.STRENGTH}, {StatusEffects.REGENERATION}};
    private static final Set<StatusEffect> EFFECTS = Arrays.stream(EFFECTS_BY_LEVEL).flatMap(Arrays::stream).collect(Collectors.toSet());
    private List<BeamSegment> beamSegments = Lists.newArrayList();
    private List<BeamSegment> field_19178 = Lists.newArrayList();
    private int level;
    private int field_19179 = -1;
    @Nullable
    private StatusEffect primary;
    @Nullable
    private StatusEffect secondary;
    @Nullable
    private Text customName;
    private ContainerLock lock = ContainerLock.EMPTY;
    private final PropertyDelegate propertyDelegate = new PropertyDelegate(){

        @Override
        public int get(int index) {
            switch (index) {
                case 0: {
                    return BeaconBlockEntity.this.level;
                }
                case 1: {
                    return StatusEffect.getRawId(BeaconBlockEntity.this.primary);
                }
                case 2: {
                    return StatusEffect.getRawId(BeaconBlockEntity.this.secondary);
                }
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0: {
                    BeaconBlockEntity.this.level = value;
                    break;
                }
                case 1: {
                    if (!BeaconBlockEntity.this.world.isClient && !BeaconBlockEntity.this.beamSegments.isEmpty()) {
                        BeaconBlockEntity.this.playSound(SoundEvents.BLOCK_BEACON_POWER_SELECT);
                    }
                    BeaconBlockEntity.this.primary = BeaconBlockEntity.getPotionEffectById(value);
                    break;
                }
                case 2: {
                    BeaconBlockEntity.this.secondary = BeaconBlockEntity.getPotionEffectById(value);
                }
            }
        }

        @Override
        public int size() {
            return 3;
        }
    };

    public BeaconBlockEntity() {
        super(BlockEntityType.BEACON);
    }

    @Override
    public void tick() {
        BlockPos lv2;
        int i = this.pos.getX();
        int j = this.pos.getY();
        int k = this.pos.getZ();
        if (this.field_19179 < j) {
            BlockPos lv = this.pos;
            this.field_19178 = Lists.newArrayList();
            this.field_19179 = lv.getY() - 1;
        } else {
            lv2 = new BlockPos(i, this.field_19179 + 1, k);
        }
        BeamSegment lv3 = this.field_19178.isEmpty() ? null : this.field_19178.get(this.field_19178.size() - 1);
        int l = this.world.getTopY(Heightmap.Type.WORLD_SURFACE, i, k);
        for (int m = 0; m < 10 && lv2.getY() <= l; ++m) {
            block18: {
                Block lv5;
                BlockState lv4;
                block16: {
                    float[] fs;
                    block17: {
                        lv4 = this.world.getBlockState(lv2);
                        lv5 = lv4.getBlock();
                        if (!(lv5 instanceof Stainable)) break block16;
                        fs = ((Stainable)((Object)lv5)).getColor().getColorComponents();
                        if (this.field_19178.size() > 1) break block17;
                        lv3 = new BeamSegment(fs);
                        this.field_19178.add(lv3);
                        break block18;
                    }
                    if (lv3 == null) break block18;
                    if (Arrays.equals(fs, lv3.color)) {
                        lv3.increaseHeight();
                    } else {
                        lv3 = new BeamSegment(new float[]{(lv3.color[0] + fs[0]) / 2.0f, (lv3.color[1] + fs[1]) / 2.0f, (lv3.color[2] + fs[2]) / 2.0f});
                        this.field_19178.add(lv3);
                    }
                    break block18;
                }
                if (lv3 != null && (lv4.getOpacity(this.world, lv2) < 15 || lv5 == Blocks.BEDROCK)) {
                    lv3.increaseHeight();
                } else {
                    this.field_19178.clear();
                    this.field_19179 = l;
                    break;
                }
            }
            lv2 = lv2.up();
            ++this.field_19179;
        }
        int n = this.level;
        if (this.world.getTime() % 80L == 0L) {
            if (!this.beamSegments.isEmpty()) {
                this.updateLevel(i, j, k);
            }
            if (this.level > 0 && !this.beamSegments.isEmpty()) {
                this.applyPlayerEffects();
                this.playSound(SoundEvents.BLOCK_BEACON_AMBIENT);
            }
        }
        if (this.field_19179 >= l) {
            this.field_19179 = -1;
            boolean bl = n > 0;
            this.beamSegments = this.field_19178;
            if (!this.world.isClient) {
                boolean bl2;
                boolean bl3 = bl2 = this.level > 0;
                if (!bl && bl2) {
                    this.playSound(SoundEvents.BLOCK_BEACON_ACTIVATE);
                    for (ServerPlayerEntity lv6 : this.world.getNonSpectatingEntities(ServerPlayerEntity.class, new Box(i, j, k, i, j - 4, k).expand(10.0, 5.0, 10.0))) {
                        Criteria.CONSTRUCT_BEACON.trigger(lv6, this);
                    }
                } else if (bl && !bl2) {
                    this.playSound(SoundEvents.BLOCK_BEACON_DEACTIVATE);
                }
            }
        }
    }

    private void updateLevel(int x, int y, int z) {
        int m;
        this.level = 0;
        int l = 1;
        while (l <= 4 && (m = y - l) >= 0) {
            boolean bl = true;
            block1: for (int n = x - l; n <= x + l && bl; ++n) {
                for (int o = z - l; o <= z + l; ++o) {
                    if (this.world.getBlockState(new BlockPos(n, m, o)).isIn(BlockTags.BEACON_BASE_BLOCKS)) continue;
                    bl = false;
                    continue block1;
                }
            }
            if (!bl) break;
            this.level = l++;
        }
    }

    @Override
    public void markRemoved() {
        this.playSound(SoundEvents.BLOCK_BEACON_DEACTIVATE);
        super.markRemoved();
    }

    private void applyPlayerEffects() {
        if (this.world.isClient || this.primary == null) {
            return;
        }
        double d = this.level * 10 + 10;
        int i = 0;
        if (this.level >= 4 && this.primary == this.secondary) {
            i = 1;
        }
        int j = (9 + this.level * 2) * 20;
        Box lv = new Box(this.pos).expand(d).stretch(0.0, this.world.getHeight(), 0.0);
        List<PlayerEntity> list = this.world.getNonSpectatingEntities(PlayerEntity.class, lv);
        for (PlayerEntity lv2 : list) {
            lv2.addStatusEffect(new StatusEffectInstance(this.primary, j, i, true, true));
        }
        if (this.level >= 4 && this.primary != this.secondary && this.secondary != null) {
            for (PlayerEntity lv3 : list) {
                lv3.addStatusEffect(new StatusEffectInstance(this.secondary, j, 0, true, true));
            }
        }
    }

    public void playSound(SoundEvent arg) {
        this.world.playSound(null, this.pos, arg, SoundCategory.BLOCKS, 1.0f, 1.0f);
    }

    @Environment(value=EnvType.CLIENT)
    public List<BeamSegment> getBeamSegments() {
        return this.level == 0 ? ImmutableList.of() : this.beamSegments;
    }

    public int getLevel() {
        return this.level;
    }

    @Override
    @Nullable
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return new BlockEntityUpdateS2CPacket(this.pos, 3, this.toInitialChunkDataTag());
    }

    @Override
    public CompoundTag toInitialChunkDataTag() {
        return this.toTag(new CompoundTag());
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public double getSquaredRenderDistance() {
        return 256.0;
    }

    @Nullable
    private static StatusEffect getPotionEffectById(int id) {
        StatusEffect lv = StatusEffect.byRawId(id);
        return EFFECTS.contains(lv) ? lv : null;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        this.primary = BeaconBlockEntity.getPotionEffectById(tag.getInt("Primary"));
        this.secondary = BeaconBlockEntity.getPotionEffectById(tag.getInt("Secondary"));
        if (tag.contains("CustomName", 8)) {
            this.customName = Text.Serializer.fromJson(tag.getString("CustomName"));
        }
        this.lock = ContainerLock.fromTag(tag);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        tag.putInt("Primary", StatusEffect.getRawId(this.primary));
        tag.putInt("Secondary", StatusEffect.getRawId(this.secondary));
        tag.putInt("Levels", this.level);
        if (this.customName != null) {
            tag.putString("CustomName", Text.Serializer.toJson(this.customName));
        }
        this.lock.toTag(tag);
        return tag;
    }

    public void setCustomName(@Nullable Text arg) {
        this.customName = arg;
    }

    @Override
    @Nullable
    public ScreenHandler createMenu(int i, PlayerInventory arg, PlayerEntity arg2) {
        if (LockableContainerBlockEntity.checkUnlocked(arg2, this.lock, this.getDisplayName())) {
            return new BeaconScreenHandler(i, arg, this.propertyDelegate, ScreenHandlerContext.create(this.world, this.getPos()));
        }
        return null;
    }

    @Override
    public Text getDisplayName() {
        return this.customName != null ? this.customName : new TranslatableText("container.beacon");
    }

    public static class BeamSegment {
        private final float[] color;
        private int height;

        public BeamSegment(float[] color) {
            this.color = color;
            this.height = 1;
        }

        protected void increaseHeight() {
            ++this.height;
        }

        @Environment(value=EnvType.CLIENT)
        public float[] getColor() {
            return this.color;
        }

        @Environment(value=EnvType.CLIENT)
        public int getHeight() {
            return this.height;
        }
    }
}

