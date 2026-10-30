package dev.fcku.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.*;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

/** Through-walls wireframe box rendering. */
public class RenderUtil {
    private static BufferBuilder buf;
    private static Matrix4f mat;
    private static Vec3d cam;

    public static void begin(WorldRenderContext ctx) {
        cam = ctx.camera().getPos();
        mat = ctx.matrixStack().peek().getPositionMatrix();
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.lineWidth(2f);
        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
        buf = Tessellator.getInstance().begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
    }

    public static void end() {
        try { BufferRenderer.drawWithGlobalProgram(buf.end()); } catch (Exception ignored) {}
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    public static void box(Box b, float r, float g, float bl, float a) {
        float x1 = (float)(b.minX - cam.x), y1 = (float)(b.minY - cam.y), z1 = (float)(b.minZ - cam.z);
        float x2 = (float)(b.maxX - cam.x), y2 = (float)(b.maxY - cam.y), z2 = (float)(b.maxZ - cam.z);
        line(x1,y1,z1,x2,y1,z1,r,g,bl,a); line(x2,y1,z1,x2,y1,z2,r,g,bl,a);
        line(x2,y1,z2,x1,y1,z2,r,g,bl,a); line(x1,y1,z2,x1,y1,z1,r,g,bl,a);
        line(x1,y2,z1,x2,y2,z1,r,g,bl,a); line(x2,y2,z1,x2,y2,z2,r,g,bl,a);
        line(x2,y2,z2,x1,y2,z2,r,g,bl,a); line(x1,y2,z2,x1,y2,z1,r,g,bl,a);
        line(x1,y1,z1,x1,y2,z1,r,g,bl,a); line(x2,y1,z1,x2,y2,z1,r,g,bl,a);
        line(x2,y1,z2,x2,y2,z2,r,g,bl,a); line(x1,y1,z2,x1,y2,z2,r,g,bl,a);
    }

    private static void line(float ax,float ay,float az,float bx,float by,float bz,float r,float g,float b,float a) {
        buf.vertex(mat, ax, ay, az).color(r, g, b, a);
        buf.vertex(mat, bx, by, bz).color(r, g, b, a);
    }
}
