/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.arguments.DoubleArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
 *  com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.UUID;
import net.minecraft.class_5242;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.command.arguments.IdentifierArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.registry.Registry;

public class AttributeCommand {
    private static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER = (commandContext, suggestionsBuilder) -> CommandSource.suggestIdentifiers(Registry.ATTRIBUTES.getIds(), suggestionsBuilder);
    private static final DynamicCommandExceptionType ENTITY_FAILED_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("commands.attribute.failed.entity", object));
    private static final Dynamic2CommandExceptionType NO_ATTRIBUTE_EXCEPTION = new Dynamic2CommandExceptionType((object, object2) -> new TranslatableText("commands.attribute.failed.no_attribute", object, object2));
    private static final Dynamic3CommandExceptionType NO_MODIFIER_EXCEPTION = new Dynamic3CommandExceptionType((object, object2, object3) -> new TranslatableText("commands.attribute.failed.no_modifier", object2, object, object3));
    private static final Dynamic3CommandExceptionType MODIFIER_ALREADY_PRESENT_EXCEPTION = new Dynamic3CommandExceptionType((object, object2, object3) -> new TranslatableText("commands.attribute.failed.modifier_already_present", object3, object2, object));

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("attribute").requires(arg -> arg.hasPermissionLevel(2))).then(CommandManager.argument("target", EntityArgumentType.entity()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("attribute", IdentifierArgumentType.identifier()).suggests(SUGGESTION_PROVIDER).then(((LiteralArgumentBuilder)CommandManager.literal("get").executes(commandContext -> AttributeCommand.executeValueGet((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntity((CommandContext<ServerCommandSource>)commandContext, "target"), IdentifierArgumentType.method_27575((CommandContext<ServerCommandSource>)commandContext, "attribute"), 1.0))).then(CommandManager.argument("scale", DoubleArgumentType.doubleArg()).executes(commandContext -> AttributeCommand.executeValueGet((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntity((CommandContext<ServerCommandSource>)commandContext, "target"), IdentifierArgumentType.method_27575((CommandContext<ServerCommandSource>)commandContext, "attribute"), DoubleArgumentType.getDouble((CommandContext)commandContext, (String)"scale")))))).then(((LiteralArgumentBuilder)CommandManager.literal("base").then(CommandManager.literal("set").then(CommandManager.argument("value", DoubleArgumentType.doubleArg()).executes(commandContext -> AttributeCommand.executeBaseValueSet((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntity((CommandContext<ServerCommandSource>)commandContext, "target"), IdentifierArgumentType.method_27575((CommandContext<ServerCommandSource>)commandContext, "attribute"), DoubleArgumentType.getDouble((CommandContext)commandContext, (String)"value")))))).then(((LiteralArgumentBuilder)CommandManager.literal("get").executes(commandContext -> AttributeCommand.executeBaseValueGet((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntity((CommandContext<ServerCommandSource>)commandContext, "target"), IdentifierArgumentType.method_27575((CommandContext<ServerCommandSource>)commandContext, "attribute"), 1.0))).then(CommandManager.argument("scale", DoubleArgumentType.doubleArg()).executes(commandContext -> AttributeCommand.executeBaseValueGet((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntity((CommandContext<ServerCommandSource>)commandContext, "target"), IdentifierArgumentType.method_27575((CommandContext<ServerCommandSource>)commandContext, "attribute"), DoubleArgumentType.getDouble((CommandContext)commandContext, (String)"scale"))))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("modifier").then(CommandManager.literal("add").then(CommandManager.argument("uuid", class_5242.method_27643()).then(CommandManager.argument("name", StringArgumentType.string()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("value", DoubleArgumentType.doubleArg()).then(CommandManager.literal("add").executes(commandContext -> AttributeCommand.executeModifierAdd((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntity((CommandContext<ServerCommandSource>)commandContext, "target"), IdentifierArgumentType.method_27575((CommandContext<ServerCommandSource>)commandContext, "attribute"), class_5242.method_27645((CommandContext<ServerCommandSource>)commandContext, "uuid"), StringArgumentType.getString((CommandContext)commandContext, (String)"name"), DoubleArgumentType.getDouble((CommandContext)commandContext, (String)"value"), EntityAttributeModifier.Operation.ADDITION)))).then(CommandManager.literal("multiply").executes(commandContext -> AttributeCommand.executeModifierAdd((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntity((CommandContext<ServerCommandSource>)commandContext, "target"), IdentifierArgumentType.method_27575((CommandContext<ServerCommandSource>)commandContext, "attribute"), class_5242.method_27645((CommandContext<ServerCommandSource>)commandContext, "uuid"), StringArgumentType.getString((CommandContext)commandContext, (String)"name"), DoubleArgumentType.getDouble((CommandContext)commandContext, (String)"value"), EntityAttributeModifier.Operation.MULTIPLY_TOTAL)))).then(CommandManager.literal("multiply_base").executes(commandContext -> AttributeCommand.executeModifierAdd((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntity((CommandContext<ServerCommandSource>)commandContext, "target"), IdentifierArgumentType.method_27575((CommandContext<ServerCommandSource>)commandContext, "attribute"), class_5242.method_27645((CommandContext<ServerCommandSource>)commandContext, "uuid"), StringArgumentType.getString((CommandContext)commandContext, (String)"name"), DoubleArgumentType.getDouble((CommandContext)commandContext, (String)"value"), EntityAttributeModifier.Operation.MULTIPLY_BASE)))))))).then(CommandManager.literal("remove").then(CommandManager.argument("uuid", class_5242.method_27643()).executes(commandContext -> AttributeCommand.executeModifierRemove((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntity((CommandContext<ServerCommandSource>)commandContext, "target"), IdentifierArgumentType.method_27575((CommandContext<ServerCommandSource>)commandContext, "attribute"), class_5242.method_27645((CommandContext<ServerCommandSource>)commandContext, "uuid")))))).then(CommandManager.literal("value").then(CommandManager.literal("get").then(((RequiredArgumentBuilder)CommandManager.argument("uuid", class_5242.method_27643()).executes(commandContext -> AttributeCommand.executeModifierValueGet((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntity((CommandContext<ServerCommandSource>)commandContext, "target"), IdentifierArgumentType.method_27575((CommandContext<ServerCommandSource>)commandContext, "attribute"), class_5242.method_27645((CommandContext<ServerCommandSource>)commandContext, "uuid"), 1.0))).then(CommandManager.argument("scale", DoubleArgumentType.doubleArg()).executes(commandContext -> AttributeCommand.executeModifierValueGet((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntity((CommandContext<ServerCommandSource>)commandContext, "target"), IdentifierArgumentType.method_27575((CommandContext<ServerCommandSource>)commandContext, "attribute"), class_5242.method_27645((CommandContext<ServerCommandSource>)commandContext, "uuid"), DoubleArgumentType.getDouble((CommandContext)commandContext, (String)"scale")))))))))));
    }

    private static EntityAttributeInstance getAttributeInstance(Entity arg, EntityAttribute arg2) throws CommandSyntaxException {
        EntityAttributeInstance lv = AttributeCommand.getLivingEntity(arg).getAttributes().getCustomInstance(arg2);
        if (lv == null) {
            throw NO_ATTRIBUTE_EXCEPTION.create((Object)arg.getName(), (Object)new TranslatableText(arg2.getTranslationKey()));
        }
        return lv;
    }

    private static LivingEntity getLivingEntity(Entity arg) throws CommandSyntaxException {
        if (!(arg instanceof LivingEntity)) {
            throw ENTITY_FAILED_EXCEPTION.create((Object)arg.getName());
        }
        return (LivingEntity)arg;
    }

    private static LivingEntity getLivingEntityWithAttribute(Entity arg, EntityAttribute arg2) throws CommandSyntaxException {
        LivingEntity lv = AttributeCommand.getLivingEntity(arg);
        if (!lv.getAttributes().hasAttribute(arg2)) {
            throw NO_ATTRIBUTE_EXCEPTION.create((Object)arg.getName(), (Object)new TranslatableText(arg2.getTranslationKey()));
        }
        return lv;
    }

    private static int executeValueGet(ServerCommandSource arg, Entity arg2, EntityAttribute arg3, double d) throws CommandSyntaxException {
        LivingEntity lv = AttributeCommand.getLivingEntityWithAttribute(arg2, arg3);
        double e = lv.getAttributeValue(arg3);
        arg.sendFeedback(new TranslatableText("commands.attribute.value.get.success", new TranslatableText(arg3.getTranslationKey()), arg2.getName(), e), false);
        return (int)(e * d);
    }

    private static int executeBaseValueGet(ServerCommandSource arg, Entity arg2, EntityAttribute arg3, double d) throws CommandSyntaxException {
        LivingEntity lv = AttributeCommand.getLivingEntityWithAttribute(arg2, arg3);
        double e = lv.getAttributeBaseValue(arg3);
        arg.sendFeedback(new TranslatableText("commands.attribute.base_value.get.success", new TranslatableText(arg3.getTranslationKey()), arg2.getName(), e), false);
        return (int)(e * d);
    }

    private static int executeModifierValueGet(ServerCommandSource arg, Entity arg2, EntityAttribute arg3, UUID uUID, double d) throws CommandSyntaxException {
        LivingEntity lv = AttributeCommand.getLivingEntityWithAttribute(arg2, arg3);
        AttributeContainer lv2 = lv.getAttributes();
        if (!lv2.hasModifierForAttribute(arg3, uUID)) {
            throw NO_MODIFIER_EXCEPTION.create((Object)arg2.getName(), (Object)new TranslatableText(arg3.getTranslationKey()), (Object)uUID);
        }
        double e = lv2.getModifierValue(arg3, uUID);
        arg.sendFeedback(new TranslatableText("commands.attribute.modifier.value.get.success", uUID, new TranslatableText(arg3.getTranslationKey()), arg2.getName(), e), false);
        return (int)(e * d);
    }

    private static int executeBaseValueSet(ServerCommandSource arg, Entity arg2, EntityAttribute arg3, double d) throws CommandSyntaxException {
        AttributeCommand.getAttributeInstance(arg2, arg3).setBaseValue(d);
        arg.sendFeedback(new TranslatableText("commands.attribute.base_value.set.success", new TranslatableText(arg3.getTranslationKey()), arg2.getName(), d), false);
        return 1;
    }

    private static int executeModifierAdd(ServerCommandSource arg, Entity arg2, EntityAttribute arg3, UUID uUID, String string, double d, EntityAttributeModifier.Operation arg4) throws CommandSyntaxException {
        EntityAttributeModifier lv2;
        EntityAttributeInstance lv = AttributeCommand.getAttributeInstance(arg2, arg3);
        if (lv.hasModifier(lv2 = new EntityAttributeModifier(uUID, string, d, arg4))) {
            throw MODIFIER_ALREADY_PRESENT_EXCEPTION.create((Object)arg2.getName(), (Object)new TranslatableText(arg3.getTranslationKey()), (Object)uUID);
        }
        lv.addPersistentModifier(lv2);
        arg.sendFeedback(new TranslatableText("commands.attribute.modifier.add.success", uUID, new TranslatableText(arg3.getTranslationKey()), arg2.getName()), false);
        return 1;
    }

    private static int executeModifierRemove(ServerCommandSource arg, Entity arg2, EntityAttribute arg3, UUID uUID) throws CommandSyntaxException {
        EntityAttributeInstance lv = AttributeCommand.getAttributeInstance(arg2, arg3);
        if (lv.tryRemoveModifier(uUID)) {
            arg.sendFeedback(new TranslatableText("commands.attribute.modifier.remove.success", uUID, new TranslatableText(arg3.getTranslationKey()), arg2.getName()), false);
            return 1;
        }
        throw NO_MODIFIER_EXCEPTION.create((Object)arg2.getName(), (Object)new TranslatableText(arg3.getTranslationKey()), (Object)uUID);
    }
}

