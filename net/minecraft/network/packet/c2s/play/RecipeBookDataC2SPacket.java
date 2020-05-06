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
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;

public class RecipeBookDataC2SPacket
implements Packet<ServerPlayPacketListener> {
    private Mode mode;
    private Identifier recipeId;
    private boolean guiOpen;
    private boolean filteringCraftable;
    private boolean furnaceGuiOpen;
    private boolean furnaceFilteringCraftable;
    private boolean blastFurnaceGuiOpen;
    private boolean blastFurnaceFilteringCraftable;
    private boolean smokerGuiOpen;
    private boolean smokerGuiFilteringCraftable;

    public RecipeBookDataC2SPacket() {
    }

    public RecipeBookDataC2SPacket(Recipe<?> arg) {
        this.mode = Mode.SHOWN;
        this.recipeId = arg.getId();
    }

    @Environment(value=EnvType.CLIENT)
    public RecipeBookDataC2SPacket(boolean bl, boolean bl2, boolean bl3, boolean bl4, boolean bl5, boolean bl6) {
        this.mode = Mode.SETTINGS;
        this.guiOpen = bl;
        this.filteringCraftable = bl2;
        this.furnaceGuiOpen = bl3;
        this.furnaceFilteringCraftable = bl4;
        this.blastFurnaceGuiOpen = bl5;
        this.blastFurnaceFilteringCraftable = bl6;
        this.smokerGuiOpen = bl5;
        this.smokerGuiFilteringCraftable = bl6;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.mode = arg.readEnumConstant(Mode.class);
        if (this.mode == Mode.SHOWN) {
            this.recipeId = arg.readIdentifier();
        } else if (this.mode == Mode.SETTINGS) {
            this.guiOpen = arg.readBoolean();
            this.filteringCraftable = arg.readBoolean();
            this.furnaceGuiOpen = arg.readBoolean();
            this.furnaceFilteringCraftable = arg.readBoolean();
            this.blastFurnaceGuiOpen = arg.readBoolean();
            this.blastFurnaceFilteringCraftable = arg.readBoolean();
            this.smokerGuiOpen = arg.readBoolean();
            this.smokerGuiFilteringCraftable = arg.readBoolean();
        }
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeEnumConstant(this.mode);
        if (this.mode == Mode.SHOWN) {
            arg.writeIdentifier(this.recipeId);
        } else if (this.mode == Mode.SETTINGS) {
            arg.writeBoolean(this.guiOpen);
            arg.writeBoolean(this.filteringCraftable);
            arg.writeBoolean(this.furnaceGuiOpen);
            arg.writeBoolean(this.furnaceFilteringCraftable);
            arg.writeBoolean(this.blastFurnaceGuiOpen);
            arg.writeBoolean(this.blastFurnaceFilteringCraftable);
            arg.writeBoolean(this.smokerGuiOpen);
            arg.writeBoolean(this.smokerGuiFilteringCraftable);
        }
    }

    @Override
    public void apply(ServerPlayPacketListener arg) {
        arg.onRecipeBookData(this);
    }

    public Mode getMode() {
        return this.mode;
    }

    public Identifier getRecipeId() {
        return this.recipeId;
    }

    public boolean isGuiOpen() {
        return this.guiOpen;
    }

    public boolean isFilteringCraftable() {
        return this.filteringCraftable;
    }

    public boolean isFurnaceGuiOpen() {
        return this.furnaceGuiOpen;
    }

    public boolean isFurnaceFilteringCraftable() {
        return this.furnaceFilteringCraftable;
    }

    public boolean isBlastFurnaceGuiOpen() {
        return this.blastFurnaceGuiOpen;
    }

    public boolean isBlastFurnaceFilteringCraftable() {
        return this.blastFurnaceFilteringCraftable;
    }

    public boolean isSmokerGuiOpen() {
        return this.smokerGuiOpen;
    }

    public boolean isSmokerGuiFilteringCraftable() {
        return this.smokerGuiFilteringCraftable;
    }

    public static enum Mode {
        SHOWN,
        SETTINGS;

    }
}

