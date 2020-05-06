/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.data.client.model;

import java.util.Optional;
import java.util.stream.IntStream;
import net.minecraft.data.client.model.Model;
import net.minecraft.data.client.model.TextureKey;
import net.minecraft.util.Identifier;

public class Models {
    public static final Model CUBE = Models.block("cube", TextureKey.PARTICLE, TextureKey.NORTH, TextureKey.SOUTH, TextureKey.EAST, TextureKey.WEST, TextureKey.UP, TextureKey.DOWN);
    public static final Model CUBE_DIRECTIONAL = Models.block("cube_directional", TextureKey.PARTICLE, TextureKey.NORTH, TextureKey.SOUTH, TextureKey.EAST, TextureKey.WEST, TextureKey.UP, TextureKey.DOWN);
    public static final Model CUBE_ALL = Models.block("cube_all", TextureKey.ALL);
    public static final Model CUBE_MIRRORED_ALL = Models.block("cube_mirrored_all", "_mirrored", TextureKey.ALL);
    public static final Model CUBE_COLUMN = Models.block("cube_column", TextureKey.END, TextureKey.SIDE);
    public static final Model CUBE_COLUMN_HORIZONTAL = Models.block("cube_column_horizontal", "_horizontal", TextureKey.END, TextureKey.SIDE);
    public static final Model CUBE_TOP = Models.block("cube_top", TextureKey.TOP, TextureKey.SIDE);
    public static final Model CUBE_BOTTOM_TOP = Models.block("cube_bottom_top", TextureKey.TOP, TextureKey.BOTTOM, TextureKey.SIDE);
    public static final Model ORIENTABLE = Models.block("orientable", TextureKey.TOP, TextureKey.FRONT, TextureKey.SIDE);
    public static final Model ORIENTABLE_WITH_BOTTOM = Models.block("orientable_with_bottom", TextureKey.TOP, TextureKey.BOTTOM, TextureKey.SIDE, TextureKey.FRONT);
    public static final Model ORIENTABLE_VERTICAL = Models.block("orientable_vertical", "_vertical", TextureKey.FRONT, TextureKey.SIDE);
    public static final Model BUTTON = Models.block("button", TextureKey.TEXTURE);
    public static final Model BUTTON_PRESSED = Models.block("button_pressed", "_pressed", TextureKey.TEXTURE);
    public static final Model BUTTON_INVENTORY = Models.block("button_inventory", "_inventory", TextureKey.TEXTURE);
    public static final Model DOOR_BOTTOM = Models.block("door_bottom", "_bottom", TextureKey.TOP, TextureKey.BOTTOM);
    public static final Model DOOR_BOTTOM_RH = Models.block("door_bottom_rh", "_bottom_hinge", TextureKey.TOP, TextureKey.BOTTOM);
    public static final Model DOOR_TOP = Models.block("door_top", "_top", TextureKey.TOP, TextureKey.BOTTOM);
    public static final Model DOOR_TOP_RH = Models.block("door_top_rh", "_top_hinge", TextureKey.TOP, TextureKey.BOTTOM);
    public static final Model FENCE_POST = Models.block("fence_post", "_post", TextureKey.TEXTURE);
    public static final Model FENCE_SIDE = Models.block("fence_side", "_side", TextureKey.TEXTURE);
    public static final Model FENCE_INVENTORY = Models.block("fence_inventory", "_inventory", TextureKey.TEXTURE);
    public static final Model TEMPLATE_WALL_POST = Models.block("template_wall_post", "_post", TextureKey.WALL);
    public static final Model TEMPLATE_WALL_SIDE = Models.block("template_wall_side", "_side", TextureKey.WALL);
    public static final Model TEMPLATE_WALL_SIDE_TALL = Models.block("template_wall_side_tall", "_side_tall", TextureKey.WALL);
    public static final Model WALL_INVENTORY = Models.block("wall_inventory", "_inventory", TextureKey.WALL);
    public static final Model TEMPLATE_FENCE_GATE = Models.block("template_fence_gate", TextureKey.TEXTURE);
    public static final Model TEMPLATE_FENCE_GATE_OPEN = Models.block("template_fence_gate_open", "_open", TextureKey.TEXTURE);
    public static final Model TEMPLATE_FENCE_GATE_WALL = Models.block("template_fence_gate_wall", "_wall", TextureKey.TEXTURE);
    public static final Model TEMPLATE_FENCE_GATE_WALL_OPEN = Models.block("template_fence_gate_wall_open", "_wall_open", TextureKey.TEXTURE);
    public static final Model PRESSURE_PLATE_UP = Models.block("pressure_plate_up", TextureKey.TEXTURE);
    public static final Model PRESSURE_PLATE_DOWN = Models.block("pressure_plate_down", "_down", TextureKey.TEXTURE);
    public static final Model PARTICLE = Models.make(TextureKey.PARTICLE);
    public static final Model SLAB = Models.block("slab", TextureKey.BOTTOM, TextureKey.TOP, TextureKey.SIDE);
    public static final Model SLAB_TOP = Models.block("slab_top", "_top", TextureKey.BOTTOM, TextureKey.TOP, TextureKey.SIDE);
    public static final Model LEAVES = Models.block("leaves", TextureKey.ALL);
    public static final Model STAIRS = Models.block("stairs", TextureKey.BOTTOM, TextureKey.TOP, TextureKey.SIDE);
    public static final Model INNER_STAIRS = Models.block("inner_stairs", "_inner", TextureKey.BOTTOM, TextureKey.TOP, TextureKey.SIDE);
    public static final Model OUTER_STAIRS = Models.block("outer_stairs", "_outer", TextureKey.BOTTOM, TextureKey.TOP, TextureKey.SIDE);
    public static final Model TEMPLATE_TRAPDOOR_TOP = Models.block("template_trapdoor_top", "_top", TextureKey.TEXTURE);
    public static final Model TEMPLATE_TRAPDOOR_BOTTOM = Models.block("template_trapdoor_bottom", "_bottom", TextureKey.TEXTURE);
    public static final Model TEMPLATE_TRAPDOOR_OPEN = Models.block("template_trapdoor_open", "_open", TextureKey.TEXTURE);
    public static final Model TEMPLATE_ORIENTABLE_TRAPDOOR_TOP = Models.block("template_orientable_trapdoor_top", "_top", TextureKey.TEXTURE);
    public static final Model TEMPLATE_ORIENTABLE_TRAPDOOR_BOTTOM = Models.block("template_orientable_trapdoor_bottom", "_bottom", TextureKey.TEXTURE);
    public static final Model TEMPLATE_ORIENTABLE_TRAPDOOR_OPEN = Models.block("template_orientable_trapdoor_open", "_open", TextureKey.TEXTURE);
    public static final Model CROSS = Models.block("cross", TextureKey.CROSS);
    public static final Model TINTED_CROSS = Models.block("tinted_cross", TextureKey.CROSS);
    public static final Model FLOWER_POT_CROSS = Models.block("flower_pot_cross", TextureKey.PLANT);
    public static final Model TINTED_FLOWER_POT_CROSS = Models.block("tinted_flower_pot_cross", TextureKey.PLANT);
    public static final Model RAIL_FLAT = Models.block("rail_flat", TextureKey.RAIL);
    public static final Model RAIL_CURVED = Models.block("rail_curved", "_corner", TextureKey.RAIL);
    public static final Model TEMPLATE_RAIL_RAISED_NE = Models.block("template_rail_raised_ne", "_raised_ne", TextureKey.RAIL);
    public static final Model TEMPLATE_RAIL_RAISED_SW = Models.block("template_rail_raised_sw", "_raised_sw", TextureKey.RAIL);
    public static final Model CARPET = Models.block("carpet", TextureKey.WOOL);
    public static final Model CORAL_FAN = Models.block("coral_fan", TextureKey.FAN);
    public static final Model CORAL_WALL_FAN = Models.block("coral_wall_fan", TextureKey.FAN);
    public static final Model TEMPLATE_GLAZED_TERRACOTTA = Models.block("template_glazed_terracotta", TextureKey.PATTERN);
    public static final Model TEMPLATE_CHORUS_FLOWER = Models.block("template_chorus_flower", TextureKey.TEXTURE);
    public static final Model TEMPLATE_DAYLIGHT_DETECTOR = Models.block("template_daylight_detector", TextureKey.TOP, TextureKey.SIDE);
    public static final Model TEMPLATE_GLASS_PANE_NOSIDE = Models.block("template_glass_pane_noside", "_noside", TextureKey.PANE);
    public static final Model TEMPLATE_GLASS_PANE_NOSIDE_ALT = Models.block("template_glass_pane_noside_alt", "_noside_alt", TextureKey.PANE);
    public static final Model TEMPLATE_GLASS_PANE_POST = Models.block("template_glass_pane_post", "_post", TextureKey.PANE, TextureKey.EDGE);
    public static final Model TEMPLATE_GLASS_PANE_SIDE = Models.block("template_glass_pane_side", "_side", TextureKey.PANE, TextureKey.EDGE);
    public static final Model TEMPLATE_GLASS_PANE_SIDE_ALT = Models.block("template_glass_pane_side_alt", "_side_alt", TextureKey.PANE, TextureKey.EDGE);
    public static final Model TEMPLATE_COMMAND_BLOCK = Models.block("template_command_block", TextureKey.FRONT, TextureKey.BACK, TextureKey.SIDE);
    public static final Model TEMPLATE_ANVIL = Models.block("template_anvil", TextureKey.TOP);
    public static final Model[] STEM_GROWTH_STAGES = (Model[])IntStream.range(0, 8).mapToObj(i -> Models.block("stem_growth" + i, "_stage" + i, TextureKey.STEM)).toArray(Model[]::new);
    public static final Model STEM_FRUIT = Models.block("stem_fruit", TextureKey.STEM, TextureKey.UPPERSTEM);
    public static final Model CROP = Models.block("crop", TextureKey.CROP);
    public static final Model TEMPLATE_FARMLAND = Models.block("template_farmland", TextureKey.DIRT, TextureKey.TOP);
    public static final Model TEMPLATE_FIRE_FLOOR = Models.block("template_fire_floor", TextureKey.FIRE);
    public static final Model TEMPLATE_FIRE_SIDE = Models.block("template_fire_side", TextureKey.FIRE);
    public static final Model TEMPLATE_FIRE_SIDE_ALT = Models.block("template_fire_side_alt", TextureKey.FIRE);
    public static final Model TEMPLATE_FIRE_UP = Models.block("template_fire_up", TextureKey.FIRE);
    public static final Model TEMPLATE_FIRE_UP_ALT = Models.block("template_fire_up_alt", TextureKey.FIRE);
    public static final Model TEMPLATE_CAMPFIRE = Models.block("template_campfire", TextureKey.FIRE, TextureKey.LIT_LOG);
    public static final Model TEMPLATE_LANTERN = Models.block("template_lantern", TextureKey.LANTERN);
    public static final Model TEMPLATE_HANGING_LANTERN = Models.block("template_hanging_lantern", "_hanging", TextureKey.LANTERN);
    public static final Model TEMPLATE_TORCH = Models.block("template_torch", TextureKey.TORCH);
    public static final Model TEMPLATE_TORCH_WALL = Models.block("template_torch_wall", TextureKey.TORCH);
    public static final Model TEMPLATE_PISTON = Models.block("template_piston", TextureKey.PLATFORM, TextureKey.BOTTOM, TextureKey.SIDE);
    public static final Model TEMPLATE_PISTON_HEAD = Models.block("template_piston_head", TextureKey.PLATFORM, TextureKey.SIDE, TextureKey.UNSTICKY);
    public static final Model TEMPLATE_PISTON_HEAD_SHORT = Models.block("template_piston_head_short", TextureKey.PLATFORM, TextureKey.SIDE, TextureKey.UNSTICKY);
    public static final Model TEMPLATE_SEAGRASS = Models.block("template_seagrass", TextureKey.TEXTURE);
    public static final Model TEMPLATE_TURTLE_EGG = Models.block("template_turtle_egg", TextureKey.ALL);
    public static final Model TEMPLATE_TWO_TURTLE_EGGS = Models.block("template_two_turtle_eggs", TextureKey.ALL);
    public static final Model TEMPLATE_THREE_TURTLE_EGGS = Models.block("template_three_turtle_eggs", TextureKey.ALL);
    public static final Model TEMPLATE_FOUR_TURTLE_EGGS = Models.block("template_four_turtle_eggs", TextureKey.ALL);
    public static final Model TEMPLATE_SINGLE_FACE = Models.block("template_single_face", TextureKey.TEXTURE);
    public static final Model GENERATED = Models.item("generated", TextureKey.LAYER0);
    public static final Model HANDHELD = Models.item("handheld", TextureKey.LAYER0);
    public static final Model HANDHELD_ROD = Models.item("handheld_rod", TextureKey.LAYER0);
    public static final Model TEMPLATE_SHULKER_BOX = Models.item("template_shulker_box", TextureKey.PARTICLE);
    public static final Model TEMPLATE_BED = Models.item("template_bed", TextureKey.PARTICLE);
    public static final Model TEMPLATE_BANNER = Models.item("template_banner", new TextureKey[0]);
    public static final Model TEMPLATE_SKULL = Models.item("template_skull", new TextureKey[0]);

    private static Model make(TextureKey ... args) {
        return new Model(Optional.empty(), Optional.empty(), args);
    }

    private static Model block(String string, TextureKey ... args) {
        return new Model(Optional.of(new Identifier("minecraft", "block/" + string)), Optional.empty(), args);
    }

    private static Model item(String string, TextureKey ... args) {
        return new Model(Optional.of(new Identifier("minecraft", "item/" + string)), Optional.empty(), args);
    }

    private static Model block(String string, String string2, TextureKey ... args) {
        return new Model(Optional.of(new Identifier("minecraft", "block/" + string)), Optional.of(string2), args);
    }
}

