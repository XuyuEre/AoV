package tamaized.aov.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import tamaized.aov.AoV;
import tamaized.aov.proxy.ClientProxy;

@Mod.EventBusSubscriber(modid = AoV.MODID)
public class ClientTicker {

	public static final int dangerBiomeMaxTick = 20 * 10;
	public static TickerDataCharges charges = new TickerDataCharges();
	public static int dangerBiomeTicks;
	public static boolean dangerBiomeTicksFlag;
	public static float frames;

	@SubscribeEvent
	public static void update(TickEvent.ClientTickEvent event) {
		if (Minecraft.getInstance().isGamePaused() || event.phase != TickEvent.Phase.START)
			return;
		frames++;
		charges.update();
		if (ClientProxy.getTarget() != null && !ClientProxy.getTarget().isAlive())
			ClientProxy.setTarget(null);
		{
			if (dangerBiomeTicksFlag) {
				if (dangerBiomeTicks < dangerBiomeMaxTick)
					dangerBiomeTicks++;
			} else if (dangerBiomeTicks > 0) {
				dangerBiomeTicks--;
			}
		}
	}

}
