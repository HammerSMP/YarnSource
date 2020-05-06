/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 */
package net.minecraft.block.enums;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Direction;

public enum JigsawOrientation implements StringIdentifiable
{
    DOWN_EAST("down_east", Direction.DOWN, Direction.EAST),
    DOWN_NORTH("down_north", Direction.DOWN, Direction.NORTH),
    DOWN_SOUTH("down_south", Direction.DOWN, Direction.SOUTH),
    DOWN_WEST("down_west", Direction.DOWN, Direction.WEST),
    UP_EAST("up_east", Direction.UP, Direction.EAST),
    UP_NORTH("up_north", Direction.UP, Direction.NORTH),
    UP_SOUTH("up_south", Direction.UP, Direction.SOUTH),
    UP_WEST("up_west", Direction.UP, Direction.WEST),
    WEST_UP("west_up", Direction.WEST, Direction.UP),
    EAST_UP("east_up", Direction.EAST, Direction.UP),
    NORTH_UP("north_up", Direction.NORTH, Direction.UP),
    SOUTH_UP("south_up", Direction.SOUTH, Direction.UP);

    private static final Int2ObjectMap<JigsawOrientation> BY_INDEX;
    private final String name;
    private final Direction field_23395;
    private final Direction field_23396;

    private static int getIndex(Direction arg, Direction arg2) {
        return arg.ordinal() << 3 | arg2.ordinal();
    }

    private JigsawOrientation(String string2, Direction arg, Direction arg2) {
        this.name = string2;
        this.field_23396 = arg;
        this.field_23395 = arg2;
    }

    @Override
    public String asString() {
        return this.name;
    }

    public static JigsawOrientation byDirections(Direction arg, Direction arg2) {
        int i = JigsawOrientation.getIndex(arg2, arg);
        return (JigsawOrientation)BY_INDEX.get(i);
    }

    public Direction method_26426() {
        return this.field_23396;
    }

    public Direction method_26428() {
        return this.field_23395;
    }

    static {
        BY_INDEX = new Int2ObjectOpenHashMap(JigsawOrientation.values().length);
        for (JigsawOrientation lv : JigsawOrientation.values()) {
            BY_INDEX.put(JigsawOrientation.getIndex(lv.field_23395, lv.field_23396), (Object)lv);
        }
    }
}

