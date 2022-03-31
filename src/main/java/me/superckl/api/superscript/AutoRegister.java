package me.superckl.api.superscript;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import me.superckl.api.superscript.script.object.ScriptObject;

@Target({ElementType.TYPE, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(AutoRegisters.class)
public @interface AutoRegister {

	String name();

	Class<? extends ScriptObject>[] classes();

	@Target(ElementType.CONSTRUCTOR)
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface RegisterExempt{

	}

	@Target(ElementType.CONSTRUCTOR)
	@Retention(RetentionPolicy.RUNTIME)
	@Repeatable(ParameterOverrides.class)
	public static @interface ParameterOverride{

		int parameterIndex();

		String exceptionKey();

	}

	@Target(ElementType.CONSTRUCTOR)
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface ParameterOverrides{

		ParameterOverride[] value();

	}

}
