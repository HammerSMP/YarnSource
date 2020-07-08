/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.recipe.book.RecipeBookCategory;

public class RecipeCategoryOptionsC2SPacket
implements Packet<ServerPlayPacketListener> {
    private RecipeBookCategory category;
    private boolean guiOpen;
    private boolean filteringCraftable;

    public RecipeCategoryOptionsC2SPacket() {
    }

    @Environment(value=EnvType.CLIENT)
    public RecipeCategoryOptionsC2SPacket(RecipeBookCategory arg, boolean bl, boolean bl2) {
        this.category = arg;
        this.guiOpen = bl;
        this.filteringCraftable = bl2;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.category = arg.readEnumConstant(RecipeBookCategory.class);
        this.guiOpen = arg.readBoolean();
        this.filteringCraftable = arg.readBoolean();
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeEnumConstant(this.category);
        arg.writeBoolean(this.guiOpen);
        arg.writeBoolean(this.filteringCraftable);
    }

    @Override
    public void apply(ServerPlayPacketListener arg) {
        arg.onRecipeCategoryOptions(this);
    }

    public RecipeBookCategory getCategory() {
        return this.category;
    }

    public boolean isGuiOpen() {
        return this.guiOpen;
    }

    public boolean isFilteringCraftable() {
        return this.filteringCraftable;
    }
}

