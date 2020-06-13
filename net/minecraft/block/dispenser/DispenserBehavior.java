/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block.dispenser;

import java.util.List;
import java.util.Random;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.CarvedPumpkinBlock;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.TntBlock;
import net.minecraft.block.WitherSkullBlock;
import net.minecraft.block.dispenser.BlockPlacementDispenserBehavior;
import net.minecraft.block.dispenser.BoatDispenserBehavior;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.dispenser.ProjectileDispenserBehavior;
import net.minecraft.block.dispenser.ShearsDispenserBehavior;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Saddleable;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.entity.projectile.thrown.EggEntity;
import net.minecraft.entity.projectile.thrown.ExperienceBottleEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;

public interface DispenserBehavior {
    public static final DispenserBehavior NOOP = (arg, arg2) -> arg2;

    public ItemStack dispense(BlockPointer var1, ItemStack var2);

    public static void registerDefaults() {
        DispenserBlock.registerBehavior(Items.ARROW, new ProjectileDispenserBehavior(){

            @Override
            protected ProjectileEntity createProjectile(World arg, Position arg2, ItemStack arg3) {
                ArrowEntity lv = new ArrowEntity(arg, arg2.getX(), arg2.getY(), arg2.getZ());
                lv.pickupType = PersistentProjectileEntity.PickupPermission.ALLOWED;
                return lv;
            }
        });
        DispenserBlock.registerBehavior(Items.TIPPED_ARROW, new ProjectileDispenserBehavior(){

            @Override
            protected ProjectileEntity createProjectile(World arg, Position arg2, ItemStack arg3) {
                ArrowEntity lv = new ArrowEntity(arg, arg2.getX(), arg2.getY(), arg2.getZ());
                lv.initFromStack(arg3);
                lv.pickupType = PersistentProjectileEntity.PickupPermission.ALLOWED;
                return lv;
            }
        });
        DispenserBlock.registerBehavior(Items.SPECTRAL_ARROW, new ProjectileDispenserBehavior(){

            @Override
            protected ProjectileEntity createProjectile(World arg, Position arg2, ItemStack arg3) {
                SpectralArrowEntity lv = new SpectralArrowEntity(arg, arg2.getX(), arg2.getY(), arg2.getZ());
                lv.pickupType = PersistentProjectileEntity.PickupPermission.ALLOWED;
                return lv;
            }
        });
        DispenserBlock.registerBehavior(Items.EGG, new ProjectileDispenserBehavior(){

            @Override
            protected ProjectileEntity createProjectile(World arg, Position arg22, ItemStack arg3) {
                return Util.make(new EggEntity(arg, arg22.getX(), arg22.getY(), arg22.getZ()), arg2 -> arg2.setItem(arg3));
            }
        });
        DispenserBlock.registerBehavior(Items.SNOWBALL, new ProjectileDispenserBehavior(){

            @Override
            protected ProjectileEntity createProjectile(World arg, Position arg22, ItemStack arg3) {
                return Util.make(new SnowballEntity(arg, arg22.getX(), arg22.getY(), arg22.getZ()), arg2 -> arg2.setItem(arg3));
            }
        });
        DispenserBlock.registerBehavior(Items.EXPERIENCE_BOTTLE, new ProjectileDispenserBehavior(){

            @Override
            protected ProjectileEntity createProjectile(World arg, Position arg22, ItemStack arg3) {
                return Util.make(new ExperienceBottleEntity(arg, arg22.getX(), arg22.getY(), arg22.getZ()), arg2 -> arg2.setItem(arg3));
            }

            @Override
            protected float getVariation() {
                return super.getVariation() * 0.5f;
            }

            @Override
            protected float getForce() {
                return super.getForce() * 1.25f;
            }
        });
        DispenserBlock.registerBehavior(Items.SPLASH_POTION, new DispenserBehavior(){

            @Override
            public ItemStack dispense(BlockPointer arg, ItemStack arg2) {
                return new ProjectileDispenserBehavior(){

                    @Override
                    protected ProjectileEntity createProjectile(World arg, Position arg22, ItemStack arg3) {
                        return Util.make(new PotionEntity(arg, arg22.getX(), arg22.getY(), arg22.getZ()), arg2 -> arg2.setItem(arg3));
                    }

                    @Override
                    protected float getVariation() {
                        return super.getVariation() * 0.5f;
                    }

                    @Override
                    protected float getForce() {
                        return super.getForce() * 1.25f;
                    }
                }.dispense(arg, arg2);
            }
        });
        DispenserBlock.registerBehavior(Items.LINGERING_POTION, new DispenserBehavior(){

            @Override
            public ItemStack dispense(BlockPointer arg, ItemStack arg2) {
                return new ProjectileDispenserBehavior(){

                    @Override
                    protected ProjectileEntity createProjectile(World arg, Position arg22, ItemStack arg3) {
                        return Util.make(new PotionEntity(arg, arg22.getX(), arg22.getY(), arg22.getZ()), arg2 -> arg2.setItem(arg3));
                    }

                    @Override
                    protected float getVariation() {
                        return super.getVariation() * 0.5f;
                    }

                    @Override
                    protected float getForce() {
                        return super.getForce() * 1.25f;
                    }
                }.dispense(arg, arg2);
            }
        });
        ItemDispenserBehavior lv = new ItemDispenserBehavior(){

            @Override
            public ItemStack dispenseSilently(BlockPointer arg, ItemStack arg2) {
                Direction lv = arg.getBlockState().get(DispenserBlock.FACING);
                EntityType<?> lv2 = ((SpawnEggItem)arg2.getItem()).getEntityType(arg2.getTag());
                lv2.spawnFromItemStack(arg.getWorld(), arg2, null, arg.getBlockPos().offset(lv), SpawnReason.DISPENSER, lv != Direction.UP, false);
                arg2.decrement(1);
                return arg2;
            }
        };
        for (SpawnEggItem lv2 : SpawnEggItem.getAll()) {
            DispenserBlock.registerBehavior(lv2, lv);
        }
        DispenserBlock.registerBehavior(Items.ARMOR_STAND, new ItemDispenserBehavior(){

            @Override
            public ItemStack dispenseSilently(BlockPointer arg, ItemStack arg2) {
                Direction lv = arg.getBlockState().get(DispenserBlock.FACING);
                BlockPos lv2 = arg.getBlockPos().offset(lv);
                World lv3 = arg.getWorld();
                ArmorStandEntity lv4 = new ArmorStandEntity(lv3, (double)lv2.getX() + 0.5, lv2.getY(), (double)lv2.getZ() + 0.5);
                EntityType.loadFromEntityTag(lv3, null, lv4, arg2.getTag());
                lv4.yaw = lv.asRotation();
                lv3.spawnEntity(lv4);
                arg2.decrement(1);
                return arg2;
            }
        });
        DispenserBlock.registerBehavior(Items.SADDLE, new FallibleItemDispenserBehavior(){

            @Override
            public ItemStack dispenseSilently(BlockPointer arg2, ItemStack arg22) {
                BlockPos lv = arg2.getBlockPos().offset(arg2.getBlockState().get(DispenserBlock.FACING));
                List<LivingEntity> list = arg2.getWorld().getEntities(LivingEntity.class, new Box(lv), arg -> {
                    if (arg instanceof Saddleable) {
                        Saddleable lv = (Saddleable)((Object)arg);
                        return !lv.isSaddled() && lv.canBeSaddled();
                    }
                    return false;
                });
                if (!list.isEmpty()) {
                    ((Saddleable)((Object)list.get(0))).saddle(SoundCategory.BLOCKS);
                    arg22.decrement(1);
                    this.setSuccess(true);
                    return arg22;
                }
                return super.dispenseSilently(arg2, arg22);
            }
        });
        FallibleItemDispenserBehavior lv3 = new FallibleItemDispenserBehavior(){

            @Override
            protected ItemStack dispenseSilently(BlockPointer arg2, ItemStack arg22) {
                BlockPos lv = arg2.getBlockPos().offset(arg2.getBlockState().get(DispenserBlock.FACING));
                List<HorseBaseEntity> list = arg2.getWorld().getEntities(HorseBaseEntity.class, new Box(lv), arg -> arg.isAlive() && arg.canEquip());
                for (HorseBaseEntity lv2 : list) {
                    if (!lv2.canEquip(arg22) || lv2.setSaddled() || !lv2.isTame()) continue;
                    lv2.equip(401, arg22.split(1));
                    this.setSuccess(true);
                    return arg22;
                }
                return super.dispenseSilently(arg2, arg22);
            }
        };
        DispenserBlock.registerBehavior(Items.LEATHER_HORSE_ARMOR, lv3);
        DispenserBlock.registerBehavior(Items.IRON_HORSE_ARMOR, lv3);
        DispenserBlock.registerBehavior(Items.GOLDEN_HORSE_ARMOR, lv3);
        DispenserBlock.registerBehavior(Items.DIAMOND_HORSE_ARMOR, lv3);
        DispenserBlock.registerBehavior(Items.WHITE_CARPET, lv3);
        DispenserBlock.registerBehavior(Items.ORANGE_CARPET, lv3);
        DispenserBlock.registerBehavior(Items.CYAN_CARPET, lv3);
        DispenserBlock.registerBehavior(Items.BLUE_CARPET, lv3);
        DispenserBlock.registerBehavior(Items.BROWN_CARPET, lv3);
        DispenserBlock.registerBehavior(Items.BLACK_CARPET, lv3);
        DispenserBlock.registerBehavior(Items.GRAY_CARPET, lv3);
        DispenserBlock.registerBehavior(Items.GREEN_CARPET, lv3);
        DispenserBlock.registerBehavior(Items.LIGHT_BLUE_CARPET, lv3);
        DispenserBlock.registerBehavior(Items.LIGHT_GRAY_CARPET, lv3);
        DispenserBlock.registerBehavior(Items.LIME_CARPET, lv3);
        DispenserBlock.registerBehavior(Items.MAGENTA_CARPET, lv3);
        DispenserBlock.registerBehavior(Items.PINK_CARPET, lv3);
        DispenserBlock.registerBehavior(Items.PURPLE_CARPET, lv3);
        DispenserBlock.registerBehavior(Items.RED_CARPET, lv3);
        DispenserBlock.registerBehavior(Items.YELLOW_CARPET, lv3);
        DispenserBlock.registerBehavior(Items.CHEST, new FallibleItemDispenserBehavior(){

            @Override
            public ItemStack dispenseSilently(BlockPointer arg2, ItemStack arg22) {
                BlockPos lv = arg2.getBlockPos().offset(arg2.getBlockState().get(DispenserBlock.FACING));
                List<AbstractDonkeyEntity> list = arg2.getWorld().getEntities(AbstractDonkeyEntity.class, new Box(lv), arg -> arg.isAlive() && !arg.hasChest());
                for (AbstractDonkeyEntity lv2 : list) {
                    if (!lv2.isTame() || !lv2.equip(499, arg22)) continue;
                    arg22.decrement(1);
                    this.setSuccess(true);
                    return arg22;
                }
                return super.dispenseSilently(arg2, arg22);
            }
        });
        DispenserBlock.registerBehavior(Items.FIREWORK_ROCKET, new ItemDispenserBehavior(){

            @Override
            public ItemStack dispenseSilently(BlockPointer arg, ItemStack arg2) {
                Direction lv = arg.getBlockState().get(DispenserBlock.FACING);
                FireworkRocketEntity lv2 = new FireworkRocketEntity(arg.getWorld(), arg2, arg.getX(), arg.getY(), arg.getX(), true);
                DispenserBehavior.method_27042(arg, lv2, lv);
                lv2.setVelocity(lv.getOffsetX(), lv.getOffsetY(), lv.getOffsetZ(), 0.5f, 1.0f);
                arg.getWorld().spawnEntity(lv2);
                arg2.decrement(1);
                return arg2;
            }

            @Override
            protected void playSound(BlockPointer arg) {
                arg.getWorld().syncWorldEvent(1004, arg.getBlockPos(), 0);
            }
        });
        DispenserBlock.registerBehavior(Items.FIRE_CHARGE, new ItemDispenserBehavior(){

            @Override
            public ItemStack dispenseSilently(BlockPointer arg, ItemStack arg22) {
                Direction lv = arg.getBlockState().get(DispenserBlock.FACING);
                Position lv2 = DispenserBlock.getOutputLocation(arg);
                double d = lv2.getX() + (double)((float)lv.getOffsetX() * 0.3f);
                double e = lv2.getY() + (double)((float)lv.getOffsetY() * 0.3f);
                double f = lv2.getZ() + (double)((float)lv.getOffsetZ() * 0.3f);
                World lv3 = arg.getWorld();
                Random random = lv3.random;
                double g = random.nextGaussian() * 0.05 + (double)lv.getOffsetX();
                double h = random.nextGaussian() * 0.05 + (double)lv.getOffsetY();
                double i = random.nextGaussian() * 0.05 + (double)lv.getOffsetZ();
                lv3.spawnEntity(Util.make(new SmallFireballEntity(lv3, d, e, f, g, h, i), arg2 -> arg2.setItem(arg22)));
                arg22.decrement(1);
                return arg22;
            }

            @Override
            protected void playSound(BlockPointer arg) {
                arg.getWorld().syncWorldEvent(1018, arg.getBlockPos(), 0);
            }
        });
        DispenserBlock.registerBehavior(Items.OAK_BOAT, new BoatDispenserBehavior(BoatEntity.Type.OAK));
        DispenserBlock.registerBehavior(Items.SPRUCE_BOAT, new BoatDispenserBehavior(BoatEntity.Type.SPRUCE));
        DispenserBlock.registerBehavior(Items.BIRCH_BOAT, new BoatDispenserBehavior(BoatEntity.Type.BIRCH));
        DispenserBlock.registerBehavior(Items.JUNGLE_BOAT, new BoatDispenserBehavior(BoatEntity.Type.JUNGLE));
        DispenserBlock.registerBehavior(Items.DARK_OAK_BOAT, new BoatDispenserBehavior(BoatEntity.Type.DARK_OAK));
        DispenserBlock.registerBehavior(Items.ACACIA_BOAT, new BoatDispenserBehavior(BoatEntity.Type.ACACIA));
        ItemDispenserBehavior lv4 = new ItemDispenserBehavior(){
            private final ItemDispenserBehavior field_13367 = new ItemDispenserBehavior();

            @Override
            public ItemStack dispenseSilently(BlockPointer arg, ItemStack arg2) {
                BucketItem lv = (BucketItem)arg2.getItem();
                BlockPos lv2 = arg.getBlockPos().offset(arg.getBlockState().get(DispenserBlock.FACING));
                World lv3 = arg.getWorld();
                if (lv.placeFluid(null, lv3, lv2, null)) {
                    lv.onEmptied(lv3, arg2, lv2);
                    return new ItemStack(Items.BUCKET);
                }
                return this.field_13367.dispense(arg, arg2);
            }
        };
        DispenserBlock.registerBehavior(Items.LAVA_BUCKET, lv4);
        DispenserBlock.registerBehavior(Items.WATER_BUCKET, lv4);
        DispenserBlock.registerBehavior(Items.SALMON_BUCKET, lv4);
        DispenserBlock.registerBehavior(Items.COD_BUCKET, lv4);
        DispenserBlock.registerBehavior(Items.PUFFERFISH_BUCKET, lv4);
        DispenserBlock.registerBehavior(Items.TROPICAL_FISH_BUCKET, lv4);
        DispenserBlock.registerBehavior(Items.BUCKET, new ItemDispenserBehavior(){
            private final ItemDispenserBehavior field_13368 = new ItemDispenserBehavior();

            /*
             * WARNING - void declaration
             */
            @Override
            public ItemStack dispenseSilently(BlockPointer arg, ItemStack arg2) {
                void lv7;
                Fluid lv5;
                BlockPos lv2;
                World lv = arg.getWorld();
                BlockState lv3 = lv.getBlockState(lv2 = arg.getBlockPos().offset(arg.getBlockState().get(DispenserBlock.FACING)));
                Block lv4 = lv3.getBlock();
                if (lv4 instanceof FluidDrainable) {
                    lv5 = ((FluidDrainable)((Object)lv4)).tryDrainFluid(lv, lv2, lv3);
                    if (!(lv5 instanceof FlowableFluid)) {
                        return super.dispenseSilently(arg, arg2);
                    }
                } else {
                    return super.dispenseSilently(arg, arg2);
                }
                Item lv6 = lv5.getBucketItem();
                arg2.decrement(1);
                if (arg2.isEmpty()) {
                    return new ItemStack((ItemConvertible)lv7);
                }
                if (((DispenserBlockEntity)arg.getBlockEntity()).addToFirstFreeSlot(new ItemStack((ItemConvertible)lv7)) < 0) {
                    this.field_13368.dispense(arg, new ItemStack((ItemConvertible)lv7));
                }
                return arg2;
            }
        });
        DispenserBlock.registerBehavior(Items.FLINT_AND_STEEL, new FallibleItemDispenserBehavior(){

            @Override
            protected ItemStack dispenseSilently(BlockPointer arg, ItemStack arg2) {
                World lv = arg.getWorld();
                this.setSuccess(true);
                BlockPos lv2 = arg.getBlockPos().offset(arg.getBlockState().get(DispenserBlock.FACING));
                BlockState lv3 = lv.getBlockState(lv2);
                if (AbstractFireBlock.method_30032(lv, lv2)) {
                    lv.setBlockState(lv2, AbstractFireBlock.getState(lv, lv2));
                } else if (CampfireBlock.method_30035(lv3)) {
                    lv.setBlockState(lv2, (BlockState)lv3.with(Properties.LIT, true));
                } else if (lv3.getBlock() instanceof TntBlock) {
                    TntBlock.primeTnt(lv, lv2);
                    lv.removeBlock(lv2, false);
                } else {
                    this.setSuccess(false);
                }
                if (this.isSuccess() && arg2.damage(1, lv.random, null)) {
                    arg2.setCount(0);
                }
                return arg2;
            }
        });
        DispenserBlock.registerBehavior(Items.BONE_MEAL, new FallibleItemDispenserBehavior(){

            @Override
            protected ItemStack dispenseSilently(BlockPointer arg, ItemStack arg2) {
                this.setSuccess(true);
                World lv = arg.getWorld();
                BlockPos lv2 = arg.getBlockPos().offset(arg.getBlockState().get(DispenserBlock.FACING));
                if (BoneMealItem.useOnFertilizable(arg2, lv, lv2) || BoneMealItem.useOnGround(arg2, lv, lv2, null)) {
                    if (!lv.isClient) {
                        lv.syncWorldEvent(2005, lv2, 0);
                    }
                } else {
                    this.setSuccess(false);
                }
                return arg2;
            }
        });
        DispenserBlock.registerBehavior(Blocks.TNT, new ItemDispenserBehavior(){

            @Override
            protected ItemStack dispenseSilently(BlockPointer arg, ItemStack arg2) {
                World lv = arg.getWorld();
                BlockPos lv2 = arg.getBlockPos().offset(arg.getBlockState().get(DispenserBlock.FACING));
                TntEntity lv3 = new TntEntity(lv, (double)lv2.getX() + 0.5, lv2.getY(), (double)lv2.getZ() + 0.5, null);
                lv.spawnEntity(lv3);
                lv.playSound(null, lv3.getX(), lv3.getY(), lv3.getZ(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0f, 1.0f);
                arg2.decrement(1);
                return arg2;
            }
        });
        FallibleItemDispenserBehavior lv5 = new FallibleItemDispenserBehavior(){

            @Override
            protected ItemStack dispenseSilently(BlockPointer arg, ItemStack arg2) {
                this.setSuccess(ArmorItem.dispenseArmor(arg, arg2));
                return arg2;
            }
        };
        DispenserBlock.registerBehavior(Items.CREEPER_HEAD, lv5);
        DispenserBlock.registerBehavior(Items.ZOMBIE_HEAD, lv5);
        DispenserBlock.registerBehavior(Items.DRAGON_HEAD, lv5);
        DispenserBlock.registerBehavior(Items.SKELETON_SKULL, lv5);
        DispenserBlock.registerBehavior(Items.PLAYER_HEAD, lv5);
        DispenserBlock.registerBehavior(Items.WITHER_SKELETON_SKULL, new FallibleItemDispenserBehavior(){

            @Override
            protected ItemStack dispenseSilently(BlockPointer arg, ItemStack arg2) {
                World lv = arg.getWorld();
                Direction lv2 = arg.getBlockState().get(DispenserBlock.FACING);
                BlockPos lv3 = arg.getBlockPos().offset(lv2);
                if (lv.isAir(lv3) && WitherSkullBlock.canDispense(lv, lv3, arg2)) {
                    lv.setBlockState(lv3, (BlockState)Blocks.WITHER_SKELETON_SKULL.getDefaultState().with(SkullBlock.ROTATION, lv2.getAxis() == Direction.Axis.Y ? 0 : lv2.getOpposite().getHorizontal() * 4), 3);
                    BlockEntity lv4 = lv.getBlockEntity(lv3);
                    if (lv4 instanceof SkullBlockEntity) {
                        WitherSkullBlock.onPlaced(lv, lv3, (SkullBlockEntity)lv4);
                    }
                    arg2.decrement(1);
                    this.setSuccess(true);
                } else {
                    this.setSuccess(ArmorItem.dispenseArmor(arg, arg2));
                }
                return arg2;
            }
        });
        DispenserBlock.registerBehavior(Blocks.CARVED_PUMPKIN, new FallibleItemDispenserBehavior(){

            @Override
            protected ItemStack dispenseSilently(BlockPointer arg, ItemStack arg2) {
                World lv = arg.getWorld();
                BlockPos lv2 = arg.getBlockPos().offset(arg.getBlockState().get(DispenserBlock.FACING));
                CarvedPumpkinBlock lv3 = (CarvedPumpkinBlock)Blocks.CARVED_PUMPKIN;
                if (lv.isAir(lv2) && lv3.canDispense(lv, lv2)) {
                    if (!lv.isClient) {
                        lv.setBlockState(lv2, lv3.getDefaultState(), 3);
                    }
                    arg2.decrement(1);
                    this.setSuccess(true);
                } else {
                    this.setSuccess(ArmorItem.dispenseArmor(arg, arg2));
                }
                return arg2;
            }
        });
        DispenserBlock.registerBehavior(Blocks.SHULKER_BOX.asItem(), new BlockPlacementDispenserBehavior());
        for (DyeColor lv6 : DyeColor.values()) {
            DispenserBlock.registerBehavior(ShulkerBoxBlock.get(lv6).asItem(), new BlockPlacementDispenserBehavior());
        }
        DispenserBlock.registerBehavior(Items.GLASS_BOTTLE.asItem(), new FallibleItemDispenserBehavior(){
            private final ItemDispenserBehavior field_20533 = new ItemDispenserBehavior();

            private ItemStack method_22141(BlockPointer arg, ItemStack arg2, ItemStack arg3) {
                arg2.decrement(1);
                if (arg2.isEmpty()) {
                    return arg3.copy();
                }
                if (((DispenserBlockEntity)arg.getBlockEntity()).addToFirstFreeSlot(arg3.copy()) < 0) {
                    this.field_20533.dispense(arg, arg3.copy());
                }
                return arg2;
            }

            @Override
            public ItemStack dispenseSilently(BlockPointer arg2, ItemStack arg22) {
                this.setSuccess(false);
                World lv = arg2.getWorld();
                BlockPos lv2 = arg2.getBlockPos().offset(arg2.getBlockState().get(DispenserBlock.FACING));
                BlockState lv3 = lv.getBlockState(lv2);
                if (lv3.method_27851(BlockTags.BEEHIVES, arg -> arg.contains(BeehiveBlock.HONEY_LEVEL)) && lv3.get(BeehiveBlock.HONEY_LEVEL) >= 5) {
                    ((BeehiveBlock)lv3.getBlock()).takeHoney(lv.getWorld(), lv3, lv2, null, BeehiveBlockEntity.BeeState.BEE_RELEASED);
                    this.setSuccess(true);
                    return this.method_22141(arg2, arg22, new ItemStack(Items.HONEY_BOTTLE));
                }
                if (lv.getFluidState(lv2).matches(FluidTags.WATER)) {
                    this.setSuccess(true);
                    return this.method_22141(arg2, arg22, PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.WATER));
                }
                return super.dispenseSilently(arg2, arg22);
            }
        });
        DispenserBlock.registerBehavior(Items.GLOWSTONE, new FallibleItemDispenserBehavior(){

            @Override
            public ItemStack dispenseSilently(BlockPointer arg, ItemStack arg2) {
                Direction lv = arg.getBlockState().get(DispenserBlock.FACING);
                BlockPos lv2 = arg.getBlockPos().offset(lv);
                World lv3 = arg.getWorld();
                BlockState lv4 = lv3.getBlockState(lv2);
                this.setSuccess(true);
                if (lv4.isOf(Blocks.RESPAWN_ANCHOR)) {
                    if (lv4.get(RespawnAnchorBlock.CHARGES) != 4) {
                        RespawnAnchorBlock.charge(lv3, lv2, lv4);
                        arg2.decrement(1);
                    } else {
                        this.setSuccess(false);
                    }
                    return arg2;
                }
                return super.dispenseSilently(arg, arg2);
            }
        });
        DispenserBlock.registerBehavior(Items.SHEARS.asItem(), new ShearsDispenserBehavior());
    }

    public static void method_27042(BlockPointer arg, Entity arg2, Direction arg3) {
        arg2.updatePosition(arg.getX() + (double)arg3.getOffsetX() * (0.5000099999997474 - (double)arg2.getWidth() / 2.0), arg.getY() + (double)arg3.getOffsetY() * (0.5000099999997474 - (double)arg2.getHeight() / 2.0) - (double)arg2.getHeight() / 2.0, arg.getZ() + (double)arg3.getOffsetZ() * (0.5000099999997474 - (double)arg2.getWidth() / 2.0));
    }
}

