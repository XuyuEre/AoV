package tamaized.aov.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.opengl.GL11;
import tamaized.aov.AoV;
import tamaized.aov.common.capabilities.CapabilityList;
import tamaized.aov.common.capabilities.aov.IAoVCapability;
import tamaized.aov.common.config.ConfigHandler;
import tamaized.aov.common.core.abilities.Ability;
import tamaized.aov.common.core.abilities.universal.InvokeMass;
import tamaized.aov.proxy.ClientProxy;

@SuppressWarnings("WeakerAccess")
@Mod.EventBusSubscriber(modid = AoV.modid, value = Side.CLIENT)
public class AoVUIBar {

	public static final ResourceLocation widgetsTexPath = new ResourceLocation("textures/gui/widgets.png");
	private static final Minecraft mc = Minecraft.getMinecraft();
	public static int slotLoc = 0;

	@SubscribeEvent
	public static void disableHotbar(RenderGameOverlayEvent e) {
		if (ConfigHandler.barPos == ConfigHandler.BarPos.BOTTOM && e.getType() == RenderGameOverlayEvent.ElementType.HOTBAR && ClientProxy.barToggle)
			e.setCanceled(true);
	}

	@SuppressWarnings("ConstantConditions")
	public static void render(Gui gui) {
		if (ConfigHandler.barPos == ConfigHandler.BarPos.BOTTOM && !ClientProxy.barToggle)
			return;
		if (mc.player == null || !mc.player.hasCapability(CapabilityList.AOV, null))
			return;
		IAoVCapability cap = mc.player.getCapability(CapabilityList.AOV, null);
		GlStateManager.pushMatrix();
		{
			ScaledResolution sr = new ScaledResolution(mc);
			if (ConfigHandler.barPos == ConfigHandler.BarPos.BOTTOM)
				GlStateManager.translate(0, sr.getScaledHeight() - 23, 0);
			float alpha = 0.2f;
			if (ClientProxy.barToggle)
				alpha = 1.0f;
			GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
			mc.getTextureManager().bindTexture(widgetsTexPath);
			int i = sr.getScaledWidth() / 2;
			gui.drawTexturedModalRect(i - 91, 1, 0, 0, 182, 22);
			gui.drawTexturedModalRect(i - 91 - 1 + slotLoc * 20, 0, 0, 22, 24, 22);
			GlStateManager.pushMatrix();
			{
				GlStateManager.translate(0.01f, 0, 0);
				GlStateManager.translate(-20.01f, 0, 0);
				for (int j = 0; j < 9; ++j) {
					GlStateManager.translate(20.01f, 0, 0);
					Ability ability = cap.getSlot(j);
					if (ability == null)
						continue;
					int k = sr.getScaledWidth() / 2 - 90 + 2;
					int l = 4;
					GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
					renderHotbarIcon(gui, cap, j, k, l, ability.getAbility().getIcon(), (ability.getAbility() instanceof InvokeMass) && cap.getInvokeMass());
					GlStateManager.pushAttrib();
					{
						if (ability.getCooldown() > 0)
							renderCooldown(gui, mc.fontRenderer, k, l, ability.getCooldownPerc(), ability.getCooldown());
					}
					GlStateManager.popAttrib();
				}
			}
			GlStateManager.popMatrix();
		}
		GlStateManager.popMatrix();
	}

	public static void renderHotbarIcon(Gui gui, IAoVCapability cap, int index, int xPos, int yPos, ResourceLocation icon, boolean active) {
		if (icon != null) {
			GlStateManager.pushMatrix();
			{
				float f1 = 1.0F / 16.0F;
				GlStateManager.translate((float) (xPos), (float) (yPos), 0.0F);
				GlStateManager.scale(1.0F * f1, 1.0f * f1, 1.0F);
				GlStateManager.pushMatrix();
				{
					GlStateManager.enableBlend();
					renderIcon(gui, icon);
					if (active)
						Gui.drawRect(0, 0, 256, 256, 0x7700FFFF);
					Ability ability = cap == null ? null : cap.getSlot(index);
					if (ability != null && !ability.canUse(cap))
						Gui.drawRect(0, 0, 256, 256, 0x77FF0000);
					GlStateManager.disableBlend();
				}
				GlStateManager.popMatrix();

			}
			GlStateManager.popMatrix();
		}
	}

	private static void renderIcon(Gui gui, ResourceLocation icon) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(icon);
		gui.drawTexturedModalRect(0, 0, 0, 0, 256, 256);
	}

	private static void renderCooldown(Gui gui, FontRenderer fr, int xPos, int yPos, float perc, int timeLeft) {
		GlStateManager.pushMatrix();
		{
			float f1 = 1.0F / 16.0F;
			GlStateManager.translate((float) (xPos), (float) (yPos), 0.0F);
			GlStateManager.scale(1.0F * f1, 1.0f * f1, 1.0F);

			GlStateManager.pushMatrix();
			{
				// GlStateManager.enableRescaleNormal();
				// GlStateManager.enableAlpha();
				// GlStateManager.alphaFunc(516, 0.1F);
				// GlStateManager.enableBlend();
				// GlStateManager.blendFunc(770, 771);

				{
					GlStateManager.scale(16.0f, 16.0f, 1.0f);
					renderRadial(0, 0, perc);
					gui.drawCenteredString(fr, String.valueOf(timeLeft), 8, 4, 0xFFFF00);
				}

				// GlStateManager.disableAlpha();
				// GlStateManager.disableRescaleNormal();
				// GlStateManager.disableLighting();
			}
			GlStateManager.popMatrix();
		}
		GlStateManager.popMatrix();
	}

	/**
	 * Credit to Vazkii here, I used code from Botania :P
	 */
	@SuppressWarnings("SameParameterValue")
	public static void renderRadial(int x, int y, float perc) {
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glEnable(GL11.GL_STENCIL_TEST);
		GL11.glColorMask(false, false, false, false);
		GL11.glDepthMask(false);
		GL11.glStencilFunc(GL11.GL_NEVER, 1, 0xFF);
		GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_KEEP, GL11.GL_KEEP);
		GL11.glStencilMask(0xFF);

		int r = 10;
		int centerX = x + 8;
		int centerY = y + 8;
		float a = 0.5f;

		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColorMask(true, true, true, true);
		GL11.glDepthMask(true);
		GL11.glStencilMask(0x00);
		GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
		GL11.glColor4f(0F, 0.0F, 0.0F, a);
		GL11.glVertex2i(centerX, centerY);
		GL11.glColor4f(0F, 0F, 0.0F, a);
		int v = (int) ((405f) * perc);
		int v1 = v > 45 ? -45 : -v;
		int v2 = v > 135 ? -135 : -v;
		int v3 = v > 225 ? -225 : -v;
		int v4 = v > 315 ? -315 : -v;
		int v5 = v > 405 ? -405 : -v;
		double rad = (v1) / 180F * Math.PI;
		GL11.glVertex2d(centerX + Math.cos((-45) / 180F * Math.PI) * r, centerY + Math.sin(rad) * r);
		rad = (v2) / 180F * Math.PI;
		if (v > 45)
			GL11.glVertex2d(centerX + Math.cos(rad) * r, centerY + Math.sin((-135) / 180F * Math.PI) * r);
		rad = (v3) / 180F * Math.PI;
		if (v > 135)
			GL11.glVertex2d(centerX + Math.cos((-225) / 180F * Math.PI) * r, centerY + Math.sin(rad) * r);
		rad = (v4) / 180F * Math.PI;
		if (v > 225)
			GL11.glVertex2d(centerX + Math.cos(rad) * r, centerY + Math.sin((-315) / 180F * Math.PI) * r);
		rad = (v5) / 180F * Math.PI;
		if (v > 315)
			GL11.glVertex2d(centerX + Math.cos((-405) / 180F * Math.PI) * r, centerY + Math.sin(rad) * r);

		GL11.glVertex2i(centerX, centerY);
		GL11.glEnd();
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glShadeModel(GL11.GL_FLAT);
		GL11.glDisable(GL11.GL_STENCIL_TEST);
	}
}