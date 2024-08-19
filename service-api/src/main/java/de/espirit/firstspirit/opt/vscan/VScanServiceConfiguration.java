package de.espirit.firstspirit.opt.vscan;

import de.espirit.common.base.Logging;

import java.io.Serial;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Properties;


public class VScanServiceConfiguration implements Serializable {


	@Serial
	private static final long serialVersionUID = 0L;
	private static final Class<?> LOGGER = VScanServiceConfiguration.class;

	private final Properties _props;
	private URI _executable;
	private String _clazz;
	private long _timeout;
	private List<String> _availableEngines;


	public VScanServiceConfiguration() {
		_props = new Properties();
	}


	public void init() {
		try {
			setExecutable(new URI(_props.getProperty("fsm.vscan.engine.executable")));
			setClassName(_props.getProperty("fsm.vscan.engine.class"));
			setTimeout(Long.parseLong(_props.getProperty("fsm.vscan.engine.timeout")));
		} catch (final URISyntaxException e) {
			Logging.logError("URI syntax failure: " + getExecutable().toString(), e, LOGGER);
		}
	}


	public Properties getServiceProperties() {
		return _props;
	}


	/**
	 * Getter for property 'availableEngines'.
	 *
	 * @return Value for property 'availableEngines'.
	 */
	public List<String> getAvailableEngines() {
		return _availableEngines;
	}


	/**
	 * Setter for property 'availableEngines'.
	 *
	 * @param availableEngines Value to set for property 'availableEngines'.
	 */
	public void setAvailableEngines(final List<String> availableEngines) {
		_availableEngines = availableEngines;
	}


	// -- Parameter --//


	public URI getExecutable() {
		return _executable;
	}


	public void setExecutable(final URI executable) {
		_executable = executable;
	}


	public String getClassName() {
		return _clazz;
	}


	public void setClassName(final String clazz) {
		_clazz = clazz;
	}


	public long getTimeout() {
		return _timeout;
	}


	public void setTimeout(final long timeout) {
		_timeout = timeout;
	}


	/**
	 * Returns a specific parameter or <code>null</code> if it's not available.
	 *
	 * @param name parameter name.
	 * @return parameter or <code>null</code>.
	 */
	public String getParameter(final String name) {
		return getServiceProperties().getProperty(name);
	}

}
