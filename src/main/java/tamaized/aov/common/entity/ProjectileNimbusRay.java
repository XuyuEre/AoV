package tamaized.aov.common.entity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import tamaized.aov.common.capabilities.CapabilityList;
import tamaized.aov.common.capabilities.aov.IAoVCapability;
import tamaized.aov.registry.AoVDamageSource;
import tamaized.aov.registry.AoVEntities;

import java.util.Objects;

public class ProjectileNimbusRay extends ProjectileBase {

	public ProjectileNimbusRay(World worldIn) {
		super(Objects.requireNonNull(AoVEntities.projectilenimbusray), worldIn);
		setDamageRangeSpeed(2.0F, 0, 0.0F);
	}

	public ProjectileNimbusRay(World world, EntityPlayer shooter, double x, double y, double z) {
		super(Objects.requireNonNull(AoVEntities.projectilenimbusray), world, shooter, x, y, z);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public int getBrightnessForRender() {
		return 0xF000F0;
	}

	@Override
	protected DamageSource getDamageSource() {
		return AoVDamageSource.createEntityDamageSource(AoVDamageSource.NIMBUS, shootingEntity);
	}

	@Override
	protected float getDamageAmp(double damage, Entity shooter, Entity target) {
		return (float) (damage * (target instanceof EntityMob && ((EntityMob) target).isEntityUndead() ? 2 : 1));
	}

	@Override
	protected void arrowHit(EntityLivingBase entity) {
		if (shootingEntity != null) {
			IAoVCapability cap = CapabilityList.getCap(shootingEntity, CapabilityList.AOV);
			if (cap != null)
				cap.addExp(shootingEntity, 20, getSpell());
		}
	}

	@Override
	protected void blockHit(IBlockState state, BlockPos pos) {

	}

}
