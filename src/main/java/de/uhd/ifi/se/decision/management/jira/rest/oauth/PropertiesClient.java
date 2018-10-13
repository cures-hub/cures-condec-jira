package de.uhd.ifi.se.decision.management.jira.rest.oauth;


import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

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

public class PropertiesClient {
	  public static final String CONSUMER_KEY = "consumer_key";
	    public static final String PRIVATE_KEY = "private_key";
	    public static final String REQUEST_TOKEN = "request_token";
	    public static final String ACCESS_TOKEN = "access_token";
	    public static final String SECRET = "secret";
	    public static final String JIRA_HOME = "jira_home";


    private final static Map<String, String> DEFAULT_PROPERTY_VALUES = ImmutableMap.<String, String>builder()
            .put(JIRA_HOME, "http://cures.ifi.uni-heidelberg.de:8080")
            .put(CONSUMER_KEY, "OauthKey")
//            .put(REQUEST_TOKEN,"AUJmP3i9fY8cqW2SDMb5F8jfLGZnmVey")
//            .put(SECRET,"TbB2ZD")
//            .put(ACCESS_TOKEN,"p2d0VpiBSMRhQdAgu0ZOnerlzBKffpJ3")
            .put(PRIVATE_KEY, "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAMhmjkK9MsLveS9o0wzT/rLLefFuGz1pvmFikI9cBWRx8dawXSSnIItEtPO6yJjqK+ZiLKrd3WvMwSd45yggjeiNKe2jRhGia/QgJePDC/+09Z9iWOhwPA/Eci+E4cwD/JGtjS0Gg6U8qCQc3wlZX6/z5g/+3paEgHV+FOelQSztAgMBAAECgYEAsOkQR0x8xmffpIG2ZsmzPCWytfaMp491GMWJdnU28XBBnVQ+NcAwU6HI2K0Yrx1yucQLSJ/p+2NbVLw/3EW433NTgQPTxM/xrjIlvtZQDdgttEEmczfsVzD3tgvj9TvqDKngKQH0o9rUjDC4rI4f766gL7142Qb5elqMkJZrg0UCQQD9336IqPXV0WHxMR2el89MmclMtrek15LraDCnxbpb3rxajB4pGr5h9q8eOAU4ANPnIXCG6vYyU8x/l7lnydG3AkEAyhRfwK8UW58tOyoiJe2FuOXEFJYPeUPGM6JLalwfZFuDYpW3TQhg+mrpVPNyCAgaL4V97NzSoMH1LTzp1hnmewJBAOZBPlJUbCNxtJM9KNAegDXJhXm+fvFTVD2OUhLYkx2f9tVpIDHHv8S6KDoQNSuGFKsc+SJlGMasml1fDxnDQiECQQCdy5EFnfEwpjgklf76TOH5gnk9dfv5PiH72cQ39l2Q+SC8D5qFrYBEqs0ux7aIbQM9jmjJV5mlbC8uNv2FcM4XAkA1jdGDbtkKY0NKvjU3G0VhG1PTrCDSu7FRHb4/kMjfLPVlTaioKOp654ZiWPimVHnlTy0kqZ+ratK8qtuiucNs") 
            		            .build();

    private final String fileUrl;
    private final String propFileName = "config.properties";

    public PropertiesClient() throws Exception {
        fileUrl = "./" + propFileName;
    }

    public Map<String, String> getPropertiesOrDefaults() {
        try {
            Map<String, String> map = toMap(tryGetProperties());
            map.putAll(Maps.difference(map, DEFAULT_PROPERTY_VALUES).entriesOnlyOnRight());
            return map;
        } catch (FileNotFoundException e) {
            tryCreateDefaultFile();
            return new HashMap<>(DEFAULT_PROPERTY_VALUES);
        } catch (IOException e) {
            return new HashMap<>(DEFAULT_PROPERTY_VALUES);
        }
    }

    private Map<String, String> toMap(Properties properties) {
        return properties.entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.toMap(o -> o.getKey().toString(), t -> t.getValue().toString()));
    }

    private Properties toProperties(Map<String, String> propertiesMap) {
        Properties properties = new Properties();
        propertiesMap.entrySet()
                .stream()
                .forEach(entry -> properties.put(entry.getKey(), entry.getValue()));
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
