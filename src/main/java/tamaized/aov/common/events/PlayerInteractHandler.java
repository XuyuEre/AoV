package tamaized.aov.common.events;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tamaized.aov.AoV;
import tamaized.aov.common.capabilities.CapabilityList;
import tamaized.aov.common.capabilities.aov.IAoVCapability;
import tamaized.aov.common.capabilities.polymorph.IPolymorphCapability;

@Mod.EventBusSubscriber(modid = AoV.MODID)
public class PlayerInteractHandler {

	@SubscribeEvent
	public static void onXPGain(PlayerPickupXpEvent e) {
		if (!AoV.config.experience.get())
			return;
		EntityPlayer player = e.getEntityPlayer();
		if (player != null) {
			IAoVCapability cap = CapabilityList.getCap(player, CapabilityList.AOV);
			if (cap != null)
				cap.addExp(player, e.getOrb().getXpValue(), null);
		}
	}

	@SubscribeEvent
	public static void onItemPickup(EntityItemPickupEvent e) {
		IPolymorphCapability cap = CapabilityList.getCap(e.getEntityPlayer(), CapabilityList.POLYMORPH);
		if (cap != null && cap.getMorph() == IPolymorphCapability.Morph.Wolf && e.getEntityPlayer().getFoodStats().needFood() && e.getItem().getItem().getItem() instanceof ItemFood) {
			e.getItem().getItem().getItem().onItemUseFinish(e.getItem().getItem(), e.getEntityPlayer().world, e.getEntityPlayer());
			e.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onWakeUp(PlayerWakeUpEvent e) {
		// WorldServer#wakeAllPlayers is the only vanilla method that passes (false, false, true)
		// ForgeEventFactory.fireSleepingTimeCheck passes false, true, true, lets ensure the time is day when we check this
		EntityPlayer player = e.getEntityPlayer();
		if (player != null) {
			boolean flag = !e.wakeImmediately() && !e.updateWorld() && e.shouldSetSpawn();
			if (!flag) {
				if (!e.wakeImmediately() && e.updateWorld() && e.shouldSetSpawn()) {
					flag = player.world.isDaytime();
				}
			}
			if (flag) {
				IAoVCapability cap = CapabilityList.getCap(player, CapabilityList.AOV, null);
				if (cap != null)
					cap.resetCharges(player);
			}
		}
	}

}
