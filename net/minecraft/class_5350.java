/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.class_5349;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.condition.LootConditionManager;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.server.command.CommandManager;
import net.minecraft.tag.RegistryTagManager;
import net.minecraft.util.Unit;

public class class_5350 {
    private static final CompletableFuture<Unit> field_25334 = CompletableFuture.completedFuture(Unit.INSTANCE);
    private final ReloadableResourceManager field_25335 = new ReloadableResourceManagerImpl(ResourceType.SERVER_DATA);
    private final CommandManager field_25336;
    private final RecipeManager field_25337 = new RecipeManager();
    private final RegistryTagManager field_25338 = new RegistryTagManager();
    private final LootConditionManager field_25339 = new LootConditionManager();
    private final LootManager field_25340 = new LootManager(this.field_25339);
    private final ServerAdvancementLoader field_25341 = new ServerAdvancementLoader(this.field_25339);
    private final class_5349 field_25342;

    public class_5350(boolean bl, int i) {
        this.field_25336 = new CommandManager(bl);
        this.field_25342 = new class_5349(i, this.field_25336.getDispatcher());
        this.field_25335.registerListener(this.field_25338);
        this.field_25335.registerListener(this.field_25339);
        this.field_25335.registerListener(this.field_25337);
        this.field_25335.registerListener(this.field_25340);
        this.field_25335.registerListener(this.field_25342);
        this.field_25335.registerListener(this.field_25341);
    }

    public class_5349 method_29465() {
        return this.field_25342;
    }

    public LootConditionManager method_29468() {
        return this.field_25339;
    }

    public LootManager method_29469() {
        return this.field_25340;
    }

    public RegistryTagManager method_29470() {
        return this.field_25338;
    }

    public RecipeManager method_29471() {
        return this.field_25337;
    }

    public CommandManager method_29472() {
        return this.field_25336;
    }

    public ServerAdvancementLoader method_29473() {
        return this.field_25341;
    }

    public ResourceManager method_29474() {
        return this.field_25335;
    }

    public static CompletableFuture<class_5350> method_29466(List<ResourcePack> list, boolean bl, int i, Executor executor, Executor executor2) {
        class_5350 lv = new class_5350(bl, i);
        CompletableFuture<Unit> completableFuture = lv.field_25335.beginReload(executor, executor2, list, field_25334);
        return completableFuture.thenApply(arg2 -> lv);
    }

    public void method_29475() {
        this.field_25338.method_29226();
    }
}

