/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.util.Pair
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.io.IOException;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class EntityEquipmentUpdateS2CPacket
implements Packet<ClientPlayPacketListener> {
    private int id;
    private final List<Pair<EquipmentSlot, ItemStack>> field_25721;

    public EntityEquipmentUpdateS2CPacket() {
        this.field_25721 = Lists.newArrayList();
    }

    public EntityEquipmentUpdateS2CPacket(int i, List<Pair<EquipmentSlot, ItemStack>> list) {
        this.id = i;
        this.field_25721 = list;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        byte i;
        this.id = arg.readVarInt();
        EquipmentSlot[] lvs = EquipmentSlot.values();
        do {
            i = arg.readByte();
            EquipmentSlot lv = lvs[i & 0x7F];
            ItemStack lv2 = arg.readItemStack();
            this.field_25721.add((Pair<EquipmentSlot, ItemStack>)Pair.of((Object)((Object)lv), (Object)lv2));
        } while ((i & 0xFFFFFF80) != 0);
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeVarInt(this.id);
        int i = this.field_25721.size();
        for (int j = 0; j < i; ++j) {
            Pair<EquipmentSlot, ItemStack> pair = this.field_25721.get(j);
            EquipmentSlot lv = (EquipmentSlot)((Object)pair.getFirst());
            boolean bl = j != i - 1;
            int k = lv.ordinal();
            arg.writeByte(bl ? k | 0xFFFFFF80 : k);
            arg.writeItemStack((ItemStack)pair.getSecond());
        }
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onEquipmentUpdate(this);
    }

    @Environment(value=EnvType.CLIENT)
    public int getId() {
        return this.id;
    }

    @Environment(value=EnvType.CLIENT)
    public List<Pair<EquipmentSlot, ItemStack>> method_30145() {
        return this.field_25721;
    }
}

