/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.Identifier;

public class UnlockRecipesS2CPacket
implements Packet<ClientPlayPacketListener> {
    private Action action;
    private List<Identifier> recipeIdsToChange;
    private List<Identifier> recipeIdsToInit;
    private boolean guiOpen;
    private boolean filteringCraftable;
    private boolean furnaceGuiOpen;
    private boolean furnaceFilteringCraftable;

    public UnlockRecipesS2CPacket() {
    }

    public UnlockRecipesS2CPacket(Action arg, Collection<Identifier> collection, Collection<Identifier> collection2, boolean bl, boolean bl2, boolean bl3, boolean bl4) {
        this.action = arg;
        this.recipeIdsToChange = ImmutableList.copyOf(collection);
        this.recipeIdsToInit = ImmutableList.copyOf(collection2);
        this.guiOpen = bl;
        this.filteringCraftable = bl2;
        this.furnaceGuiOpen = bl3;
        this.furnaceFilteringCraftable = bl4;
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onUnlockRecipes(this);
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.action = arg.readEnumConstant(Action.class);
        this.guiOpen = arg.readBoolean();
        this.filteringCraftable = arg.readBoolean();
        this.furnaceGuiOpen = arg.readBoolean();
        this.furnaceFilteringCraftable = arg.readBoolean();
        int i = arg.readVarInt();
        this.recipeIdsToChange = Lists.newArrayList();
        for (int j = 0; j < i; ++j) {
            this.recipeIdsToChange.add(arg.readIdentifier());
        }
        if (this.action == Action.INIT) {
            i = arg.readVarInt();
            this.recipeIdsToInit = Lists.newArrayList();
            for (int k = 0; k < i; ++k) {
                this.recipeIdsToInit.add(arg.readIdentifier());
            }
        }
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeEnumConstant(this.action);
        arg.writeBoolean(this.guiOpen);
        arg.writeBoolean(this.filteringCraftable);
        arg.writeBoolean(this.furnaceGuiOpen);
        arg.writeBoolean(this.furnaceFilteringCraftable);
        arg.writeVarInt(this.recipeIdsToChange.size());
        for (Identifier lv : this.recipeIdsToChange) {
            arg.writeIdentifier(lv);
        }
        if (this.action == Action.INIT) {
            arg.writeVarInt(this.recipeIdsToInit.size());
            for (Identifier lv2 : this.recipeIdsToInit) {
                arg.writeIdentifier(lv2);
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    public List<Identifier> getRecipeIdsToChange() {
        return this.recipeIdsToChange;
    }

    @Environment(value=EnvType.CLIENT)
    public List<Identifier> getRecipeIdsToInit() {
        return this.recipeIdsToInit;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isGuiOpen() {
        return this.guiOpen;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isFilteringCraftable() {
        return this.filteringCraftable;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isFurnaceGuiOpen() {
        return this.furnaceGuiOpen;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isFurnaceFilteringCraftable() {
        return this.furnaceFilteringCraftable;
    }

    @Environment(value=EnvType.CLIENT)
    public Action getAction() {
        return this.action;
    }

    public static enum Action {
        INIT,
        ADD,
        REMOVE;

    }
}

