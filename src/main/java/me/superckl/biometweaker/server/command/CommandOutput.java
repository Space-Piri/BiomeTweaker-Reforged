package me.superckl.biometweaker.server.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import me.superckl.biometweaker.BiomeTweaker;
import me.superckl.biometweaker.Output;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.OutgoingChatMessage;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.ServerPlayer;

public class CommandOutput {

	public static int output(final CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		Output.generateOutputFiles(ctx.getSource().getServer().registryAccess(), BiomeTweaker.getINSTANCE().getOutputDir());
//		ctx.getSource().sendSuccess(Component.literal("Regenerated output files."), true);
//		ctx.getSource().sendSuccess(() -> " ", true);
		System.out.println("im trying");
		return 0;
	}

}
