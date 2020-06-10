/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.pack;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.ResourcePackCompatibility;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class ResourcePackOrganizer<T extends ResourcePackProfile> {
    private final ResourcePackManager<T> field_25626;
    private final List<T> enabledPacks;
    private final List<T> disabledPacks;
    private final BiConsumer<T, TextureManager> renderer;
    private final Runnable updateCallback;
    private final Consumer<ResourcePackManager<T>> applier;

    public ResourcePackOrganizer(Runnable runnable, BiConsumer<T, TextureManager> biConsumer, ResourcePackManager<T> arg, Consumer<ResourcePackManager<T>> consumer) {
        this.updateCallback = runnable;
        this.renderer = biConsumer;
        this.field_25626 = arg;
        this.enabledPacks = Lists.newArrayList(arg.getEnabledProfiles());
        Collections.reverse(this.enabledPacks);
        this.disabledPacks = Lists.newArrayList(arg.getProfiles());
        this.disabledPacks.removeAll(this.enabledPacks);
        this.applier = consumer;
    }

    public Stream<Pack> getDisabledPacks() {
        return this.disabledPacks.stream().map(arg -> new DisabledPack(this, arg));
    }

    public Stream<Pack> getEnabledPacks() {
        return this.enabledPacks.stream().map(arg -> new EnabledPack(this, arg));
    }

    public void apply() {
        this.field_25626.setEnabledProfiles((Collection)Lists.reverse(this.enabledPacks).stream().map(ResourcePackProfile::getName).collect(ImmutableList.toImmutableList()));
        this.applier.accept(this.field_25626);
    }

    public void method_29981() {
        this.field_25626.scanPacks();
        this.disabledPacks.clear();
        this.disabledPacks.addAll(this.field_25626.getProfiles());
        this.disabledPacks.removeAll(this.enabledPacks);
    }

    @Environment(value=EnvType.CLIENT)
    static class DisabledPack
    extends AbstractPack {
        final /* synthetic */ ResourcePackOrganizer field_25463;

        public DisabledPack(T arg2) {
            this.field_25463 = arg;
            super((ResourcePackOrganizer)arg, arg2);
        }

        @Override
        protected List<T> getCurrentList() {
            return this.field_25463.disabledPacks;
        }

        @Override
        protected List<T> getOppositeList() {
            return this.field_25463.enabledPacks;
        }

        @Override
        public boolean isEnabled() {
            return false;
        }

        @Override
        public void enable() {
            this.toggle();
        }

        @Override
        public void disable() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class EnabledPack
    extends AbstractPack {
        final /* synthetic */ ResourcePackOrganizer field_25462;

        public EnabledPack(T arg2) {
            this.field_25462 = arg;
            super((ResourcePackOrganizer)arg, arg2);
        }

        @Override
        protected List<T> getCurrentList() {
            return this.field_25462.enabledPacks;
        }

        @Override
        protected List<T> getOppositeList() {
            return this.field_25462.disabledPacks;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public void enable() {
        }

        @Override
        public void disable() {
            this.toggle();
        }
    }

    @Environment(value=EnvType.CLIENT)
    static abstract class AbstractPack
    implements Pack {
        private final T profile;
        final /* synthetic */ ResourcePackOrganizer field_25460;

        public AbstractPack(T arg2) {
            this.field_25460 = arg;
            this.profile = arg2;
        }

        protected abstract List<T> getCurrentList();

        protected abstract List<T> getOppositeList();

        @Override
        public void render(TextureManager arg) {
            this.field_25460.renderer.accept(this.profile, arg);
        }

        @Override
        public ResourcePackCompatibility getCompatibility() {
            return ((ResourcePackProfile)this.profile).getCompatibility();
        }

        @Override
        public Text getDisplayName() {
            return ((ResourcePackProfile)this.profile).getDisplayName();
        }

        @Override
        public Text getDescription() {
            return ((ResourcePackProfile)this.profile).getDescription();
        }

        @Override
        public ResourcePackSource getSource() {
            return ((ResourcePackProfile)this.profile).getSource();
        }

        @Override
        public boolean isPinned() {
            return ((ResourcePackProfile)this.profile).isPinned();
        }

        @Override
        public boolean isAlwaysEnabled() {
            return ((ResourcePackProfile)this.profile).isAlwaysEnabled();
        }

        protected void toggle() {
            this.getCurrentList().remove(this.profile);
            ((ResourcePackProfile)this.profile).getInitialPosition().insert(this.getOppositeList(), this.profile, Function.identity(), true);
            this.field_25460.updateCallback.run();
        }

        protected void move(int i) {
            List list = this.getCurrentList();
            int j = list.indexOf(this.profile);
            list.remove(j);
            list.add(j + i, this.profile);
            this.field_25460.updateCallback.run();
        }

        @Override
        public boolean canMoveTowardStart() {
            List list = this.getCurrentList();
            int i = list.indexOf(this.profile);
            return i > 0 && !((ResourcePackProfile)list.get(i - 1)).isPinned();
        }

        @Override
        public void moveTowardStart() {
            this.move(-1);
        }

        @Override
        public boolean canMoveTowardEnd() {
            List list = this.getCurrentList();
            int i = list.indexOf(this.profile);
            return i >= 0 && i < list.size() - 1 && !((ResourcePackProfile)list.get(i + 1)).isPinned();
        }

        @Override
        public void moveTowardEnd() {
            this.move(1);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static interface Pack {
        public void render(TextureManager var1);

        public ResourcePackCompatibility getCompatibility();

        public Text getDisplayName();

        public Text getDescription();

        public ResourcePackSource getSource();

        default public StringRenderable getDecoratedDescription() {
            return this.getSource().decorate(this.getDescription());
        }

        public boolean isPinned();

        public boolean isAlwaysEnabled();

        public void enable();

        public void disable();

        public void moveTowardStart();

        public void moveTowardEnd();

        public boolean isEnabled();

        default public boolean canBeEnabled() {
            return !this.isEnabled();
        }

        default public boolean canBeDisabled() {
            return this.isEnabled() && !this.isAlwaysEnabled();
        }

        public boolean canMoveTowardStart();

        public boolean canMoveTowardEnd();
    }
}

