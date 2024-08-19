package de.espirit.firstspirit.opt.vscan;

import com.espirit.moddev.components.annotations.ServiceComponent;
import de.espirit.firstspirit.access.ModuleAgent;
import de.espirit.firstspirit.access.store.mediastore.UploadRejectedException;
import de.espirit.firstspirit.client.access.editor.EditorValues;
import de.espirit.firstspirit.io.FileHandle;
import de.espirit.firstspirit.module.ServerEnvironment;
import de.espirit.firstspirit.module.Service;
import de.espirit.firstspirit.module.ServiceProxy;
import de.espirit.firstspirit.module.descriptor.ComponentDescriptor;
import de.espirit.firstspirit.module.descriptor.ServiceDescriptor;

import de.espirit.firstspirit.opt.vscan.admin.gui.VScanServiceConfigPanel;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.List;

@ServiceComponent(name = "VScanService", description = "FirstSpirit Virus Scan Service",
		configurable = VScanServiceConfigPanel.class)
public class VScanServiceImpl implements VScanService, Service<VScanService> {

	private static final Logger LOGGER = LoggerFactory.getLogger(VScanServiceImpl.class);

	public static final String MODULE_CONFIG_FILE = "fs-vscan.conf";

	private ServerEnvironment _environment;
	private volatile boolean _running;
	private @NotNull VScanServiceConfiguration _config;
	private FileHandle _configFile;


	public VScanServiceImpl() {
		_config = new VScanServiceConfiguration();
	}


	/** Starts the service. */
	@Override
	public void start() {
		LOGGER.info("Starting " + VScanService.MODULE_SERVICE_NAME + "...");
        try {
            loadConfiguration();
			_running = true;
        } catch (final IOException e) {
            if (_configFile != null) {
				LOGGER.error("Failed to load configuration file '{}'", _configFile.getName(), e);
			} else {
				LOGGER.error("Config file location could not be determined", e);
			}
			_running = false;
        }
	}


	/** Stops the service. */
	@Override
	public void stop() {
		LOGGER.info("Shutting down " + VScanService.MODULE_SERVICE_NAME + "...");
		_running = false;
	}


	/**
	 * Returns whether the service is running.
	 *
	 * @return <code>true</code> if the service is running.
	 */
	@Override
	public boolean isRunning() {
		return _running;
	}


	/**
	 * Returns the service interface. Only methods of this interface are accessible, so <code>Service</code> instances must
	 * also implement this interface.
	 *
	 * @return service interface class.
	 */
	@Override
	public @NotNull Class<? extends VScanService> getServiceInterface() {
		return VScanService.class;
	}


	/**
	 * A service proxy is an optional, client-side service implementation. It will be instantiated, {@link
	 * de.espirit.firstspirit.module.ServiceProxy#init(Object, de.espirit.firstspirit.access.Connection) initialized} and
	 * returned by {@link de.espirit.firstspirit.access.Connection#getService(String)}. The proxy class must have a no-arg
	 * constructor and must implement the {@link de.espirit.firstspirit.module.ServiceProxy} interface, but has not to
	 * implement the {@link #getServiceInterface() service-interface} itself.
	 *
	 * @return service proxy class or <code>null</code> if no proxy is provided.
	 */
	@Override
	public Class<? extends ServiceProxy<VScanService>> getProxyClass() {
		return null;
	}


	/**
	 * Initializes this component with the given {@link de.espirit.firstspirit.module.descriptor.ComponentDescriptor
	 * descriptor} and {@link de.espirit.firstspirit.module.ServerEnvironment environment}. No other method will be called
	 * before the component is initialized!
	 *
	 * @param descriptor useful descriptor information for this component.
	 * @param env		useful environment information for this component.
	 */
	@Override
	public void init(final ServiceDescriptor descriptor, final ServerEnvironment env) {
		_environment = env;
	}


	/** Event method: called if Component was successfully installed (not updated!) */
	@Override
	public void installed() {
		copyConfigFile(getConfigFileName());
        LOGGER.debug("{} installed...", getName());
	}


	/** Event method: called if Component was in uninstalling procedure */
	@Override
	public void uninstalling() {
        LOGGER.debug("{} uninstalling...", getName());
	}


	/**
	 * Event method: called if Component was completely updated
	 *
	 * @param oldVersionString old version, before component was updated
	 */
	@Override
	public void updated(final String oldVersionString) {
		// e.g. merge, diff, backup the config file or do not overwrite
		copyConfigFile(getConfigFileName());
        LOGGER.debug("{} updated...", getName());
	}


	/**
	 * Copy a config file from the package dir to module config dir -> conf/modules/$module_name$.$module_service_name$
	 *
	 * @param file the module config file name string
	 */
	private void copyConfigFile(final String file) {
		final FileHandle configFile;

		try {
			configFile = _environment.getConfDir().obtain(file);
		} catch (final IOException e) {
			LOGGER.error("Error saving file for {} component", getName(), e);
			return;
		}

		if (configFile.exists()) {
			LOGGER.debug("Config file {} exists, do not overwrite", configFile.getPath());
		} else {
			try (final InputStream is = getClass().getClassLoader().getResourceAsStream(file)) {
				if (is != null) {
					configFile.save(is);
				}
			} catch (final IOException e) {
				LOGGER.error("Error saving file for {} component", getName(), e);
            }
        }
	}


	public @NotNull List<String> getAvailableEngines() {
		final var moduleAgent = _environment.getBroker().requireSpecialist(ModuleAgent.TYPE);
		final Collection<ComponentDescriptor> descriptors = moduleAgent.getComponents(ScanEngine.class);
		return descriptors.stream()
				.map(ComponentDescriptor::getComponentClass)
				.toList();
	}


	@SuppressWarnings({"UnusedCatchParameter"})
	private ScanEngine getScanEngine(final @NotNull String className) throws ClassNotFoundException {
		final Class<?> clazz = getClass().getClassLoader().loadClass(className);
		LOGGER.info("Loading Anti Virus Scanning Engine: {} ...", className);
		if (!ScanEngine.class.isAssignableFrom(clazz)) {
			throw new IllegalStateException(clazz + " does not implement " + ScanEngine.class.getName());
		}

		final ScanEngine result;
		try {
			result = (ScanEngine) clazz.getDeclaredConstructor().newInstance();
		} catch (final InstantiationException | InvocationTargetException | NoSuchMethodException e) {
			throw new InstantiationError("Could not instantiate virus scan engine " + className);
		} catch (final IllegalAccessException e) {
			throw new IllegalStateException("Access denied - class: " + className);
        }

		result.init(_config);
		return result;
    }


	@Override
	public void scanFile(@NotNull final File file) throws ClassNotFoundException, UploadRejectedException {
		try {
			final ScanEngine scanEngine = getScanEngine(_config.getClassName());
            LOGGER.info("Scanning {} with {}", file, scanEngine.getName());
			scanEngine.scanFile(file);
            LOGGER.info("File '{}' ok", file);
		} catch (final UploadRejectedException e) {
            LOGGER.warn("File '{}' not ok - {}", file, e.getMessage());
			throw e;
		} catch (final ClassNotFoundException e) {
            LOGGER.error("Loading of virus scanning engine failed: {}", _config.getClassName(), e);
			throw new ClassNotFoundException("Loading of virus scanning engine failed: " + _config.getClassName(), e);
		}
	}


	// VScanServiceConfiguration


	private void loadConfiguration() throws IOException {
		_configFile = _environment.getConfDir().obtain(MODULE_CONFIG_FILE);
		try (final InputStream is = _configFile.load()) {
			if (_configFile.isFile()) {
				_config.getServiceProperties().load(is);
				_config.init();
				_config.setAvailableEngines(getAvailableEngines());
			}
		}
	}


	private void saveConfiguration() throws IOException {
		if (_configFile.exists() && _configFile.isFile()) {
			Files.copy(Paths.get(_configFile.getPath()), Paths.get(_configFile.getPath() + ".1"), StandardCopyOption.REPLACE_EXISTING);
		}

		_config.getServiceProperties().setProperty("fsm.vscan.engine.executable", _config.getExecutable().toString());
		LOGGER.debug("Setting property - fsm.vscan.engine.executable={}", _config.getExecutable());

		_config.getServiceProperties().setProperty("fsm.vscan.engine.class", _config.getClassName());
		LOGGER.debug("Setting property - fsm.vscan.engine.class={}", _config.getClassName());

		_config.getServiceProperties().setProperty("fsm.vscan.engine.timeout", String.valueOf(_config.getTimeout()));
		LOGGER.debug("Setting property - fsm.vscan.engine.timeout={}", _config.getTimeout());

		try (final var os = _configFile.getOutputStream()) {
			_config.getServiceProperties().store(os, "Last modified");
		}
	}


	/** {@inheritDoc} */
	@Override
	@NotNull
	public String getName() {
		return MODULE_NAME;
	}


	/**
	 * Get the module configuration file name
	 *
	 * @return the config file name string
	 */
	@NotNull
	private static String getConfigFileName() {
		return MODULE_CONFIG_FILE;
	}


	/** {@inheritDoc} */
	@Override
	public boolean isEnabled() {
		return _running;
	}


	/** {@inheritDoc} */
	@Override
	public @NotNull VScanServiceConfiguration getServiceConfiguration() {
		try {
			loadConfiguration();
			return _config;
		} catch (final IOException e) {
			if (_configFile != null) {
				throw new UncheckedIOException("Failed to load configuration file: " + _configFile.getName(), e);
			} else {
				throw new UncheckedIOException("Config file location could not be determined", e);
			}
		}
	}


	@Override
	public void setServiceConfiguration(final @NotNull VScanServiceConfiguration config) {
		_config = config;

		try {
			saveConfiguration();
		} catch (final IOException e) {
			LOGGER.error("Failed to save configuration file: {}", _configFile.getName(), e);
		}
	}

}
