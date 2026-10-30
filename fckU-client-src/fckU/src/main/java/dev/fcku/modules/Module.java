package dev.fcku.modules;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;

public abstract class Module {
    public final String name;
    public boolean enabled;
    protected final MinecraftClient mc = MinecraftClient.getInstance();

    protected Module(String name) { this.name = name; }
    public void toggle() { enabled = !enabled; if (enabled) onEnable(); else onDisable(); }
    public void onEnable() {}
    public void onDisable() {}
    public void onTick(MinecraftClient mc) {}
    public void onRender(WorldRenderContext ctx) {}

    protected void info(String msg) {
        if (mc.player != null) mc.player.sendMessage(net.minecraft.text.Text.literal("§c[fckU] §f" + msg), false);
    }
}
