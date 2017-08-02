package tools.license;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;
import java.util.prefs.Preferences;
import javax.security.auth.x500.X500Principal;
import de.schlichtherle.license.CipherParam;
import de.schlichtherle.license.KeyStoreParam;
import de.schlichtherle.license.LicenseContent;
import de.schlichtherle.license.LicenseManager;
import de.schlichtherle.license.LicenseParam;

/**
 * Copyright 2010, Alvin J. Alexander, http://devdaily.com.
 *
 * This software is released under the terms of the GNU LGPL license. See
 * http://www.gnu.org/licenses/lgpl.html for more information.
 *
 * Usage: java LicenseServer.jar [baseFilename]
 *
 * This results in reading in a file named "baseFilename.dat", and writing a
 * filename named "baseFilename.lic".
 *
 * baseFilename will be in a format like "firstName-lastName-mmddhhmmss"
 *
 */
public class LicenseServer {
	// TODO move this to the properties file
	private static final String APP_VERSION = "1.1";
	private static final String PROPERTIES_FILENAME = "DSLicenseServer.properties";

	// get these from properties file
	private String appName;
	private String dataFileExtension;
	private String licenseFileExtension;

	// keystore information (from properties file)
	private static String keystoreFilename; // this app needs the "private" keystore
	private static String keystorePassword;
	private static String keyPassword;
	private static String alias;
	private static String cipherParamPassword; // 6+ chars, and both letters and numbers

	// built by our app
	private final KeyStoreParam privateKeyStoreParam;
	private final CipherParam cipherParam;

	// exit status codes
	private static int EXIT_STATUS_ALL_GOOD = 0;
	private static int EXIT_STATUS_ERR_WRONG_NUM_ARGS = 1;
	private static int EXIT_STATUS_ERR_EXCEPTION_THROWN = 2;
	private static int EXIT_STATUS_ERR_CANT_READ_DATA_FILE = 3;
	private static int EXIT_STATUS_ERR_CANT_OUR_PROPERTIES_FILE = 4;

	// properties we get from the data file, and write to the license file
	private String firstName;
	private String lastName;
	private String city;
	private String state;
	private String country;

	public static void main(String[] args) {
		// should have one arg, and it should be the basename of the file(s)
		if (args.length != 2) {
			System.err.println("Need two args: [directory] [baseFilename]");
			System.exit(EXIT_STATUS_ERR_WRONG_NUM_ARGS);
		}

		// args ok, run program
		new LicenseServer(args[0], args[1]);
	}

	public LicenseServer(final String directory, final String fileBasename) {
		// load all the properties we need to run; such as keystore file name, password...
		loadOurPropertiesFile();

		// read all the attributes from the data file for this customer
		loadInfoFromCustomerDataFile(directory, fileBasename);

		// set up an implementation of the KeyStoreParam interface that returns
		// the information required to work with the keystore containing the private
		// key:
		privateKeyStoreParam = new KeyStoreParam() {
			public InputStream getStream() throws IOException {
				final String resourceName = keystoreFilename;
				final InputStream in = getClass().getResourceAsStream(resourceName);
				if (in == null) {
					System.err.println("Could not load file: " + resourceName);
					throw new FileNotFoundException(resourceName);
				}
				return in;
			}

			public String getAlias() {
				return alias;
			}

			public String getStorePwd() {
				return keystorePassword;
			}

			public String getKeyPwd() {
				return keyPassword;
			}
		};

		// Set up an implementation of the CipherParam interface to return the password
		// to be
		// used when performing the PKCS-5 encryption.
		cipherParam = new CipherParam() {
			public String getKeyPwd() {
				return cipherParamPassword;
			}
		};

		// Set up an implementation of the LicenseParam interface.
		// Note that the subject string returned by getSubject() must match the subject
		// property
		// of any LicenseContent instance to be used with this LicenseParam instance.
		LicenseParam licenseParam = new LicenseParam() {
			public String getSubject() {
				return appName;
			}

			public Preferences getPreferences() {
				// TODO why is this needed for the app that creates the license?
				// return Preferences.userNodeForPackage(LicenseServer.class);
				return null;
			}

			public KeyStoreParam getKeyStoreParam() {
				return privateKeyStoreParam;
			}

			public CipherParam getCipherParam() {
				return cipherParam;
			}
		};

		// create the license file
		LicenseManager lm = new LicenseManager(licenseParam);
		try {
			// write the file to the same directory we read it in from
			String filename = directory + "/" + fileBasename + licenseFileExtension;
			lm.store(createLicenseContent(licenseParam), new File(filename));
			System.exit(EXIT_STATUS_ALL_GOOD);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(EXIT_STATUS_ERR_EXCEPTION_THROWN);
		}
	}

	/**
	 * Load the general properties this application needs in order to run.
	 */
	private void loadOurPropertiesFile() {
		Properties properties = new Properties();
		FileInputStream in;
		try {
			in = new FileInputStream(PROPERTIES_FILENAME);
			properties.load(in);
			in.close();
			appName = properties.getProperty("app_name");
			dataFileExtension = properties.getProperty("data_file_extension");
			licenseFileExtension = properties.getProperty("license_file_extension");
			keystoreFilename = properties.getProperty("keystore_filename");
			keystorePassword = properties.getProperty("keystore_password");
			alias = properties.getProperty("alias");
			keyPassword = properties.getProperty("key_password");
			cipherParamPassword = properties.getProperty("cipher_param_password");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(EXIT_STATUS_ERR_CANT_OUR_PROPERTIES_FILE);
		}
	}

	/**
	 * Read the data file that has information about the current customer.
	 *
	 * @param directory
	 *            The directory where the properties file is located.
	 * @param fileBasename
	 *            The base portion of the filename.
	 */
	private void loadInfoFromCustomerDataFile(final String directory, final String fileBasename) {
		Properties properties = new Properties();
		FileInputStream in;
		try {
			in = new FileInputStream(directory + "/" + fileBasename + dataFileExtension);
			properties.load(in);
			in.close();
			firstName = properties.getProperty("first_name");
			lastName = properties.getProperty("last_name");
			city = properties.getProperty("city");
			state = properties.getProperty("state");
			country = properties.getProperty("country");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(EXIT_STATUS_ERR_CANT_READ_DATA_FILE);
		}
	}

	// Set up the LicenseContent instance. This is the information that will be in
	// the
	// generated license file.

	private LicenseContent createLicenseContent(LicenseParam licenseParam) {
		LicenseContent result = new LicenseContent();
		X500Principal holder = new X500Principal(
				"CN=" + firstName + " " + lastName + ", " + "L=" + city + ", " + "ST=" + state + ", " + "C=" + country);
		result.setHolder(holder);
		X500Principal issuer = new X500Principal("CN=devdaily.com, L=Simpsonville, ST=KY, "
				+ " OU=Software Development," + " O=DevDaily Interactive," + " C=United States," + " DC=US");
		result.setIssuer(issuer);
		// i'm selling one license
		result.setConsumerAmount(1);
		// i think this needs to be "user"
		result.setConsumerType("User");
		result.setInfo("License key for the " + appName + " application.");
		Date now = new Date();
		result.setIssued(now);
		now.setYear(now.getYear() + 50);
		result.setNotAfter(now);
		result.setSubject(licenseParam.getSubject());
		return result;
	}

}
