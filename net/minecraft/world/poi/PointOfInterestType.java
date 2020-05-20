/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Maps
 */
package net.minecraft.world.poi;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.BedPart;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.VillagerProfession;

public class PointOfInterestType {
    private static final Predicate<PointOfInterestType> IS_USED_BY_PROFESSION = arg -> Registry.VILLAGER_PROFESSION.stream().map(VillagerProfession::getWorkStation).collect(Collectors.toSet()).contains(arg);
    public static final Predicate<PointOfInterestType> ALWAYS_TRUE = arg -> true;
    private static final Set<BlockState> BED_STATES = (Set)ImmutableList.of((Object)Blocks.RED_BED, (Object)Blocks.BLACK_BED, (Object)Blocks.BLUE_BED, (Object)Blocks.BROWN_BED, (Object)Blocks.CYAN_BED, (Object)Blocks.GRAY_BED, (Object)Blocks.GREEN_BED, (Object)Blocks.LIGHT_BLUE_BED, (Object)Blocks.LIGHT_GRAY_BED, (Object)Blocks.LIME_BED, (Object)Blocks.MAGENTA_BED, (Object)Blocks.ORANGE_BED, (Object[])new Block[]{Blocks.PINK_BED, Blocks.PURPLE_BED, Blocks.WHITE_BED, Blocks.YELLOW_BED}).stream().flatMap(arg -> arg.getStateManager().getStates().stream()).filter(arg -> arg.get(BedBlock.PART) == BedPart.HEAD).collect(ImmutableSet.toImmutableSet());
    private static final Map<BlockState, PointOfInterestType> BLOCK_STATE_TO_POINT_OF_INTEREST_TYPE = Maps.newHashMap();
    public static final PointOfInterestType UNEMPLOYED = PointOfInterestType.register("unemployed", (Set<BlockState>)ImmutableSet.of(), 1, IS_USED_BY_PROFESSION, 1);
    public static final PointOfInterestType ARMORER = PointOfInterestType.register("armorer", PointOfInterestType.getAllStatesOf(Blocks.BLAST_FURNACE), 1, 1);
    public static final PointOfInterestType BUTCHER = PointOfInterestType.register("butcher", PointOfInterestType.getAllStatesOf(Blocks.SMOKER), 1, 1);
    public static final PointOfInterestType CARTOGRAPHER = PointOfInterestType.register("cartographer", PointOfInterestType.getAllStatesOf(Blocks.CARTOGRAPHY_TABLE), 1, 1);
    public static final PointOfInterestType CLERIC = PointOfInterestType.register("cleric", PointOfInterestType.getAllStatesOf(Blocks.BREWING_STAND), 1, 1);
    public static final PointOfInterestType FARMER = PointOfInterestType.register("farmer", PointOfInterestType.getAllStatesOf(Blocks.COMPOSTER), 1, 1);
    public static final PointOfInterestType FISHERMAN = PointOfInterestType.register("fisherman", PointOfInterestType.getAllStatesOf(Blocks.BARREL), 1, 1);
    public static final PointOfInterestType FLETCHER = PointOfInterestType.register("fletcher", PointOfInterestType.getAllStatesOf(Blocks.FLETCHING_TABLE), 1, 1);
    public static final PointOfInterestType LEATHERWORKER = PointOfInterestType.register("leatherworker", PointOfInterestType.getAllStatesOf(Blocks.CAULDRON), 1, 1);
    public static final PointOfInterestType LIBRARIAN = PointOfInterestType.register("librarian", PointOfInterestType.getAllStatesOf(Blocks.LECTERN), 1, 1);
    public static final PointOfInterestType MASON = PointOfInterestType.register("mason", PointOfInterestType.getAllStatesOf(Blocks.STONECUTTER), 1, 1);
    public static final PointOfInterestType NITWIT = PointOfInterestType.register("nitwit", (Set<BlockState>)ImmutableSet.of(), 1, 1);
    public static final PointOfInterestType SHEPHERD = PointOfInterestType.register("shepherd", PointOfInterestType.getAllStatesOf(Blocks.LOOM), 1, 1);
    public static final PointOfInterestType TOOLSMITH = PointOfInterestType.register("toolsmith", PointOfInterestType.getAllStatesOf(Blocks.SMITHING_TABLE), 1, 1);
    public static final PointOfInterestType WEAPONSMITH = PointOfInterestType.register("weaponsmith", PointOfInterestType.getAllStatesOf(Blocks.GRINDSTONE), 1, 1);
    public static final PointOfInterestType HOME = PointOfInterestType.register("home", BED_STATES, 1, 1);
    public static final PointOfInterestType MEETING = PointOfInterestType.register("meeting", PointOfInterestType.getAllStatesOf(Blocks.BELL), 32, 6);
    public static final PointOfInterestType BEEHIVE = PointOfInterestType.register("beehive", PointOfInterestType.getAllStatesOf(Blocks.BEEHIVE), 0, 1);
    public static final PointOfInterestType BEE_NEST = PointOfInterestType.register("bee_nest", PointOfInterestType.getAllStatesOf(Blocks.BEE_NEST), 0, 1);
    public static final PointOfInterestType NETHER_PORTAL = PointOfInterestType.register("nether_portal", PointOfInterestType.getAllStatesOf(Blocks.NETHER_PORTAL), 0, 1);
    public static final PointOfInterestType LODESTONE = PointOfInterestType.register("lodestone", PointOfInterestType.getAllStatesOf(Blocks.LODESTONE), 0, 1);
    private final String id;
    private final Set<BlockState> blockStates;
    private final int ticketCount;
    private final Predicate<PointOfInterestType> completionCondition;
    private final int searchDistance;

    private static Set<BlockState> getAllStatesOf(Block arg) {
        return ImmutableSet.copyOf(arg.getStateManager().getStates());
    }

    private PointOfInterestType(String string, Set<BlockState> set, int i, Predicate<PointOfInterestType> predicate, int j) {
        this.id = string;
        this.blockStates = ImmutableSet.copyOf(set);
        this.ticketCount = i;
        this.completionCondition = predicate;
        this.searchDistance = j;
    }

    private PointOfInterestType(String string, Set<BlockState> set, int i, int j) {
        this.id = string;
        this.blockStates = ImmutableSet.copyOf(set);
        this.ticketCount = i;
        this.completionCondition = arg -> arg == this;
        this.searchDistance = j;
    }

    public int getTicketCount() {
        return this.ticketCount;
    }

    public Predicate<PointOfInterestType> getCompletionCondition() {
        return this.completionCondition;
    }

    public int getSearchDistance() {
        return this.searchDistance;
    }

    public String toString() {
        return this.id;
    }

    private static PointOfInterestType register(String string, Set<BlockState> set, int i, int j) {
        return PointOfInterestType.setup(Registry.POINT_OF_INTEREST_TYPE.add(new Identifier(string), new PointOfInterestType(string, set, i, j)));
    }

    private static PointOfInterestType register(String string, Set<BlockState> set, int i, Predicate<PointOfInterestType> predicate, int j) {
        return PointOfInterestType.setup(Registry.POINT_OF_INTEREST_TYPE.add(new Identifier(string), new PointOfInterestType(string, set, i, predicate, j)));
    }

    private static PointOfInterestType setup(PointOfInterestType arg) {
        arg.blockStates.forEach(arg2 -> {
            PointOfInterestType lv = BLOCK_STATE_TO_POINT_OF_INTEREST_TYPE.put((BlockState)arg2, arg);
            if (lv != null) {
                throw Util.throwOrPause(new IllegalStateException(String.format("%s is defined in too many tags", arg2)));
            }
        });
        return arg;
    }

    public static Optional<PointOfInterestType> from(BlockState arg) {
        return Optional.ofNullable(BLOCK_STATE_TO_POINT_OF_INTEREST_TYPE.get(arg));
    }

    public static Stream<BlockState> getAllAssociatedStates() {
        return BLOCK_STATE_TO_POINT_OF_INTEREST_TYPE.keySet().stream();
    }
}
