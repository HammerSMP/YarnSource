/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.realms.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.Realms;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.RealmsLabel;
import net.minecraft.client.realms.RealmsObjectSelectionList;
import net.minecraft.client.realms.RealmsScreen;
import net.minecraft.client.realms.dto.Ops;
import net.minecraft.client.realms.dto.PlayerInfo;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.gui.screen.RealmsConfigureWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsConfirmScreen;
import net.minecraft.client.realms.gui.screen.RealmsInviteScreen;
import net.minecraft.client.realms.util.RealmsTextureManager;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsPlayerScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Identifier OP_ICON = new Identifier("realms", "textures/gui/realms/op_icon.png");
    private static final Identifier USER_ICON = new Identifier("realms", "textures/gui/realms/user_icon.png");
    private static final Identifier CROSS_PLAYER_ICON = new Identifier("realms", "textures/gui/realms/cross_player_icon.png");
    private static final Identifier OPTIONS_BACKGROUND = new Identifier("minecraft", "textures/gui/options_background.png");
    private String tooltipText;
    private final RealmsConfigureWorldScreen parent;
    private final RealmsServer serverData;
    private InvitedObjectSelectionList invitedObjectSelectionList;
    private int column1_x;
    private int column_width;
    private int column2_x;
    private ButtonWidget removeButton;
    private ButtonWidget opdeopButton;
    private int selectedInvitedIndex = -1;
    private String selectedInvited;
    private int player = -1;
    private boolean stateChanged;
    private RealmsLabel titleLabel;

    public RealmsPlayerScreen(RealmsConfigureWorldScreen arg, RealmsServer arg2) {
        this.parent = arg;
        this.serverData = arg2;
    }

    @Override
    public void init() {
        this.column1_x = this.width / 2 - 160;
        this.column_width = 150;
        this.column2_x = this.width / 2 + 12;
        this.client.keyboard.enableRepeatEvents(true);
        this.invitedObjectSelectionList = new InvitedObjectSelectionList();
        this.invitedObjectSelectionList.setLeftPos(this.column1_x);
        this.addChild(this.invitedObjectSelectionList);
        for (PlayerInfo lv : this.serverData.players) {
            this.invitedObjectSelectionList.addEntry(lv);
        }
        this.addButton(new ButtonWidget(this.column2_x, RealmsPlayerScreen.row(1), this.column_width + 10, 20, new TranslatableText("mco.configure.world.buttons.invite"), arg -> this.client.openScreen(new RealmsInviteScreen(this.parent, this, this.serverData))));
        this.removeButton = this.addButton(new ButtonWidget(this.column2_x, RealmsPlayerScreen.row(7), this.column_width + 10, 20, new TranslatableText("mco.configure.world.invites.remove.tooltip"), arg -> this.uninvite(this.player)));
        this.opdeopButton = this.addButton(new ButtonWidget(this.column2_x, RealmsPlayerScreen.row(9), this.column_width + 10, 20, new TranslatableText("mco.configure.world.invites.ops.tooltip"), arg -> {
            if (this.serverData.players.get(this.player).isOperator()) {
                this.deop(this.player);
            } else {
                this.op(this.player);
            }
        }));
        this.addButton(new ButtonWidget(this.column2_x + this.column_width / 2 + 2, RealmsPlayerScreen.row(12), this.column_width / 2 + 10 - 2, 20, ScreenTexts.BACK, arg -> this.backButtonClicked()));
        this.titleLabel = this.addChild(new RealmsLabel(new TranslatableText("mco.configure.world.players.title"), this.width / 2, 17, 0xFFFFFF));
        this.narrateLabels();
        this.updateButtonStates();
    }

    private void updateButtonStates() {
        this.removeButton.visible = this.shouldRemoveAndOpdeopButtonBeVisible(this.player);
        this.opdeopButton.visible = this.shouldRemoveAndOpdeopButtonBeVisible(this.player);
    }

    private boolean shouldRemoveAndOpdeopButtonBeVisible(int i) {
        return i != -1;
    }

    @Override
    public void removed() {
        this.client.keyboard.enableRepeatEvents(false);
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (i == 256) {
            this.backButtonClicked();
            return true;
        }
        return super.keyPressed(i, j, k);
    }

    private void backButtonClicked() {
        if (this.stateChanged) {
            this.client.openScreen(this.parent.getNewScreen());
        } else {
            this.client.openScreen(this.parent);
        }
    }

    private void op(int i) {
        this.updateButtonStates();
        RealmsClient lv = RealmsClient.createRealmsClient();
        String string = this.serverData.players.get(i).getUuid();
        try {
            this.updateOps(lv.op(this.serverData.id, string));
        }
        catch (RealmsServiceException lv2) {
            LOGGER.error("Couldn't op the user");
        }
    }

    private void deop(int i) {
        this.updateButtonStates();
        RealmsClient lv = RealmsClient.createRealmsClient();
        String string = this.serverData.players.get(i).getUuid();
        try {
            this.updateOps(lv.deop(this.serverData.id, string));
        }
        catch (RealmsServiceException lv2) {
            LOGGER.error("Couldn't deop the user");
        }
    }

    private void updateOps(Ops arg) {
        for (PlayerInfo lv : this.serverData.players) {
            lv.setOperator(arg.ops.contains(lv.getName()));
        }
    }

    private void uninvite(int i) {
        this.updateButtonStates();
        if (i >= 0 && i < this.serverData.players.size()) {
            PlayerInfo lv = this.serverData.players.get(i);
            this.selectedInvited = lv.getUuid();
            this.selectedInvitedIndex = i;
            RealmsConfirmScreen lv2 = new RealmsConfirmScreen(bl -> {
                if (bl) {
                    RealmsClient lv = RealmsClient.createRealmsClient();
                    try {
                        lv.uninvite(this.serverData.id, this.selectedInvited);
                    }
                    catch (RealmsServiceException lv2) {
                        LOGGER.error("Couldn't uninvite user");
                    }
                    this.deleteFromInvitedList(this.selectedInvitedIndex);
                    this.player = -1;
                    this.updateButtonStates();
                }
                this.stateChanged = true;
                this.client.openScreen(this);
            }, new LiteralText("Question"), new TranslatableText("mco.configure.world.uninvite.question").append(" '").append(lv.getName()).append("' ?"));
            this.client.openScreen(lv2);
        }
    }

    private void deleteFromInvitedList(int i) {
        this.serverData.players.remove(i);
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        this.tooltipText = null;
        this.renderBackground(arg);
        if (this.invitedObjectSelectionList != null) {
            this.invitedObjectSelectionList.render(arg, i, j, f);
        }
        int k = RealmsPlayerScreen.row(12) + 20;
        Tessellator lv = Tessellator.getInstance();
        BufferBuilder lv2 = lv.getBuffer();
        this.client.getTextureManager().bindTexture(OPTIONS_BACKGROUND);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        float g = 32.0f;
        lv2.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
        lv2.vertex(0.0, this.height, 0.0).texture(0.0f, (float)(this.height - k) / 32.0f + 0.0f).color(64, 64, 64, 255).next();
        lv2.vertex(this.width, this.height, 0.0).texture((float)this.width / 32.0f, (float)(this.height - k) / 32.0f + 0.0f).color(64, 64, 64, 255).next();
        lv2.vertex(this.width, k, 0.0).texture((float)this.width / 32.0f, 0.0f).color(64, 64, 64, 255).next();
        lv2.vertex(0.0, k, 0.0).texture(0.0f, 0.0f).color(64, 64, 64, 255).next();
        lv.draw();
        this.titleLabel.render(this, arg);
        if (this.serverData != null && this.serverData.players != null) {
            this.textRenderer.draw(arg, I18n.translate("mco.configure.world.invited", new Object[0]) + " (" + this.serverData.players.size() + ")", (float)this.column1_x, (float)RealmsPlayerScreen.row(0), 0xA0A0A0);
        } else {
            this.textRenderer.draw(arg, I18n.translate("mco.configure.world.invited", new Object[0]), (float)this.column1_x, (float)RealmsPlayerScreen.row(0), 0xA0A0A0);
        }
        super.render(arg, i, j, f);
        if (this.serverData == null) {
            return;
        }
        if (this.tooltipText != null) {
            this.renderMousehoverTooltip(arg, this.tooltipText, i, j);
        }
    }

    protected void renderMousehoverTooltip(MatrixStack arg, String string, int i, int j) {
        if (string == null) {
            return;
        }
        int k = i + 12;
        int l = j - 12;
        int m = this.textRenderer.getWidth(string);
        this.fillGradient(arg, k - 3, l - 3, k + m + 3, l + 8 + 3, -1073741824, -1073741824);
        this.textRenderer.drawWithShadow(arg, string, (float)k, (float)l, 0xFFFFFF);
    }

    private void drawRemoveIcon(MatrixStack arg, int i, int j, int k, int l) {
        boolean bl = k >= i && k <= i + 9 && l >= j && l <= j + 9 && l < RealmsPlayerScreen.row(12) + 20 && l > RealmsPlayerScreen.row(1);
        this.client.getTextureManager().bindTexture(CROSS_PLAYER_ICON);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        float f = bl ? 7.0f : 0.0f;
        DrawableHelper.drawTexture(arg, i, j, 0.0f, f, 8, 7, 8, 14);
        if (bl) {
            this.tooltipText = I18n.translate("mco.configure.world.invites.remove.tooltip", new Object[0]);
        }
    }

    private void drawOpped(MatrixStack arg, int i, int j, int k, int l) {
        boolean bl = k >= i && k <= i + 9 && l >= j && l <= j + 9 && l < RealmsPlayerScreen.row(12) + 20 && l > RealmsPlayerScreen.row(1);
        this.client.getTextureManager().bindTexture(OP_ICON);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        float f = bl ? 8.0f : 0.0f;
        DrawableHelper.drawTexture(arg, i, j, 0.0f, f, 8, 8, 8, 16);
        if (bl) {
            this.tooltipText = I18n.translate("mco.configure.world.invites.ops.tooltip", new Object[0]);
        }
    }

    private void drawNormal(MatrixStack arg, int i, int j, int k, int l) {
        boolean bl = k >= i && k <= i + 9 && l >= j && l <= j + 9 && l < RealmsPlayerScreen.row(12) + 20 && l > RealmsPlayerScreen.row(1);
        this.client.getTextureManager().bindTexture(USER_ICON);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        float f = bl ? 8.0f : 0.0f;
        DrawableHelper.drawTexture(arg, i, j, 0.0f, f, 8, 8, 8, 16);
        if (bl) {
            this.tooltipText = I18n.translate("mco.configure.world.invites.normal.tooltip", new Object[0]);
        }
    }

    @Environment(value=EnvType.CLIENT)
    class InvitedObjectSelectionListEntry
    extends AlwaysSelectedEntryListWidget.Entry<InvitedObjectSelectionListEntry> {
        private final PlayerInfo playerInfo;

        public InvitedObjectSelectionListEntry(PlayerInfo arg2) {
            this.playerInfo = arg2;
        }

        @Override
        public void render(MatrixStack arg, int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
            this.renderInvitedItem(arg, this.playerInfo, k, j, n, o);
        }

        private void renderInvitedItem(MatrixStack arg, PlayerInfo arg2, int i, int j, int k, int l) {
            int o;
            if (!arg2.getAccepted()) {
                int m = 0xA0A0A0;
            } else if (arg2.getOnline()) {
                int n = 0x7FFF7F;
            } else {
                o = 0xFFFFFF;
            }
            RealmsPlayerScreen.this.textRenderer.draw(arg, arg2.getName(), (float)(RealmsPlayerScreen.this.column1_x + 3 + 12), (float)(j + 1), o);
            if (arg2.isOperator()) {
                RealmsPlayerScreen.this.drawOpped(arg, RealmsPlayerScreen.this.column1_x + RealmsPlayerScreen.this.column_width - 10, j + 1, k, l);
            } else {
                RealmsPlayerScreen.this.drawNormal(arg, RealmsPlayerScreen.this.column1_x + RealmsPlayerScreen.this.column_width - 10, j + 1, k, l);
            }
            RealmsPlayerScreen.this.drawRemoveIcon(arg, RealmsPlayerScreen.this.column1_x + RealmsPlayerScreen.this.column_width - 22, j + 2, k, l);
            RealmsPlayerScreen.this.textRenderer.draw(arg, I18n.translate("mco.configure.world.activityfeed.disabled", new Object[0]), (float)RealmsPlayerScreen.this.column2_x, (float)RealmsPlayerScreen.row(5), 0xA0A0A0);
            RealmsTextureManager.withBoundFace(arg2.getUuid(), () -> {
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                DrawableHelper.drawTexture(arg, RealmsPlayerScreen.this.column1_x + 2 + 2, j + 1, 8, 8, 8.0f, 8.0f, 8, 8, 64, 64);
                DrawableHelper.drawTexture(arg, RealmsPlayerScreen.this.column1_x + 2 + 2, j + 1, 8, 8, 40.0f, 8.0f, 8, 8, 64, 64);
            });
        }
    }

    @Environment(value=EnvType.CLIENT)
    class InvitedObjectSelectionList
    extends RealmsObjectSelectionList<InvitedObjectSelectionListEntry> {
        public InvitedObjectSelectionList() {
            super(RealmsPlayerScreen.this.column_width + 10, RealmsPlayerScreen.row(12) + 20, RealmsPlayerScreen.row(1), RealmsPlayerScreen.row(12) + 20, 13);
        }

        public void addEntry(PlayerInfo arg) {
            this.addEntry(new InvitedObjectSelectionListEntry(arg));
        }

        @Override
        public int getRowWidth() {
            return (int)((double)this.width * 1.0);
        }

        @Override
        public boolean isFocused() {
            return RealmsPlayerScreen.this.getFocused() == this;
        }

        @Override
        public boolean mouseClicked(double d, double e, int i) {
            if (i == 0 && d < (double)this.getScrollbarPositionX() && e >= (double)this.top && e <= (double)this.bottom) {
                int j = RealmsPlayerScreen.this.column1_x;
                int k = RealmsPlayerScreen.this.column1_x + RealmsPlayerScreen.this.column_width;
                int l = (int)Math.floor(e - (double)this.top) - this.headerHeight + (int)this.getScrollAmount() - 4;
                int m = l / this.itemHeight;
                if (d >= (double)j && d <= (double)k && m >= 0 && l >= 0 && m < this.getItemCount()) {
                    this.setSelected(m);
                    this.itemClicked(l, m, d, e, this.width);
                }
                return true;
            }
            return super.mouseClicked(d, e, i);
        }

        @Override
        public void itemClicked(int i, int j, double d, double e, int k) {
            if (j < 0 || j > ((RealmsPlayerScreen)RealmsPlayerScreen.this).serverData.players.size() || RealmsPlayerScreen.this.tooltipText == null) {
                return;
            }
            if (RealmsPlayerScreen.this.tooltipText.equals(I18n.translate("mco.configure.world.invites.ops.tooltip", new Object[0])) || RealmsPlayerScreen.this.tooltipText.equals(I18n.translate("mco.configure.world.invites.normal.tooltip", new Object[0]))) {
                if (((RealmsPlayerScreen)RealmsPlayerScreen.this).serverData.players.get(j).isOperator()) {
                    RealmsPlayerScreen.this.deop(j);
                } else {
                    RealmsPlayerScreen.this.op(j);
                }
            } else if (RealmsPlayerScreen.this.tooltipText.equals(I18n.translate("mco.configure.world.invites.remove.tooltip", new Object[0]))) {
                RealmsPlayerScreen.this.uninvite(j);
            }
        }

        @Override
        public void setSelected(int i) {
            this.setSelectedItem(i);
            if (i != -1) {
                Realms.narrateNow(I18n.translate("narrator.select", ((RealmsPlayerScreen)RealmsPlayerScreen.this).serverData.players.get(i).getName()));
            }
            this.selectInviteListItem(i);
        }

        public void selectInviteListItem(int i) {
            RealmsPlayerScreen.this.player = i;
            RealmsPlayerScreen.this.updateButtonStates();
        }

        @Override
        public void setSelected(@Nullable InvitedObjectSelectionListEntry arg) {
            super.setSelected(arg);
            RealmsPlayerScreen.this.player = this.children().indexOf(arg);
            RealmsPlayerScreen.this.updateButtonStates();
        }

        @Override
        public void renderBackground(MatrixStack arg) {
            RealmsPlayerScreen.this.renderBackground(arg);
        }

        @Override
        public int getScrollbarPositionX() {
            return RealmsPlayerScreen.this.column1_x + this.width - 5;
        }

        @Override
        public int getMaxPosition() {
            return this.getItemCount() * 13;
        }
    }
}

