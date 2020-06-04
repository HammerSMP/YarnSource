/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client;

import java.util.Locale;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.screen.GameModeSelectionScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.options.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screen.options.ChatOptionsScreen;
import net.minecraft.client.gui.screen.options.ControlsOptionsScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.options.Option;
import net.minecraft.client.util.Clipboard;
import net.minecraft.client.util.GlfwUtil;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.ScreenshotUtils;
import net.minecraft.command.arguments.BlockArgumentParser;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

@Environment(value=EnvType.CLIENT)
public class Keyboard {
    private final MinecraftClient client;
    private boolean repeatEvents;
    private final Clipboard clipboard = new Clipboard();
    private long debugCrashStartTime = -1L;
    private long debugCrashLastLogTime = -1L;
    private long debugCrashElapsedTime = -1L;
    private boolean switchF3State;

    public Keyboard(MinecraftClient arg) {
        this.client = arg;
    }

    private void debugWarn(String string, Object ... objects) {
        this.client.inGameHud.getChatHud().addMessage(new LiteralText("").append(new TranslatableText("debug.prefix").formatted(Formatting.YELLOW, Formatting.BOLD)).append(" ").append(new TranslatableText(string, objects)));
    }

    private void debugError(String string, Object ... objects) {
        this.client.inGameHud.getChatHud().addMessage(new LiteralText("").append(new TranslatableText("debug.prefix").formatted(Formatting.RED, Formatting.BOLD)).append(" ").append(new TranslatableText(string, objects)));
    }

    private boolean processF3(int i) {
        if (this.debugCrashStartTime > 0L && this.debugCrashStartTime < Util.getMeasuringTimeMs() - 100L) {
            return true;
        }
        switch (i) {
            case 65: {
                this.client.worldRenderer.reload();
                this.debugWarn("debug.reload_chunks.message", new Object[0]);
                return true;
            }
            case 66: {
                boolean bl = !this.client.getEntityRenderManager().shouldRenderHitboxes();
                this.client.getEntityRenderManager().setRenderHitboxes(bl);
                this.debugWarn(bl ? "debug.show_hitboxes.on" : "debug.show_hitboxes.off", new Object[0]);
                return true;
            }
            case 68: {
                if (this.client.inGameHud != null) {
                    this.client.inGameHud.getChatHud().clear(false);
                }
                return true;
            }
            case 70: {
                Option.RENDER_DISTANCE.set(this.client.options, MathHelper.clamp((double)(this.client.options.viewDistance + (Screen.hasShiftDown() ? -1 : 1)), Option.RENDER_DISTANCE.getMin(), Option.RENDER_DISTANCE.getMax()));
                this.debugWarn("debug.cycle_renderdistance.message", this.client.options.viewDistance);
                return true;
            }
            case 71: {
                boolean bl2 = this.client.debugRenderer.toggleShowChunkBorder();
                this.debugWarn(bl2 ? "debug.chunk_boundaries.on" : "debug.chunk_boundaries.off", new Object[0]);
                return true;
            }
            case 72: {
                this.client.options.advancedItemTooltips = !this.client.options.advancedItemTooltips;
                this.debugWarn(this.client.options.advancedItemTooltips ? "debug.advanced_tooltips.on" : "debug.advanced_tooltips.off", new Object[0]);
                this.client.options.write();
                return true;
            }
            case 73: {
                if (!this.client.player.getReducedDebugInfo()) {
                    this.copyLookAt(this.client.player.hasPermissionLevel(2), !Screen.hasShiftDown());
                }
                return true;
            }
            case 78: {
                if (!this.client.player.hasPermissionLevel(2)) {
                    this.debugWarn("debug.creative_spectator.error", new Object[0]);
                } else if (!this.client.player.isSpectator()) {
                    this.client.player.sendChatMessage("/gamemode spectator");
                } else {
                    this.client.player.sendChatMessage("/gamemode " + this.client.interactionManager.getPreviousGameMode().getName());
                }
                return true;
            }
            case 293: {
                if (!this.client.player.hasPermissionLevel(2)) {
                    this.debugWarn("debug.gamemodes.error", new Object[0]);
                } else {
                    this.client.openScreen(new GameModeSelectionScreen());
                }
                return true;
            }
            case 80: {
                this.client.options.pauseOnLostFocus = !this.client.options.pauseOnLostFocus;
                this.client.options.write();
                this.debugWarn(this.client.options.pauseOnLostFocus ? "debug.pause_focus.on" : "debug.pause_focus.off", new Object[0]);
                return true;
            }
            case 81: {
                this.debugWarn("debug.help.message", new Object[0]);
                ChatHud lv = this.client.inGameHud.getChatHud();
                lv.addMessage(new TranslatableText("debug.reload_chunks.help"));
                lv.addMessage(new TranslatableText("debug.show_hitboxes.help"));
                lv.addMessage(new TranslatableText("debug.copy_location.help"));
                lv.addMessage(new TranslatableText("debug.clear_chat.help"));
                lv.addMessage(new TranslatableText("debug.cycle_renderdistance.help"));
                lv.addMessage(new TranslatableText("debug.chunk_boundaries.help"));
                lv.addMessage(new TranslatableText("debug.advanced_tooltips.help"));
                lv.addMessage(new TranslatableText("debug.inspect.help"));
                lv.addMessage(new TranslatableText("debug.creative_spectator.help"));
                lv.addMessage(new TranslatableText("debug.pause_focus.help"));
                lv.addMessage(new TranslatableText("debug.help.help"));
                lv.addMessage(new TranslatableText("debug.reload_resourcepacks.help"));
                lv.addMessage(new TranslatableText("debug.pause.help"));
                lv.addMessage(new TranslatableText("debug.gamemodes.help"));
                return true;
            }
            case 84: {
                this.debugWarn("debug.reload_resourcepacks.message", new Object[0]);
                this.client.reloadResources();
                return true;
            }
            case 67: {
                if (this.client.player.getReducedDebugInfo()) {
                    return false;
                }
                ClientPlayNetworkHandler lv2 = this.client.player.networkHandler;
                if (lv2 == null) {
                    return false;
                }
                this.debugWarn("debug.copy_location.message", new Object[0]);
                this.setClipboard(String.format(Locale.ROOT, "/execute in %s run tp @s %.2f %.2f %.2f %.2f %.2f", this.client.player.world.getRegistryKey().getValue(), this.client.player.getX(), this.client.player.getY(), this.client.player.getZ(), Float.valueOf(this.client.player.yaw), Float.valueOf(this.client.player.pitch)));
                return true;
            }
        }
        return false;
    }

    private void copyLookAt(boolean bl, boolean bl2) {
        HitResult lv = this.client.crosshairTarget;
        if (lv == null) {
            return;
        }
        switch (lv.getType()) {
            case BLOCK: {
                BlockPos lv2 = ((BlockHitResult)lv).getBlockPos();
                BlockState lv3 = this.client.player.world.getBlockState(lv2);
                if (bl) {
                    if (bl2) {
                        this.client.player.networkHandler.getDataQueryHandler().queryBlockNbt(lv2, arg3 -> {
                            this.copyBlock(lv3, lv2, (CompoundTag)arg3);
                            this.debugWarn("debug.inspect.server.block", new Object[0]);
                        });
                        break;
                    }
                    BlockEntity lv4 = this.client.player.world.getBlockEntity(lv2);
                    CompoundTag lv5 = lv4 != null ? lv4.toTag(new CompoundTag()) : null;
                    this.copyBlock(lv3, lv2, lv5);
                    this.debugWarn("debug.inspect.client.block", new Object[0]);
                    break;
                }
                this.copyBlock(lv3, lv2, null);
                this.debugWarn("debug.inspect.client.block", new Object[0]);
                break;
            }
            case ENTITY: {
                Entity lv6 = ((EntityHitResult)lv).getEntity();
                Identifier lv7 = Registry.ENTITY_TYPE.getId(lv6.getType());
                if (bl) {
                    if (bl2) {
                        this.client.player.networkHandler.getDataQueryHandler().queryEntityNbt(lv6.getEntityId(), arg3 -> {
                            this.copyEntity(lv7, lv6.getPos(), (CompoundTag)arg3);
                            this.debugWarn("debug.inspect.server.entity", new Object[0]);
                        });
                        break;
                    }
                    CompoundTag lv8 = lv6.toTag(new CompoundTag());
                    this.copyEntity(lv7, lv6.getPos(), lv8);
                    this.debugWarn("debug.inspect.client.entity", new Object[0]);
                    break;
                }
                this.copyEntity(lv7, lv6.getPos(), null);
                this.debugWarn("debug.inspect.client.entity", new Object[0]);
                break;
            }
        }
    }

    private void copyBlock(BlockState arg, BlockPos arg2, @Nullable CompoundTag arg3) {
        if (arg3 != null) {
            arg3.remove("x");
            arg3.remove("y");
            arg3.remove("z");
            arg3.remove("id");
        }
        StringBuilder stringBuilder = new StringBuilder(BlockArgumentParser.stringifyBlockState(arg));
        if (arg3 != null) {
            stringBuilder.append(arg3);
        }
        String string = String.format(Locale.ROOT, "/setblock %d %d %d %s", arg2.getX(), arg2.getY(), arg2.getZ(), stringBuilder);
        this.setClipboard(string);
    }

    private void copyEntity(Identifier arg, Vec3d arg2, @Nullable CompoundTag arg3) {
        String string3;
        if (arg3 != null) {
            arg3.remove("UUID");
            arg3.remove("Pos");
            arg3.remove("Dimension");
            String string = arg3.toText().getString();
            String string2 = String.format(Locale.ROOT, "/summon %s %.2f %.2f %.2f %s", arg.toString(), arg2.x, arg2.y, arg2.z, string);
        } else {
            string3 = String.format(Locale.ROOT, "/summon %s %.2f %.2f %.2f", arg.toString(), arg2.x, arg2.y, arg2.z);
        }
        this.setClipboard(string3);
    }

    public void onKey(long l, int i, int j, int k, int m) {
        boolean bl;
        if (l != this.client.getWindow().getHandle()) {
            return;
        }
        if (this.debugCrashStartTime > 0L) {
            if (!InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 67) || !InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 292)) {
                this.debugCrashStartTime = -1L;
            }
        } else if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 67) && InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 292)) {
            this.switchF3State = true;
            this.debugCrashStartTime = Util.getMeasuringTimeMs();
            this.debugCrashLastLogTime = Util.getMeasuringTimeMs();
            this.debugCrashElapsedTime = 0L;
        }
        Screen lv = this.client.currentScreen;
        if (!(k != 1 || this.client.currentScreen instanceof ControlsOptionsScreen && ((ControlsOptionsScreen)lv).time > Util.getMeasuringTimeMs() - 20L)) {
            if (this.client.options.keyFullscreen.matchesKey(i, j)) {
                this.client.getWindow().toggleFullscreen();
                this.client.options.fullscreen = this.client.getWindow().isFullscreen();
                this.client.options.write();
                return;
            }
            if (this.client.options.keyScreenshot.matchesKey(i, j)) {
                if (Screen.hasControlDown()) {
                    // empty if block
                }
                ScreenshotUtils.saveScreenshot(this.client.runDirectory, this.client.getWindow().getFramebufferWidth(), this.client.getWindow().getFramebufferHeight(), this.client.getFramebuffer(), arg -> this.client.execute(() -> this.client.inGameHud.getChatHud().addMessage((Text)arg)));
                return;
            }
        }
        boolean bl2 = bl = lv == null || !(lv.getFocused() instanceof TextFieldWidget) || !((TextFieldWidget)lv.getFocused()).isActive();
        if (k != 0 && i == 66 && Screen.hasControlDown() && bl) {
            Option.NARRATOR.cycle(this.client.options, 1);
            if (lv instanceof ChatOptionsScreen) {
                ((ChatOptionsScreen)lv).setNarratorMessage();
            }
            if (lv instanceof AccessibilityOptionsScreen) {
                ((AccessibilityOptionsScreen)lv).setNarratorMessage();
            }
        }
        if (lv != null) {
            boolean[] bls = new boolean[]{false};
            Screen.wrapScreenError(() -> {
                if (k == 1 || k == 2 && this.repeatEvents) {
                    bls[0] = lv.keyPressed(i, j, m);
                } else if (k == 0) {
                    bls[0] = lv.keyReleased(i, j, m);
                }
            }, "keyPressed event handler", lv.getClass().getCanonicalName());
            if (bls[0]) {
                return;
            }
        }
        if (this.client.currentScreen == null || this.client.currentScreen.passEvents) {
            InputUtil.Key lv2 = InputUtil.fromKeyCode(i, j);
            if (k == 0) {
                KeyBinding.setKeyPressed(lv2, false);
                if (i == 292) {
                    if (this.switchF3State) {
                        this.switchF3State = false;
                    } else {
                        this.client.options.debugEnabled = !this.client.options.debugEnabled;
                        this.client.options.debugProfilerEnabled = this.client.options.debugEnabled && Screen.hasShiftDown();
                        this.client.options.debugTpsEnabled = this.client.options.debugEnabled && Screen.hasAltDown();
                    }
                }
            } else {
                if (i == 293 && this.client.gameRenderer != null) {
                    this.client.gameRenderer.toggleShadersEnabled();
                }
                boolean bl22 = false;
                if (this.client.currentScreen == null) {
                    if (i == 256) {
                        boolean bl3 = InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 292);
                        this.client.openPauseMenu(bl3);
                    }
                    bl22 = InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 292) && this.processF3(i);
                    this.switchF3State |= bl22;
                    if (i == 290) {
                        boolean bl3 = this.client.options.hudHidden = !this.client.options.hudHidden;
                    }
                }
                if (bl22) {
                    KeyBinding.setKeyPressed(lv2, false);
                } else {
                    KeyBinding.setKeyPressed(lv2, true);
                    KeyBinding.onKeyPressed(lv2);
                }
                if (this.client.options.debugProfilerEnabled && i >= 48 && i <= 57) {
                    this.client.handleProfilerKeyPress(i - 48);
                }
            }
        }
    }

    private void onChar(long l, int i, int j) {
        if (l != this.client.getWindow().getHandle()) {
            return;
        }
        Screen lv = this.client.currentScreen;
        if (lv == null || this.client.getOverlay() != null) {
            return;
        }
        if (Character.charCount(i) == 1) {
            Screen.wrapScreenError(() -> lv.charTyped((char)i, j), "charTyped event handler", lv.getClass().getCanonicalName());
        } else {
            for (char c : Character.toChars(i)) {
                Screen.wrapScreenError(() -> lv.charTyped(c, j), "charTyped event handler", lv.getClass().getCanonicalName());
            }
        }
    }

    public void enableRepeatEvents(boolean bl) {
        this.repeatEvents = bl;
    }

    public void setup(long l2) {
        InputUtil.setKeyboardCallbacks(l2, (l, i, j, k, m) -> this.client.execute(() -> this.onKey(l, i, j, k, m)), (l, i, j) -> this.client.execute(() -> this.onChar(l, i, j)));
    }

    public String getClipboard() {
        return this.clipboard.getClipboard(this.client.getWindow().getHandle(), (i, l) -> {
            if (i != 65545) {
                this.client.getWindow().logGlError(i, l);
            }
        });
    }

    public void setClipboard(String string) {
        this.clipboard.setClipboard(this.client.getWindow().getHandle(), string);
    }

    public void pollDebugCrash() {
        if (this.debugCrashStartTime > 0L) {
            long l = Util.getMeasuringTimeMs();
            long m = 10000L - (l - this.debugCrashStartTime);
            long n = l - this.debugCrashLastLogTime;
            if (m < 0L) {
                if (Screen.hasControlDown()) {
                    GlfwUtil.makeJvmCrash();
                }
                throw new CrashException(new CrashReport("Manually triggered debug crash", new Throwable()));
            }
            if (n >= 1000L) {
                if (this.debugCrashElapsedTime == 0L) {
                    this.debugWarn("debug.crash.message", new Object[0]);
                } else {
                    this.debugError("debug.crash.warning", MathHelper.ceil((float)m / 1000.0f));
                }
                this.debugCrashLastLogTime = l;
                ++this.debugCrashElapsedTime;
            }
        }
    }
}

