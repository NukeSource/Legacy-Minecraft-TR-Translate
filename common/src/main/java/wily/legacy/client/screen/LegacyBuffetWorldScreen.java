package wily.legacy.client.screen;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import wily.legacy.util.ScreenUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Environment(value=EnvType.CLIENT)
public class LegacyBuffetWorldScreen extends PanelVListScreen {
    private final Consumer<Holder<Biome>> applySettings;
    protected Holder<Biome> selectedBiome;

    public LegacyBuffetWorldScreen(Screen screen, HolderLookup.RegistryLookup<Biome> biomeGetter, Consumer<Holder<Biome>> consumer) {
        super(screen,282,248,Component.translatable("createWorld.customize.buffet.title"));
        parent = Minecraft.getInstance().screen instanceof WorldMoreOptionsScreen s ? s : screen;
        renderableVList.layoutSpacing(l->0);
        this.applySettings = consumer;
        biomeGetter.listElements().forEach(this::addBiome);
    }
    public void addBiome(Holder.Reference<Biome> biome){
        renderableVList.addRenderable(new AbstractButton(0,0,260,30, Component.translatable("biome."+biome.key().location().toLanguageKey())) {
            @Override
            public void onPress() {
                selectedBiome = biome;
            }

            @Override
            protected void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
                super.renderWidget(guiGraphics, i, j, f);
                RenderSystem.enableBlend();
                guiGraphics.blitSprite(TickBox.SPRITES[isHoveredOrFocused() ? 1 : 0], this.getX() + 6, this.getY() + (height - 12) / 2, 12, 12);
                if (selectedBiome == biome) guiGraphics.blitSprite(TickBox.TICK_SPRITE, this.getX() + 6, this.getY()  + (height - 12) / 2, 14, 12);
                RenderSystem.disableBlend();
            }
            @Override
            protected void renderScrollingString(GuiGraphics guiGraphics, Font font, int i, int j) {
                int k = this.getX() + 54;
                int l = this.getX() + this.getWidth();
                TickBox.renderScrollingString(guiGraphics, font, this.getMessage(), k, this.getY(), l, this.getY() + this.getHeight(), j,true);
            }
            @Override
            protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

            }
        });
    }

    public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
        ScreenUtil.renderDefaultBackground(guiGraphics,false);
    }
    @Override
    protected void init() {
        panel.height = Math.min(height,248);
        addRenderableOnly(panel);
        panel.init();
        addRenderableOnly(((guiGraphics, i, j, f) -> ScreenUtil.renderPanelRecess(guiGraphics, panel.x + 7, panel.y + 7, panel.width - 14, panel.height - 14, 2)));
        getRenderableVList().init(this,panel.x + 11,panel.y + 11,260, panel.height - 5);
    }
    @Override
    public boolean mouseScrolled(double d, double e, double f, double g) {
        getRenderableVList().mouseScrolled(d,e,f,g);
        return super.mouseScrolled(d, e, f, g);
    }

    @Override
    public void onClose() {
        super.onClose();
        if (selectedBiome != null) applySettings.accept(selectedBiome);
    }
}

