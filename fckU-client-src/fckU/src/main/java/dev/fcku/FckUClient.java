package dev.fcku;

import dev.fcku.gui.ClickGui;
import dev.fcku.modules.*;
import dev.fcku.util.RenderUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class FckUClient implements ClientModInitializer {
    public static final String NAME = "fckU client";
    public static final int RED = 0xFFFF1A1A;
    public static final int DARK_RED = 0xFF8B0000;
    public static final List<Module> MODULES = new ArrayList<>();
    public static KeyBinding guiKey;

    @Override
    public void onInitializeClient() {
        MODULES.add(new ESP());
        MODULES.add(new StashFinder());
        MODULES.add(new AutoCrystal());
        MODULES.add(new AutoAnchor());

        guiKey = KeyBindingHelper.registerKeyBinding(
            new KeyBinding("key.fcku.gui", GLFW.GLFW_KEY_RIGHT_SHIFT, "category.fcku"));

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            if (mc.player == null || mc.world == null) return;
            while (guiKey.wasPressed()) mc.setScreen(new ClickGui());
            for (Module m : MODULES) if (m.enabled) m.onTick(mc);
        });

        WorldRenderEvents.LAST.register(ctx -> {
            if (ctx.matrixStack() == null) return;
            RenderUtil.begin(ctx);
            for (Module m : MODULES) if (m.enabled) m.onRender(ctx);
            RenderUtil.end();
        });

        HudRenderCallback.EVENT.register(FckUClient::renderHud);
    }

    private static void renderHud(DrawContext ctx, net.minecraft.client.render.RenderTickCounter tc) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.options.hudHidden) return;
        ctx.drawTextWithShadow(mc.textRenderer, NAME, 4, 4, RED);
        int y = 16;
        for (Module m : MODULES) {
            if (!m.enabled) continue;
            int w = mc.textRenderer.getWidth(m.name);
            ctx.drawTextWithShadow(mc.textRenderer, m.name, ctx.getScaledWindowWidth() - w - 4, y - 12 + 4, 0xFFFF4D4D);
            y += 10;
        }
    }
}
