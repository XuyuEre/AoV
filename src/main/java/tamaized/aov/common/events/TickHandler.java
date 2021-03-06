package tamaized.aov.common.events;

import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import tamaized.aov.AoV;
import tamaized.aov.common.capabilities.CapabilityList;
import tamaized.aov.common.capabilities.aov.IAoVCapability;
import tamaized.aov.common.capabilities.astro.IAstroCapability;
import tamaized.aov.common.capabilities.leap.ILeapCapability;
import tamaized.aov.common.capabilities.polymorph.IPolymorphCapability;
import tamaized.aov.common.capabilities.stun.IStunCapability;
import tamaized.aov.proxy.CommonProxy;
import tamaized.aov.registry.AoVPotions;

import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = AoV.MODID)
public class TickHandler {

	private static final List<UUID> FLYING = Lists.newArrayList();

	private static void spawnSlowfallParticles(EntityLivingBase living) {
		ILeapCapability cap = CapabilityList.getCap(living, CapabilityList.LEAP);
		if (cap == null || cap.getLeapDuration() <= 0)
			return;
		final float perc = MathHelper.clamp((float) cap.getLeapDuration() / (float) cap.getMaxLeapDuration(), 0, 1);
		final int bound = 100 - ((int) (perc * 100)) + 1;
		Vec3d pos = living.getPositionVector();
		for (int i = 0; i < 3; i++)
			if (living.world.rand.nextInt(bound > 0 ? bound : 1) <= 2) {
				double yaw = Math.toRadians(living.renderYawOffset + 63);
				float range = 1.0F;
				float r = ((living.world.rand.nextFloat() * (1.0F + range)) - (0.5F + range));
				Vec3d vec = new Vec3d(-Math.cos(yaw), 1.7F, -Math.sin(yaw)).rotateYaw(r);
				vec = pos.add(vec).add(0, living.world.rand.nextFloat() * 0.5F - 0.5F, 0);
				AoV.proxy.spawnParticle(CommonProxy.ParticleType.Feather, living.world, vec, new Vec3d(0, 0, 0), 55, 0.1F, 1.5F, 0xFFFF00FF);
			}
	}

	@SubscribeEvent
	public static void updateLiving(LivingEvent.LivingUpdateEvent e) {
		EntityLivingBase living = e.getEntityLiving();
		IPolymorphCapability poly = CapabilityList.getCap(living, CapabilityList.POLYMORPH);
		if (poly != null && (poly.getMorph() == IPolymorphCapability.Morph.WaterElemental || poly.getMorph() == IPolymorphCapability.Morph.FireElemental || poly.getMorph() == IPolymorphCapability.Morph.ArchAngel))
			for (Potion potion : IPolymorphCapability.ELEMENTAL_IMMUNITY_EFFECTS)
				living.removePotionEffect(potion);
		if (living.world.isRemote)
			spawnSlowfallParticles(living);
		else {
			EntityPlayer player = living instanceof EntityPlayer ? (EntityPlayer) living : null;
			if (player != null)
				if (poly != null && poly.getMorph() == IPolymorphCapability.Morph.ArchAngel) {
					player.abilities.allowFlying = true;
					player.sendPlayerAbilities();
					if (!FLYING.contains(player.getUniqueID()))
						FLYING.add(player.getUniqueID());
				} else if (FLYING.remove(player.getUniqueID()) && !player.isCreative() && !player.isSpectator()) {
					player.abilities.allowFlying = false;
					player.sendPlayerAbilities();
				} else if (!player.isCreative() && !player.isSpectator() && !player.abilities.allowFlying) { // This will run one tick later so we can detect if other mods enable flight
					player.abilities.disableDamage = false;
					player.abilities.isFlying = false;
					player.sendPlayerAbilities();
				}
			ILeapCapability cap = CapabilityList.getCap(living, CapabilityList.LEAP);
			PotionEffect pot = living.getActivePotionEffect(AoVPotions.slowFall);
			if (pot == null || cap == null)
				return;
			if (living.ticksExisted % 20 == 0)
				cap.setLeapDuration(pot.getDuration());
		}
	}

	@SubscribeEvent
	public static void updateEntity(TickEvent.WorldTickEvent e) {
		if (e.phase == TickEvent.Phase.START)
			return;
		List<Entity> list = Lists.newArrayList(e.world.loadedEntityList);
		for (Entity entity : list) {
			if (!(entity instanceof EntityLivingBase))
				continue;
			if (entity.removed)
				continue;
			IStunCapability cap = CapabilityList.getCap(entity, CapabilityList.STUN);
			if (cap != null)
				cap.update((EntityLivingBase) entity);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void update(PlayerTickEvent e) {
		if (e.phase == TickEvent.Phase.START)
			return;
		EntityPlayer player = e.player;
		if (player.getHealth() <= (player.getMaxHealth() / 2)) {
			if (player.getActivePotionEffect(AoVPotions.stalwartPact) != null) {
				player.removeActivePotionEffect(AoVPotions.stalwartPact);
				player.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, (20 * (60 * 5)), 2));
				player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, (20 * (10)), 2));
			}
			if (player.getActivePotionEffect(AoVPotions.naturesBounty) != null) {
				player.removeActivePotionEffect(AoVPotions.naturesBounty);
				player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, (20 * (10)), 2));
			}
		}
		{
			IAoVCapability cap = CapabilityList.getCap(player, CapabilityList.AOV);
			if (cap != null)
				cap.update(player);
		}
		{
			IAstroCapability cap = CapabilityList.getCap(player, CapabilityList.ASTRO);
			if (cap != null)
				cap.update(player);
		}
		{
			ILeapCapability cap = CapabilityList.getCap(player, CapabilityList.LEAP);
			if (cap != null)
				cap.update(player);
		}
		{
			IPolymorphCapability cap = CapabilityList.getCap(player, CapabilityList.POLYMORPH);
			if (cap != null)
				cap.update(player);
		}
	}

}
