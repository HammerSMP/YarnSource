/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;

@Environment(value=EnvType.CLIENT)
public class InventoryScreen
extends AbstractInventoryScreen<PlayerScreenHandler>
implements RecipeBookProvider {
    private static final Identifier RECIPE_BUTTON_TEXTURE = new Identifier("textures/gui/recipe_button.png");
    private float mouseX;
    private float mouseY;
    private final RecipeBookWidget recipeBook = new RecipeBookWidget();
    private boolean open;
    private boolean narrow;
    private boolean mouseDown;

    public InventoryScreen(PlayerEntity arg) {
        super(arg.playerScreenHandler, arg.inventory, new TranslatableText("container.crafting"));
        this.passEvents = true;
        this.titleX = 97;
    }

    @Override
    public void tick() {
        if (this.client.interactionManager.hasCreativeInventory()) {
            this.client.openScreen(new CreativeInventoryScreen(this.client.player));
            return;
        }
        this.recipeBook.update();
    }

    @Override
    protected void init() {
        if (this.client.interactionManager.hasCreativeInventory()) {
            this.client.openScreen(new CreativeInventoryScreen(this.client.player));
            return;
        }
        super.init();
        this.narrow = this.width < 379;
        this.recipeBook.initialize(this.width, this.height, this.client, this.narrow, (AbstractRecipeScreenHandler)this.handler);
        this.open = true;
        this.x = this.recipeBook.findLeftEdge(this.narrow, this.width, this.backgroundWidth);
        this.children.add(this.recipeBook);
        this.setInitialFocus(this.recipeBook);
        this.addButton(new TexturedButtonWidget(this.x + 104, this.height / 2 - 22, 20, 18, 0, 0, 19, RECIPE_BUTTON_TEXTURE, arg -> {
            this.recipeBook.reset(this.narrow);
            this.recipeBook.toggleOpen();
            this.x = this.recipeBook.findLeftEdge(this.narrow, this.width, this.backgroundWidth);
            ((TexturedButtonWidget)arg).setPos(this.x + 104, this.height / 2 - 22);
            this.mouseDown = true;
        }));
    }

    @Override
    protected void drawForeground(MatrixStack arg, int i, int j) {
        this.textRenderer.draw(arg, this.title, (float)this.titleX, (float)this.titleY, 0x404040);
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        this.renderBackground(arg);
        boolean bl = this.drawStatusEffects = !this.recipeBook.isOpen();
        if (this.recipeBook.isOpen() && this.narrow) {
            this.drawBackground(arg, f, i, j);
            this.recipeBook.render(arg, i, j, f);
        } else {
            this.recipeBook.render(arg, i, j, f);
            super.render(arg, i, j, f);
            this.recipeBook.drawGhostSlots(arg, this.x, this.y, false, f);
        }
        this.drawMouseoverTooltip(arg, i, j);
        this.recipeBook.drawTooltip(arg, this.x, this.y, i, j);
        this.mouseX = i;
        this.mouseY = j;
    }

    @Override
    protected void drawBackground(MatrixStack arg, float f, int i, int j) {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.client.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        int k = this.x;
        int l = this.y;
        this.drawTexture(arg, k, l, 0, 0, this.backgroundWidth, this.backgroundHeight);
        InventoryScreen.drawEntity(k + 51, l + 75, 30, (float)(k + 51) - this.mouseX, (float)(l + 75 - 50) - this.mouseY, this.client.player);
    }

    public static void drawEntity(int i, int j, int k, float f, float g, LivingEntity arg) {
        float h = (float)Math.atan(f / 40.0f);
        float l = (float)Math.atan(g / 40.0f);
        RenderSystem.pushMatrix();
        RenderSystem.translatef(i, j, 1050.0f);
        RenderSystem.scalef(1.0f, 1.0f, -1.0f);
        MatrixStack lv = new MatrixStack();
        lv.translate(0.0, 0.0, 1000.0);
        lv.scale(k, k, k);
        Quaternion lv2 = Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0f);
        Quaternion lv3 = Vector3f.POSITIVE_X.getDegreesQuaternion(l * 20.0f);
        lv2.hamiltonProduct(lv3);
        lv.multiply(lv2);
        float m = arg.bodyYaw;
        float n = arg.yaw;
        float o = arg.pitch;
        float p = arg.prevHeadYaw;
        float q = arg.headYaw;
        arg.bodyYaw = 180.0f + h * 20.0f;
        arg.yaw = 180.0f + h * 40.0f;
        arg.pitch = -l * 20.0f;
        arg.headYaw = arg.yaw;
        arg.prevHeadYaw = arg.yaw;
        EntityRenderDispatcher lv4 = MinecraftClient.getInstance().getEntityRenderManager();
        lv3.conjugate();
        lv4.setRotation(lv3);
        lv4.setRenderShadows(false);
        VertexConsumerProvider.Immediate lv5 = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        RenderSystem.runAsFancy(() -> lv4.render(arg, 0.0, 0.0, 0.0, 0.0f, 1.0f, lv, lv5, 0xF000F0));
        lv5.draw();
        lv4.setRenderShadows(true);
        arg.bodyYaw = m;
        arg.yaw = n;
        arg.pitch = o;
        arg.prevHeadYaw = p;
        arg.headYaw = q;
        RenderSystem.popMatrix();
    }

    @Override
    protected boolean isPointWithinBounds(int i, int j, int k, int l, double d, double e) {
        return (!this.narrow || !this.recipeBook.isOpen()) && super.isPointWithinBounds(i, j, k, l, d, e);
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        if (this.recipeBook.mouseClicked(d, e, i)) {
            this.setFocused(this.recipeBook);
            return true;
        }
        if (this.narrow && this.recipeBook.isOpen()) {
            return false;
        }
        return super.mouseClicked(d, e, i);
    }

    @Override
    public boolean mouseReleased(double d, double e, int i) {
        if (this.mouseDown) {
            this.mouseDown = false;
            return true;
        }
        return super.mouseReleased(d, e, i);
    }

    @Override
    protected boolean isClickOutsideBounds(double d, double e, int i, int j, int k) {
        boolean bl = d < (double)i || e < (double)j || d >= (double)(i + this.backgroundWidth) || e >= (double)(j + this.backgroundHeight);
        return this.recipeBook.isClickOutsideBounds(d, e, this.x, this.y, this.backgroundWidth, this.backgroundHeight, k) && bl;
    }

    @Override
    protected void onMouseClick(Slot arg, int i, int j, SlotActionType arg2) {
        super.onMouseClick(arg, i, j, arg2);
        this.recipeBook.slotClicked(arg);
    }

    @Override
    public void refreshRecipeBook() {
        this.recipeBook.refresh();
    }

    @Override
    public void removed() {
        if (this.open) {
            this.recipeBook.close();
        }
        super.removed();
    }

    @Override
    public RecipeBookWidget getRecipeBookWidget() {
        return this.recipeBook;
    }
}

