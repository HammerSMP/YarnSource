/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.class_5413;
import net.minecraft.class_5414;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.registry.Registry;

public interface class_5415 {
    public static final class_5415 field_25744 = class_5415.method_30216(class_5414.method_30214(), class_5414.method_30214(), class_5414.method_30214(), class_5414.method_30214());

    public class_5414<Block> method_30215();

    public class_5414<Item> method_30218();

    public class_5414<Fluid> method_30220();

    public class_5414<EntityType<?>> method_30221();

    default public void method_30222() {
        class_5413.method_30198(this);
        Blocks.refreshShapeCache();
    }

    default public void method_30217(PacketByteBuf arg) {
        this.method_30215().method_30208(arg, Registry.BLOCK);
        this.method_30218().method_30208(arg, Registry.ITEM);
        this.method_30220().method_30208(arg, Registry.FLUID);
        this.method_30221().method_30208(arg, Registry.ENTITY_TYPE);
    }

    public static class_5415 method_30219(PacketByteBuf arg) {
        class_5414<Block> lv = class_5414.method_30209(arg, Registry.BLOCK);
        class_5414<Item> lv2 = class_5414.method_30209(arg, Registry.ITEM);
        class_5414<Fluid> lv3 = class_5414.method_30209(arg, Registry.FLUID);
        class_5414<EntityType<?>> lv4 = class_5414.method_30209(arg, Registry.ENTITY_TYPE);
        return class_5415.method_30216(lv, lv2, lv3, lv4);
    }

    public static class_5415 method_30216(final class_5414<Block> arg, final class_5414<Item> arg2, final class_5414<Fluid> arg3, final class_5414<EntityType<?>> arg4) {
        return new class_5415(){

            @Override
            public class_5414<Block> method_30215() {
                return arg;
            }

            @Override
            public class_5414<Item> method_30218() {
                return arg2;
            }

            @Override
            public class_5414<Fluid> method_30220() {
                return arg3;
            }

            @Override
            public class_5414<EntityType<?>> method_30221() {
                return arg4;
            }
        };
    }
}

