package de.espirit.firstspirit.opt.vscan.admin.gui;

import de.espirit.firstspirit.access.ServiceNotFoundException;
import de.espirit.firstspirit.module.Configuration;
import de.espirit.firstspirit.module.ServerEnvironment;
import de.espirit.firstspirit.opt.vscan.VScanService;
import de.espirit.firstspirit.opt.vscan.VScanServiceConfiguration;
import de.espirit.firstspirit.opt.vscan.resources.ModuleResources;

import info.clearthought.layout.TableLayout;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.Frame;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;


public class VScanServiceConfigPanel implements Configuration<ServerEnvironment> {

	private VScanServiceConfiguration _config;
	private ServerEnvironment _env;
	private JPanel _component;
	private JTextField _engineExecutableField;
	private JTextField _engineTimeoutField;
	private JComboBox<String> _enginesClassList;
	private List<String> _availableEngines;


	/**
	 * @return <code>true</code> if this component has a gui to show and change its configuration.
	 */
	@Override
	public boolean hasGui() {
		return true;
	}


	/**
	 * Returns the configuration gui.
	 *
	 * @param masterFrame basic frame component, for creating new windows
	 * @return configuration gui or <code>null</code>.
	 */
	@Override
	public JComponent getGui(final Frame masterFrame) {
		if (_component == null) {

			final double border = 5;
			final double rowsGap = 5;
			final double[][] size = {{border, TableLayout.FILL, border}, {border, TableLayout.PREFERRED, rowsGap, TableLayout.PREFERRED, rowsGap, TableLayout.PREFERRED, border}};
			final TableLayout tbl = new TableLayout(size);

			final JPanel panel = new JPanel();
			panel.setOpaque(false);
			panel.setBorder(BorderFactory.createTitledBorder(VScanService.MODULE_NAME));
			panel.setLayout(tbl);

			panel.add(getEnginePanel(), "1, 1, 1, 1");

			_component = (JPanel) masterFrame.add(panel);

			clearGuiValues();
			initGuiValues();
		}

		return _component;
	}


	private @NotNull JComponent getEnginePanel() {
		final double border = 5;
		final double rowsGap = 5;
		final double colsGap = 5;
		final double[][] size = {{border, TableLayout.PREFERRED, colsGap, TableLayout.FILL, colsGap, TableLayout.MINIMUM, border}, {border, TableLayout.PREFERRED, rowsGap, TableLayout.PREFERRED, rowsGap, TableLayout.PREFERRED, border}};
		final TableLayout tbl = new TableLayout(size);

		final JPanel enginePanel = new JPanel();
		enginePanel.setOpaque(false);
		enginePanel.setBorder(BorderFactory.createTitledBorder(ModuleResources.getString("fs-resource.module.vscan.EngineSetup")));
		enginePanel.setLayout(tbl);

		enginePanel.add(new JLabel(ModuleResources.getString("fs-resource.module.vscan.EngineExecutable")), "1, 1, 1, 1");
		_engineExecutableField = new JTextField();
		enginePanel.add(_engineExecutableField, "3, 1, 5, 1");


		_enginesClassList = new JComboBox<>(loadEnginesList());
		enginePanel.add(new JLabel(ModuleResources.getString("fs-resource.module.vscan.EngineClassname")), "1, 3, 1, 3");
		enginePanel.add(_enginesClassList, "3, 3, 5, 3");


		enginePanel.add(new JLabel(ModuleResources.getString("fs-resource.module.vscan.EngineTimeout")), "1, 5, 1, 5");
		_engineTimeoutField = new JTextField();
		enginePanel.add(_engineTimeoutField, "3, 5, 3, 5");
		enginePanel.add(new JLabel("ms"), "5, 5, 5, 5");

		return enginePanel;
	}


	/**
	 * Loads the current configuration from appropriate {@link de.espirit.firstspirit.module.ServerEnvironment#getConfDir()
	 * conf directory}.
	 */
	@Override
	public void load() {
		Object service;

		try {
			service = _env.getConnection().getService(VScanService.MODULE_SERVICE_NAME);
		} catch (final ServiceNotFoundException e) {
			service = null;
		}

		if (service instanceof final VScanService vScanService) {
			_config = vScanService.getServiceConfiguration();
		} else {
			final var message = ModuleResources.getString("fs-resource.module.vscan.ServiceNotFound");
			JOptionPane.showMessageDialog(null, message, "", JOptionPane.ERROR_MESSAGE);
		}
	}


	/** Stores the current configuration. */
	@Override
	public void store() {
		_config.setExecutable(URI.create(_engineExecutableField.getText().trim()));
		_config.setClassName(Objects.requireNonNull(_enginesClassList.getSelectedItem()).toString());
		_config.setTimeout(Long.parseLong(_engineTimeoutField.getText().trim()));

		if (_env.getConnection().getService(VScanService.MODULE_SERVICE_NAME) instanceof final VScanService service) {
			service.setServiceConfiguration(_config);
		}
	}


	private void clearGuiValues() {
		_engineExecutableField.setText("");
		_engineTimeoutField.setText("");
	}


	private void initGuiValues() {
		_engineExecutableField.setText(_config.getExecutable().toString().trim());
		_engineTimeoutField.setText(String.valueOf(_config.getTimeout()).trim());

		for (final Object engineClasses : _availableEngines) {
			if (_config.getClassName().equals(engineClasses.toString())) {
				_enginesClassList.setSelectedItem(engineClasses.toString());
			}
		}
	}


	private String[] loadEnginesList() {
		_availableEngines = _config.getAvailableEngines();

		if (_availableEngines.isEmpty()) {
			_availableEngines.add(ModuleResources.getString("fs-resource.module.vscan.ErrorNoEngineAvailable"));
		}

		return _availableEngines.toArray(new String[]{});
	}


	/**
	 * Returns all parameter names.
	 *
	 * @return all parameter names.
	 */
	@Override
	public Set<String> getParameterNames() {
		return new HashSet<>(_config.getServiceProperties().stringPropertyNames());
	}


	/**
	 * Returns a specific parameter or <code>null</code> if it's not available.
	 *
	 * @param name parameter name.
	 * @return parameter or <code>null</code>.
	 */
	@Override
	public String getParameter(final String name) {
		return _config.getParameter(name);
	}


	/**
	 * Initializes this component with the given {@link de.espirit.firstspirit.module.ServerEnvironment environment}. This
	 * method is called before the instance is used.
	 *
	 * @param moduleName	module name
	 * @param componentName component name
	 * @param env		   useful environment information for this component.
	 */
	@Override
	public void init(final String moduleName, final String componentName, final ServerEnvironment env) {
		_env = env;
	}


	/**
	 * Returns ComponentEnvironment
	 *
	 * @return ComponentEnvironment
	 */
	@Override
	public ServerEnvironment getEnvironment() {
		return _env;
	}
}
