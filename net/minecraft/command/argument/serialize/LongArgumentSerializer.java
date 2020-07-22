/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.LongArgumentType
 */
package net.minecraft.command.argument.serialize;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import net.minecraft.command.argument.BrigadierArgumentTypes;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;

public class LongArgumentSerializer
implements ArgumentSerializer<LongArgumentType> {
    @Override
    public void toPacket(LongArgumentType longArgumentType, PacketByteBuf arg) {
        boolean bl = longArgumentType.getMinimum() != Long.MIN_VALUE;
        boolean bl2 = longArgumentType.getMaximum() != Long.MAX_VALUE;
        arg.writeByte(BrigadierArgumentTypes.createFlag(bl, bl2));
        if (bl) {
            arg.writeLong(longArgumentType.getMinimum());
        }
        if (bl2) {
            arg.writeLong(longArgumentType.getMaximum());
        }
    }

    @Override
    public LongArgumentType fromPacket(PacketByteBuf arg) {
        byte b = arg.readByte();
        long l = BrigadierArgumentTypes.hasMin(b) ? arg.readLong() : Long.MIN_VALUE;
        long m = BrigadierArgumentTypes.hasMax(b) ? arg.readLong() : Long.MAX_VALUE;
        return LongArgumentType.longArg((long)l, (long)m);
    }

    @Override
    public void toJson(LongArgumentType longArgumentType, JsonObject jsonObject) {
        if (longArgumentType.getMinimum() != Long.MIN_VALUE) {
            jsonObject.addProperty("min", (Number)longArgumentType.getMinimum());
        }
        if (longArgumentType.getMaximum() != Long.MAX_VALUE) {
            jsonObject.addProperty("max", (Number)longArgumentType.getMaximum());
        }
    }

    @Override
    public /* synthetic */ ArgumentType fromPacket(PacketByteBuf arg) {
        return this.fromPacket(arg);
    }
}

