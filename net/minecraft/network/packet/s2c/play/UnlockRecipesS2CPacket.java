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
import net.minecraft.recipe.book.RecipeBookOptions;
import net.minecraft.util.Identifier;

public class UnlockRecipesS2CPacket
implements Packet<ClientPlayPacketListener> {
    private Action action;
    private List<Identifier> recipeIdsToChange;
    private List<Identifier> recipeIdsToInit;
    private RecipeBookOptions field_25797;

    public UnlockRecipesS2CPacket() {
    }

    public UnlockRecipesS2CPacket(Action arg, Collection<Identifier> collection, Collection<Identifier> collection2, RecipeBookOptions arg2) {
        this.action = arg;
        this.recipeIdsToChange = ImmutableList.copyOf(collection);
        this.recipeIdsToInit = ImmutableList.copyOf(collection2);
        this.field_25797 = arg2;
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onUnlockRecipes(this);
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.action = arg.readEnumConstant(Action.class);
        this.field_25797 = RecipeBookOptions.fromPacket(arg);
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
        this.field_25797.toPacket(arg);
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
    public RecipeBookOptions isFurnaceFilteringCraftable() {
        return this.field_25797;
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

