package tamaized.aov.client;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;

public class SizedFontHelper {

	public static void render(FontRenderer font, String text, float x, float y, float scale, int color, boolean shadow) {
		GlStateManager.pushMatrix();
		{
			GlStateManager.scalef(scale, scale, scale);
			x /= scale;
			y /= scale;
			if (shadow)
				drawShadowString(font, text, x, y, color);
			else
				drawString(font, text, x, y, color);
		}
		GlStateManager.popMatrix();
	}

	public static void drawString(FontRenderer fontRendererIn, String text, float x, float y, int color) {
		fontRendererIn.drawString(text, x, y, color);
	}

	public static void drawShadowString(FontRenderer fontRendererIn, String text, float x, float y, int color) {
		fontRendererIn.drawStringWithShadow(text, x, y, color);
	}

}
