/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.source.BiomeSourceType;
import net.minecraft.world.level.LevelGeneratorOptions;
import net.minecraft.world.level.LevelGeneratorType;

@Environment(value=EnvType.CLIENT)
public class CustomizeBuffetLevelScreen
extends Screen {
    private static final List<Identifier> CHUNK_GENERATOR_TYPES = Registry.CHUNK_GENERATOR_TYPE.getIds().stream().filter(arg -> Registry.CHUNK_GENERATOR_TYPE.get((Identifier)arg).isBuffetScreenOption()).collect(Collectors.toList());
    private final CreateWorldScreen parent;
    private final CompoundTag generatorOptionsTag;
    private BuffetBiomesListWidget biomeSelectionList;
    private int biomeListLength;
    private ButtonWidget confirmButton;

    public CustomizeBuffetLevelScreen(CreateWorldScreen arg, LevelGeneratorOptions arg2) {
        super(new TranslatableText("createWorld.customize.buffet.title"));
        this.parent = arg;
        this.generatorOptionsTag = arg2.getType() == LevelGeneratorType.BUFFET ? (CompoundTag)arg2.getDynamic().convert((DynamicOps)NbtOps.INSTANCE).getValue() : new CompoundTag();
    }

    @Override
    protected void init() {
        this.client.keyboard.enableRepeatEvents(true);
        this.addButton(new ButtonWidget((this.width - 200) / 2, 40, 200, 20, CustomizeBuffetLevelScreen.method_27569(this.biomeListLength), arg -> {
            ++this.biomeListLength;
            if (this.biomeListLength >= CHUNK_GENERATOR_TYPES.size()) {
                this.biomeListLength = 0;
            }
            arg.setMessage(CustomizeBuffetLevelScreen.method_27569(this.biomeListLength));
        }));
        this.biomeSelectionList = new BuffetBiomesListWidget();
        this.children.add(this.biomeSelectionList);
        this.confirmButton = this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, ScreenTexts.DONE, arg -> {
            this.parent.generatorOptions = LevelGeneratorType.BUFFET.loadOptions(new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)this.getGeneratorTag()));
            this.client.openScreen(this.parent);
        }));
        this.addButton(new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, ScreenTexts.CANCEL, arg -> this.client.openScreen(this.parent)));
        this.initListSelectLogic();
        this.refreshConfirmButton();
    }

    private static Text method_27569(int i) {
        return new TranslatableText("createWorld.customize.buffet.generatortype").append(" ").append(new TranslatableText(Util.createTranslationKey("generator", CHUNK_GENERATOR_TYPES.get(i))));
    }

    private void initListSelectLogic() {
        if (this.generatorOptionsTag.contains("chunk_generator", 10) && this.generatorOptionsTag.getCompound("chunk_generator").contains("type", 8)) {
            Identifier lv = new Identifier(this.generatorOptionsTag.getCompound("chunk_generator").getString("type"));
            for (int i = 0; i < CHUNK_GENERATOR_TYPES.size(); ++i) {
                if (!CHUNK_GENERATOR_TYPES.get(i).equals(lv)) continue;
                this.biomeListLength = i;
                break;
            }
        }
        if (this.generatorOptionsTag.contains("biome_source", 10) && this.generatorOptionsTag.getCompound("biome_source").contains("biomes", 9)) {
            ListTag lv2 = this.generatorOptionsTag.getCompound("biome_source").getList("biomes", 8);
            for (int j = 0; j < lv2.size(); ++j) {
                Identifier lv3 = new Identifier(lv2.getString(j));
                this.biomeSelectionList.setSelected((BuffetBiomesListWidget.BuffetBiomeItem)this.biomeSelectionList.children().stream().filter(arg2 -> Objects.equals(((BuffetBiomesListWidget.BuffetBiomeItem)arg2).biome, lv3)).findFirst().orElse(null));
            }
        }
        this.generatorOptionsTag.remove("chunk_generator");
        this.generatorOptionsTag.remove("biome_source");
    }

    private CompoundTag getGeneratorTag() {
        CompoundTag lv = new CompoundTag();
        CompoundTag lv2 = new CompoundTag();
        lv2.putString("type", Registry.BIOME_SOURCE_TYPE.getId(BiomeSourceType.FIXED).toString());
        CompoundTag lv3 = new CompoundTag();
        ListTag lv4 = new ListTag();
        lv4.add(StringTag.of(((BuffetBiomesListWidget.BuffetBiomeItem)this.biomeSelectionList.getSelected()).biome.toString()));
        lv3.put("biomes", lv4);
        lv2.put("options", lv3);
        CompoundTag lv5 = new CompoundTag();
        CompoundTag lv6 = new CompoundTag();
        lv5.putString("type", CHUNK_GENERATOR_TYPES.get(this.biomeListLength).toString());
        lv6.putString("default_block", "minecraft:stone");
        lv6.putString("default_fluid", "minecraft:water");
        lv5.put("options", lv6);
        lv.put("biome_source", lv2);
        lv.put("chunk_generator", lv5);
        return lv;
    }

    public void refreshConfirmButton() {
        this.confirmButton.active = this.biomeSelectionList.getSelected() != null;
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        this.renderDirtBackground(0);
        this.biomeSelectionList.render(arg, i, j, f);
        this.drawStringWithShadow(arg, this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
        this.drawCenteredString(arg, this.textRenderer, I18n.translate("createWorld.customize.buffet.generator", new Object[0]), this.width / 2, 30, 0xA0A0A0);
        this.drawCenteredString(arg, this.textRenderer, I18n.translate("createWorld.customize.buffet.biome", new Object[0]), this.width / 2, 68, 0xA0A0A0);
        super.render(arg, i, j, f);
    }

    @Environment(value=EnvType.CLIENT)
    class BuffetBiomesListWidget
    extends AlwaysSelectedEntryListWidget<BuffetBiomeItem> {
        private BuffetBiomesListWidget() {
            super(CustomizeBuffetLevelScreen.this.client, CustomizeBuffetLevelScreen.this.width, CustomizeBuffetLevelScreen.this.height, 80, CustomizeBuffetLevelScreen.this.height - 37, 16);
            Registry.BIOME.getIds().stream().sorted(Comparator.comparing(arg -> Registry.BIOME.get((Identifier)arg).getName().getString())).forEach(arg -> this.addEntry(new BuffetBiomeItem((Identifier)arg)));
        }

        @Override
        protected boolean isFocused() {
            return CustomizeBuffetLevelScreen.this.getFocused() == this;
        }

        @Override
        public void setSelected(@Nullable BuffetBiomeItem arg) {
            super.setSelected(arg);
            if (arg != null) {
                NarratorManager.INSTANCE.narrate(new TranslatableText("narrator.select", Registry.BIOME.get(arg.biome).getName().getString()).getString());
            }
        }

        @Override
        protected void moveSelection(int i) {
            super.moveSelection(i);
            CustomizeBuffetLevelScreen.this.refreshConfirmButton();
        }

        @Environment(value=EnvType.CLIENT)
        class BuffetBiomeItem
        extends AlwaysSelectedEntryListWidget.Entry<BuffetBiomeItem> {
            private final Identifier biome;

            public BuffetBiomeItem(Identifier arg2) {
                this.biome = arg2;
            }

            @Override
            public void render(MatrixStack arg, int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
                BuffetBiomesListWidget.this.drawString(arg, CustomizeBuffetLevelScreen.this.textRenderer, Registry.BIOME.get(this.biome).getName().getString(), k + 5, j + 2, 0xFFFFFF);
            }

            @Override
            public boolean mouseClicked(double d, double e, int i) {
                if (i == 0) {
                    BuffetBiomesListWidget.this.setSelected(this);
                    CustomizeBuffetLevelScreen.this.refreshConfirmButton();
                    return true;
                }
                return false;
            }
        }
    }
}

