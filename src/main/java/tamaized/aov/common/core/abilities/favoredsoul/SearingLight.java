package tamaized.aov.common.core.abilities.favoredsoul;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import tamaized.aov.AoV;
import tamaized.aov.common.capabilities.CapabilityList;
import tamaized.aov.common.capabilities.aov.IAoVCapability;
import tamaized.aov.common.core.abilities.Ability;
import tamaized.aov.common.core.abilities.AbilityBase;
import tamaized.aov.common.entity.ProjectileNimbusRay;

public class SearingLight extends AbilityBase {

	private static final int damage = 8;
	private static final int charges = 6;
	private static final int distance = 40;

	public SearingLight() {
		super(

				new TextComponentTranslation(getStaticName()),

				new TextComponentTranslation(""),

				new TextComponentTranslation("aov.spells.global.charges", charges),

				new TextComponentTranslation("aov.spells.global.range", distance),

				new TextComponentTranslation("aov.spells.global.damage", damage),

				new TextComponentTranslation(""),

				new TextComponentTranslation("aov.spells.searing.desc")

		);
	}

	public static String getStaticName() {
		return "aov.spells.searing.name";
	}

	@Override
	public ResourceLocation getIcon() {
		return new ResourceLocation(AoV.MODID, "textures/spells/searing.png");
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public String getName() {
		return I18n.format(getStaticName());
	}

	@Override
	public int getCoolDown() {
		return 4;
	}

	@Override
	public int getMaxCharges() {
		return charges;
	}

	@Override
	public int getChargeCost() {
		return 1;
	}

	@Override
	public double getMaxDistance() {
		return distance;
	}

	@Override
	public boolean usesInvoke() {
		return false;
	}

	@Override
	public boolean isCastOnTarget(EntityPlayer caster, IAoVCapability cap, EntityLivingBase target) {
		return false;
	}

	@Override
	public boolean cast(Ability ability, EntityPlayer caster, EntityLivingBase target) {
		IAoVCapability cap = CapabilityList.getCap(caster, CapabilityList.AOV);
		if (cap == null)
			return false;
		int a = (int) (damage * (1f + (cap.getSpellPower() / 100f)));
		ProjectileNimbusRay ray = new ProjectileNimbusRay(caster.world, caster, caster.posX, caster.posY, caster.posZ);
		ray.setSpell(this);
		ray.setColor(0xFFFFFFFF);
		ray.setDamage(a);
		ray.setMaxRange(distance);
		ray.setSpeed(3);
		caster.world.spawnEntity(ray);
		return true;
	}

}
