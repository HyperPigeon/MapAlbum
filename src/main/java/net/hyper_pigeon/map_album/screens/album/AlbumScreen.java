package net.hyper_pigeon.map_album.screens.album;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PageTurnWidget;
import net.minecraft.item.map.MapState;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.Nullable;

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
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> this.close()).dimensions(this.width / 2 - 100, 245, 200, 20).build());
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
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        this.renderBackground(ctx);
        drawBackground(ctx,(this.width/2)- 90,(this.height/14));
        if(this.mapsInfo.size() > 0) {
            Pair<Pair<Integer, String>, MapState> mapInfo = this.mapsInfo.get(pageIndex);
            int mapId = mapInfo.getLeft().getLeft();
            String name = mapInfo.getLeft().getRight();
            MapState mapState = mapInfo.getRight();

            ctx.getMatrices().push();
            ctx.getMatrices().translate((this.width/2F)-(client.textRenderer.getWidth(name)/2F), 5F,0F);
            ctx.getMatrices().scale(1.2F,1.2F,1.0F);
            ctx.drawText(client.textRenderer, name, 0, 0, 0xFFFFFF, false);
            ctx.getMatrices().pop();

            drawMap(ctx,mapId,mapState,(this.width/2) - 80,(this.height/14)+10,1.25F);
        }
        else {
            String text = "No maps :(";
            ctx.getMatrices().push();
            ctx.getMatrices().translate((this.width-client.textRenderer.getWidth(text))/2F - 10F, (this.height/13F)+30,0F);
            ctx.getMatrices().scale(1.5F,1.5F,1.0F);
            ctx.drawText(client.textRenderer, text, 0, 0, 0, false);
            ctx.getMatrices().pop();
        }
        super.render(ctx, mouseX, mouseY, delta);
    }

    public boolean shouldPause() {
        return false;
    }

    private void drawBackground(DrawContext context, int x, int y) {
        context.getMatrices().push();
        context.drawTexture(MAP_BACKGROUND_TEXTURE,x,y,0,0,180,180,180,180);
        context.getMatrices().pop();
    }

    private void drawMap(DrawContext context, @Nullable Integer mapId, @Nullable MapState mapState, int x, int y, float scale) {
        if (mapId != null && mapState != null) {
            context.getMatrices().push();
            context.getMatrices().translate((float)x, (float)y, 1.0F);
            context.getMatrices().scale(scale, scale, 1.0F);
            this.client.gameRenderer.getMapRenderer().draw(context.getMatrices(), context.getVertexConsumers(), mapId, mapState, true, 15728880);
            context.draw();
            context.getMatrices().pop();
        }
    }


}
