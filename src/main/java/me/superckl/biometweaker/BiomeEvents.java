package me.superckl.biometweaker;

import java.util.Optional;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player.BedSleepingProblem;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.player.BonemealEvent;
import net.neoforged.neoforge.event.entity.player.CanContinueSleepingEvent;
import net.neoforged.neoforge.event.level.block.CropGrowEvent;
import net.neoforged.neoforge.event.level.BlockGrowFeatureEvent;
import net.neoforged.bus.api.Event;
//import net.neoforged.eventbus.api.Event.Result;
import net.neoforged.bus.api.SubscribeEvent;

public class BiomeEvents {

//	@SubscribeEvent
//	public void onSleep(final CanContinueSleepingEvent e) {
//		if(e.getEntity().level().isClientSide || e.getResultStatus() != null)
//			return;
//		e.getEntity().getPosition(1).ifPresent(pos -> {
//			final Optional<BiomeModificationManager> mod = BiomeModificationManager.forBiomeOpt(e.getEntity().level().getBiome(pos).unwrapKey().get());
//			mod.filter(BiomeModificationManager::isDisableSleep).ifPresent(x -> {
//				e.setResult(BedSleepingProblem.NOT_POSSIBLE_HERE);
//				((ServerPlayer) e.getEntity()).displayClientMessage(Component.translatable("biometweaker.sleep.biome"), true);
//			});
//		});
//	}
//
//	@SubscribeEvent
//	public void onBonemeal(final BonemealEvent e) {
//		if(e.getLevel().isClientSide)
//			return;
//		final Optional<BiomeModificationManager> mod = BiomeModificationManager.forBiomeOpt(e.getLevel().getBiome(e.getPos()).unwrapKey().get());
//		mod.filter(BiomeModificationManager::isDisableBonemeal).ifPresent(x -> {
//			if(Config.consumeBonemeal())
//				e.setResult(Result.ALLOW);
//			else
//				e.setCanceled(true);
//		});
//	}
//
//	@SubscribeEvent
//	public void onCropGrow(final CropGrowEvent.Pre e) {
//		if(e.getLevel().isClientSide())
//			return;
//		final Optional<BiomeModificationManager> mod = BiomeModificationManager.forBiomeOpt(e.getLevel().getBiome(e.getPos()).unwrapKey().get());
//		mod.filter(BiomeModificationManager::isDisableCropGrowth).ifPresent(x -> e.setResult(Result.DENY));
//	}
//
//	@SubscribeEvent
//	public void onSaplingGrow(final BlockGrowFeatureEvent e) {
//		if(e.getLevel().isClientSide())
//			return;
//		final Optional<BiomeModificationManager> mod = BiomeModificationManager.forBiomeOpt(e.getLevel().getBiome(e.getPos()).unwrapKey().get());
//		mod.filter(BiomeModificationManager::isDisableSaplingGrowth).ifPresent(x -> e.setResult(Result.DENY));
//	}

	@SubscribeEvent
	public void onLivingTick(final LivingEvent e) {
		//Avoid the biome lookup for every entity every tick if there are no effects to add
		if(!BiomeModificationManager.hasMobEffects)
			return;
		final LivingEntity entity = e.getEntity();
		if(entity.level().isClientSide || !entity.isAffectedByPotions())
			return;
		final Optional<BiomeModificationManager> modOpt = BiomeModificationManager.forBiomeOpt(entity.level().getBiome(entity.getOnPos()).unwrapKey().get());
		modOpt.ifPresent(mod -> {
			mod.getMobEffects(entity.getType()).forEach(effect -> {
				if(entity.tickCount % effect.interval() != 0 || entity.getRandom().nextFloat() > effect.chance())
					return;
				if(effect.effect().isInstantenous())
					effect.effect().applyInstantenousEffect(null, null, entity, effect.amplifier(), 1D);
				else
					entity.addEffect(effect.createInstance());
			});
		});
	}

}
