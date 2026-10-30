package dev.fcku.gui;

import dev.fcku.FckUClient;
import dev.fcku.modules.Module;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ClickGui extends Screen {
    private static final int W = 120, H = 16;

    public ClickGui() { super(Text.literal("fckU client")); }

    @Override
    public void render(DrawContext c, int mx, int my, float delta) {
        c.fill(0, 0, width, height, 0x88100000);
        int x = width / 2 - W / 2, y = height / 2 - (FckUClient.MODULES.size() * H + 24) / 2;
        c.fill(x - 2, y - 2, x + W + 2, y + 18 + FckUClient.MODULES.size() * H + 2, 0xFF2A0000);
        c.fill(x, y, x + W, y + 16, FckUClient.DARK_RED);
        c.drawCenteredTextWithShadow(textRenderer, "fckU client", x + W / 2, y + 4, 0xFFFFFFFF);
        int cy = y + 18;
        for (Module m : FckUClient.MODULES) {
            boolean hover = mx >= x && mx <= x + W && my >= cy && my < cy + H;
            int bg = m.enabled ? FckUClient.RED : (hover ? 0xFF4A0000 : 0xFF1A0000);
            c.fill(x, cy, x + W, cy + H, bg);
            c.drawTextWithShadow(textRenderer, m.name, x + 6, cy + 4, m.enabled ? 0xFFFFFFFF : 0xFFFF6666);
            cy += H;
        }
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        int x = width / 2 - W / 2, y = height / 2 - (FckUClient.MODULES.size() * H + 24) / 2 + 18;
        for (Module m : FckUClient.MODULES) {
            if (mx >= x && mx <= x + W && my >= y && my < y + H) { m.toggle(); return true; }
            y += H;
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override public boolean shouldPause() { return false; }
}
