package de.espirit.firstspirit.opt.vscan;

import com.espirit.moddev.components.annotations.PublicComponent;
import de.espirit.firstspirit.server.mediamanagement.FileBasedUploadFilter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;


@PublicComponent(name = "VScanFilterProxy",
		description = "The main engine which calls the specialized engine implementations.",
		hidden = true)
public class VScanFilterProxy extends FileBasedUploadFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(VScanFilterProxy.class);


	@Override
	public void init() {
		LOGGER.trace("VScanFilterProxy.init()");
	}


	// -- get VScanService, get VScanServiceConfiguration -- //


	/**
	 * Get the VScanService by the serviceLocator
	 *
	 * @return the VScanService
	 */
	private VScanService getVScanService() {
		return (VScanService) getServiceLocator().getService(VScanService.MODULE_SERVICE_NAME);
	}


	// -- Filter, Execute, Grant or Reject -- //


	/** {@inheritDoc} */
	@Override
	public void doFilter(final @NotNull File tempFile) throws IOException {
		try {
			getVScanService().scanFile(tempFile);
		} catch (final ClassNotFoundException e) {
			final var message = "Loading of virus scanning engine failed!";
			LOGGER.info(message, e);
			throw new IOException(message);
		}
	}

}
