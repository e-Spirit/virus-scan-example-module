package de.espirit.firstspirit.opt.vscan;

import de.espirit.firstspirit.access.store.mediastore.UploadRejectedException;
import org.jetbrains.annotations.NotNull;

import java.io.File;


/**
 * Interface for implementing different kinds of virus scanning engines. local, remote (icap).
 */
public interface ScanEngine {


	/**
	 * Get the virus scanning engine name.
	 *
	 * @return the engine name <code>String</code>
	 */
	@NotNull String getName();


	/**
	 * The VirusScanner-Engine configuration.
	 *
	 * @param configuration <code>VScanServiceConfiguration</code>
	 */
	void init(@NotNull VScanServiceConfiguration configuration);


	/**
	 * Invoke the executable including the argument.
	 * Could be something like: /usr/local/bin/clamscan fileName.pdf or http://icap.service.test
	 *
	 * @param file the <code>File</code> to scan
	 * @throws UploadRejectedException on file upload rejection.
	 */
	void scanFile(@NotNull File file) throws UploadRejectedException;


}
