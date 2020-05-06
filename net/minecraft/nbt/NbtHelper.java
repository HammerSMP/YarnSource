/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.properties.Property
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.nbt;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.state.State;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.DynamicSerializableUuid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class NbtHelper {
    private static final Logger LOGGER = LogManager.getLogger();

    @Nullable
    public static GameProfile toGameProfile(CompoundTag arg) {
        String string = null;
        UUID uUID = null;
        if (arg.contains("Name", 8)) {
            string = arg.getString("Name");
        }
        if (arg.containsUuidNew("Id")) {
            uUID = arg.getUuidNew("Id");
        }
        try {
            GameProfile gameProfile = new GameProfile(uUID, string);
            if (arg.contains("Properties", 10)) {
                CompoundTag lv = arg.getCompound("Properties");
                for (String string2 : lv.getKeys()) {
                    ListTag lv2 = lv.getList(string2, 10);
                    for (int i = 0; i < lv2.size(); ++i) {
                        CompoundTag lv3 = lv2.getCompound(i);
                        String string3 = lv3.getString("Value");
                        if (lv3.contains("Signature", 8)) {
                            gameProfile.getProperties().put((Object)string2, (Object)new com.mojang.authlib.properties.Property(string2, string3, lv3.getString("Signature")));
                            continue;
                        }
                        gameProfile.getProperties().put((Object)string2, (Object)new com.mojang.authlib.properties.Property(string2, string3));
                    }
                }
            }
            return gameProfile;
        }
        catch (Throwable throwable) {
            return null;
        }
    }

    public static CompoundTag fromGameProfile(CompoundTag arg, GameProfile gameProfile) {
        if (!ChatUtil.isEmpty(gameProfile.getName())) {
            arg.putString("Name", gameProfile.getName());
        }
        if (gameProfile.getId() != null) {
            arg.putUuidNew("Id", gameProfile.getId());
        }
        if (!gameProfile.getProperties().isEmpty()) {
            CompoundTag lv = new CompoundTag();
            for (String string : gameProfile.getProperties().keySet()) {
                ListTag lv2 = new ListTag();
                for (com.mojang.authlib.properties.Property property : gameProfile.getProperties().get((Object)string)) {
                    CompoundTag lv3 = new CompoundTag();
                    lv3.putString("Value", property.getValue());
                    if (property.hasSignature()) {
                        lv3.putString("Signature", property.getSignature());
                    }
                    lv2.add(lv3);
                }
                lv.put(string, lv2);
            }
            arg.put("Properties", lv);
        }
        return arg;
    }

    @VisibleForTesting
    public static boolean matches(@Nullable Tag arg, @Nullable Tag arg2, boolean bl) {
        if (arg == arg2) {
            return true;
        }
        if (arg == null) {
            return true;
        }
        if (arg2 == null) {
            return false;
        }
        if (!arg.getClass().equals(arg2.getClass())) {
            return false;
        }
        if (arg instanceof CompoundTag) {
            CompoundTag lv = (CompoundTag)arg;
            CompoundTag lv2 = (CompoundTag)arg2;
            for (String string : lv.getKeys()) {
                Tag lv3 = lv.get(string);
                if (NbtHelper.matches(lv3, lv2.get(string), bl)) continue;
                return false;
            }
            return true;
        }
        if (arg instanceof ListTag && bl) {
            ListTag lv4 = (ListTag)arg;
            ListTag lv5 = (ListTag)arg2;
            if (lv4.isEmpty()) {
                return lv5.isEmpty();
            }
            for (int i = 0; i < lv4.size(); ++i) {
                Tag lv6 = lv4.get(i);
                boolean bl2 = false;
                for (int j = 0; j < lv5.size(); ++j) {
                    if (!NbtHelper.matches(lv6, lv5.get(j), bl)) continue;
                    bl2 = true;
                    break;
                }
                if (bl2) continue;
                return false;
            }
            return true;
        }
        return arg.equals(arg2);
    }

    public static IntArrayTag fromUuidNew(UUID uUID) {
        return new IntArrayTag(DynamicSerializableUuid.method_26275(uUID));
    }

    public static UUID toUuidNew(Tag arg) {
        if (arg.getReader() != IntArrayTag.READER) {
            throw new IllegalArgumentException("Expected UUID-Tag to be of type " + IntArrayTag.READER.getCrashReportName() + ", but found " + arg.getReader().getCrashReportName() + ".");
        }
        int[] is = ((IntArrayTag)arg).getIntArray();
        if (is.length != 4) {
            throw new IllegalArgumentException("Expected UUID-Array to be of length 4, but found " + is.length + ".");
        }
        return DynamicSerializableUuid.method_26276(is);
    }

    public static BlockPos toBlockPos(CompoundTag arg) {
        return new BlockPos(arg.getInt("X"), arg.getInt("Y"), arg.getInt("Z"));
    }

    public static CompoundTag fromBlockPos(BlockPos arg) {
        CompoundTag lv = new CompoundTag();
        lv.putInt("X", arg.getX());
        lv.putInt("Y", arg.getY());
        lv.putInt("Z", arg.getZ());
        return lv;
    }

    public static BlockState toBlockState(CompoundTag arg) {
        if (!arg.contains("Name", 8)) {
            return Blocks.AIR.getDefaultState();
        }
        Block lv = Registry.BLOCK.get(new Identifier(arg.getString("Name")));
        BlockState lv2 = lv.getDefaultState();
        if (arg.contains("Properties", 10)) {
            CompoundTag lv3 = arg.getCompound("Properties");
            StateManager<Block, BlockState> lv4 = lv.getStateManager();
            for (String string : lv3.getKeys()) {
                Property<?> lv5 = lv4.getProperty(string);
                if (lv5 == null) continue;
                lv2 = NbtHelper.withProperty(lv2, lv5, string, lv3, arg);
            }
        }
        return lv2;
    }

    private static <S extends State<S>, T extends Comparable<T>> S withProperty(S arg, Property<T> arg2, String string, CompoundTag arg3, CompoundTag arg4) {
        Optional<T> optional = arg2.parse(arg3.getString(string));
        if (optional.isPresent()) {
            return (S)((State)arg.with(arg2, (Comparable)((Comparable)optional.get())));
        }
        LOGGER.warn("Unable to read property: {} with value: {} for blockstate: {}", (Object)string, (Object)arg3.getString(string), (Object)arg4.toString());
        return arg;
    }

    public static CompoundTag fromBlockState(BlockState arg) {
        CompoundTag lv = new CompoundTag();
        lv.putString("Name", Registry.BLOCK.getId(arg.getBlock()).toString());
        ImmutableMap<Property<?>, Comparable<?>> immutableMap = arg.getEntries();
        if (!immutableMap.isEmpty()) {
            CompoundTag lv2 = new CompoundTag();
            for (Map.Entry entry : immutableMap.entrySet()) {
                Property lv3 = (Property)entry.getKey();
                lv2.putString(lv3.getName(), NbtHelper.nameValue(lv3, (Comparable)entry.getValue()));
            }
            lv.put("Properties", lv2);
        }
        return lv;
    }

    private static <T extends Comparable<T>> String nameValue(Property<T> arg, Comparable<?> comparable) {
        return arg.name(comparable);
    }

    public static CompoundTag update(DataFixer dataFixer, DataFixTypes arg, CompoundTag arg2, int i) {
        return NbtHelper.update(dataFixer, arg, arg2, i, SharedConstants.getGameVersion().getWorldVersion());
    }

    public static CompoundTag update(DataFixer dataFixer, DataFixTypes arg, CompoundTag arg2, int i, int j) {
        return (CompoundTag)dataFixer.update(arg.getTypeReference(), new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)arg2), i, j).getValue();
    }
}

