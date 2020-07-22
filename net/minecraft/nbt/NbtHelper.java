/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.properties.Property
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.nbt;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
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
    public static GameProfile toGameProfile(CompoundTag tag) {
        String string = null;
        UUID uUID = null;
        if (tag.contains("Name", 8)) {
            string = tag.getString("Name");
        }
        if (tag.containsUuid("Id")) {
            uUID = tag.getUuid("Id");
        }
        try {
            GameProfile gameProfile = new GameProfile(uUID, string);
            if (tag.contains("Properties", 10)) {
                CompoundTag lv = tag.getCompound("Properties");
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

    public static CompoundTag fromGameProfile(CompoundTag tag, GameProfile profile) {
        if (!ChatUtil.isEmpty(profile.getName())) {
            tag.putString("Name", profile.getName());
        }
        if (profile.getId() != null) {
            tag.putUuid("Id", profile.getId());
        }
        if (!profile.getProperties().isEmpty()) {
            CompoundTag lv = new CompoundTag();
            for (String string : profile.getProperties().keySet()) {
                ListTag lv2 = new ListTag();
                for (com.mojang.authlib.properties.Property property : profile.getProperties().get((Object)string)) {
                    CompoundTag lv3 = new CompoundTag();
                    lv3.putString("Value", property.getValue());
                    if (property.hasSignature()) {
                        lv3.putString("Signature", property.getSignature());
                    }
                    lv2.add(lv3);
                }
                lv.put(string, lv2);
            }
            tag.put("Properties", lv);
        }
        return tag;
    }

    @VisibleForTesting
    public static boolean matches(@Nullable Tag standard, @Nullable Tag subject, boolean equalValue) {
        if (standard == subject) {
            return true;
        }
        if (standard == null) {
            return true;
        }
        if (subject == null) {
            return false;
        }
        if (!standard.getClass().equals(subject.getClass())) {
            return false;
        }
        if (standard instanceof CompoundTag) {
            CompoundTag lv = (CompoundTag)standard;
            CompoundTag lv2 = (CompoundTag)subject;
            for (String string : lv.getKeys()) {
                Tag lv3 = lv.get(string);
                if (NbtHelper.matches(lv3, lv2.get(string), equalValue)) continue;
                return false;
            }
            return true;
        }
        if (standard instanceof ListTag && equalValue) {
            ListTag lv4 = (ListTag)standard;
            ListTag lv5 = (ListTag)subject;
            if (lv4.isEmpty()) {
                return lv5.isEmpty();
            }
            for (int i = 0; i < lv4.size(); ++i) {
                Tag lv6 = lv4.get(i);
                boolean bl2 = false;
                for (int j = 0; j < lv5.size(); ++j) {
                    if (!NbtHelper.matches(lv6, lv5.get(j), equalValue)) continue;
                    bl2 = true;
                    break;
                }
                if (bl2) continue;
                return false;
            }
            return true;
        }
        return standard.equals(subject);
    }

    public static IntArrayTag fromUuid(UUID uuid) {
        return new IntArrayTag(DynamicSerializableUuid.toIntArray(uuid));
    }

    public static UUID toUuid(Tag tag) {
        if (tag.getReader() != IntArrayTag.READER) {
            throw new IllegalArgumentException("Expected UUID-Tag to be of type " + IntArrayTag.READER.getCrashReportName() + ", but found " + tag.getReader().getCrashReportName() + ".");
        }
        int[] is = ((IntArrayTag)tag).getIntArray();
        if (is.length != 4) {
            throw new IllegalArgumentException("Expected UUID-Array to be of length 4, but found " + is.length + ".");
        }
        return DynamicSerializableUuid.toUuid(is);
    }

    public static BlockPos toBlockPos(CompoundTag tag) {
        return new BlockPos(tag.getInt("X"), tag.getInt("Y"), tag.getInt("Z"));
    }

    public static CompoundTag fromBlockPos(BlockPos pos) {
        CompoundTag lv = new CompoundTag();
        lv.putInt("X", pos.getX());
        lv.putInt("Y", pos.getY());
        lv.putInt("Z", pos.getZ());
        return lv;
    }

    public static BlockState toBlockState(CompoundTag tag) {
        if (!tag.contains("Name", 8)) {
            return Blocks.AIR.getDefaultState();
        }
        Block lv = Registry.BLOCK.get(new Identifier(tag.getString("Name")));
        BlockState lv2 = lv.getDefaultState();
        if (tag.contains("Properties", 10)) {
            CompoundTag lv3 = tag.getCompound("Properties");
            StateManager<Block, BlockState> lv4 = lv.getStateManager();
            for (String string : lv3.getKeys()) {
                Property<?> lv5 = lv4.getProperty(string);
                if (lv5 == null) continue;
                lv2 = NbtHelper.withProperty(lv2, lv5, string, lv3, tag);
            }
        }
        return lv2;
    }

    private static <S extends State<?, S>, T extends Comparable<T>> S withProperty(S state, Property<T> property, String key, CompoundTag propertiesTag, CompoundTag mainTag) {
        Optional<T> optional = property.parse(propertiesTag.getString(key));
        if (optional.isPresent()) {
            return (S)((State)state.with(property, (Comparable)((Comparable)optional.get())));
        }
        LOGGER.warn("Unable to read property: {} with value: {} for blockstate: {}", (Object)key, (Object)propertiesTag.getString(key), (Object)mainTag.toString());
        return state;
    }

    public static CompoundTag fromBlockState(BlockState state) {
        CompoundTag lv = new CompoundTag();
        lv.putString("Name", Registry.BLOCK.getId(state.getBlock()).toString());
        ImmutableMap<Property<?>, Comparable<?>> immutableMap = state.getEntries();
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

    private static <T extends Comparable<T>> String nameValue(Property<T> property, Comparable<?> value) {
        return property.name(value);
    }

    public static CompoundTag update(DataFixer fixer, DataFixTypes fixTypes, CompoundTag tag, int oldVersion) {
        return NbtHelper.update(fixer, fixTypes, tag, oldVersion, SharedConstants.getGameVersion().getWorldVersion());
    }

    public static CompoundTag update(DataFixer fixer, DataFixTypes fixTypes, CompoundTag tag, int oldVersion, int targetVersion) {
        return (CompoundTag)fixer.update(fixTypes.getTypeReference(), new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)tag), oldVersion, targetVersion).getValue();
    }
}

