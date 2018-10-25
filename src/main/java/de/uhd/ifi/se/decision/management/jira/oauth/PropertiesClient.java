package de.uhd.ifi.se.decision.management.jira.oauth;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistence;

public class PropertiesClient {
	public static final String CONSUMER_KEY = "consumer_key";
	public static final String PRIVATE_KEY = "private_key";
	public static final String REQUEST_TOKEN = "request_token";
	public static final String ACCESS_TOKEN = "access_token";
	public static final String SECRET = "secret";
	public static final String JIRA_HOME = "jira_home";

	private static Map<String, String> DEFAULT_PROPERTY_VALUES;

	private final String fileUrl;
	private final String propFileName = "config.properties";

	public PropertiesClient() throws Exception {
		fileUrl = "./" + propFileName;
		initAuthPropertysFromConfigPersistance();
	}

	private void initAuthPropertysFromConfigPersistance() {
		PropertiesClient.DEFAULT_PROPERTY_VALUES = ImmutableMap.<String, String>builder()
				.put(JIRA_HOME, ConfigPersistence.getOauthJiraHome())
				.put(CONSUMER_KEY, ConfigPersistence.getConsumerKey())
				.put(REQUEST_TOKEN, ConfigPersistence.getRequestToken())
				.put(SECRET, ConfigPersistence.getSecretForOAuth())
				.put(ACCESS_TOKEN, ConfigPersistence.getAccessToken())
				.put(PRIVATE_KEY, ConfigPersistence.getPrivateKey()).build();
	}

	public Map<String, String> getPropertiesOrDefaults() {
		try {
			Map<String, String> map = toMap(tryGetProperties());
			map.putAll(Maps.difference(map, DEFAULT_PROPERTY_VALUES).entriesOnlyOnRight());
			map = DEFAULT_PROPERTY_VALUES;
			return map;
		} catch (FileNotFoundException e) {
			tryCreateDefaultFile();
			return new HashMap<>(DEFAULT_PROPERTY_VALUES);
		} catch (IOException e) {
			return DEFAULT_PROPERTY_VALUES;
		}
	}

	private Map<String, String> toMap(Properties properties) {
		return properties.entrySet().stream().filter(entry -> entry.getValue() != null)
				.collect(Collectors.toMap(o -> o.getKey().toString(), t -> t.getValue().toString()));
	}

	private Properties toProperties(Map<String, String> propertiesMap) {
		Properties properties = new Properties();
		propertiesMap.entrySet().stream().forEach(entry -> properties.put(entry.getKey(), entry.getValue()));
		return properties;
	}

	private Properties tryGetProperties() throws IOException {
		InputStream inputStream = new FileInputStream(new File(fileUrl));
		Properties prop = new Properties();
		prop.load(inputStream);
		return prop;
	}

	public void savePropertiesToFile(Map<String, String> properties) {
		OutputStream outputStream = null;
		File file = new File(fileUrl);

		try {
			outputStream = new FileOutputStream(file);
			Properties p = toProperties(properties);
			p.store(outputStream, null);
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		} finally {
			closeQuietly(outputStream);
		}
	}

	public void tryCreateDefaultFile() {
		System.out.println("Creating default properties file: " + propFileName);
		tryCreateFile().ifPresent(file -> savePropertiesToFile(DEFAULT_PROPERTY_VALUES));
	}

	private Optional<File> tryCreateFile() {
		try {
			File file = new File(fileUrl);
			file.createNewFile();
			return Optional.of(file);
		} catch (IOException e) {
			return Optional.empty();
		}
	}

	private void closeQuietly(Closeable closeable) {
		try {
			if (closeable != null) {
				closeable.close();
			}
		} catch (IOException e) {
			// ignored
		}
	}
}
