package net.piinut.voidophobia.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.piinut.voidophobia.Voidophobia;
import net.piinut.voidophobia.gui.handler.VacuumCoaterScreenHandler;

public class VacuumCoaterScreen extends HandledScreen<VacuumCoaterScreenHandler> {

    private static final Identifier TEXTURE = new Identifier(Voidophobia.MODID, "textures/gui/container/vacuum_coater.png");

    public VacuumCoaterScreen(VacuumCoaterScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int i = this.x;
        int j = this.y;
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);
        int k = this.handler.getProcessProgress();
        this.drawTexture(matrices, i + 72, j + 54 - k, 176, 74 - k, 32, k + 1);
        int p = this.handler.getVuxStorage();
        this.drawTexture(matrices, i + 138, j + 73 - p, 176, 0, 10, p);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void drawMouseoverTooltip(MatrixStack matrices, int x, int y) {
        super.drawMouseoverTooltip(matrices, x, y);
        if(this.isPointWithinBounds(138, 18, 10, 56, x, y)){
            int vuxStored = this.handler.getVuxStored();
            renderTooltip(matrices, Text.of("Vux Level: " + vuxStored), x, y);
        }
    }

    @Override
    protected void init() {
        super.init();
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
    }

}
