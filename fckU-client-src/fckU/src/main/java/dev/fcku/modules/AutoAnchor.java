package dev.fcku.modules;

import net.minecraft.block.Blocks;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.Comparator;

/** Places, charges and detonates respawn anchors near the closest player. Only works outside the Nether. */
public class AutoAnchor extends Module {
    public double range = 4.5, targetRange = 7;
    public int delay = 2;
    private int timer;

    public AutoAnchor() { super("AutoAnchor"); }

    @Override
    public void onTick(MinecraftClient mc) {
        if (timer-- > 0) return;
        if (mc.world.getDimension().respawnAnchorWorks()) return;
        PlayerEntity target = mc.world.getPlayers().stream()
            .filter(p -> p != mc.player && p.isAlive() && mc.player.distanceTo(p) <= targetRange)
            .min(Comparator.comparingDouble(p -> mc.player.distanceTo(p))).orElse(null);
        if (target == null) return;

        int anchorSlot = AutoCrystal.findHotbar(mc, Items.RESPAWN_ANCHOR);
        int glowSlot = AutoCrystal.findHotbar(mc, Items.GLOWSTONE);
        int safeSlot = findSafeSlot(anchorSlot, glowSlot);
        if (glowSlot == -1 || safeSlot == -1) return;

        BlockPos anchor = findAnchor(mc, target);
        int prev = mc.player.getInventory().selectedSlot;

        if (anchor == null) {
            if (anchorSlot == -1) return;
            BlockPos place = findPlacement(mc, target);
            if (place == null) return;
            mc.player.getInventory().selectedSlot = anchorSlot;
            interact(mc, place.down(), Direction.UP);
        } else {
            int charges = mc.world.getBlockState(anchor).get(RespawnAnchorBlock.CHARGES);
            mc.player.getInventory().selectedSlot = charges == 0 ? glowSlot : safeSlot;
            interact(mc, anchor, Direction.UP);
        }
        mc.player.getInventory().selectedSlot = prev;
        timer = delay;
    }

    private void interact(MinecraftClient mc, BlockPos pos, Direction side) {
        BlockHitResult hit = new BlockHitResult(Vec3d.ofCenter(pos).add(0, 0.5, 0), side, pos, false);
        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hit);
        mc.player.swingHand(Hand.MAIN_HAND);
    }

    private BlockPos findAnchor(MinecraftClient mc, PlayerEntity t) {
        BlockPos o = t.getBlockPos();
        for (int x = -3; x <= 3; x++) for (int y = -1; y <= 3; y++) for (int z = -3; z <= 3; z++) {
            BlockPos p = o.add(x, y, z);
            if (mc.world.getBlockState(p).isOf(Blocks.RESPAWN_ANCHOR)
                && mc.player.getEyePos().distanceTo(Vec3d.ofCenter(p)) <= range) return p;
        }
        return null;
    }

    private BlockPos findPlacement(MinecraftClient mc, PlayerEntity t) {
        BlockPos o = t.getBlockPos();
        BlockPos best = null; double bestD = Double.MAX_VALUE;
        for (int x = -2; x <= 2; x++) for (int y = 0; y <= 2; y++) for (int z = -2; z <= 2; z++) {
            BlockPos p = o.add(x, y, z);
            if (!mc.world.isAir(p) || mc.world.isAir(p.down())) continue;
            if (mc.player.getEyePos().distanceTo(Vec3d.ofCenter(p)) > range) continue;
            double d = Vec3d.ofCenter(p).distanceTo(t.getPos());
            if (d < bestD && d > 1.0) { bestD = d; best = p; }
        }
        return best;
    }

    private int findSafeSlot(int anchorSlot, int glowSlot) {
        for (int i = 0; i < 9; i++) if (i != glowSlot && i != anchorSlot) return i;
        return -1;
    }
}
