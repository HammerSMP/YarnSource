/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.data;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.text.Text;
import net.minecraft.util.collection.Int2ObjectBiMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.VillagerData;

public class TrackedDataHandlerRegistry {
    private static final Int2ObjectBiMap<TrackedDataHandler<?>> field_13328 = new Int2ObjectBiMap(16);
    public static final TrackedDataHandler<Byte> BYTE = new TrackedDataHandler<Byte>(){

        @Override
        public void write(PacketByteBuf arg, Byte byte_) {
            arg.writeByte(byte_.byteValue());
        }

        @Override
        public Byte read(PacketByteBuf arg) {
            return arg.readByte();
        }

        @Override
        public Byte copy(Byte byte_) {
            return byte_;
        }

        @Override
        public /* synthetic */ Object read(PacketByteBuf arg) {
            return this.read(arg);
        }
    };
    public static final TrackedDataHandler<Integer> INTEGER = new TrackedDataHandler<Integer>(){

        @Override
        public void write(PacketByteBuf arg, Integer integer) {
            arg.writeVarInt(integer);
        }

        @Override
        public Integer read(PacketByteBuf arg) {
            return arg.readVarInt();
        }

        @Override
        public Integer copy(Integer integer) {
            return integer;
        }

        @Override
        public /* synthetic */ Object read(PacketByteBuf arg) {
            return this.read(arg);
        }
    };
    public static final TrackedDataHandler<Float> FLOAT = new TrackedDataHandler<Float>(){

        @Override
        public void write(PacketByteBuf arg, Float float_) {
            arg.writeFloat(float_.floatValue());
        }

        @Override
        public Float read(PacketByteBuf arg) {
            return Float.valueOf(arg.readFloat());
        }

        @Override
        public Float copy(Float float_) {
            return float_;
        }

        @Override
        public /* synthetic */ Object read(PacketByteBuf arg) {
            return this.read(arg);
        }
    };
    public static final TrackedDataHandler<String> STRING = new TrackedDataHandler<String>(){

        @Override
        public void write(PacketByteBuf arg, String string) {
            arg.writeString(string);
        }

        @Override
        public String read(PacketByteBuf arg) {
            return arg.readString(32767);
        }

        @Override
        public String copy(String string) {
            return string;
        }

        @Override
        public /* synthetic */ Object read(PacketByteBuf arg) {
            return this.read(arg);
        }
    };
    public static final TrackedDataHandler<Text> TEXT_COMPONENT = new TrackedDataHandler<Text>(){

        @Override
        public void write(PacketByteBuf arg, Text arg2) {
            arg.writeText(arg2);
        }

        @Override
        public Text read(PacketByteBuf arg) {
            return arg.readText();
        }

        @Override
        public Text copy(Text arg) {
            return arg;
        }

        @Override
        public /* synthetic */ Object read(PacketByteBuf arg) {
            return this.read(arg);
        }
    };
    public static final TrackedDataHandler<Optional<Text>> OPTIONAL_TEXT_COMPONENT = new TrackedDataHandler<Optional<Text>>(){

        @Override
        public void write(PacketByteBuf arg, Optional<Text> optional) {
            if (optional.isPresent()) {
                arg.writeBoolean(true);
                arg.writeText(optional.get());
            } else {
                arg.writeBoolean(false);
            }
        }

        @Override
        public Optional<Text> read(PacketByteBuf arg) {
            return arg.readBoolean() ? Optional.of(arg.readText()) : Optional.empty();
        }

        @Override
        public Optional<Text> copy(Optional<Text> optional) {
            return optional;
        }

        @Override
        public /* synthetic */ Object read(PacketByteBuf arg) {
            return this.read(arg);
        }
    };
    public static final TrackedDataHandler<ItemStack> ITEM_STACK = new TrackedDataHandler<ItemStack>(){

        @Override
        public void write(PacketByteBuf arg, ItemStack arg2) {
            arg.writeItemStack(arg2);
        }

        @Override
        public ItemStack read(PacketByteBuf arg) {
            return arg.readItemStack();
        }

        @Override
        public ItemStack copy(ItemStack arg) {
            return arg.copy();
        }

        @Override
        public /* synthetic */ Object read(PacketByteBuf arg) {
            return this.read(arg);
        }
    };
    public static final TrackedDataHandler<Optional<BlockState>> OPTIONAL_BLOCK_STATE = new TrackedDataHandler<Optional<BlockState>>(){

        @Override
        public void write(PacketByteBuf arg, Optional<BlockState> optional) {
            if (optional.isPresent()) {
                arg.writeVarInt(Block.getRawIdFromState(optional.get()));
            } else {
                arg.writeVarInt(0);
            }
        }

        @Override
        public Optional<BlockState> read(PacketByteBuf arg) {
            int i = arg.readVarInt();
            if (i == 0) {
                return Optional.empty();
            }
            return Optional.of(Block.getStateFromRawId(i));
        }

        @Override
        public Optional<BlockState> copy(Optional<BlockState> optional) {
            return optional;
        }

        @Override
        public /* synthetic */ Object read(PacketByteBuf arg) {
            return this.read(arg);
        }
    };
    public static final TrackedDataHandler<Boolean> BOOLEAN = new TrackedDataHandler<Boolean>(){

        @Override
        public void write(PacketByteBuf arg, Boolean boolean_) {
            arg.writeBoolean(boolean_);
        }

        @Override
        public Boolean read(PacketByteBuf arg) {
            return arg.readBoolean();
        }

        @Override
        public Boolean copy(Boolean boolean_) {
            return boolean_;
        }

        @Override
        public /* synthetic */ Object read(PacketByteBuf arg) {
            return this.read(arg);
        }
    };
    public static final TrackedDataHandler<ParticleEffect> PARTICLE = new TrackedDataHandler<ParticleEffect>(){

        @Override
        public void write(PacketByteBuf arg, ParticleEffect arg2) {
            arg.writeVarInt(Registry.PARTICLE_TYPE.getRawId(arg2.getType()));
            arg2.write(arg);
        }

        @Override
        public ParticleEffect read(PacketByteBuf arg) {
            return this.method_12744(arg, (ParticleType)Registry.PARTICLE_TYPE.get(arg.readVarInt()));
        }

        private <T extends ParticleEffect> T method_12744(PacketByteBuf arg, ParticleType<T> arg2) {
            return arg2.getParametersFactory().read(arg2, arg);
        }

        @Override
        public ParticleEffect copy(ParticleEffect arg) {
            return arg;
        }

        @Override
        public /* synthetic */ Object read(PacketByteBuf arg) {
            return this.read(arg);
        }
    };
    public static final TrackedDataHandler<EulerAngle> ROTATION = new TrackedDataHandler<EulerAngle>(){

        @Override
        public void write(PacketByteBuf arg, EulerAngle arg2) {
            arg.writeFloat(arg2.getPitch());
            arg.writeFloat(arg2.getYaw());
            arg.writeFloat(arg2.getRoll());
        }

        @Override
        public EulerAngle read(PacketByteBuf arg) {
            return new EulerAngle(arg.readFloat(), arg.readFloat(), arg.readFloat());
        }

        @Override
        public EulerAngle copy(EulerAngle arg) {
            return arg;
        }

        @Override
        public /* synthetic */ Object read(PacketByteBuf arg) {
            return this.read(arg);
        }
    };
    public static final TrackedDataHandler<BlockPos> BLOCK_POS = new TrackedDataHandler<BlockPos>(){

        @Override
        public void write(PacketByteBuf arg, BlockPos arg2) {
            arg.writeBlockPos(arg2);
        }

        @Override
        public BlockPos read(PacketByteBuf arg) {
            return arg.readBlockPos();
        }

        @Override
        public BlockPos copy(BlockPos arg) {
            return arg;
        }

        @Override
        public /* synthetic */ Object read(PacketByteBuf arg) {
            return this.read(arg);
        }
    };
    public static final TrackedDataHandler<Optional<BlockPos>> OPTIONA_BLOCK_POS = new TrackedDataHandler<Optional<BlockPos>>(){

        @Override
        public void write(PacketByteBuf arg, Optional<BlockPos> optional) {
            arg.writeBoolean(optional.isPresent());
            if (optional.isPresent()) {
                arg.writeBlockPos(optional.get());
            }
        }

        @Override
        public Optional<BlockPos> read(PacketByteBuf arg) {
            if (!arg.readBoolean()) {
                return Optional.empty();
            }
            return Optional.of(arg.readBlockPos());
        }

        @Override
        public Optional<BlockPos> copy(Optional<BlockPos> optional) {
            return optional;
        }

        @Override
        public /* synthetic */ Object read(PacketByteBuf arg) {
            return this.read(arg);
        }
    };
    public static final TrackedDataHandler<Direction> FACING = new TrackedDataHandler<Direction>(){

        @Override
        public void write(PacketByteBuf arg, Direction arg2) {
            arg.writeEnumConstant(arg2);
        }

        @Override
        public Direction read(PacketByteBuf arg) {
            return arg.readEnumConstant(Direction.class);
        }

        @Override
        public Direction copy(Direction arg) {
            return arg;
        }

        @Override
        public /* synthetic */ Object read(PacketByteBuf arg) {
            return this.read(arg);
        }
    };
    public static final TrackedDataHandler<Optional<UUID>> OPTIONAL_UUID = new TrackedDataHandler<Optional<UUID>>(){

        @Override
        public void write(PacketByteBuf arg, Optional<UUID> optional) {
            arg.writeBoolean(optional.isPresent());
            if (optional.isPresent()) {
                arg.writeUuid(optional.get());
            }
        }

        @Override
        public Optional<UUID> read(PacketByteBuf arg) {
            if (!arg.readBoolean()) {
                return Optional.empty();
            }
            return Optional.of(arg.readUuid());
        }

        @Override
        public Optional<UUID> copy(Optional<UUID> optional) {
            return optional;
        }

        @Override
        public /* synthetic */ Object read(PacketByteBuf arg) {
            return this.read(arg);
        }
    };
    public static final TrackedDataHandler<CompoundTag> TAG_COMPOUND = new TrackedDataHandler<CompoundTag>(){

        @Override
        public void write(PacketByteBuf arg, CompoundTag arg2) {
            arg.writeCompoundTag(arg2);
        }

        @Override
        public CompoundTag read(PacketByteBuf arg) {
            return arg.readCompoundTag();
        }

        @Override
        public CompoundTag copy(CompoundTag arg) {
            return arg.copy();
        }

        @Override
        public /* synthetic */ Object read(PacketByteBuf arg) {
            return this.read(arg);
        }
    };
    public static final TrackedDataHandler<VillagerData> VILLAGER_DATA = new TrackedDataHandler<VillagerData>(){

        @Override
        public void write(PacketByteBuf arg, VillagerData arg2) {
            arg.writeVarInt(Registry.VILLAGER_TYPE.getRawId(arg2.getType()));
            arg.writeVarInt(Registry.VILLAGER_PROFESSION.getRawId(arg2.getProfession()));
            arg.writeVarInt(arg2.getLevel());
        }

        @Override
        public VillagerData read(PacketByteBuf arg) {
            return new VillagerData(Registry.VILLAGER_TYPE.get(arg.readVarInt()), Registry.VILLAGER_PROFESSION.get(arg.readVarInt()), arg.readVarInt());
        }

        @Override
        public VillagerData copy(VillagerData arg) {
            return arg;
        }

        @Override
        public /* synthetic */ Object read(PacketByteBuf arg) {
            return this.read(arg);
        }
    };
    public static final TrackedDataHandler<OptionalInt> FIREWORK_DATA = new TrackedDataHandler<OptionalInt>(){

        @Override
        public void write(PacketByteBuf arg, OptionalInt optionalInt) {
            arg.writeVarInt(optionalInt.orElse(-1) + 1);
        }

        @Override
        public OptionalInt read(PacketByteBuf arg) {
            int i = arg.readVarInt();
            return i == 0 ? OptionalInt.empty() : OptionalInt.of(i - 1);
        }

        @Override
        public OptionalInt copy(OptionalInt optionalInt) {
            return optionalInt;
        }

        @Override
        public /* synthetic */ Object read(PacketByteBuf arg) {
            return this.read(arg);
        }
    };
    public static final TrackedDataHandler<EntityPose> ENTITY_POSE = new TrackedDataHandler<EntityPose>(){

        @Override
        public void write(PacketByteBuf arg, EntityPose arg2) {
            arg.writeEnumConstant(arg2);
        }

        @Override
        public EntityPose read(PacketByteBuf arg) {
            return arg.readEnumConstant(EntityPose.class);
        }

        @Override
        public EntityPose copy(EntityPose arg) {
            return arg;
        }

        @Override
        public /* synthetic */ Object read(PacketByteBuf arg) {
            return this.read(arg);
        }
    };

    public static void register(TrackedDataHandler<?> arg) {
        field_13328.add(arg);
    }

    @Nullable
    public static TrackedDataHandler<?> get(int i) {
        return field_13328.get(i);
    }

    public static int getId(TrackedDataHandler<?> arg) {
        return field_13328.getId(arg);
    }

    static {
        TrackedDataHandlerRegistry.register(BYTE);
        TrackedDataHandlerRegistry.register(INTEGER);
        TrackedDataHandlerRegistry.register(FLOAT);
        TrackedDataHandlerRegistry.register(STRING);
        TrackedDataHandlerRegistry.register(TEXT_COMPONENT);
        TrackedDataHandlerRegistry.register(OPTIONAL_TEXT_COMPONENT);
        TrackedDataHandlerRegistry.register(ITEM_STACK);
        TrackedDataHandlerRegistry.register(BOOLEAN);
        TrackedDataHandlerRegistry.register(ROTATION);
        TrackedDataHandlerRegistry.register(BLOCK_POS);
        TrackedDataHandlerRegistry.register(OPTIONA_BLOCK_POS);
        TrackedDataHandlerRegistry.register(FACING);
        TrackedDataHandlerRegistry.register(OPTIONAL_UUID);
        TrackedDataHandlerRegistry.register(OPTIONAL_BLOCK_STATE);
        TrackedDataHandlerRegistry.register(TAG_COMPOUND);
        TrackedDataHandlerRegistry.register(PARTICLE);
        TrackedDataHandlerRegistry.register(VILLAGER_DATA);
        TrackedDataHandlerRegistry.register(FIREWORK_DATA);
        TrackedDataHandlerRegistry.register(ENTITY_POSE);
    }
}

