package de.espirit.firstspirit.opt.vscan.engines.clamav;

import com.espirit.moddev.components.annotations.PublicComponent;
import de.espirit.firstspirit.access.AccessUtil;
import de.espirit.firstspirit.access.store.mediastore.UploadRejectedException;
import de.espirit.firstspirit.opt.vscan.ScanEngine;
import de.espirit.firstspirit.opt.vscan.VScanService;
import de.espirit.firstspirit.opt.vscan.VScanServiceConfiguration;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;


@PublicComponent(name = "ClamAvEngine", description = "ClamAV core class implementing the ScanEngine interface")
public class ClamScanEngine implements ScanEngine {

	public static final Logger LOGGER = LoggerFactory.getLogger(ClamScanEngine.class);
	public static final String ENGINE_NAME = "ClamAv";

	private URI _executable;


	public ClamScanEngine() {
		LOGGER.debug("ClamScanEngine created");
	}


	@Override
	public @NotNull String getName() {
		return ENGINE_NAME;
	}


	@Override
	public void init(final @NotNull VScanServiceConfiguration configuration) {
		_executable = configuration.getExecutable();
        LOGGER.debug("Executable set to: {}", _executable);
		if (!executableExists()) {
            LOGGER.error("{}: Executable '{}' not found.", getName(), _executable);
			throw new IllegalStateException(VScanService.MODULE_NAME + ": " + getName() + " executable '" + _executable + "' not found.");
		}
	}


	private boolean executableExists() {
		return new File(_executable.toString()).isFile();
	}


	@Override
	public void scanFile(final @NotNull File file) throws UploadRejectedException {
        LOGGER.debug("Scanning file {}", file);
		final String[] args = {_executable.toString(), file.toString()};
		final StringWriter out = new StringWriter();
		final StringWriter error = new StringWriter();

		try {
			AccessUtil.executeProcess(out, error, args);
		} catch (final IOException e) {
			LOGGER.error("Couldn't start process", e);
			throw new UploadRejectedException("Couldn't start upload filter process", e);
		}

		final String message = out.toString();
		final String[] stat = message.split(":");
		final String[] cause = message.split("\n");
		if (!" OK".equals(stat[1].substring(0, 3))) {
			LOGGER.error("File upload rejected - {}", message);
			throw new UploadRejectedException(cause[0]);
		}
		LOGGER.debug("Scanned file {}", file);
	}


}
