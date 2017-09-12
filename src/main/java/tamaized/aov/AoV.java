package tamaized.aov;

import tamaized.aov.common.capabilities.CapabilityList;
import tamaized.aov.common.capabilities.aov.AoVCapabilityHandler;
import tamaized.aov.common.capabilities.aov.AoVCapabilityStorage;
import tamaized.aov.common.capabilities.aov.IAoVCapability;
import tamaized.aov.common.entity.EntitySpellBladeBarrier;
import tamaized.aov.common.entity.EntitySpellImplosion;
import tamaized.aov.common.entity.ProjectileFlameStrike;
import tamaized.aov.common.entity.ProjectileNimbusRay;
import tamaized.aov.common.events.LivingAttackEvent;
import tamaized.aov.common.events.PlayerInteractHandler;
import tamaized.aov.common.events.TickHandler;
import tamaized.aov.common.gui.GuiHandler;
import tamaized.aov.network.ServerPacketHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.apache.logging.log4j.LogManager;
import tamaized.aov.registry.*;
import tamaized.tammodized.TamModBase;
import tamaized.tammodized.TamModized;
import tamaized.tammodized.proxy.AbstractProxy;

@Mod(modid = AoV.modid, name = "Angel of Vengeance", version = AoV.version, dependencies = "required-before:" + TamModized.modid + "@[${tamversion},)")
public class AoV extends TamModBase {

	public static final String modid = "aov";
	public static final String networkChannelName = "AoV";
	public static final AoVTabs tabs = new AoVTabs();
	public static final AoVItems items = new AoVItems();
	public static final AoVArmors armors = new AoVArmors();
	public static final AoVBlocks blocks = new AoVBlocks();
	public static final AoVPotions potions = new AoVPotions();
	public static final AoVAchievements achievements = new AoVAchievements();
	public static final AoVDamageSource damageSources = new AoVDamageSource();
	protected final static String version = "${version}";
	@Instance(modid)
	public static AoV instance = new AoV();
	public static FMLEventChannel channel;
	@SidedProxy(clientSide = "tamaized.aov.proxy.ClientProxy", serverSide = "tamaized.aov.proxy.ServerProxy")
	public static AbstractProxy proxy;

	public static String getVersion() {
		return version;
	}

	@Override
	protected AbstractProxy getProxy() {
		return proxy;
	}

	@Override
	public String getModID() {
		return modid;
	}

	@Override
	@EventHandler
	public void FMLpreInit(FMLPreInitializationEvent event) {
		super.FMLpreInit(event);
	}

	@Override
	@EventHandler
	public void FMLinit(FMLInitializationEvent event) {
		super.FMLinit(event);
	}

	@Override
	@EventHandler
	public void FMLpostInit(FMLPostInitializationEvent event) {
		super.FMLpostInit(event);
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		logger = LogManager.getLogger("AoV");

		logger.info("Starting AoV PreInit");

		channel = NetworkRegistry.INSTANCE.newEventDrivenChannel(networkChannelName);

		CapabilityManager.INSTANCE.register(IAoVCapability.class, new AoVCapabilityStorage(), AoVCapabilityHandler.class);
		MinecraftForge.EVENT_BUS.register(new CapabilityList());
	}

	@Override
	public void init(FMLInitializationEvent event) {
		logger.info("Starting AoV Init");

		MinecraftForge.EVENT_BUS.register(new TickHandler());
		MinecraftForge.EVENT_BUS.register(new PlayerInteractHandler());
		MinecraftForge.EVENT_BUS.register(new LivingAttackEvent());

		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

		registerEntity(ProjectileNimbusRay.class, "ProjectileNimbusRay", this, modid, 128, 1, true);
		registerEntity(ProjectileFlameStrike.class, "ProjectileFlameStrike", this, modid, 128, 1, true);
		registerEntity(EntitySpellImplosion.class, "EntitySpellImplosion", this, modid, 64, 1, true);
		registerEntity(EntitySpellBladeBarrier.class, "EntitySpellBladeBarrier", this, modid, 64, 1, true);
	}

	@Override
	public void postInit(FMLPostInitializationEvent e) {
		logger.info("Starting AoV PostInit");

		channel.register(new ServerPacketHandler());

	}

}