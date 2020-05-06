/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.text;

import com.google.common.base.Joiner;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.command.arguments.BlockPosArgumentType;
import net.minecraft.command.arguments.NbtPathArgumentType;
import net.minecraft.command.arguments.PosArgument;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.BaseText;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.ParsableText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class NbtText
extends BaseText
implements ParsableText {
    private static final Logger LOGGER = LogManager.getLogger();
    protected final boolean interpret;
    protected final String rawPath;
    @Nullable
    protected final NbtPathArgumentType.NbtPath path;

    @Nullable
    private static NbtPathArgumentType.NbtPath parsePath(String string) {
        try {
            return new NbtPathArgumentType().parse(new StringReader(string));
        }
        catch (CommandSyntaxException commandSyntaxException) {
            return null;
        }
    }

    public NbtText(String string, boolean bl) {
        this(string, NbtText.parsePath(string), bl);
    }

    protected NbtText(String string, @Nullable NbtPathArgumentType.NbtPath arg, boolean bl) {
        this.rawPath = string;
        this.path = arg;
        this.interpret = bl;
    }

    protected abstract Stream<CompoundTag> toNbt(ServerCommandSource var1) throws CommandSyntaxException;

    public String getPath() {
        return this.rawPath;
    }

    public boolean shouldInterpret() {
        return this.interpret;
    }

    @Override
    public MutableText parse(@Nullable ServerCommandSource arg3, @Nullable Entity arg22, int i) throws CommandSyntaxException {
        if (arg3 == null || this.path == null) {
            return new LiteralText("");
        }
        Stream<String> stream = this.toNbt(arg3).flatMap(arg -> {
            try {
                return this.path.get((Tag)arg).stream();
            }
            catch (CommandSyntaxException commandSyntaxException) {
                return Stream.empty();
            }
        }).map(Tag::asString);
        if (this.interpret) {
            return stream.flatMap(string -> {
                try {
                    MutableText lv = Text.Serializer.fromJson(string);
                    return Stream.of(Texts.parse(arg3, lv, arg22, i));
                }
                catch (Exception exception) {
                    LOGGER.warn("Failed to parse component: " + string, (Throwable)exception);
                    return Stream.of(new MutableText[0]);
                }
            }).reduce((arg, arg2) -> arg.append(", ").append((Text)arg2)).orElse(new LiteralText(""));
        }
        return new LiteralText(Joiner.on((String)", ").join(stream.iterator()));
    }

    public static class StorageNbtText
    extends NbtText {
        private final Identifier id;

        public StorageNbtText(String string, boolean bl, Identifier arg) {
            super(string, bl);
            this.id = arg;
        }

        public StorageNbtText(String string, @Nullable NbtPathArgumentType.NbtPath arg, boolean bl, Identifier arg2) {
            super(string, arg, bl);
            this.id = arg2;
        }

        public Identifier getId() {
            return this.id;
        }

        @Override
        public StorageNbtText copy() {
            return new StorageNbtText(this.rawPath, this.path, this.interpret, this.id);
        }

        @Override
        protected Stream<CompoundTag> toNbt(ServerCommandSource arg) {
            CompoundTag lv = arg.getMinecraftServer().getDataCommandStorage().get(this.id);
            return Stream.of(lv);
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object instanceof StorageNbtText) {
                StorageNbtText lv = (StorageNbtText)object;
                return Objects.equals(this.id, lv.id) && Objects.equals(this.rawPath, lv.rawPath) && super.equals(object);
            }
            return false;
        }

        @Override
        public String toString() {
            return "StorageNbtComponent{id='" + this.id + '\'' + "path='" + this.rawPath + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
        }

        @Override
        public /* synthetic */ BaseText copy() {
            return this.copy();
        }

        @Override
        public /* synthetic */ MutableText copy() {
            return this.copy();
        }
    }

    public static class BlockNbtText
    extends NbtText {
        private final String rawPos;
        @Nullable
        private final PosArgument pos;

        public BlockNbtText(String string, boolean bl, String string2) {
            super(string, bl);
            this.rawPos = string2;
            this.pos = this.parsePos(this.rawPos);
        }

        @Nullable
        private PosArgument parsePos(String string) {
            try {
                return BlockPosArgumentType.blockPos().parse(new StringReader(string));
            }
            catch (CommandSyntaxException commandSyntaxException) {
                return null;
            }
        }

        private BlockNbtText(String string, @Nullable NbtPathArgumentType.NbtPath arg, boolean bl, String string2, @Nullable PosArgument arg2) {
            super(string, arg, bl);
            this.rawPos = string2;
            this.pos = arg2;
        }

        @Nullable
        public String getPos() {
            return this.rawPos;
        }

        @Override
        public BlockNbtText copy() {
            return new BlockNbtText(this.rawPath, this.path, this.interpret, this.rawPos, this.pos);
        }

        @Override
        protected Stream<CompoundTag> toNbt(ServerCommandSource arg) {
            BlockEntity lv3;
            BlockPos lv2;
            ServerWorld lv;
            if (this.pos != null && (lv = arg.getWorld()).canSetBlock(lv2 = this.pos.toAbsoluteBlockPos(arg)) && (lv3 = lv.getBlockEntity(lv2)) != null) {
                return Stream.of(lv3.toTag(new CompoundTag()));
            }
            return Stream.empty();
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object instanceof BlockNbtText) {
                BlockNbtText lv = (BlockNbtText)object;
                return Objects.equals(this.rawPos, lv.rawPos) && Objects.equals(this.rawPath, lv.rawPath) && super.equals(object);
            }
            return false;
        }

        @Override
        public String toString() {
            return "BlockPosArgument{pos='" + this.rawPos + '\'' + "path='" + this.rawPath + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
        }

        @Override
        public /* synthetic */ BaseText copy() {
            return this.copy();
        }

        @Override
        public /* synthetic */ MutableText copy() {
            return this.copy();
        }
    }

    public static class EntityNbtText
    extends NbtText {
        private final String rawSelector;
        @Nullable
        private final EntitySelector selector;

        public EntityNbtText(String string, boolean bl, String string2) {
            super(string, bl);
            this.rawSelector = string2;
            this.selector = EntityNbtText.parseSelector(string2);
        }

        @Nullable
        private static EntitySelector parseSelector(String string) {
            try {
                EntitySelectorReader lv = new EntitySelectorReader(new StringReader(string));
                return lv.read();
            }
            catch (CommandSyntaxException commandSyntaxException) {
                return null;
            }
        }

        private EntityNbtText(String string, @Nullable NbtPathArgumentType.NbtPath arg, boolean bl, String string2, @Nullable EntitySelector arg2) {
            super(string, arg, bl);
            this.rawSelector = string2;
            this.selector = arg2;
        }

        public String getSelector() {
            return this.rawSelector;
        }

        @Override
        public EntityNbtText copy() {
            return new EntityNbtText(this.rawPath, this.path, this.interpret, this.rawSelector, this.selector);
        }

        @Override
        protected Stream<CompoundTag> toNbt(ServerCommandSource arg) throws CommandSyntaxException {
            if (this.selector != null) {
                List<? extends Entity> list = this.selector.getEntities(arg);
                return list.stream().map(NbtPredicate::entityToTag);
            }
            return Stream.empty();
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object instanceof EntityNbtText) {
                EntityNbtText lv = (EntityNbtText)object;
                return Objects.equals(this.rawSelector, lv.rawSelector) && Objects.equals(this.rawPath, lv.rawPath) && super.equals(object);
            }
            return false;
        }

        @Override
        public String toString() {
            return "EntityNbtComponent{selector='" + this.rawSelector + '\'' + "path='" + this.rawPath + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
        }

        @Override
        public /* synthetic */ BaseText copy() {
            return this.copy();
        }

        @Override
        public /* synthetic */ MutableText copy() {
            return this.copy();
        }
    }
}

