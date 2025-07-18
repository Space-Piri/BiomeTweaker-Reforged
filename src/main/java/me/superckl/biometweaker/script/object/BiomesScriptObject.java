package me.superckl.biometweaker.script.object;

import me.superckl.api.biometweaker.BiomeTweakerAPI;
import me.superckl.api.biometweaker.script.object.BiomePackScriptObject;
import me.superckl.api.biometweaker.script.pack.BiomePackage;
import me.superckl.api.biometweaker.script.pack.MergedBiomesPackage;
import me.superckl.api.superscript.script.ScriptHandler;
import me.superckl.api.superscript.script.command.ScriptCommand;
import me.superckl.api.superscript.util.CollectionHelper;
import me.superckl.biometweaker.BiomeTweaker;
import me.superckl.biometweaker.script.command.StagedScriptCommand;

public class BiomesScriptObject extends BiomePackScriptObject{

	@Override
	public String[] modifyArguments(final String[] args, final ScriptHandler handler) {
		final String name = CollectionHelper.reverseLookup(handler.getObjects(), this);
		final String[] newArgs = new String[args.length+1];
		newArgs[0] = name;
		System.arraycopy(args, 0, newArgs, 1, args.length);
		return newArgs;
	}

	@Override
	public void addCommand(final ScriptCommand command) {
		if(command instanceof final StagedScriptCommand bCommand &&
				!bCommand.requiredStage().isValid(BiomeTweaker.getCommandManager().getCurrentStage()))
			throw new IllegalStateException(String.format("Invalid application stage! Command requires stages %s but found stage %s",
					bCommand.requiredStage().getValidStages(), BiomeTweaker.getCommandManager().getCurrentStage()));
		BiomeTweakerAPI.getCommandAdder().accept(command);
	}

	@Override
	public void readArgs(final Object... packs) throws Exception {
		final BiomePackage[] bPacks = new BiomePackage[packs.length];
		System.arraycopy(packs, 0, bPacks, 0, packs.length);
		if(bPacks.length == 1)
			this.pack = bPacks[0];
		else
			this.pack = new MergedBiomesPackage(bPacks);
	}



}
