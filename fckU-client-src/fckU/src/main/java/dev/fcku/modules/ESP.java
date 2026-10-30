package dev.fcku.modules;

import dev.fcku.util.RenderUtil;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.block.entity.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.Map;

public class ESP extends Module {
    public ESP() { super("ESP"); enabled = true; }

    @Override
    public void onRender(WorldRenderContext ctx) {
        if (mc.world == null || mc.player == null) return;
        float pt = ctx.tickCounter().getTickDelta(true);

        for (Entity e : mc.world.getEntities()) {
            if (e == mc.player) continue;
            Box b = e.getBoundingBox().offset(
                (e.getX() - e.lastRenderX) * (pt - 1), (e.getY() - e.lastRenderY) * (pt - 1), (e.getZ() - e.lastRenderZ) * (pt - 1));
            if (e instanceof PlayerEntity) RenderUtil.box(b, 1f, 0.1f, 0.1f, 1f);
            else if (e instanceof EndCrystalEntity) RenderUtil.box(b, 1f, 0.4f, 0.8f, 1f);
            else if (e instanceof HostileEntity) RenderUtil.box(b, 0.6f, 0f, 0f, 0.8f);
        }

        int cx = mc.player.getChunkPos().x, cz = mc.player.getChunkPos().z;
        int r = Math.min(mc.options.getViewDistance().getValue(), 6);
        for (int x = cx - r; x <= cx + r; x++) for (int z = cz - r; z <= cz + r; z++) {
            var chunk = mc.world.getChunk(x, z, net.minecraft.world.chunk.ChunkStatus.FULL, false);
            if (chunk == null) continue;
            for (Map.Entry<BlockPos, BlockEntity> en : chunk.getBlockEntities().entrySet()) {
                BlockEntity be = en.getValue();
                Box box = new Box(en.getKey());
                if (be instanceof ShulkerBoxBlockEntity) RenderUtil.box(box, 1f, 0.3f, 0.3f, 1f);
                else if (be instanceof ChestBlockEntity || be instanceof BarrelBlockEntity)
                    RenderUtil.box(box, 0.9f, 0.1f, 0.1f, 0.7f);
                else if (be instanceof EnderChestBlockEntity) RenderUtil.box(box, 0.5f, 0f, 0.5f, 0.7f);
            }
        }
    }
}
