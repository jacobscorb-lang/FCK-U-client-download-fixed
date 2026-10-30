package dev.fcku.modules;

import dev.fcku.util.RenderUtil;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.block.entity.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.ChunkStatus;

import java.util.HashMap;
import java.util.Map;

/** Scans loaded chunks, flags chunks with many storage blocks and reports coordinates in chat. */
public class StashFinder extends Module {
    public int threshold = 10;
    private final Map<ChunkPos, Integer> found = new HashMap<>();

    public StashFinder() { super("StashFinder"); }

    @Override public void onEnable() { found.clear(); }

    @Override
    public void onTick(MinecraftClient mc) {
        if (mc.world.getTime() % 20 != 0) return;
        int cx = mc.player.getChunkPos().x, cz = mc.player.getChunkPos().z;
        int r = mc.options.getViewDistance().getValue();
        for (int x = cx - r; x <= cx + r; x++) for (int z = cz - r; z <= cz + r; z++) {
            var chunk = mc.world.getChunk(x, z, ChunkStatus.FULL, false);
            if (chunk == null) continue;
            int count = 0;
            for (BlockEntity be : chunk.getBlockEntities().values()) {
                if (be instanceof ChestBlockEntity || be instanceof BarrelBlockEntity
                    || be instanceof ShulkerBoxBlockEntity || be instanceof HopperBlockEntity
                    || be instanceof DispenserBlockEntity || be instanceof FurnaceBlockEntity) count++;
            }
            ChunkPos cp = chunk.getPos();
            if (count >= threshold && !found.containsKey(cp)) {
                found.put(cp, count);
                info("Possible stash: " + count + " storage blocks at X " + cp.getStartX() + " Z " + cp.getStartZ());
            } else if (count >= threshold) found.put(cp, count);
        }
    }

    @Override
    public void onRender(WorldRenderContext ctx) {
        for (ChunkPos cp : found.keySet()) {
            double y0 = mc.world.getBottomY(), y1 = mc.world.getTopY();
            RenderUtil.box(new Box(cp.getStartX(), y0, cp.getStartZ(), cp.getEndX() + 1, y1, cp.getEndZ() + 1), 1f, 0f, 0f, 0.6f);
        }
    }
}
