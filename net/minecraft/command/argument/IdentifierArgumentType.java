/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 */
package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.advancement.Advancement;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionManager;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class IdentifierArgumentType
implements ArgumentType<Identifier> {
    private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012");
    private static final DynamicCommandExceptionType UNKNOWN_ADVANCEMENT_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("advancement.advancementNotFound", object));
    private static final DynamicCommandExceptionType UNKNOWN_RECIPE_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("recipe.notFound", object));
    private static final DynamicCommandExceptionType field_21506 = new DynamicCommandExceptionType(object -> new TranslatableText("predicate.unknown", object));
    private static final DynamicCommandExceptionType field_24267 = new DynamicCommandExceptionType(object -> new TranslatableText("attribute.unknown", object));

    public static IdentifierArgumentType identifier() {
        return new IdentifierArgumentType();
    }

    public static Advancement getAdvancementArgument(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
        Identifier lv = (Identifier)commandContext.getArgument(string, Identifier.class);
        Advancement lv2 = ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getAdvancementLoader().get(lv);
        if (lv2 == null) {
            throw UNKNOWN_ADVANCEMENT_EXCEPTION.create((Object)lv);
        }
        return lv2;
    }

    public static Recipe<?> getRecipeArgument(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
        RecipeManager lv = ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getRecipeManager();
        Identifier lv2 = (Identifier)commandContext.getArgument(string, Identifier.class);
        return lv.get(lv2).orElseThrow(() -> UNKNOWN_RECIPE_EXCEPTION.create((Object)lv2));
    }

    public static LootCondition method_23727(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
        Identifier lv = (Identifier)commandContext.getArgument(string, Identifier.class);
        LootConditionManager lv2 = ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getPredicateManager();
        LootCondition lv3 = lv2.get(lv);
        if (lv3 == null) {
            throw field_21506.create((Object)lv);
        }
        return lv3;
    }

    public static EntityAttribute method_27575(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
        Identifier lv = (Identifier)commandContext.getArgument(string, Identifier.class);
        return Registry.ATTRIBUTE.getOrEmpty(lv).orElseThrow(() -> field_24267.create((Object)lv));
    }

    public static Identifier getIdentifier(CommandContext<ServerCommandSource> context, String name) {
        return (Identifier)context.getArgument(name, Identifier.class);
    }

    public Identifier parse(StringReader stringReader) throws CommandSyntaxException {
        return Identifier.fromCommandInput(stringReader);
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
        return this.parse(stringReader);
    }
}

