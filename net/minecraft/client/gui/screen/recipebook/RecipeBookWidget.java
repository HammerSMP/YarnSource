/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet
 *  it.unimi.dsi.fastutil.objects.ObjectSet
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.recipebook;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookGhostSlots;
import net.minecraft.client.gui.screen.recipebook.RecipeBookResults;
import net.minecraft.client.gui.screen.recipebook.RecipeDisplayListener;
import net.minecraft.client.gui.screen.recipebook.RecipeGroupButtonWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.client.search.SearchManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.RecipeBookDataC2SPacket;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.RecipeGridAligner;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class RecipeBookWidget
extends DrawableHelper
implements Drawable,
Element,
RecipeDisplayListener,
RecipeGridAligner<Ingredient> {
    protected static final Identifier TEXTURE = new Identifier("textures/gui/recipe_book.png");
    private static final Text field_25711 = new TranslatableText("gui.recipebook.search_hint").formatted(Formatting.ITALIC).formatted(Formatting.GRAY);
    private int leftOffset;
    private int parentWidth;
    private int parentHeight;
    protected final RecipeBookGhostSlots ghostSlots = new RecipeBookGhostSlots();
    private final List<RecipeGroupButtonWidget> tabButtons = Lists.newArrayList();
    private RecipeGroupButtonWidget currentTab;
    protected ToggleButtonWidget toggleCraftableButton;
    protected AbstractRecipeScreenHandler<?> craftingScreenHandler;
    protected MinecraftClient client;
    private TextFieldWidget searchField;
    private String searchText = "";
    protected ClientRecipeBook recipeBook;
    protected final RecipeBookResults recipesArea = new RecipeBookResults();
    protected final RecipeFinder recipeFinder = new RecipeFinder();
    private int cachedInvChangeCount;
    private boolean searching;

    public void initialize(int i, int j, MinecraftClient arg, boolean bl, AbstractRecipeScreenHandler<?> arg2) {
        this.client = arg;
        this.parentWidth = i;
        this.parentHeight = j;
        this.craftingScreenHandler = arg2;
        arg.player.currentScreenHandler = arg2;
        this.recipeBook = arg.player.getRecipeBook();
        this.cachedInvChangeCount = arg.player.inventory.getChangeCount();
        if (this.isOpen()) {
            this.reset(bl);
        }
        arg.keyboard.enableRepeatEvents(true);
    }

    public void reset(boolean bl) {
        this.leftOffset = bl ? 0 : 86;
        int i = (this.parentWidth - 147) / 2 - this.leftOffset;
        int j = (this.parentHeight - 166) / 2;
        this.recipeFinder.clear();
        this.client.player.inventory.populateRecipeFinder(this.recipeFinder);
        this.craftingScreenHandler.populateRecipeFinder(this.recipeFinder);
        String string = this.searchField != null ? this.searchField.getText() : "";
        this.client.textRenderer.getClass();
        this.searchField = new TextFieldWidget(this.client.textRenderer, i + 25, j + 14, 80, 9 + 5, new TranslatableText("itemGroup.search"));
        this.searchField.setMaxLength(50);
        this.searchField.setHasBorder(false);
        this.searchField.setVisible(true);
        this.searchField.setEditableColor(0xFFFFFF);
        this.searchField.setText(string);
        this.recipesArea.initialize(this.client, i, j);
        this.recipesArea.setGui(this);
        this.toggleCraftableButton = new ToggleButtonWidget(i + 110, j + 12, 26, 16, this.recipeBook.isFilteringCraftable(this.craftingScreenHandler));
        this.setBookButtonTexture();
        this.tabButtons.clear();
        for (RecipeBookGroup lv : ClientRecipeBook.getGroups(this.craftingScreenHandler)) {
            this.tabButtons.add(new RecipeGroupButtonWidget(lv));
        }
        if (this.currentTab != null) {
            this.currentTab = this.tabButtons.stream().filter(arg -> arg.getCategory().equals((Object)this.currentTab.getCategory())).findFirst().orElse(null);
        }
        if (this.currentTab == null) {
            this.currentTab = this.tabButtons.get(0);
        }
        this.currentTab.setToggled(true);
        this.refreshResults(false);
        this.refreshTabButtons();
    }

    @Override
    public boolean changeFocus(boolean bl) {
        return false;
    }

    protected void setBookButtonTexture() {
        this.toggleCraftableButton.setTextureUV(152, 41, 28, 18, TEXTURE);
    }

    public void close() {
        this.searchField = null;
        this.currentTab = null;
        this.client.keyboard.enableRepeatEvents(false);
    }

    public int findLeftEdge(boolean bl, int i, int j) {
        int l;
        if (this.isOpen() && !bl) {
            int k = 177 + (i - j - 200) / 2;
        } else {
            l = (i - j) / 2;
        }
        return l;
    }

    public void toggleOpen() {
        this.setOpen(!this.isOpen());
    }

    public boolean isOpen() {
        return this.recipeBook.isGuiOpen();
    }

    protected void setOpen(boolean bl) {
        this.recipeBook.setGuiOpen(bl);
        if (!bl) {
            this.recipesArea.hideAlternates();
        }
        this.sendBookDataPacket();
    }

    public void slotClicked(@Nullable Slot arg) {
        if (arg != null && arg.id < this.craftingScreenHandler.getCraftingSlotCount()) {
            this.ghostSlots.reset();
            if (this.isOpen()) {
                this.refreshInputs();
            }
        }
    }

    private void refreshResults(boolean bl) {
        List<RecipeResultCollection> list = this.recipeBook.getResultsForGroup(this.currentTab.getCategory());
        list.forEach(arg -> arg.computeCraftables(this.recipeFinder, this.craftingScreenHandler.getCraftingWidth(), this.craftingScreenHandler.getCraftingHeight(), this.recipeBook));
        ArrayList list2 = Lists.newArrayList(list);
        list2.removeIf(arg -> !arg.isInitialized());
        list2.removeIf(arg -> !arg.hasFittingRecipes());
        String string = this.searchField.getText();
        if (!string.isEmpty()) {
            ObjectLinkedOpenHashSet objectSet = new ObjectLinkedOpenHashSet(this.client.getSearchableContainer(SearchManager.RECIPE_OUTPUT).findAll(string.toLowerCase(Locale.ROOT)));
            list2.removeIf(arg_0 -> RecipeBookWidget.method_2594((ObjectSet)objectSet, arg_0));
        }
        if (this.recipeBook.isFilteringCraftable(this.craftingScreenHandler)) {
            list2.removeIf(arg -> !arg.hasCraftableRecipes());
        }
        this.recipesArea.setResults(list2, bl);
    }

    private void refreshTabButtons() {
        int i = (this.parentWidth - 147) / 2 - this.leftOffset - 30;
        int j = (this.parentHeight - 166) / 2 + 3;
        int k = 27;
        int l = 0;
        for (RecipeGroupButtonWidget lv : this.tabButtons) {
            RecipeBookGroup lv2 = lv.getCategory();
            if (lv2 == RecipeBookGroup.SEARCH || lv2 == RecipeBookGroup.FURNACE_SEARCH) {
                lv.visible = true;
                lv.setPos(i, j + 27 * l++);
                continue;
            }
            if (!lv.hasKnownRecipes(this.recipeBook)) continue;
            lv.setPos(i, j + 27 * l++);
            lv.checkForNewRecipes(this.client);
        }
    }

    public void update() {
        if (!this.isOpen()) {
            return;
        }
        if (this.cachedInvChangeCount != this.client.player.inventory.getChangeCount()) {
            this.refreshInputs();
            this.cachedInvChangeCount = this.client.player.inventory.getChangeCount();
        }
    }

    private void refreshInputs() {
        this.recipeFinder.clear();
        this.client.player.inventory.populateRecipeFinder(this.recipeFinder);
        this.craftingScreenHandler.populateRecipeFinder(this.recipeFinder);
        this.refreshResults(false);
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        if (!this.isOpen()) {
            return;
        }
        RenderSystem.pushMatrix();
        RenderSystem.translatef(0.0f, 0.0f, 100.0f);
        this.client.getTextureManager().bindTexture(TEXTURE);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        int k = (this.parentWidth - 147) / 2 - this.leftOffset;
        int l = (this.parentHeight - 166) / 2;
        this.drawTexture(arg, k, l, 1, 1, 147, 166);
        if (!this.searchField.isFocused() && this.searchField.getText().isEmpty()) {
            this.drawTextWithShadow(arg, this.client.textRenderer, field_25711, k + 25, l + 14, -1);
        } else {
            this.searchField.render(arg, i, j, f);
        }
        for (RecipeGroupButtonWidget lv : this.tabButtons) {
            lv.render(arg, i, j, f);
        }
        this.toggleCraftableButton.render(arg, i, j, f);
        this.recipesArea.draw(arg, k, l, i, j, f);
        RenderSystem.popMatrix();
    }

    public void drawTooltip(MatrixStack arg, int i, int j, int k, int l) {
        if (!this.isOpen()) {
            return;
        }
        this.recipesArea.drawTooltip(arg, k, l);
        if (this.toggleCraftableButton.isHovered()) {
            Text lv = this.getCraftableButtonText();
            if (this.client.currentScreen != null) {
                this.client.currentScreen.renderTooltip(arg, lv, k, l);
            }
        }
        this.drawGhostSlotTooltip(arg, i, j, k, l);
    }

    protected Text getCraftableButtonText() {
        return new TranslatableText(this.toggleCraftableButton.isToggled() ? "gui.recipebook.toggleRecipes.craftable" : "gui.recipebook.toggleRecipes.all");
    }

    private void drawGhostSlotTooltip(MatrixStack arg, int i, int j, int k, int l) {
        ItemStack lv = null;
        for (int m = 0; m < this.ghostSlots.getSlotCount(); ++m) {
            RecipeBookGhostSlots.GhostInputSlot lv2 = this.ghostSlots.getSlot(m);
            int n = lv2.getX() + i;
            int o = lv2.getY() + j;
            if (k < n || l < o || k >= n + 16 || l >= o + 16) continue;
            lv = lv2.getCurrentItemStack();
        }
        if (lv != null && this.client.currentScreen != null) {
            this.client.currentScreen.renderTooltip(arg, this.client.currentScreen.getTooltipFromItem(lv), k, l);
        }
    }

    public void drawGhostSlots(MatrixStack arg, int i, int j, boolean bl, float f) {
        this.ghostSlots.draw(arg, this.client, i, j, bl, f);
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        if (!this.isOpen() || this.client.player.isSpectator()) {
            return false;
        }
        if (this.recipesArea.mouseClicked(d, e, i, (this.parentWidth - 147) / 2 - this.leftOffset, (this.parentHeight - 166) / 2, 147, 166)) {
            Recipe<?> lv = this.recipesArea.getLastClickedRecipe();
            RecipeResultCollection lv2 = this.recipesArea.getLastClickedResults();
            if (lv != null && lv2 != null) {
                if (!lv2.isCraftable(lv) && this.ghostSlots.getRecipe() == lv) {
                    return false;
                }
                this.ghostSlots.reset();
                this.client.interactionManager.clickRecipe(this.client.player.currentScreenHandler.syncId, lv, Screen.hasShiftDown());
                if (!this.isWide()) {
                    this.setOpen(false);
                }
            }
            return true;
        }
        if (this.searchField.mouseClicked(d, e, i)) {
            return true;
        }
        if (this.toggleCraftableButton.mouseClicked(d, e, i)) {
            boolean bl = this.toggleFilteringCraftable();
            this.toggleCraftableButton.setToggled(bl);
            this.sendBookDataPacket();
            this.refreshResults(false);
            return true;
        }
        for (RecipeGroupButtonWidget lv3 : this.tabButtons) {
            if (!lv3.mouseClicked(d, e, i)) continue;
            if (this.currentTab != lv3) {
                this.currentTab.setToggled(false);
                this.currentTab = lv3;
                this.currentTab.setToggled(true);
                this.refreshResults(true);
            }
            return true;
        }
        return false;
    }

    protected boolean toggleFilteringCraftable() {
        boolean bl = !this.recipeBook.isFilteringCraftable();
        this.recipeBook.setFilteringCraftable(bl);
        return bl;
    }

    public boolean isClickOutsideBounds(double d, double e, int i, int j, int k, int l, int m) {
        if (!this.isOpen()) {
            return true;
        }
        boolean bl = d < (double)i || e < (double)j || d >= (double)(i + k) || e >= (double)(j + l);
        boolean bl2 = (double)(i - 147) < d && d < (double)i && (double)j < e && e < (double)(j + l);
        return bl && !bl2 && !this.currentTab.isHovered();
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        this.searching = false;
        if (!this.isOpen() || this.client.player.isSpectator()) {
            return false;
        }
        if (i == 256 && !this.isWide()) {
            this.setOpen(false);
            return true;
        }
        if (this.searchField.keyPressed(i, j, k)) {
            this.refreshSearchResults();
            return true;
        }
        if (this.searchField.isFocused() && this.searchField.isVisible() && i != 256) {
            return true;
        }
        if (this.client.options.keyChat.matchesKey(i, j) && !this.searchField.isFocused()) {
            this.searching = true;
            this.searchField.setSelected(true);
            return true;
        }
        return false;
    }

    @Override
    public boolean keyReleased(int i, int j, int k) {
        this.searching = false;
        return Element.super.keyReleased(i, j, k);
    }

    @Override
    public boolean charTyped(char c, int i) {
        if (this.searching) {
            return false;
        }
        if (!this.isOpen() || this.client.player.isSpectator()) {
            return false;
        }
        if (this.searchField.charTyped(c, i)) {
            this.refreshSearchResults();
            return true;
        }
        return Element.super.charTyped(c, i);
    }

    @Override
    public boolean isMouseOver(double d, double e) {
        return false;
    }

    private void refreshSearchResults() {
        String string = this.searchField.getText().toLowerCase(Locale.ROOT);
        this.triggerPirateSpeakEasterEgg(string);
        if (!string.equals(this.searchText)) {
            this.refreshResults(false);
            this.searchText = string;
        }
    }

    private void triggerPirateSpeakEasterEgg(String string) {
        if ("excitedze".equals(string)) {
            LanguageManager lv = this.client.getLanguageManager();
            LanguageDefinition lv2 = lv.getLanguage("en_pt");
            if (lv.getLanguage().compareTo(lv2) == 0) {
                return;
            }
            lv.setLanguage(lv2);
            this.client.options.language = lv2.getCode();
            this.client.reloadResources();
            this.client.options.write();
        }
    }

    private boolean isWide() {
        return this.leftOffset == 86;
    }

    public void refresh() {
        this.refreshTabButtons();
        if (this.isOpen()) {
            this.refreshResults(false);
        }
    }

    @Override
    public void onRecipesDisplayed(List<Recipe<?>> list) {
        for (Recipe<?> lv : list) {
            this.client.player.onRecipeDisplayed(lv);
        }
    }

    public void showGhostRecipe(Recipe<?> arg, List<Slot> list) {
        ItemStack lv = arg.getOutput();
        this.ghostSlots.setRecipe(arg);
        this.ghostSlots.addSlot(Ingredient.ofStacks(lv), list.get((int)0).x, list.get((int)0).y);
        this.alignRecipeToGrid(this.craftingScreenHandler.getCraftingWidth(), this.craftingScreenHandler.getCraftingHeight(), this.craftingScreenHandler.getCraftingResultSlotIndex(), arg, arg.getPreviewInputs().iterator(), 0);
    }

    @Override
    public void acceptAlignedInput(Iterator<Ingredient> iterator, int i, int j, int k, int l) {
        Ingredient lv = iterator.next();
        if (!lv.isEmpty()) {
            Slot lv2 = (Slot)this.craftingScreenHandler.slots.get(i);
            this.ghostSlots.addSlot(lv, lv2.x, lv2.y);
        }
    }

    protected void sendBookDataPacket() {
        if (this.client.getNetworkHandler() != null) {
            this.client.getNetworkHandler().sendPacket(new RecipeBookDataC2SPacket(this.recipeBook.isGuiOpen(), this.recipeBook.isFilteringCraftable(), this.recipeBook.isFurnaceGuiOpen(), this.recipeBook.isFurnaceFilteringCraftable(), this.recipeBook.isBlastFurnaceGuiOpen(), this.recipeBook.isBlastFurnaceFilteringCraftable()));
        }
    }

    private static /* synthetic */ boolean method_2594(ObjectSet objectSet, RecipeResultCollection arg) {
        return !objectSet.contains((Object)arg);
    }
}

