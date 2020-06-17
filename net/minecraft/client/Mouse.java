/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.lwjgl.glfw.GLFWDropCallback
 */
package net.minecraft.client;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.GlfwUtil;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.SmoothUtil;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFWDropCallback;

@Environment(value=EnvType.CLIENT)
public class Mouse {
    private final MinecraftClient client;
    private boolean leftButtonClicked;
    private boolean middleButtonClicked;
    private boolean rightButtonClicked;
    private double x;
    private double y;
    private int controlLeftTicks;
    private int activeButton = -1;
    private boolean hasResolutionChanged = true;
    private int field_1796;
    private double glfwTime;
    private final SmoothUtil cursorXSmoother = new SmoothUtil();
    private final SmoothUtil cursorYSmoother = new SmoothUtil();
    private double cursorDeltaX;
    private double cursorDeltaY;
    private double eventDeltaWheel;
    private double lastMouseUpdateTime = Double.MIN_VALUE;
    private boolean isCursorLocked;

    public Mouse(MinecraftClient arg) {
        this.client = arg;
    }

    private void onMouseButton(long l, int i, int j, int k) {
        boolean bl;
        if (l != this.client.getWindow().getHandle()) {
            return;
        }
        boolean bl2 = bl = j == 1;
        if (MinecraftClient.IS_SYSTEM_MAC && i == 0) {
            if (bl) {
                if ((k & 2) == 2) {
                    i = 1;
                    ++this.controlLeftTicks;
                }
            } else if (this.controlLeftTicks > 0) {
                i = 1;
                --this.controlLeftTicks;
            }
        }
        int m = i;
        if (bl) {
            if (this.client.options.touchscreen && this.field_1796++ > 0) {
                return;
            }
            this.activeButton = m;
            this.glfwTime = GlfwUtil.getTime();
        } else if (this.activeButton != -1) {
            if (this.client.options.touchscreen && --this.field_1796 > 0) {
                return;
            }
            this.activeButton = -1;
        }
        boolean[] bls = new boolean[]{false};
        if (this.client.overlay == null) {
            if (this.client.currentScreen == null) {
                if (!this.isCursorLocked && bl) {
                    this.lockCursor();
                }
            } else {
                double d = this.x * (double)this.client.getWindow().getScaledWidth() / (double)this.client.getWindow().getWidth();
                double e = this.y * (double)this.client.getWindow().getScaledHeight() / (double)this.client.getWindow().getHeight();
                if (bl) {
                    Screen.wrapScreenError(() -> {
                        bls[0] = this.client.currentScreen.mouseClicked(d, e, m);
                    }, "mouseClicked event handler", this.client.currentScreen.getClass().getCanonicalName());
                } else {
                    Screen.wrapScreenError(() -> {
                        bls[0] = this.client.currentScreen.mouseReleased(d, e, m);
                    }, "mouseReleased event handler", this.client.currentScreen.getClass().getCanonicalName());
                }
            }
        }
        if (!bls[0] && (this.client.currentScreen == null || this.client.currentScreen.passEvents) && this.client.overlay == null) {
            if (m == 0) {
                this.leftButtonClicked = bl;
            } else if (m == 2) {
                this.middleButtonClicked = bl;
            } else if (m == 1) {
                this.rightButtonClicked = bl;
            }
            KeyBinding.setKeyPressed(InputUtil.Type.MOUSE.createFromCode(m), bl);
            if (bl) {
                if (this.client.player.isSpectator() && m == 2) {
                    this.client.inGameHud.getSpectatorHud().useSelectedCommand();
                } else {
                    KeyBinding.onKeyPressed(InputUtil.Type.MOUSE.createFromCode(m));
                }
            }
        }
    }

    private void onMouseScroll(long l, double d, double e) {
        if (l == MinecraftClient.getInstance().getWindow().getHandle()) {
            double f = (this.client.options.discreteMouseScroll ? Math.signum(e) : e) * this.client.options.mouseWheelSensitivity;
            if (this.client.overlay == null) {
                if (this.client.currentScreen != null) {
                    double g = this.x * (double)this.client.getWindow().getScaledWidth() / (double)this.client.getWindow().getWidth();
                    double h = this.y * (double)this.client.getWindow().getScaledHeight() / (double)this.client.getWindow().getHeight();
                    this.client.currentScreen.mouseScrolled(g, h, f);
                } else if (this.client.player != null) {
                    if (this.eventDeltaWheel != 0.0 && Math.signum(f) != Math.signum(this.eventDeltaWheel)) {
                        this.eventDeltaWheel = 0.0;
                    }
                    this.eventDeltaWheel += f;
                    float i = (int)this.eventDeltaWheel;
                    if (i == 0.0f) {
                        return;
                    }
                    this.eventDeltaWheel -= (double)i;
                    if (this.client.player.isSpectator()) {
                        if (this.client.inGameHud.getSpectatorHud().isOpen()) {
                            this.client.inGameHud.getSpectatorHud().cycleSlot(-i);
                        } else {
                            float j = MathHelper.clamp(this.client.player.abilities.getFlySpeed() + i * 0.005f, 0.0f, 0.2f);
                            this.client.player.abilities.setFlySpeed(j);
                        }
                    } else {
                        this.client.player.inventory.scrollInHotbar(i);
                    }
                }
            }
        }
    }

    private void method_29616(long l, List<Path> list) {
        if (this.client.currentScreen != null) {
            this.client.currentScreen.method_29638(list);
        }
    }

    public void setup(long l2) {
        InputUtil.setMouseCallbacks(l2, (l, d, e) -> this.client.execute(() -> this.onCursorPos(l, d, e)), (l, i, j, k) -> this.client.execute(() -> this.onMouseButton(l, i, j, k)), (l, d, e) -> this.client.execute(() -> this.onMouseScroll(l, d, e)), (l, i, m) -> {
            Path[] paths = new Path[i];
            for (int j = 0; j < i; ++j) {
                paths[j] = Paths.get(GLFWDropCallback.getName((long)m, (int)j), new String[0]);
            }
            this.client.execute(() -> this.method_29616(l, Arrays.asList(paths)));
        });
    }

    private void onCursorPos(long l, double d, double e) {
        Screen lv;
        if (l != MinecraftClient.getInstance().getWindow().getHandle()) {
            return;
        }
        if (this.hasResolutionChanged) {
            this.x = d;
            this.y = e;
            this.hasResolutionChanged = false;
        }
        if ((lv = this.client.currentScreen) != null && this.client.overlay == null) {
            double f = d * (double)this.client.getWindow().getScaledWidth() / (double)this.client.getWindow().getWidth();
            double g = e * (double)this.client.getWindow().getScaledHeight() / (double)this.client.getWindow().getHeight();
            Screen.wrapScreenError(() -> lv.mouseMoved(f, g), "mouseMoved event handler", lv.getClass().getCanonicalName());
            if (this.activeButton != -1 && this.glfwTime > 0.0) {
                double h = (d - this.x) * (double)this.client.getWindow().getScaledWidth() / (double)this.client.getWindow().getWidth();
                double i = (e - this.y) * (double)this.client.getWindow().getScaledHeight() / (double)this.client.getWindow().getHeight();
                Screen.wrapScreenError(() -> lv.mouseDragged(f, g, this.activeButton, h, i), "mouseDragged event handler", lv.getClass().getCanonicalName());
            }
        }
        this.client.getProfiler().push("mouse");
        if (this.isCursorLocked() && this.client.isWindowFocused()) {
            this.cursorDeltaX += d - this.x;
            this.cursorDeltaY += e - this.y;
        }
        this.updateMouse();
        this.x = d;
        this.y = e;
        this.client.getProfiler().pop();
    }

    public void updateMouse() {
        double m;
        double l;
        double d = GlfwUtil.getTime();
        double e = d - this.lastMouseUpdateTime;
        this.lastMouseUpdateTime = d;
        if (!this.isCursorLocked() || !this.client.isWindowFocused()) {
            this.cursorDeltaX = 0.0;
            this.cursorDeltaY = 0.0;
            return;
        }
        double f = this.client.options.mouseSensitivity * (double)0.6f + (double)0.2f;
        double g = f * f * f * 8.0;
        if (this.client.options.smoothCameraEnabled) {
            double h = this.cursorXSmoother.smooth(this.cursorDeltaX * g, e * g);
            double i = this.cursorYSmoother.smooth(this.cursorDeltaY * g, e * g);
            double j = h;
            double k = i;
        } else {
            this.cursorXSmoother.clear();
            this.cursorYSmoother.clear();
            l = this.cursorDeltaX * g;
            m = this.cursorDeltaY * g;
        }
        this.cursorDeltaX = 0.0;
        this.cursorDeltaY = 0.0;
        int n = 1;
        if (this.client.options.invertYMouse) {
            n = -1;
        }
        this.client.getTutorialManager().onUpdateMouse(l, m);
        if (this.client.player != null) {
            this.client.player.changeLookDirection(l, m * (double)n);
        }
    }

    public boolean wasLeftButtonClicked() {
        return this.leftButtonClicked;
    }

    public boolean wasRightButtonClicked() {
        return this.rightButtonClicked;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public void onResolutionChanged() {
        this.hasResolutionChanged = true;
    }

    public boolean isCursorLocked() {
        return this.isCursorLocked;
    }

    public void lockCursor() {
        if (!this.client.isWindowFocused()) {
            return;
        }
        if (this.isCursorLocked) {
            return;
        }
        if (!MinecraftClient.IS_SYSTEM_MAC) {
            KeyBinding.updatePressedStates();
        }
        this.isCursorLocked = true;
        this.x = this.client.getWindow().getWidth() / 2;
        this.y = this.client.getWindow().getHeight() / 2;
        InputUtil.setCursorParameters(this.client.getWindow().getHandle(), 212995, this.x, this.y);
        this.client.openScreen(null);
        this.client.attackCooldown = 10000;
        this.hasResolutionChanged = true;
    }

    public void unlockCursor() {
        if (!this.isCursorLocked) {
            return;
        }
        this.isCursorLocked = false;
        this.x = this.client.getWindow().getWidth() / 2;
        this.y = this.client.getWindow().getHeight() / 2;
        InputUtil.setCursorParameters(this.client.getWindow().getHandle(), 212993, this.x, this.y);
    }

    public void method_30134() {
        this.hasResolutionChanged = true;
    }
}

