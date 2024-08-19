package de.espirit.firstspirit.opt.vscan.resources;

import org.jetbrains.annotations.NotNull;

import java.util.ResourceBundle;


public final class ModuleResources {

	private static ResourceBundle RESOURCES;
	private static final String LOCALE_RESOURCES = "de.espirit.firstspirit.opt.vscan.resources.locale.Messages";


	private ModuleResources() {
	}


	public static @NotNull ResourceBundle getResourceBundle() {
		if (RESOURCES == null) {
			RESOURCES = ResourceBundle.getBundle(LOCALE_RESOURCES);
		}
		return RESOURCES;
	}


	public static @NotNull String getString(final @NotNull String key) {
		return getResourceBundle().getString(key);
	}


}