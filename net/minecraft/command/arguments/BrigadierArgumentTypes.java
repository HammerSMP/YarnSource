/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.BoolArgumentType
 *  com.mojang.brigadier.arguments.DoubleArgumentType
 *  com.mojang.brigadier.arguments.FloatArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.arguments.LongArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 */
package net.minecraft.command.arguments;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.command.arguments.serialize.ConstantArgumentSerializer;
import net.minecraft.command.arguments.serialize.DoubleArgumentSerializer;
import net.minecraft.command.arguments.serialize.FloatArgumentSerializer;
import net.minecraft.command.arguments.serialize.IntegerArgumentSerializer;
import net.minecraft.command.arguments.serialize.LongArgumentSerializer;
import net.minecraft.command.arguments.serialize.StringArgumentSerializer;

public class BrigadierArgumentTypes {
    public static void register() {
        ArgumentTypes.register("brigadier:bool", BoolArgumentType.class, new ConstantArgumentSerializer<BoolArgumentType>(BoolArgumentType::bool));
        ArgumentTypes.register("brigadier:float", FloatArgumentType.class, new FloatArgumentSerializer());
        ArgumentTypes.register("brigadier:double", DoubleArgumentType.class, new DoubleArgumentSerializer());
        ArgumentTypes.register("brigadier:integer", IntegerArgumentType.class, new IntegerArgumentSerializer());
        ArgumentTypes.register("brigadier:long", LongArgumentType.class, new LongArgumentSerializer());
        ArgumentTypes.register("brigadier:string", StringArgumentType.class, new StringArgumentSerializer());
    }

    public static byte createFlag(boolean bl, boolean bl2) {
        byte b = 0;
        if (bl) {
            b = (byte)(b | true ? 1 : 0);
        }
        if (bl2) {
            b = (byte)(b | 2);
        }
        return b;
    }

    public static boolean hasMin(byte b) {
        return (b & 1) != 0;
    }

    public static boolean hasMax(byte b) {
        return (b & 2) != 0;
    }
}

