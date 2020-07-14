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
import net.minecraft.server.world.ServerWorld;
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
            protected ProjectileEntity createProjectile(World world, Position position, ItemStack stack) {
                ArrowEntity lv = new ArrowEntity(world, position.getX(), position.getY(), position.getZ());
                lv.pickupType = PersistentProjectileEntity.PickupPermission.ALLOWED;
                return lv;
            }
        });
        DispenserBlock.registerBehavior(Items.TIPPED_ARROW, new ProjectileDispenserBehavior(){

            @Override
            protected ProjectileEntity createProjectile(World world, Position position, ItemStack stack) {
                ArrowEntity lv = new ArrowEntity(world, position.getX(), position.getY(), position.getZ());
                lv.initFromStack(stack);
                lv.pickupType = PersistentProjectileEntity.PickupPermission.ALLOWED;
                return lv;
            }
        });
        DispenserBlock.registerBehavior(Items.SPECTRAL_ARROW, new ProjectileDispenserBehavior(){

            @Override
            protected ProjectileEntity createProjectile(World world, Position position, ItemStack stack) {
                SpectralArrowEntity lv = new SpectralArrowEntity(world, position.getX(), position.getY(), position.getZ());
                lv.pickupType = PersistentProjectileEntity.PickupPermission.ALLOWED;
                return lv;
            }
        });
        DispenserBlock.registerBehavior(Items.EGG, new ProjectileDispenserBehavior(){

            @Override
            protected ProjectileEntity createProjectile(World world, Position position, ItemStack stack) {
                return Util.make(new EggEntity(world, position.getX(), position.getY(), position.getZ()), arg2 -> arg2.setItem(stack));
            }
        });
        DispenserBlock.registerBehavior(Items.SNOWBALL, new ProjectileDispenserBehavior(){

            @Override
            protected ProjectileEntity createProjectile(World world, Position position, ItemStack stack) {
                return Util.make(new SnowballEntity(world, position.getX(), position.getY(), position.getZ()), arg2 -> arg2.setItem(stack));
            }
        });
        DispenserBlock.registerBehavior(Items.EXPERIENCE_BOTTLE, new ProjectileDispenserBehavior(){

            @Override
            protected ProjectileEntity createProjectile(World world, Position position, ItemStack stack) {
                return Util.make(new ExperienceBottleEntity(world, position.getX(), position.getY(), position.getZ()), arg2 -> arg2.setItem(stack));
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
                    protected ProjectileEntity createProjectile(World world, Position position, ItemStack stack) {
                        return Util.make(new PotionEntity(world, position.getX(), position.getY(), position.getZ()), arg2 -> arg2.setItem(stack));
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
                    protected ProjectileEntity createProjectile(World world, Position position, ItemStack stack) {
                        return Util.make(new PotionEntity(world, position.getX(), position.getY(), position.getZ()), arg2 -> arg2.setItem(stack));
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
            public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                Direction lv = pointer.getBlockState().get(DispenserBlock.FACING);
                EntityType<?> lv2 = ((SpawnEggItem)stack.getItem()).getEntityType(stack.getTag());
                lv2.spawnFromItemStack(pointer.getWorld(), stack, null, pointer.getBlockPos().offset(lv), SpawnReason.DISPENSER, lv != Direction.UP, false);
                stack.decrement(1);
                return stack;
            }
        };
        for (SpawnEggItem lv2 : SpawnEggItem.getAll()) {
            DispenserBlock.registerBehavior(lv2, lv);
        }
        DispenserBlock.registerBehavior(Items.ARMOR_STAND, new ItemDispenserBehavior(){

            @Override
            public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                Direction lv = pointer.getBlockState().get(DispenserBlock.FACING);
                BlockPos lv2 = pointer.getBlockPos().offset(lv);
                ServerWorld lv3 = pointer.getWorld();
                ArmorStandEntity lv4 = new ArmorStandEntity(lv3, (double)lv2.getX() + 0.5, lv2.getY(), (double)lv2.getZ() + 0.5);
                EntityType.loadFromEntityTag(lv3, null, lv4, stack.getTag());
                lv4.yaw = lv.asRotation();
                lv3.spawnEntity(lv4);
                stack.decrement(1);
                return stack;
            }
        });
        DispenserBlock.registerBehavior(Items.SADDLE, new FallibleItemDispenserBehavior(){

            @Override
            public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                BlockPos lv = pointer.getBlockPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
                List<LivingEntity> list = pointer.getWorld().getEntities(LivingEntity.class, new Box(lv), arg -> {
                    if (arg instanceof Saddleable) {
                        Saddleable lv = (Saddleable)((Object)arg);
                        return !lv.isSaddled() && lv.canBeSaddled();
                    }
                    return false;
                });
                if (!list.isEmpty()) {
                    ((Saddleable)((Object)list.get(0))).saddle(SoundCategory.BLOCKS);
                    stack.decrement(1);
                    this.setSuccess(true);
                    return stack;
                }
                return super.dispenseSilently(pointer, stack);
            }
        });
        FallibleItemDispenserBehavior lv3 = new FallibleItemDispenserBehavior(){

            @Override
            protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                BlockPos lv = pointer.getBlockPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
                List<HorseBaseEntity> list = pointer.getWorld().getEntities(HorseBaseEntity.class, new Box(lv), arg -> arg.isAlive() && arg.canEquip());
                for (HorseBaseEntity lv2 : list) {
                    if (!lv2.canEquip(stack) || lv2.setSaddled() || !lv2.isTame()) continue;
                    lv2.equip(401, stack.split(1));
                    this.setSuccess(true);
                    return stack;
                }
                return super.dispenseSilently(pointer, stack);
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
            public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                BlockPos lv = pointer.getBlockPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
                List<AbstractDonkeyEntity> list = pointer.getWorld().getEntities(AbstractDonkeyEntity.class, new Box(lv), arg -> arg.isAlive() && !arg.hasChest());
                for (AbstractDonkeyEntity lv2 : list) {
                    if (!lv2.isTame() || !lv2.equip(499, stack)) continue;
                    stack.decrement(1);
                    this.setSuccess(true);
                    return stack;
                }
                return super.dispenseSilently(pointer, stack);
            }
        });
        DispenserBlock.registerBehavior(Items.FIREWORK_ROCKET, new ItemDispenserBehavior(){

            @Override
            public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                Direction lv = pointer.getBlockState().get(DispenserBlock.FACING);
                FireworkRocketEntity lv2 = new FireworkRocketEntity((World)pointer.getWorld(), stack, pointer.getX(), pointer.getY(), pointer.getX(), true);
                DispenserBehavior.method_27042(pointer, lv2, lv);
                lv2.setVelocity(lv.getOffsetX(), lv.getOffsetY(), lv.getOffsetZ(), 0.5f, 1.0f);
                pointer.getWorld().spawnEntity(lv2);
                stack.decrement(1);
                return stack;
            }

            @Override
            protected void playSound(BlockPointer pointer) {
                pointer.getWorld().syncWorldEvent(1004, pointer.getBlockPos(), 0);
            }
        });
        DispenserBlock.registerBehavior(Items.FIRE_CHARGE, new ItemDispenserBehavior(){

            @Override
            public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                Direction lv = pointer.getBlockState().get(DispenserBlock.FACING);
                Position lv2 = DispenserBlock.getOutputLocation(pointer);
                double d = lv2.getX() + (double)((float)lv.getOffsetX() * 0.3f);
                double e = lv2.getY() + (double)((float)lv.getOffsetY() * 0.3f);
                double f = lv2.getZ() + (double)((float)lv.getOffsetZ() * 0.3f);
                ServerWorld lv3 = pointer.getWorld();
                Random random = lv3.random;
                double g = random.nextGaussian() * 0.05 + (double)lv.getOffsetX();
                double h = random.nextGaussian() * 0.05 + (double)lv.getOffsetY();
                double i = random.nextGaussian() * 0.05 + (double)lv.getOffsetZ();
                lv3.spawnEntity(Util.make(new SmallFireballEntity(lv3, d, e, f, g, h, i), arg2 -> arg2.setItem(stack)));
                stack.decrement(1);
                return stack;
            }

            @Override
            protected void playSound(BlockPointer pointer) {
                pointer.getWorld().syncWorldEvent(1018, pointer.getBlockPos(), 0);
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
            public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                BucketItem lv = (BucketItem)stack.getItem();
                BlockPos lv2 = pointer.getBlockPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
                ServerWorld lv3 = pointer.getWorld();
                if (lv.placeFluid(null, lv3, lv2, null)) {
                    lv.onEmptied(lv3, stack, lv2);
                    return new ItemStack(Items.BUCKET);
                }
                return this.field_13367.dispense(pointer, stack);
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
            public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                void lv7;
                Fluid lv5;
                BlockPos lv2;
                ServerWorld lv = pointer.getWorld();
                BlockState lv3 = lv.getBlockState(lv2 = pointer.getBlockPos().offset(pointer.getBlockState().get(DispenserBlock.FACING)));
                Block lv4 = lv3.getBlock();
                if (lv4 instanceof FluidDrainable) {
                    lv5 = ((FluidDrainable)((Object)lv4)).tryDrainFluid(lv, lv2, lv3);
                    if (!(lv5 instanceof FlowableFluid)) {
                        return super.dispenseSilently(pointer, stack);
                    }
                } else {
                    return super.dispenseSilently(pointer, stack);
                }
                Item lv6 = lv5.getBucketItem();
                stack.decrement(1);
                if (stack.isEmpty()) {
                    return new ItemStack((ItemConvertible)lv7);
                }
                if (((DispenserBlockEntity)pointer.getBlockEntity()).addToFirstFreeSlot(new ItemStack((ItemConvertible)lv7)) < 0) {
                    this.field_13368.dispense(pointer, new ItemStack((ItemConvertible)lv7));
                }
                return stack;
            }
        });
        DispenserBlock.registerBehavior(Items.FLINT_AND_STEEL, new FallibleItemDispenserBehavior(){

            @Override
            protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                ServerWorld lv = pointer.getWorld();
                this.setSuccess(true);
                Direction lv2 = pointer.getBlockState().get(DispenserBlock.FACING);
                BlockPos lv3 = pointer.getBlockPos().offset(lv2);
                BlockState lv4 = lv.getBlockState(lv3);
                if (AbstractFireBlock.method_30032(lv, lv3, lv2)) {
                    lv.setBlockState(lv3, AbstractFireBlock.getState(lv, lv3));
                } else if (CampfireBlock.method_30035(lv4)) {
                    lv.setBlockState(lv3, (BlockState)lv4.with(Properties.LIT, true));
                } else if (lv4.getBlock() instanceof TntBlock) {
                    TntBlock.primeTnt(lv, lv3);
                    lv.removeBlock(lv3, false);
                } else {
                    this.setSuccess(false);
                }
                if (this.isSuccess() && stack.damage(1, lv.random, null)) {
                    stack.setCount(0);
                }
                return stack;
            }
        });
        DispenserBlock.registerBehavior(Items.BONE_MEAL, new FallibleItemDispenserBehavior(){

            @Override
            protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                this.setSuccess(true);
                ServerWorld lv = pointer.getWorld();
                BlockPos lv2 = pointer.getBlockPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
                if (BoneMealItem.useOnFertilizable(stack, lv, lv2) || BoneMealItem.useOnGround(stack, lv, lv2, null)) {
                    if (!lv.isClient) {
                        lv.syncWorldEvent(2005, lv2, 0);
                    }
                } else {
                    this.setSuccess(false);
                }
                return stack;
            }
        });
        DispenserBlock.registerBehavior(Blocks.TNT, new ItemDispenserBehavior(){

            @Override
            protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                ServerWorld lv = pointer.getWorld();
                BlockPos lv2 = pointer.getBlockPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
                TntEntity lv3 = new TntEntity(lv, (double)lv2.getX() + 0.5, lv2.getY(), (double)lv2.getZ() + 0.5, null);
                lv.spawnEntity(lv3);
                ((World)lv).playSound(null, lv3.getX(), lv3.getY(), lv3.getZ(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0f, 1.0f);
                stack.decrement(1);
                return stack;
            }
        });
        FallibleItemDispenserBehavior lv5 = new FallibleItemDispenserBehavior(){

            @Override
            protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                this.setSuccess(ArmorItem.dispenseArmor(pointer, stack));
                return stack;
            }
        };
        DispenserBlock.registerBehavior(Items.CREEPER_HEAD, lv5);
        DispenserBlock.registerBehavior(Items.ZOMBIE_HEAD, lv5);
        DispenserBlock.registerBehavior(Items.DRAGON_HEAD, lv5);
        DispenserBlock.registerBehavior(Items.SKELETON_SKULL, lv5);
        DispenserBlock.registerBehavior(Items.PLAYER_HEAD, lv5);
        DispenserBlock.registerBehavior(Items.WITHER_SKELETON_SKULL, new FallibleItemDispenserBehavior(){

            @Override
            protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                ServerWorld lv = pointer.getWorld();
                Direction lv2 = pointer.getBlockState().get(DispenserBlock.FACING);
                BlockPos lv3 = pointer.getBlockPos().offset(lv2);
                if (lv.isAir(lv3) && WitherSkullBlock.canDispense(lv, lv3, stack)) {
                    lv.setBlockState(lv3, (BlockState)Blocks.WITHER_SKELETON_SKULL.getDefaultState().with(SkullBlock.ROTATION, lv2.getAxis() == Direction.Axis.Y ? 0 : lv2.getOpposite().getHorizontal() * 4), 3);
                    BlockEntity lv4 = lv.getBlockEntity(lv3);
                    if (lv4 instanceof SkullBlockEntity) {
                        WitherSkullBlock.onPlaced(lv, lv3, (SkullBlockEntity)lv4);
                    }
                    stack.decrement(1);
                    this.setSuccess(true);
                } else {
                    this.setSuccess(ArmorItem.dispenseArmor(pointer, stack));
                }
                return stack;
            }
        });
        DispenserBlock.registerBehavior(Blocks.CARVED_PUMPKIN, new FallibleItemDispenserBehavior(){

            @Override
            protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                ServerWorld lv = pointer.getWorld();
                BlockPos lv2 = pointer.getBlockPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
                CarvedPumpkinBlock lv3 = (CarvedPumpkinBlock)Blocks.CARVED_PUMPKIN;
                if (lv.isAir(lv2) && lv3.canDispense(lv, lv2)) {
                    if (!lv.isClient) {
                        lv.setBlockState(lv2, lv3.getDefaultState(), 3);
                    }
                    stack.decrement(1);
                    this.setSuccess(true);
                } else {
                    this.setSuccess(ArmorItem.dispenseArmor(pointer, stack));
                }
                return stack;
            }
        });
        DispenserBlock.registerBehavior(Blocks.SHULKER_BOX.asItem(), new BlockPlacementDispenserBehavior());
        for (DyeColor lv6 : DyeColor.values()) {
            DispenserBlock.registerBehavior(ShulkerBoxBlock.get(lv6).asItem(), new BlockPlacementDispenserBehavior());
        }
        DispenserBlock.registerBehavior(Items.GLASS_BOTTLE.asItem(), new FallibleItemDispenserBehavior(){
            private final ItemDispenserBehavior field_20533 = new ItemDispenserBehavior();

            private ItemStack method_22141(BlockPointer arg, ItemStack emptyBottleStack, ItemStack filledBottleStack) {
                emptyBottleStack.decrement(1);
                if (emptyBottleStack.isEmpty()) {
                    return filledBottleStack.copy();
                }
                if (((DispenserBlockEntity)arg.getBlockEntity()).addToFirstFreeSlot(filledBottleStack.copy()) < 0) {
                    this.field_20533.dispense(arg, filledBottleStack.copy());
                }
                return emptyBottleStack;
            }

            @Override
            public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                this.setSuccess(false);
                ServerWorld lv = pointer.getWorld();
                BlockPos lv2 = pointer.getBlockPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
                BlockState lv3 = lv.getBlockState(lv2);
                if (lv3.method_27851(BlockTags.BEEHIVES, arg -> arg.contains(BeehiveBlock.HONEY_LEVEL)) && lv3.get(BeehiveBlock.HONEY_LEVEL) >= 5) {
                    ((BeehiveBlock)lv3.getBlock()).takeHoney(lv, lv3, lv2, null, BeehiveBlockEntity.BeeState.BEE_RELEASED);
                    this.setSuccess(true);
                    return this.method_22141(pointer, stack, new ItemStack(Items.HONEY_BOTTLE));
                }
                if (lv.getFluidState(lv2).isIn(FluidTags.WATER)) {
                    this.setSuccess(true);
                    return this.method_22141(pointer, stack, PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.WATER));
                }
                return super.dispenseSilently(pointer, stack);
            }
        });
        DispenserBlock.registerBehavior(Items.GLOWSTONE, new FallibleItemDispenserBehavior(){

            @Override
            public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                Direction lv = pointer.getBlockState().get(DispenserBlock.FACING);
                BlockPos lv2 = pointer.getBlockPos().offset(lv);
                ServerWorld lv3 = pointer.getWorld();
                BlockState lv4 = lv3.getBlockState(lv2);
                this.setSuccess(true);
                if (lv4.isOf(Blocks.RESPAWN_ANCHOR)) {
                    if (lv4.get(RespawnAnchorBlock.CHARGES) != 4) {
                        RespawnAnchorBlock.charge(lv3, lv2, lv4);
                        stack.decrement(1);
                    } else {
                        this.setSuccess(false);
                    }
                    return stack;
                }
                return super.dispenseSilently(pointer, stack);
            }
        });
        DispenserBlock.registerBehavior(Items.SHEARS.asItem(), new ShearsDispenserBehavior());
    }

    public static void method_27042(BlockPointer arg, Entity arg2, Direction arg3) {
        arg2.updatePosition(arg.getX() + (double)arg3.getOffsetX() * (0.5000099999997474 - (double)arg2.getWidth() / 2.0), arg.getY() + (double)arg3.getOffsetY() * (0.5000099999997474 - (double)arg2.getHeight() / 2.0) - (double)arg2.getHeight() / 2.0, arg.getZ() + (double)arg3.getOffsetZ() * (0.5000099999997474 - (double)arg2.getWidth() / 2.0));
    }
}

