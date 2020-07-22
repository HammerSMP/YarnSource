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
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.argument.UuidArgumentType;
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
    private static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER = (commandContext, suggestionsBuilder) -> CommandSource.suggestIdentifiers(Registry.ATTRIBUTE.getIds(), suggestionsBuilder);
    private static final DynamicCommandExceptionType ENTITY_FAILED_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("commands.attribute.failed.entity", object));
    private static final Dynamic2CommandExceptionType NO_ATTRIBUTE_EXCEPTION = new Dynamic2CommandExceptionType((object, object2) -> new TranslatableText("commands.attribute.failed.no_attribute", object, object2));
    private static final Dynamic3CommandExceptionType NO_MODIFIER_EXCEPTION = new Dynamic3CommandExceptionType((object, object2, object3) -> new TranslatableText("commands.attribute.failed.no_modifier", object2, object, object3));
    private static final Dynamic3CommandExceptionType MODIFIER_ALREADY_PRESENT_EXCEPTION = new Dynamic3CommandExceptionType((object, object2, object3) -> new TranslatableText("commands.attribute.failed.modifier_already_present", object3, object2, object));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("attribute").requires(arg -> arg.hasPermissionLevel(2))).then(CommandManager.argument("target", EntityArgumentType.entity()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("attribute", IdentifierArgumentType.identifier()).suggests(SUGGESTION_PROVIDER).then(((LiteralArgumentBuilder)CommandManager.literal("get").executes(commandContext -> AttributeCommand.executeValueGet((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntity((CommandContext<ServerCommandSource>)commandContext, "target"), IdentifierArgumentType.method_27575((CommandContext<ServerCommandSource>)commandContext, "attribute"), 1.0))).then(CommandManager.argument("scale", DoubleArgumentType.doubleArg()).executes(commandContext -> AttributeCommand.executeValueGet((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntity((CommandContext<ServerCommandSource>)commandContext, "target"), IdentifierArgumentType.method_27575((CommandContext<ServerCommandSource>)commandContext, "attribute"), DoubleArgumentType.getDouble((CommandContext)commandContext, (String)"scale")))))).then(((LiteralArgumentBuilder)CommandManager.literal("base").then(CommandManager.literal("set").then(CommandManager.argument("value", DoubleArgumentType.doubleArg()).executes(commandContext -> AttributeCommand.executeBaseValueSet((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntity((CommandContext<ServerCommandSource>)commandContext, "target"), IdentifierArgumentType.method_27575((CommandContext<ServerCommandSource>)commandContext, "attribute"), DoubleArgumentType.getDouble((CommandContext)commandContext, (String)"value")))))).then(((LiteralArgumentBuilder)CommandManager.literal("get").executes(commandContext -> AttributeCommand.executeBaseValueGet((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntity((CommandContext<ServerCommandSource>)commandContext, "target"), IdentifierArgumentType.method_27575((CommandContext<ServerCommandSource>)commandContext, "attribute"), 1.0))).then(CommandManager.argument("scale", DoubleArgumentType.doubleArg()).executes(commandContext -> AttributeCommand.executeBaseValueGet((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntity((CommandContext<ServerCommandSource>)commandContext, "target"), IdentifierArgumentType.method_27575((CommandContext<ServerCommandSource>)commandContext, "attribute"), DoubleArgumentType.getDouble((CommandContext)commandContext, (String)"scale"))))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("modifier").then(CommandManager.literal("add").then(CommandManager.argument("uuid", UuidArgumentType.uuid()).then(CommandManager.argument("name", StringArgumentType.string()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("value", DoubleArgumentType.doubleArg()).then(CommandManager.literal("add").executes(commandContext -> AttributeCommand.executeModifierAdd((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntity((CommandContext<ServerCommandSource>)commandContext, "target"), IdentifierArgumentType.method_27575((CommandContext<ServerCommandSource>)commandContext, "attribute"), UuidArgumentType.getUuid((CommandContext<ServerCommandSource>)commandContext, "uuid"), StringArgumentType.getString((CommandContext)commandContext, (String)"name"), DoubleArgumentType.getDouble((CommandContext)commandContext, (String)"value"), EntityAttributeModifier.Operation.ADDITION)))).then(CommandManager.literal("multiply").executes(commandContext -> AttributeCommand.executeModifierAdd((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntity((CommandContext<ServerCommandSource>)commandContext, "target"), IdentifierArgumentType.method_27575((CommandContext<ServerCommandSource>)commandContext, "attribute"), UuidArgumentType.getUuid((CommandContext<ServerCommandSource>)commandContext, "uuid"), StringArgumentType.getString((CommandContext)commandContext, (String)"name"), DoubleArgumentType.getDouble((CommandContext)commandContext, (String)"value"), EntityAttributeModifier.Operation.MULTIPLY_TOTAL)))).then(CommandManager.literal("multiply_base").executes(commandContext -> AttributeCommand.executeModifierAdd((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntity((CommandContext<ServerCommandSource>)commandContext, "target"), IdentifierArgumentType.method_27575((CommandContext<ServerCommandSource>)commandContext, "attribute"), UuidArgumentType.getUuid((CommandContext<ServerCommandSource>)commandContext, "uuid"), StringArgumentType.getString((CommandContext)commandContext, (String)"name"), DoubleArgumentType.getDouble((CommandContext)commandContext, (String)"value"), EntityAttributeModifier.Operation.MULTIPLY_BASE)))))))).then(CommandManager.literal("remove").then(CommandManager.argument("uuid", UuidArgumentType.uuid()).executes(commandContext -> AttributeCommand.executeModifierRemove((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntity((CommandContext<ServerCommandSource>)commandContext, "target"), IdentifierArgumentType.method_27575((CommandContext<ServerCommandSource>)commandContext, "attribute"), UuidArgumentType.getUuid((CommandContext<ServerCommandSource>)commandContext, "uuid")))))).then(CommandManager.literal("value").then(CommandManager.literal("get").then(((RequiredArgumentBuilder)CommandManager.argument("uuid", UuidArgumentType.uuid()).executes(commandContext -> AttributeCommand.executeModifierValueGet((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntity((CommandContext<ServerCommandSource>)commandContext, "target"), IdentifierArgumentType.method_27575((CommandContext<ServerCommandSource>)commandContext, "attribute"), UuidArgumentType.getUuid((CommandContext<ServerCommandSource>)commandContext, "uuid"), 1.0))).then(CommandManager.argument("scale", DoubleArgumentType.doubleArg()).executes(commandContext -> AttributeCommand.executeModifierValueGet((ServerCommandSource)commandContext.getSource(), EntityArgumentType.getEntity((CommandContext<ServerCommandSource>)commandContext, "target"), IdentifierArgumentType.method_27575((CommandContext<ServerCommandSource>)commandContext, "attribute"), UuidArgumentType.getUuid((CommandContext<ServerCommandSource>)commandContext, "uuid"), DoubleArgumentType.getDouble((CommandContext)commandContext, (String)"scale")))))))))));
    }

    private static EntityAttributeInstance getAttributeInstance(Entity entity, EntityAttribute attribute) throws CommandSyntaxException {
        EntityAttributeInstance lv = AttributeCommand.getLivingEntity(entity).getAttributes().getCustomInstance(attribute);
        if (lv == null) {
            throw NO_ATTRIBUTE_EXCEPTION.create((Object)entity.getName(), (Object)new TranslatableText(attribute.getTranslationKey()));
        }
        return lv;
    }

    private static LivingEntity getLivingEntity(Entity entity) throws CommandSyntaxException {
        if (!(entity instanceof LivingEntity)) {
            throw ENTITY_FAILED_EXCEPTION.create((Object)entity.getName());
        }
        return (LivingEntity)entity;
    }

    private static LivingEntity getLivingEntityWithAttribute(Entity entity, EntityAttribute attribute) throws CommandSyntaxException {
        LivingEntity lv = AttributeCommand.getLivingEntity(entity);
        if (!lv.getAttributes().hasAttribute(attribute)) {
            throw NO_ATTRIBUTE_EXCEPTION.create((Object)entity.getName(), (Object)new TranslatableText(attribute.getTranslationKey()));
        }
        return lv;
    }

    private static int executeValueGet(ServerCommandSource source, Entity target, EntityAttribute attribute, double multiplier) throws CommandSyntaxException {
        LivingEntity lv = AttributeCommand.getLivingEntityWithAttribute(target, attribute);
        double e = lv.getAttributeValue(attribute);
        source.sendFeedback(new TranslatableText("commands.attribute.value.get.success", new TranslatableText(attribute.getTranslationKey()), target.getName(), e), false);
        return (int)(e * multiplier);
    }

    private static int executeBaseValueGet(ServerCommandSource source, Entity target, EntityAttribute attribute, double multiplier) throws CommandSyntaxException {
        LivingEntity lv = AttributeCommand.getLivingEntityWithAttribute(target, attribute);
        double e = lv.getAttributeBaseValue(attribute);
        source.sendFeedback(new TranslatableText("commands.attribute.base_value.get.success", new TranslatableText(attribute.getTranslationKey()), target.getName(), e), false);
        return (int)(e * multiplier);
    }

    private static int executeModifierValueGet(ServerCommandSource source, Entity target, EntityAttribute attribute, UUID uuid, double multiplier) throws CommandSyntaxException {
        LivingEntity lv = AttributeCommand.getLivingEntityWithAttribute(target, attribute);
        AttributeContainer lv2 = lv.getAttributes();
        if (!lv2.hasModifierForAttribute(attribute, uuid)) {
            throw NO_MODIFIER_EXCEPTION.create((Object)target.getName(), (Object)new TranslatableText(attribute.getTranslationKey()), (Object)uuid);
        }
        double e = lv2.getModifierValue(attribute, uuid);
        source.sendFeedback(new TranslatableText("commands.attribute.modifier.value.get.success", uuid, new TranslatableText(attribute.getTranslationKey()), target.getName(), e), false);
        return (int)(e * multiplier);
    }

    private static int executeBaseValueSet(ServerCommandSource source, Entity target, EntityAttribute attribute, double value) throws CommandSyntaxException {
        AttributeCommand.getAttributeInstance(target, attribute).setBaseValue(value);
        source.sendFeedback(new TranslatableText("commands.attribute.base_value.set.success", new TranslatableText(attribute.getTranslationKey()), target.getName(), value), false);
        return 1;
    }

    private static int executeModifierAdd(ServerCommandSource source, Entity target, EntityAttribute attribute, UUID uuid, String name, double value, EntityAttributeModifier.Operation operation) throws CommandSyntaxException {
        EntityAttributeModifier lv2;
        EntityAttributeInstance lv = AttributeCommand.getAttributeInstance(target, attribute);
        if (lv.hasModifier(lv2 = new EntityAttributeModifier(uuid, name, value, operation))) {
            throw MODIFIER_ALREADY_PRESENT_EXCEPTION.create((Object)target.getName(), (Object)new TranslatableText(attribute.getTranslationKey()), (Object)uuid);
        }
        lv.addPersistentModifier(lv2);
        source.sendFeedback(new TranslatableText("commands.attribute.modifier.add.success", uuid, new TranslatableText(attribute.getTranslationKey()), target.getName()), false);
        return 1;
    }

    private static int executeModifierRemove(ServerCommandSource source, Entity target, EntityAttribute attribute, UUID uuid) throws CommandSyntaxException {
        EntityAttributeInstance lv = AttributeCommand.getAttributeInstance(target, attribute);
        if (lv.tryRemoveModifier(uuid)) {
            source.sendFeedback(new TranslatableText("commands.attribute.modifier.remove.success", uuid, new TranslatableText(attribute.getTranslationKey()), target.getName()), false);
            return 1;
        }
        throw NO_MODIFIER_EXCEPTION.create((Object)target.getName(), (Object)new TranslatableText(attribute.getTranslationKey()), (Object)uuid);
    }
}

