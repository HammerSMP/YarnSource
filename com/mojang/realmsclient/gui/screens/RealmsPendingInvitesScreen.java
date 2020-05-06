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
package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.PendingInvite;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.screens.RealmsAcceptRejectButton;
import com.mojang.realmsclient.util.RealmsTextureManager;
import com.mojang.realmsclient.util.RealmsUtil;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsPendingInvitesScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Identifier field_22702 = new Identifier("realms", "textures/gui/realms/accept_icon.png");
    private static final Identifier field_22703 = new Identifier("realms", "textures/gui/realms/reject_icon.png");
    private final Screen lastScreen;
    private String toolTip;
    private boolean loaded;
    private PendingInvitationSelectionList pendingInvitationSelectionList;
    private RealmsLabel titleLabel;
    private int selectedInvite = -1;
    private ButtonWidget acceptButton;
    private ButtonWidget rejectButton;

    public RealmsPendingInvitesScreen(Screen arg) {
        this.lastScreen = arg;
    }

    @Override
    public void init() {
        this.client.keyboard.enableRepeatEvents(true);
        this.pendingInvitationSelectionList = new PendingInvitationSelectionList();
        new Thread("Realms-pending-invitations-fetcher"){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void run() {
                RealmsClient lv = RealmsClient.createRealmsClient();
                try {
                    List<PendingInvite> list = lv.pendingInvites().pendingInvites;
                    List list2 = list.stream().map(arg -> new PendingInvitationSelectionListEntry((PendingInvite)arg)).collect(Collectors.toList());
                    RealmsPendingInvitesScreen.this.client.execute(() -> RealmsPendingInvitesScreen.this.pendingInvitationSelectionList.replaceEntries(list2));
                }
                catch (RealmsServiceException lv2) {
                    LOGGER.error("Couldn't list invites");
                }
                finally {
                    RealmsPendingInvitesScreen.this.loaded = true;
                }
            }
        }.start();
        this.addChild(this.pendingInvitationSelectionList);
        this.acceptButton = this.addButton(new ButtonWidget(this.width / 2 - 174, this.height - 32, 100, 20, new TranslatableText("mco.invites.button.accept"), arg -> {
            this.accept(this.selectedInvite);
            this.selectedInvite = -1;
            this.updateButtonStates();
        }));
        this.addButton(new ButtonWidget(this.width / 2 - 50, this.height - 32, 100, 20, ScreenTexts.DONE, arg -> this.client.openScreen(new RealmsMainScreen(this.lastScreen))));
        this.rejectButton = this.addButton(new ButtonWidget(this.width / 2 + 74, this.height - 32, 100, 20, new TranslatableText("mco.invites.button.reject"), arg -> {
            this.reject(this.selectedInvite);
            this.selectedInvite = -1;
            this.updateButtonStates();
        }));
        this.titleLabel = new RealmsLabel(new TranslatableText("mco.invites.title"), this.width / 2, 12, 0xFFFFFF);
        this.addChild(this.titleLabel);
        this.narrateLabels();
        this.updateButtonStates();
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (i == 256) {
            this.client.openScreen(new RealmsMainScreen(this.lastScreen));
            return true;
        }
        return super.keyPressed(i, j, k);
    }

    private void updateList(int i) {
        this.pendingInvitationSelectionList.removeAtIndex(i);
    }

    private void reject(final int i) {
        if (i < this.pendingInvitationSelectionList.getItemCount()) {
            new Thread("Realms-reject-invitation"){

                @Override
                public void run() {
                    try {
                        RealmsClient lv = RealmsClient.createRealmsClient();
                        lv.rejectInvitation(((PendingInvitationSelectionListEntry)((PendingInvitationSelectionListEntry)((RealmsPendingInvitesScreen)RealmsPendingInvitesScreen.this).pendingInvitationSelectionList.children().get((int)i))).mPendingInvite.invitationId);
                        RealmsPendingInvitesScreen.this.client.execute(() -> RealmsPendingInvitesScreen.this.updateList(i));
                    }
                    catch (RealmsServiceException lv2) {
                        LOGGER.error("Couldn't reject invite");
                    }
                }
            }.start();
        }
    }

    private void accept(final int i) {
        if (i < this.pendingInvitationSelectionList.getItemCount()) {
            new Thread("Realms-accept-invitation"){

                @Override
                public void run() {
                    try {
                        RealmsClient lv = RealmsClient.createRealmsClient();
                        lv.acceptInvitation(((PendingInvitationSelectionListEntry)((PendingInvitationSelectionListEntry)((RealmsPendingInvitesScreen)RealmsPendingInvitesScreen.this).pendingInvitationSelectionList.children().get((int)i))).mPendingInvite.invitationId);
                        RealmsPendingInvitesScreen.this.client.execute(() -> RealmsPendingInvitesScreen.this.updateList(i));
                    }
                    catch (RealmsServiceException lv2) {
                        LOGGER.error("Couldn't accept invite");
                    }
                }
            }.start();
        }
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        this.toolTip = null;
        this.renderBackground(arg);
        this.pendingInvitationSelectionList.render(arg, i, j, f);
        this.titleLabel.render(this, arg);
        if (this.toolTip != null) {
            this.renderMousehoverTooltip(arg, this.toolTip, i, j);
        }
        if (this.pendingInvitationSelectionList.getItemCount() == 0 && this.loaded) {
            this.drawCenteredString(arg, this.textRenderer, I18n.translate("mco.invites.nopending", new Object[0]), this.width / 2, this.height / 2 - 20, 0xFFFFFF);
        }
        super.render(arg, i, j, f);
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

    private void updateButtonStates() {
        this.acceptButton.visible = this.shouldAcceptAndRejectButtonBeVisible(this.selectedInvite);
        this.rejectButton.visible = this.shouldAcceptAndRejectButtonBeVisible(this.selectedInvite);
    }

    private boolean shouldAcceptAndRejectButtonBeVisible(int i) {
        return i != -1;
    }

    @Environment(value=EnvType.CLIENT)
    class PendingInvitationSelectionListEntry
    extends AlwaysSelectedEntryListWidget.Entry<PendingInvitationSelectionListEntry> {
        private final PendingInvite mPendingInvite;
        private final List<RealmsAcceptRejectButton> buttons;

        PendingInvitationSelectionListEntry(PendingInvite arg2) {
            this.mPendingInvite = arg2;
            this.buttons = Arrays.asList(new AcceptButton(), new RejectButton());
        }

        @Override
        public void render(MatrixStack arg, int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
            this.renderPendingInvitationItem(arg, this.mPendingInvite, k, j, n, o);
        }

        @Override
        public boolean mouseClicked(double d, double e, int i) {
            RealmsAcceptRejectButton.handleClick(RealmsPendingInvitesScreen.this.pendingInvitationSelectionList, this, this.buttons, i, d, e);
            return true;
        }

        private void renderPendingInvitationItem(MatrixStack arg, PendingInvite arg2, int i, int j, int k, int l) {
            RealmsPendingInvitesScreen.this.textRenderer.draw(arg, arg2.worldName, (float)(i + 38), (float)(j + 1), 0xFFFFFF);
            RealmsPendingInvitesScreen.this.textRenderer.draw(arg, arg2.worldOwnerName, (float)(i + 38), (float)(j + 12), 0x6C6C6C);
            RealmsPendingInvitesScreen.this.textRenderer.draw(arg, RealmsUtil.method_25282(arg2.date), (float)(i + 38), (float)(j + 24), 0x6C6C6C);
            RealmsAcceptRejectButton.render(arg, this.buttons, RealmsPendingInvitesScreen.this.pendingInvitationSelectionList, i, j, k, l);
            RealmsTextureManager.withBoundFace(arg2.worldOwnerUuid, () -> {
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                DrawableHelper.drawTexture(arg, i, j, 32, 32, 8.0f, 8.0f, 8, 8, 64, 64);
                DrawableHelper.drawTexture(arg, i, j, 32, 32, 40.0f, 8.0f, 8, 8, 64, 64);
            });
        }

        @Environment(value=EnvType.CLIENT)
        class RejectButton
        extends RealmsAcceptRejectButton {
            RejectButton() {
                super(15, 15, 235, 5);
            }

            @Override
            protected void render(MatrixStack arg, int i, int j, boolean bl) {
                RealmsPendingInvitesScreen.this.client.getTextureManager().bindTexture(field_22703);
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                float f = bl ? 19.0f : 0.0f;
                DrawableHelper.drawTexture(arg, i, j, f, 0.0f, 18, 18, 37, 18);
                if (bl) {
                    RealmsPendingInvitesScreen.this.toolTip = I18n.translate("mco.invites.button.reject", new Object[0]);
                }
            }

            @Override
            public void handleClick(int i) {
                RealmsPendingInvitesScreen.this.reject(i);
            }
        }

        @Environment(value=EnvType.CLIENT)
        class AcceptButton
        extends RealmsAcceptRejectButton {
            AcceptButton() {
                super(15, 15, 215, 5);
            }

            @Override
            protected void render(MatrixStack arg, int i, int j, boolean bl) {
                RealmsPendingInvitesScreen.this.client.getTextureManager().bindTexture(field_22702);
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                float f = bl ? 19.0f : 0.0f;
                DrawableHelper.drawTexture(arg, i, j, f, 0.0f, 18, 18, 37, 18);
                if (bl) {
                    RealmsPendingInvitesScreen.this.toolTip = I18n.translate("mco.invites.button.accept", new Object[0]);
                }
            }

            @Override
            public void handleClick(int i) {
                RealmsPendingInvitesScreen.this.accept(i);
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    class PendingInvitationSelectionList
    extends RealmsObjectSelectionList<PendingInvitationSelectionListEntry> {
        public PendingInvitationSelectionList() {
            super(RealmsPendingInvitesScreen.this.width, RealmsPendingInvitesScreen.this.height, 32, RealmsPendingInvitesScreen.this.height - 40, 36);
        }

        public void removeAtIndex(int i) {
            this.remove(i);
        }

        @Override
        public int getMaxPosition() {
            return this.getItemCount() * 36;
        }

        @Override
        public int getRowWidth() {
            return 260;
        }

        @Override
        public boolean isFocused() {
            return RealmsPendingInvitesScreen.this.getFocused() == this;
        }

        @Override
        public void renderBackground(MatrixStack arg) {
            RealmsPendingInvitesScreen.this.renderBackground(arg);
        }

        @Override
        public void setSelected(int i) {
            this.setSelectedItem(i);
            if (i != -1) {
                List list = RealmsPendingInvitesScreen.this.pendingInvitationSelectionList.children();
                PendingInvite lv = ((PendingInvitationSelectionListEntry)list.get(i)).mPendingInvite;
                String string = I18n.translate("narrator.select.list.position", i + 1, list.size());
                String string2 = Realms.joinNarrations(Arrays.asList(lv.worldName, lv.worldOwnerName, RealmsUtil.method_25282(lv.date), string));
                Realms.narrateNow(I18n.translate("narrator.select", string2));
            }
            this.selectInviteListItem(i);
        }

        public void selectInviteListItem(int i) {
            RealmsPendingInvitesScreen.this.selectedInvite = i;
            RealmsPendingInvitesScreen.this.updateButtonStates();
        }

        @Override
        public void setSelected(@Nullable PendingInvitationSelectionListEntry arg) {
            super.setSelected(arg);
            RealmsPendingInvitesScreen.this.selectedInvite = this.children().indexOf(arg);
            RealmsPendingInvitesScreen.this.updateButtonStates();
        }
    }
}

