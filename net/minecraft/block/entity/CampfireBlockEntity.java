/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.block.entity;

import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Clearable;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class CampfireBlockEntity
extends BlockEntity
implements Clearable,
Tickable {
    private final DefaultedList<ItemStack> itemsBeingCooked = DefaultedList.ofSize(4, ItemStack.EMPTY);
    private final int[] cookingTimes = new int[4];
    private final int[] cookingTotalTimes = new int[4];

    public CampfireBlockEntity() {
        super(BlockEntityType.CAMPFIRE);
    }

    @Override
    public void tick() {
        boolean bl = this.getCachedState().get(CampfireBlock.LIT);
        boolean bl2 = this.world.isClient;
        if (bl2) {
            if (bl) {
                this.spawnSmokeParticles();
            }
            return;
        }
        if (bl) {
            this.updateItemsBeingCooked();
        } else {
            for (int i = 0; i < this.itemsBeingCooked.size(); ++i) {
                if (this.cookingTimes[i] <= 0) continue;
                this.cookingTimes[i] = MathHelper.clamp(this.cookingTimes[i] - 2, 0, this.cookingTotalTimes[i]);
            }
        }
    }

    private void updateItemsBeingCooked() {
        for (int i = 0; i < this.itemsBeingCooked.size(); ++i) {
            ItemStack lv = this.itemsBeingCooked.get(i);
            if (lv.isEmpty()) continue;
            int n = i;
            this.cookingTimes[n] = this.cookingTimes[n] + 1;
            if (this.cookingTimes[i] < this.cookingTotalTimes[i]) continue;
            SimpleInventory lv2 = new SimpleInventory(lv);
            ItemStack lv3 = this.world.getRecipeManager().getFirstMatch(RecipeType.CAMPFIRE_COOKING, lv2, this.world).map(arg2 -> arg2.craft(lv2)).orElse(lv);
            BlockPos lv4 = this.getPos();
            ItemScatterer.spawn(this.world, (double)lv4.getX(), (double)lv4.getY(), (double)lv4.getZ(), lv3);
            this.itemsBeingCooked.set(i, ItemStack.EMPTY);
            this.updateListeners();
        }
    }

    private void spawnSmokeParticles() {
        World lv = this.getWorld();
        if (lv == null) {
            return;
        }
        BlockPos lv2 = this.getPos();
        Random random = lv.random;
        if (random.nextFloat() < 0.11f) {
            for (int i = 0; i < random.nextInt(2) + 2; ++i) {
                CampfireBlock.spawnSmokeParticle(lv, lv2, this.getCachedState().get(CampfireBlock.SIGNAL_FIRE), false);
            }
        }
        int j = this.getCachedState().get(CampfireBlock.FACING).getHorizontal();
        for (int k = 0; k < this.itemsBeingCooked.size(); ++k) {
            if (this.itemsBeingCooked.get(k).isEmpty() || !(random.nextFloat() < 0.2f)) continue;
            Direction lv3 = Direction.fromHorizontal(Math.floorMod(k + j, 4));
            float f = 0.3125f;
            double d = (double)lv2.getX() + 0.5 - (double)((float)lv3.getOffsetX() * 0.3125f) + (double)((float)lv3.rotateYClockwise().getOffsetX() * 0.3125f);
            double e = (double)lv2.getY() + 0.5;
            double g = (double)lv2.getZ() + 0.5 - (double)((float)lv3.getOffsetZ() * 0.3125f) + (double)((float)lv3.rotateYClockwise().getOffsetZ() * 0.3125f);
            for (int l = 0; l < 4; ++l) {
                lv.addParticle(ParticleTypes.SMOKE, d, e, g, 0.0, 5.0E-4, 0.0);
            }
        }
    }

    public DefaultedList<ItemStack> getItemsBeingCooked() {
        return this.itemsBeingCooked;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        this.itemsBeingCooked.clear();
        Inventories.fromTag(tag, this.itemsBeingCooked);
        if (tag.contains("CookingTimes", 11)) {
            int[] is = tag.getIntArray("CookingTimes");
            System.arraycopy(is, 0, this.cookingTimes, 0, Math.min(this.cookingTotalTimes.length, is.length));
        }
        if (tag.contains("CookingTotalTimes", 11)) {
            int[] js = tag.getIntArray("CookingTotalTimes");
            System.arraycopy(js, 0, this.cookingTotalTimes, 0, Math.min(this.cookingTotalTimes.length, js.length));
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        this.saveInitialChunkData(tag);
        tag.putIntArray("CookingTimes", this.cookingTimes);
        tag.putIntArray("CookingTotalTimes", this.cookingTotalTimes);
        return tag;
    }

    private CompoundTag saveInitialChunkData(CompoundTag tag) {
        super.toTag(tag);
        Inventories.toTag(tag, this.itemsBeingCooked, true);
        return tag;
    }

    @Override
    @Nullable
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return new BlockEntityUpdateS2CPacket(this.pos, 13, this.toInitialChunkDataTag());
    }

    @Override
    public CompoundTag toInitialChunkDataTag() {
        return this.saveInitialChunkData(new CompoundTag());
    }

    public Optional<CampfireCookingRecipe> getRecipeFor(ItemStack item) {
        if (this.itemsBeingCooked.stream().noneMatch(ItemStack::isEmpty)) {
            return Optional.empty();
        }
        return this.world.getRecipeManager().getFirstMatch(RecipeType.CAMPFIRE_COOKING, new SimpleInventory(item), this.world);
    }

    public boolean addItem(ItemStack item, int integer) {
        for (int j = 0; j < this.itemsBeingCooked.size(); ++j) {
            ItemStack lv = this.itemsBeingCooked.get(j);
            if (!lv.isEmpty()) continue;
            this.cookingTotalTimes[j] = integer;
            this.cookingTimes[j] = 0;
            this.itemsBeingCooked.set(j, item.split(1));
            this.updateListeners();
            return true;
        }
        return false;
    }

    private void updateListeners() {
        this.markDirty();
        this.getWorld().updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), 3);
    }

    @Override
    public void clear() {
        this.itemsBeingCooked.clear();
    }

    public void spawnItemsBeingCooked() {
        if (this.world != null) {
            if (!this.world.isClient) {
                ItemScatterer.spawn(this.world, this.getPos(), this.getItemsBeingCooked());
            }
            this.updateListeners();
        }
    }
}

