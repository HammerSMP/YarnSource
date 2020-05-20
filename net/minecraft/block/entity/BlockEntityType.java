/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.mojang.datafixers.types.Type
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.block.entity;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.types.Type;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BellBlockEntity;
import net.minecraft.block.entity.BlastFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.ComparatorBlockEntity;
import net.minecraft.block.entity.ConduitBlockEntity;
import net.minecraft.block.entity.DaylightDetectorBlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.block.entity.DropperBlockEntity;
import net.minecraft.block.entity.EnchantingTableBlockEntity;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.block.entity.EndPortalBlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.block.entity.SmokerBlockEntity;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.block.entity.TrappedChestBlockEntity;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BlockEntityType<T extends BlockEntity> {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final BlockEntityType<FurnaceBlockEntity> FURNACE = BlockEntityType.create("furnace", Builder.create(FurnaceBlockEntity::new, Blocks.FURNACE));
    public static final BlockEntityType<ChestBlockEntity> CHEST = BlockEntityType.create("chest", Builder.create(ChestBlockEntity::new, Blocks.CHEST));
    public static final BlockEntityType<TrappedChestBlockEntity> TRAPPED_CHEST = BlockEntityType.create("trapped_chest", Builder.create(TrappedChestBlockEntity::new, Blocks.TRAPPED_CHEST));
    public static final BlockEntityType<EnderChestBlockEntity> ENDER_CHEST = BlockEntityType.create("ender_chest", Builder.create(EnderChestBlockEntity::new, Blocks.ENDER_CHEST));
    public static final BlockEntityType<JukeboxBlockEntity> JUKEBOX = BlockEntityType.create("jukebox", Builder.create(JukeboxBlockEntity::new, Blocks.JUKEBOX));
    public static final BlockEntityType<DispenserBlockEntity> DISPENSER = BlockEntityType.create("dispenser", Builder.create(DispenserBlockEntity::new, Blocks.DISPENSER));
    public static final BlockEntityType<DropperBlockEntity> DROPPER = BlockEntityType.create("dropper", Builder.create(DropperBlockEntity::new, Blocks.DROPPER));
    public static final BlockEntityType<SignBlockEntity> SIGN = BlockEntityType.create("sign", Builder.create(SignBlockEntity::new, Blocks.OAK_SIGN, Blocks.SPRUCE_SIGN, Blocks.BIRCH_SIGN, Blocks.ACACIA_SIGN, Blocks.JUNGLE_SIGN, Blocks.DARK_OAK_SIGN, Blocks.OAK_WALL_SIGN, Blocks.SPRUCE_WALL_SIGN, Blocks.BIRCH_WALL_SIGN, Blocks.ACACIA_WALL_SIGN, Blocks.JUNGLE_WALL_SIGN, Blocks.DARK_OAK_WALL_SIGN, Blocks.CRIMSON_SIGN, Blocks.CRIMSON_WALL_SIGN, Blocks.WARPED_SIGN, Blocks.WARPED_WALL_SIGN));
    public static final BlockEntityType<MobSpawnerBlockEntity> MOB_SPAWNER = BlockEntityType.create("mob_spawner", Builder.create(MobSpawnerBlockEntity::new, Blocks.SPAWNER));
    public static final BlockEntityType<PistonBlockEntity> PISTON = BlockEntityType.create("piston", Builder.create(PistonBlockEntity::new, Blocks.MOVING_PISTON));
    public static final BlockEntityType<BrewingStandBlockEntity> BREWING_STAND = BlockEntityType.create("brewing_stand", Builder.create(BrewingStandBlockEntity::new, Blocks.BREWING_STAND));
    public static final BlockEntityType<EnchantingTableBlockEntity> ENCHANTING_TABLE = BlockEntityType.create("enchanting_table", Builder.create(EnchantingTableBlockEntity::new, Blocks.ENCHANTING_TABLE));
    public static final BlockEntityType<EndPortalBlockEntity> END_PORTAL = BlockEntityType.create("end_portal", Builder.create(EndPortalBlockEntity::new, Blocks.END_PORTAL));
    public static final BlockEntityType<BeaconBlockEntity> BEACON = BlockEntityType.create("beacon", Builder.create(BeaconBlockEntity::new, Blocks.BEACON));
    public static final BlockEntityType<SkullBlockEntity> SKULL = BlockEntityType.create("skull", Builder.create(SkullBlockEntity::new, Blocks.SKELETON_SKULL, Blocks.SKELETON_WALL_SKULL, Blocks.CREEPER_HEAD, Blocks.CREEPER_WALL_HEAD, Blocks.DRAGON_HEAD, Blocks.DRAGON_WALL_HEAD, Blocks.ZOMBIE_HEAD, Blocks.ZOMBIE_WALL_HEAD, Blocks.WITHER_SKELETON_SKULL, Blocks.WITHER_SKELETON_WALL_SKULL, Blocks.PLAYER_HEAD, Blocks.PLAYER_WALL_HEAD));
    public static final BlockEntityType<DaylightDetectorBlockEntity> DAYLIGHT_DETECTOR = BlockEntityType.create("daylight_detector", Builder.create(DaylightDetectorBlockEntity::new, Blocks.DAYLIGHT_DETECTOR));
    public static final BlockEntityType<HopperBlockEntity> HOPPER = BlockEntityType.create("hopper", Builder.create(HopperBlockEntity::new, Blocks.HOPPER));
    public static final BlockEntityType<ComparatorBlockEntity> COMPARATOR = BlockEntityType.create("comparator", Builder.create(ComparatorBlockEntity::new, Blocks.COMPARATOR));
    public static final BlockEntityType<BannerBlockEntity> BANNER = BlockEntityType.create("banner", Builder.create(BannerBlockEntity::new, Blocks.WHITE_BANNER, Blocks.ORANGE_BANNER, Blocks.MAGENTA_BANNER, Blocks.LIGHT_BLUE_BANNER, Blocks.YELLOW_BANNER, Blocks.LIME_BANNER, Blocks.PINK_BANNER, Blocks.GRAY_BANNER, Blocks.LIGHT_GRAY_BANNER, Blocks.CYAN_BANNER, Blocks.PURPLE_BANNER, Blocks.BLUE_BANNER, Blocks.BROWN_BANNER, Blocks.GREEN_BANNER, Blocks.RED_BANNER, Blocks.BLACK_BANNER, Blocks.WHITE_WALL_BANNER, Blocks.ORANGE_WALL_BANNER, Blocks.MAGENTA_WALL_BANNER, Blocks.LIGHT_BLUE_WALL_BANNER, Blocks.YELLOW_WALL_BANNER, Blocks.LIME_WALL_BANNER, Blocks.PINK_WALL_BANNER, Blocks.GRAY_WALL_BANNER, Blocks.LIGHT_GRAY_WALL_BANNER, Blocks.CYAN_WALL_BANNER, Blocks.PURPLE_WALL_BANNER, Blocks.BLUE_WALL_BANNER, Blocks.BROWN_WALL_BANNER, Blocks.GREEN_WALL_BANNER, Blocks.RED_WALL_BANNER, Blocks.BLACK_WALL_BANNER));
    public static final BlockEntityType<StructureBlockBlockEntity> STRUCTURE_BLOCK = BlockEntityType.create("structure_block", Builder.create(StructureBlockBlockEntity::new, Blocks.STRUCTURE_BLOCK));
    public static final BlockEntityType<EndGatewayBlockEntity> END_GATEWAY = BlockEntityType.create("end_gateway", Builder.create(EndGatewayBlockEntity::new, Blocks.END_GATEWAY));
    public static final BlockEntityType<CommandBlockBlockEntity> COMMAND_BLOCK = BlockEntityType.create("command_block", Builder.create(CommandBlockBlockEntity::new, Blocks.COMMAND_BLOCK, Blocks.CHAIN_COMMAND_BLOCK, Blocks.REPEATING_COMMAND_BLOCK));
    public static final BlockEntityType<ShulkerBoxBlockEntity> SHULKER_BOX = BlockEntityType.create("shulker_box", Builder.create(ShulkerBoxBlockEntity::new, Blocks.SHULKER_BOX, Blocks.BLACK_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.LIGHT_GRAY_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.WHITE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX));
    public static final BlockEntityType<BedBlockEntity> BED = BlockEntityType.create("bed", Builder.create(BedBlockEntity::new, Blocks.RED_BED, Blocks.BLACK_BED, Blocks.BLUE_BED, Blocks.BROWN_BED, Blocks.CYAN_BED, Blocks.GRAY_BED, Blocks.GREEN_BED, Blocks.LIGHT_BLUE_BED, Blocks.LIGHT_GRAY_BED, Blocks.LIME_BED, Blocks.MAGENTA_BED, Blocks.ORANGE_BED, Blocks.PINK_BED, Blocks.PURPLE_BED, Blocks.WHITE_BED, Blocks.YELLOW_BED));
    public static final BlockEntityType<ConduitBlockEntity> CONDUIT = BlockEntityType.create("conduit", Builder.create(ConduitBlockEntity::new, Blocks.CONDUIT));
    public static final BlockEntityType<BarrelBlockEntity> BARREL = BlockEntityType.create("barrel", Builder.create(BarrelBlockEntity::new, Blocks.BARREL));
    public static final BlockEntityType<SmokerBlockEntity> SMOKER = BlockEntityType.create("smoker", Builder.create(SmokerBlockEntity::new, Blocks.SMOKER));
    public static final BlockEntityType<BlastFurnaceBlockEntity> BLAST_FURNACE = BlockEntityType.create("blast_furnace", Builder.create(BlastFurnaceBlockEntity::new, Blocks.BLAST_FURNACE));
    public static final BlockEntityType<LecternBlockEntity> LECTERN = BlockEntityType.create("lectern", Builder.create(LecternBlockEntity::new, Blocks.LECTERN));
    public static final BlockEntityType<BellBlockEntity> BELL = BlockEntityType.create("bell", Builder.create(BellBlockEntity::new, Blocks.BELL));
    public static final BlockEntityType<JigsawBlockEntity> JIGSAW = BlockEntityType.create("jigsaw", Builder.create(JigsawBlockEntity::new, Blocks.JIGSAW));
    public static final BlockEntityType<CampfireBlockEntity> CAMPFIRE = BlockEntityType.create("campfire", Builder.create(CampfireBlockEntity::new, Blocks.CAMPFIRE, Blocks.SOUL_CAMPFIRE));
    public static final BlockEntityType<BeehiveBlockEntity> BEEHIVE = BlockEntityType.create("beehive", Builder.create(BeehiveBlockEntity::new, Blocks.BEE_NEST, Blocks.BEEHIVE));
    private final Supplier<? extends T> supplier;
    private final Set<Block> blocks;
    private final Type<?> type;

    @Nullable
    public static Identifier getId(BlockEntityType<?> arg) {
        return Registry.BLOCK_ENTITY_TYPE.getId(arg);
    }

    private static <T extends BlockEntity> BlockEntityType<T> create(String string, Builder<T> arg) {
        if (((Builder)arg).blocks.isEmpty()) {
            LOGGER.warn("Block entity type {} requires at least one valid block to be defined!", (Object)string);
        }
        Type<?> type = Util.method_29187(TypeReferences.BLOCK_ENTITY, string);
        return Registry.register(Registry.BLOCK_ENTITY_TYPE, string, arg.build(type));
    }

    public BlockEntityType(Supplier<? extends T> supplier, Set<Block> set, Type<?> type) {
        this.supplier = supplier;
        this.blocks = set;
        this.type = type;
    }

    @Nullable
    public T instantiate() {
        return (T)((BlockEntity)this.supplier.get());
    }

    public boolean supports(Block arg) {
        return this.blocks.contains(arg);
    }

    @Nullable
    public T get(BlockView arg, BlockPos arg2) {
        BlockEntity lv = arg.getBlockEntity(arg2);
        if (lv == null || lv.getType() != this) {
            return null;
        }
        return (T)lv;
    }

    public static final class Builder<T extends BlockEntity> {
        private final Supplier<? extends T> supplier;
        private final Set<Block> blocks;

        private Builder(Supplier<? extends T> supplier, Set<Block> set) {
            this.supplier = supplier;
            this.blocks = set;
        }

        public static <T extends BlockEntity> Builder<T> create(Supplier<? extends T> supplier, Block ... args) {
            return new Builder<T>(supplier, (Set<Block>)ImmutableSet.copyOf((Object[])args));
        }

        public BlockEntityType<T> build(Type<?> type) {
            return new BlockEntityType<T>(this.supplier, this.blocks, type);
        }
    }
}

