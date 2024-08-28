/**
 * A quick implementation of a client, made mostly for testing purposes.
 */

package com.ericsson.eniq.licensing.cache;

import java.rmi.Naming;

/**
 * @author ecarbjo
 */
public class LicensingClient {

	final private LicensingSettings settings;

	/**
	 * Default constructor.
	 * 
	 * @param host
	 *            the host that runs the registry.
	 */
	public LicensingClient() {
		settings = new LicensingSettings();

		try {
			// contact the registry and get the cache instance.
			final LicensingCache cache = (LicensingCache) Naming.lookup("rmi://"
					+ settings.getServerHostName() + ":"
					+ settings.getServerPort() + "/"
					+ settings.getServerRefName());

			if (cache == null) {
				System.err.println("Could not locate the LicensingCache");
			} else {
				// update the cache.
				cache.update();

				// create a dummy license descriptor and a techpack descriptr
				final LicenseDescriptor lic1 = new DefaultLicenseDescriptor(
						"my-very-nice-license");
				final LicenseDescriptor lic2 = new DefaultLicenseDescriptor(
						"CXC4010583");

				// get a licensing response for the created descriptors.
				LicensingResponse response = cache.checkLicense(lic1);
				System.out
						.println("The license is valid: " + response.isValid()
								+ " msg: " + response.getMessage());

				response = cache.checkLicense(lic2);
				System.out
						.println("The default starter license is valid: " + response.isValid()
								+ " msg: " + response.getMessage());
			}
		} catch (Exception e) {
			System.err.println("Client exception: " + e.toString());
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		new LicensingClient();
	}
}
