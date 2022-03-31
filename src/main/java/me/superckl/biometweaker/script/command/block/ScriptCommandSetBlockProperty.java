package me.superckl.biometweaker.script.command.block;

import com.google.gson.JsonPrimitive;

import lombok.RequiredArgsConstructor;
import me.superckl.api.biometweaker.block.BlockStateBuilder;
import me.superckl.api.superscript.AutoRegister;
import me.superckl.api.superscript.script.ParameterTypes;
import me.superckl.api.superscript.script.ScriptHandler;
import me.superckl.api.superscript.script.ScriptParser;
import me.superckl.api.superscript.script.command.ScriptCommand;
import me.superckl.biometweaker.script.object.block.BasicBlockStateScriptObject;

@AutoRegister(classes = BasicBlockStateScriptObject.class, name = "setProperty")
@RequiredArgsConstructor
public class ScriptCommandSetBlockProperty extends ScriptCommand{

	private final BlockStateBuilder<?> builder;
	private final String key;
	private final JsonPrimitive value;

	private ScriptHandler handler;

	@Override
	public void perform() throws Exception {
		String value = this.value.getAsString();
		if(ScriptParser.isStringArg(value))
			value = ParameterTypes.STRING.tryParse(value, this.handler);
		this.builder.setProperty(this.key, value);
	}

	@Override
	public void setScriptHandler(final ScriptHandler handler) {
		this.handler = handler;
	}

}
