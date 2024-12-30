package net.hyper_pigeon.map_album.screens.album;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PageTurnWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.MapRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.item.map.MapDecoration;
import net.minecraft.item.map.MapDecorationType;
import net.minecraft.item.map.MapDecorationTypes;
import net.minecraft.item.map.MapState;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.List;

public class AlbumScreen extends Screen {
    private static final Identifier MAP_BACKGROUND_TEXTURE =  Identifier.of("textures/map/map_background.png");

    private static final Identifier MAP_ICONS_TEXTURE = Identifier.of("textures/map/map_icons.png");
    static final RenderLayer MAP_ICONS_RENDER_LAYER = RenderLayer.getText(MAP_ICONS_TEXTURE);
    public final List<Pair<Pair<MapIdComponent, String>, MapState>> mapsInfo;
    private boolean enablePlayerIcon = false;
    private PageTurnWidget nextPageButton;
    private PageTurnWidget previousPageButton;

    private int pageIndex = 0;

    public AlbumScreen(Text title, List<Pair<Pair<MapIdComponent, String>, MapState>> mapsInfo) {
        super(title);
        this.mapsInfo = mapsInfo;
    }

    protected void init() {
        this.addCloseButton();
        this.addPageButtons();
        this.addPlayerIconButton();
    }

    protected void drawPlayerIcon(DrawContext ctx,MapState mapState) {
        if(enablePlayerIcon && !isPlayerOutOfBounds(this.client.player, mapState)) {
            ctx.getMatrices().push();
            Pair<Byte, Byte> pair = getPlayerIconCoords(this.client.player, mapState);
            ctx.getMatrices().translate((((float) this.width /2) - 80) + ((float)pair.getLeft() / 2.0F + 64.0F)*1.25,(((float) this.height /14)+10) + ((float)pair.getRight() / 2.0F + 64.0F)*1.25, 100);
            ctx.getMatrices().multiply(RotationAxis.POSITIVE_Z.rotationDegrees(this.client.player.getYaw()));
            ctx.getMatrices().scale(4.0F, 4.0F, 3.0F);
            ctx.getMatrices().translate(-0.125F, 0.125F, 0.0F);

            float h = 0.625F;
            float l = 0.25F;
            float m = 0.75F;
            float n = 0.375F;

            Matrix4f matrix4f2 = ctx.getMatrices().peek().getPositionMatrix();
            VertexConsumer vertexConsumer2 =  ctx.getVertexConsumers().getBuffer(RenderLayer.getText(Identifier.of("minecraft","textures/atlas/map_decorations.png")));
            vertexConsumer2.vertex(matrix4f2, -1.0F, 1.0F, 0).color(Colors.WHITE).texture(h, l).light(15728880);
            vertexConsumer2.vertex(matrix4f2, 1.0F, 1.0F, 0).color(Colors.WHITE).texture(m, l).light(15728880);
            vertexConsumer2.vertex(matrix4f2, 1.0F, -1.0F, 0).color(Colors.WHITE).texture(m, n).light(15728880);
            vertexConsumer2.vertex(matrix4f2, -1.0F, -1.0F, 0).color(Colors.WHITE).texture(h, n).light(15728880);

            ctx.getMatrices().pop();
        }
    }

    protected boolean isPlayerOutOfBounds(ClientPlayerEntity playerEntity, MapState mapState) {
        int i = 1 << mapState.scale;
        float f = (float)(playerEntity.getX() - (double)mapState.centerX) / (float)i;
        float g = (float)(playerEntity.getZ() - (double)mapState.centerZ) / (float)i;
        if (Math.abs(f) < 320.0F && Math.abs(g) < 320.0F) {
            return false;
        }
        return true;
    }

    protected Pair<Byte, Byte> getPlayerIconCoords(ClientPlayerEntity playerEntity, MapState mapState){
        int i = 1 << mapState.scale;
        float f = (float)(playerEntity.getX() - (double)mapState.centerX) / (float)i;
        float g = (float)(playerEntity.getZ() - (double)mapState.centerZ) / (float)i;
        byte b = (byte)((int)((double)(f * 2.0F) + 0.5));
        byte c = (byte)((int)((double)(g * 2.0F) + 0.5));

        if (f <= -63.0F) {
            b = -128;
        }

        if (g <= -63.0F) {
            c = -128;
        }

        if (f >= 63.0F) {
            b = 127;
        }

        if (g >= 63.0F) {
            c = 127;
        }

        return new Pair<>(b, c);
    }

    protected void addCloseButton() {
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> this.close()).dimensions(this.width / 2 - 100, 245, 200, 20).build());
    }

    protected void addPlayerIconButton(){
        this.addDrawableChild(ButtonWidget.
                builder(Text.literal("Toggle Player Icon"), button -> {
                    enablePlayerIcon = !enablePlayerIcon;
                } ).
                dimensions(this.width - 125, 10, 120, 20).build());
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
        super.render(ctx, mouseX, mouseY, delta);
        drawBackground(ctx,(this.width/2)- 90,(this.height/14));
        if(!this.mapsInfo.isEmpty()) {
            Pair<Pair<MapIdComponent, String>, MapState> mapInfo = this.mapsInfo.get(pageIndex);
            MapIdComponent mapId = mapInfo.getLeft().getLeft();
            String name = mapInfo.getLeft().getRight();
            MapState mapState = mapInfo.getRight();

            ctx.getMatrices().push();
            ctx.getMatrices().translate((this.width/2F)-(client.textRenderer.getWidth(name)/2F), 5F,0F);
            ctx.getMatrices().scale(1.2F,1.2F,1.0F);
            ctx.drawText(client.textRenderer, name, 0, 0, 0xFFFFFF, false);
            ctx.getMatrices().pop();

            drawMap(ctx,mapId,mapState,(this.width/2) - 80,(this.height/14)+10,1.25F);
            drawPlayerIcon(ctx,mapState);

        }
        else {
            String text = "No maps :(";
            ctx.getMatrices().push();
            ctx.getMatrices().translate((this.width-client.textRenderer.getWidth(text))/2F - 10F, (this.height/13F)+30,0F);
            ctx.getMatrices().scale(1.5F,1.5F,1.0F);
            ctx.drawText(client.textRenderer, text, 0, 0, 0, false);
            ctx.getMatrices().pop();
        }
    }

    public boolean shouldPause() {
        return false;
    }

    private void drawBackground(DrawContext context, int x, int y) {
        context.getMatrices().push();
        context.drawTexture(MAP_BACKGROUND_TEXTURE,x,y,0,0,180,180,180,180);
        context.getMatrices().pop();
    }

    private void drawMap(DrawContext context, @Nullable MapIdComponent mapIdComponent, @Nullable MapState mapState, int x, int y, float scale) {
        if (mapIdComponent != null && mapState != null) {
            context.getMatrices().push();
            context.getMatrices().translate((float)x, (float)y, 0.0F);
            context.getMatrices().scale(scale, scale, -1.0F);
            this.client.gameRenderer.getMapRenderer().draw(context.getMatrices(), context.getVertexConsumers(), mapIdComponent, mapState, false, 15728880);
            context.draw();
            context.getMatrices().pop();
        }
    }


}
