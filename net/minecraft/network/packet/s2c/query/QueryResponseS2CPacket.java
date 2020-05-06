/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.TypeAdapterFactory
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.s2c.query;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapterFactory;
import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientQueryPacketListener;
import net.minecraft.server.ServerMetadata;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.LowercaseEnumTypeAdapterFactory;

public class QueryResponseS2CPacket
implements Packet<ClientQueryPacketListener> {
    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(ServerMetadata.Version.class, (Object)new ServerMetadata.Version.Serializer()).registerTypeAdapter(ServerMetadata.Players.class, (Object)new ServerMetadata.Players.Deserializer()).registerTypeAdapter(ServerMetadata.class, (Object)new ServerMetadata.Deserializer()).registerTypeHierarchyAdapter(Text.class, (Object)new Text.Serializer()).registerTypeHierarchyAdapter(Style.class, (Object)new Style.Serializer()).registerTypeAdapterFactory((TypeAdapterFactory)new LowercaseEnumTypeAdapterFactory()).create();
    private ServerMetadata metadata;

    public QueryResponseS2CPacket() {
    }

    public QueryResponseS2CPacket(ServerMetadata arg) {
        this.metadata = arg;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        this.metadata = JsonHelper.deserialize(GSON, arg.readString(32767), ServerMetadata.class);
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        arg.writeString(GSON.toJson((Object)this.metadata));
    }

    @Override
    public void apply(ClientQueryPacketListener arg) {
        arg.onResponse(this);
    }

    @Environment(value=EnvType.CLIENT)
    public ServerMetadata getServerMetadata() {
        return this.metadata;
    }
}

