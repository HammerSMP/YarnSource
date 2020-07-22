/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.resource;

import com.mojang.brigadier.arguments.StringArgumentType;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackCompatibility;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResourcePackProfile
implements AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final PackResourceMetadata BROKEN_PACK_META = new PackResourceMetadata(new TranslatableText("resourcePack.broken_assets").formatted(Formatting.RED, Formatting.ITALIC), SharedConstants.getGameVersion().getPackVersion());
    private final String name;
    private final Supplier<ResourcePack> packGetter;
    private final Text displayName;
    private final Text description;
    private final ResourcePackCompatibility compatibility;
    private final InsertionPosition position;
    private final boolean alwaysEnabled;
    private final boolean pinned;
    private final ResourcePackSource source;

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Nullable
    public static ResourcePackProfile of(String name, boolean alwaysEnabled, Supplier<ResourcePack> packFactory, Factory containerFactory, InsertionPosition insertionPosition, ResourcePackSource arg3) {
        try (ResourcePack lv = packFactory.get();){
            PackResourceMetadata lv2 = lv.parseMetadata(PackResourceMetadata.READER);
            if (alwaysEnabled && lv2 == null) {
                LOGGER.error("Broken/missing pack.mcmeta detected, fudging it into existance. Please check that your launcher has downloaded all assets for the game correctly!");
                lv2 = BROKEN_PACK_META;
            }
            if (lv2 != null) {
                ResourcePackProfile resourcePackProfile = containerFactory.create(name, alwaysEnabled, packFactory, lv, lv2, insertionPosition, arg3);
                return resourcePackProfile;
            }
            LOGGER.warn("Couldn't find pack meta for pack {}", (Object)name);
            return null;
        }
        catch (IOException iOException) {
            LOGGER.warn("Couldn't get pack info for: {}", (Object)iOException.toString());
        }
        return null;
    }

    public ResourcePackProfile(String name, boolean alwaysEnabled, Supplier<ResourcePack> packFactory, Text displayName, Text description, ResourcePackCompatibility compatibility, InsertionPosition direction, boolean pinned, ResourcePackSource source) {
        this.name = name;
        this.packGetter = packFactory;
        this.displayName = displayName;
        this.description = description;
        this.compatibility = compatibility;
        this.alwaysEnabled = alwaysEnabled;
        this.position = direction;
        this.pinned = pinned;
        this.source = source;
    }

    public ResourcePackProfile(String name, boolean alwaysEnabled, Supplier<ResourcePack> packFactory, ResourcePack pack, PackResourceMetadata metadata, InsertionPosition direction, ResourcePackSource source) {
        this(name, alwaysEnabled, packFactory, new LiteralText(pack.getName()), metadata.getDescription(), ResourcePackCompatibility.from(metadata.getPackFormat()), direction, false, source);
    }

    @Environment(value=EnvType.CLIENT)
    public Text getDisplayName() {
        return this.displayName;
    }

    @Environment(value=EnvType.CLIENT)
    public Text getDescription() {
        return this.description;
    }

    public Text getInformationText(boolean enabled) {
        return Texts.bracketed(this.source.decorate(new LiteralText(this.name))).styled(arg -> arg.withColor(enabled ? Formatting.GREEN : Formatting.RED).withInsertion(StringArgumentType.escapeIfRequired((String)this.name)).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("").append(this.displayName).append("\n").append(this.description))));
    }

    public ResourcePackCompatibility getCompatibility() {
        return this.compatibility;
    }

    public ResourcePack createResourcePack() {
        return this.packGetter.get();
    }

    public String getName() {
        return this.name;
    }

    public boolean isAlwaysEnabled() {
        return this.alwaysEnabled;
    }

    public boolean isPinned() {
        return this.pinned;
    }

    public InsertionPosition getInitialPosition() {
        return this.position;
    }

    @Environment(value=EnvType.CLIENT)
    public ResourcePackSource getSource() {
        return this.source;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ResourcePackProfile)) {
            return false;
        }
        ResourcePackProfile lv = (ResourcePackProfile)o;
        return this.name.equals(lv.name);
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public void close() {
    }

    public static enum InsertionPosition {
        TOP,
        BOTTOM;


        public <T> int insert(List<T> items, T item, Function<T, ResourcePackProfile> profileGetter, boolean listInverted) {
            ResourcePackProfile lv3;
            int j;
            InsertionPosition lv;
            InsertionPosition insertionPosition = lv = listInverted ? this.inverse() : this;
            if (lv == BOTTOM) {
                ResourcePackProfile lv2;
                int i;
                for (i = 0; i < items.size() && (lv2 = profileGetter.apply(items.get(i))).isPinned() && lv2.getInitialPosition() == this; ++i) {
                }
                items.add(i, item);
                return i;
            }
            for (j = items.size() - 1; j >= 0 && (lv3 = profileGetter.apply(items.get(j))).isPinned() && lv3.getInitialPosition() == this; --j) {
            }
            items.add(j + 1, item);
            return j + 1;
        }

        public InsertionPosition inverse() {
            return this == TOP ? BOTTOM : TOP;
        }
    }

    @FunctionalInterface
    public static interface Factory {
        @Nullable
        public ResourcePackProfile create(String var1, boolean var2, Supplier<ResourcePack> var3, ResourcePack var4, PackResourceMetadata var5, InsertionPosition var6, ResourcePackSource var7);
    }
}

