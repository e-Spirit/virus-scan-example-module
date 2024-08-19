package de.espirit.firstspirit.opt.vscan;

import de.espirit.firstspirit.access.store.mediastore.UploadRejectedException;

import org.jetbrains.annotations.NotNull;

import java.io.File;


public interface VScanService {

	String MODULE_NAME = "FS VScan Service";
	String MODULE_SERVICE_NAME = "VScanService";
	
	/**
	 * Returns the global service status <code>true</code> | <code>false</code>
	 *
	 * @return <code>true</code> if vscan service is activated
	 */
	boolean isEnabled();


	/**
	 * Get the VScan Service Configurations instances
	 *
	 * @return VScanServiceConfiguration
	 */
	@NotNull VScanServiceConfiguration getServiceConfiguration();


	/**
	 * Set the service configuration.
	 *
	 * @param config the service configuration properties
	 */
	void setServiceConfiguration(@NotNull VScanServiceConfiguration config);


	/**
	 * Get the module name, same as in module.xml
	 *
	 * @return the module name string
	 */
	@NotNull String getName();


	/**
	 * Run the configured AvEngine implementation
	 *
	 * @param file the <code>File</code> to scan
	 * @throws ClassNotFoundException  if the specific antivirus engine implementation couldn't be loaded
	 * @throws UploadRejectedException on file upload rejection.
	 */
	void scanFile(final @NotNull File file) throws ClassNotFoundException, UploadRejectedException;
}
