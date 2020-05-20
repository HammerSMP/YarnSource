/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.command.arguments.ItemStackArgument;
import net.minecraft.command.arguments.ItemStringReader;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.registry.Registry;

public class ItemStackParticleEffect
implements ParticleEffect {
    public static final ParticleEffect.Factory<ItemStackParticleEffect> PARAMETERS_FACTORY = new ParticleEffect.Factory<ItemStackParticleEffect>(){

        @Override
        public ItemStackParticleEffect read(ParticleType<ItemStackParticleEffect> arg, StringReader stringReader) throws CommandSyntaxException {
            stringReader.expect(' ');
            ItemStringReader lv = new ItemStringReader(stringReader, false).consume();
            ItemStack lv2 = new ItemStackArgument(lv.getItem(), lv.getTag()).createStack(1, false);
            return new ItemStackParticleEffect(arg, lv2);
        }

        @Override
        public ItemStackParticleEffect read(ParticleType<ItemStackParticleEffect> arg, PacketByteBuf arg2) {
            return new ItemStackParticleEffect(arg, arg2.readItemStack());
        }

        @Override
        public /* synthetic */ ParticleEffect read(ParticleType arg, PacketByteBuf arg2) {
            return this.read(arg, arg2);
        }

        @Override
        public /* synthetic */ ParticleEffect read(ParticleType arg, StringReader stringReader) throws CommandSyntaxException {
            return this.read(arg, stringReader);
        }
    };
    private final ParticleType<ItemStackParticleEffect> type;
    private final ItemStack stack;

    public static Codec<ItemStackParticleEffect> method_29136(ParticleType<ItemStackParticleEffect> arg3) {
        return ItemStack.field_24671.xmap(arg2 -> new ItemStackParticleEffect(arg3, (ItemStack)arg2), arg -> arg.stack);
    }

    public ItemStackParticleEffect(ParticleType<ItemStackParticleEffect> arg, ItemStack arg2) {
        this.type = arg;
        this.stack = arg2;
    }

    @Override
    public void write(PacketByteBuf arg) {
        arg.writeItemStack(this.stack);
    }

    @Override
    public String asString() {
        return Registry.PARTICLE_TYPE.getId(this.getType()) + " " + new ItemStackArgument(this.stack.getItem(), this.stack.getTag()).asString();
    }

    public ParticleType<ItemStackParticleEffect> getType() {
        return this.type;
    }

    @Environment(value=EnvType.CLIENT)
    public ItemStack getItemStack() {
        return this.stack;
    }
}

