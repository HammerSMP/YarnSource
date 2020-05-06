/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class SynchronizeRecipesS2CPacket
implements Packet<ClientPlayPacketListener> {
    private List<Recipe<?>> recipes;

    public SynchronizeRecipesS2CPacket() {
    }

    public SynchronizeRecipesS2CPacket(Collection<Recipe<?>> collection) {
        this.recipes = Lists.newArrayList(collection);
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onSynchronizeRecipes(this);
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.recipes = Lists.newArrayList();
        int i = arg.readVarInt();
        for (int j = 0; j < i; ++j) {
            this.recipes.add(SynchronizeRecipesS2CPacket.readRecipe(arg));
        }
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeVarInt(this.recipes.size());
        for (Recipe<?> lv : this.recipes) {
            SynchronizeRecipesS2CPacket.writeRecipe(lv, arg);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public List<Recipe<?>> getRecipes() {
        return this.recipes;
    }

    public static Recipe<?> readRecipe(PacketByteBuf arg) {
        Identifier lv = arg.readIdentifier();
        Identifier lv2 = arg.readIdentifier();
        return Registry.RECIPE_SERIALIZER.getOrEmpty(lv).orElseThrow(() -> new IllegalArgumentException("Unknown recipe serializer " + lv)).read(lv2, arg);
    }

    public static <T extends Recipe<?>> void writeRecipe(T arg, PacketByteBuf arg2) {
        arg2.writeIdentifier(Registry.RECIPE_SERIALIZER.getId(arg.getSerializer()));
        arg2.writeIdentifier(arg.getId());
        arg.getSerializer().write(arg2, arg);
    }
}

