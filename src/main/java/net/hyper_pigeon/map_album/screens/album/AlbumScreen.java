package net.hyper_pigeon.map_album.screens.album;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PageTurnWidget;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.map.MapState;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;

public class AlbumScreen extends Screen {
    private static final Identifier MAP_BACKGROUND_TEXTURE = new Identifier("textures/map/map_background.png");
    public final List<Pair<Pair<Integer, String>, MapState>> mapsInfo;

    private PageTurnWidget nextPageButton;
    private PageTurnWidget previousPageButton;

    private int pageIndex = 0;

    public AlbumScreen(Text title, List<Pair<Pair<Integer, String>, MapState>> mapsInfo) {
        super(title);
        this.mapsInfo = mapsInfo;
    }

    protected void init() {
        this.addCloseButton();
        this.addPageButtons();
    }

    protected void addCloseButton() {
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, 245, 200, 20, ScreenTexts.DONE, button -> this.client.setScreen(null)));
//        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> this.close()).dimensions(this.width / 2 - 100, 245, 200, 20).build());
    }

    protected void addPageButtons() {
        int i = (this.width - 192) / 2;
        this.nextPageButton = this.addDrawableChild(new PageTurnWidget(i + 116, 220, true, button -> this.goToNextPage(), true));
        this.previousPageButton = this.addDrawableChild(
                new PageTurnWidget(i + 43, 220, false, button -> this.goToPreviousPage(), true)
        );
        this.updatePageButtons();
    }

    protected void goToPreviousPage() {
        if (this.pageIndex > 0) {
            --this.pageIndex;
        }

        this.updatePageButtons();
    }

    protected void goToNextPage() {
        if (this.pageIndex < this.getPageCount() - 1) {
            ++this.pageIndex;
        }

        this.updatePageButtons();
    }

    private void updatePageButtons() {
        this.nextPageButton.visible = this.pageIndex < this.getPageCount() - 1;
        this.previousPageButton.visible = this.pageIndex > 0;
    }

    private int getPageCount() {
        return this.mapsInfo.size();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        this.drawBackground(matrices,(this.width/2)- 90,(this.height/14));
        if(this.mapsInfo.size() > 0) {
            Pair<Pair<Integer, String>, MapState> mapInfo = this.mapsInfo.get(pageIndex);
            int mapId = mapInfo.getLeft().getLeft();
            String name = mapInfo.getLeft().getRight();
            MapState mapState = mapInfo.getRight();

            matrices.push();
            matrices.translate((this.width/2F)-(client.textRenderer.getWidth(name)/2F), 5F,0F);
            matrices.scale(1.2F,1.2F,1.0F);
            client.textRenderer.draw(matrices, name, 0, 0, 0xFFFFFF);
            matrices.pop();

            drawMap(matrices,mapId,mapState,(this.width/2) - 80,(this.height/14)+10,1.25F);
        }
        else {
            String text = "No maps :(";
            matrices.push();
            matrices.translate((this.width-client.textRenderer.getWidth(text))/2F - 10F, (this.height/13F)+30,0F);
            matrices.scale(1.5F,1.5F,1.0F);
            client.textRenderer.draw(matrices,text,0,0,0);
            matrices.pop();
        }
        super.render(matrices, mouseX, mouseY, delta);
    }

    public boolean shouldPause() {
        return false;
    }

    private void drawBackground(MatrixStack matrices, int x, int y) {
        matrices.push();
        RenderSystem.setShaderTexture(0, MAP_BACKGROUND_TEXTURE);
        this.drawTexture(matrices,x,y,0,0,180,180,180,180);
        matrices.pop();
    }

    private void drawMap(MatrixStack matrices, @Nullable Integer mapId, @Nullable MapState mapState, int x, int y, float scale) {
        if (mapId != null && mapState != null) {
            matrices.push();
            matrices.translate((double)x, (double)y, 1.0);
            matrices.scale(scale, scale, 1.0F);
            VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
            this.client.gameRenderer.getMapRenderer().draw(matrices, immediate, mapId, mapState, true, 15728880);
            immediate.draw();
            matrices.pop();
        }
    }


}
