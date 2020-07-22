/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.world.gen.tree;

import com.mojang.serialization.Codec;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.tree.AlterGroundTreeDecorator;
import net.minecraft.world.gen.tree.BeehiveTreeDecorator;
import net.minecraft.world.gen.tree.CocoaBeansTreeDecorator;
import net.minecraft.world.gen.tree.LeaveVineTreeDecorator;
import net.minecraft.world.gen.tree.TreeDecorator;
import net.minecraft.world.gen.tree.TrunkVineTreeDecorator;

public class TreeDecoratorType<P extends TreeDecorator> {
    public static final TreeDecoratorType<TrunkVineTreeDecorator> TRUNK_VINE = TreeDecoratorType.register("trunk_vine", TrunkVineTreeDecorator.CODEC);
    public static final TreeDecoratorType<LeaveVineTreeDecorator> LEAVE_VINE = TreeDecoratorType.register("leave_vine", LeaveVineTreeDecorator.CODEC);
    public static final TreeDecoratorType<CocoaBeansTreeDecorator> COCOA = TreeDecoratorType.register("cocoa", CocoaBeansTreeDecorator.CODEC);
    public static final TreeDecoratorType<BeehiveTreeDecorator> BEEHIVE = TreeDecoratorType.register("beehive", BeehiveTreeDecorator.CODEC);
    public static final TreeDecoratorType<AlterGroundTreeDecorator> ALTER_GROUND = TreeDecoratorType.register("alter_ground", AlterGroundTreeDecorator.CODEC);
    private final Codec<P> codec;

    private static <P extends TreeDecorator> TreeDecoratorType<P> register(String id, Codec<P> codec) {
        return Registry.register(Registry.TREE_DECORATOR_TYPE, id, new TreeDecoratorType<P>(codec));
    }

    private TreeDecoratorType(Codec<P> codec) {
        this.codec = codec;
    }

    public Codec<P> getCodec() {
        return this.codec;
    }
}

