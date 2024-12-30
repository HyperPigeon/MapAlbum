package net.hyper_pigeon.map_album.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class AlbumInventoryScreen extends HandledScreen<AlbumInventoryScreenHandler> implements ScreenHandlerProvider<AlbumInventoryScreenHandler> {
    private static final Identifier TEXTURE = Identifier.of("textures/gui/container/generic_54.png");

    public AlbumInventoryScreen(AlbumInventoryScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundHeight = 114 + 6 * 18;
        this.playerInventoryTitleY = this.backgroundHeight - 94;
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        this.renderBackground(ctx, mouseX, mouseY, delta);
        super.render(ctx, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(ctx, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(DrawContext ctx, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        var i = (this.width - this.backgroundWidth) / 2;
        var j = (this.height - this.backgroundHeight) / 2;
        ctx.drawTexture(TEXTURE, i, j, 0, 0, this.backgroundWidth, 125);
        ctx.drawTexture(TEXTURE, i, j + 125, 0, 126, this.backgroundWidth, 96);
    }

}
