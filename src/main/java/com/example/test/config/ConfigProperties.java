package com.example.test.config;

public class ConfigProperties {
	
	private ConfigProperties() {
	    throw new IllegalStateException("Config class");
   }

    private static String inputFolder = "";
    private static String processedFolder = "";
    private static String errorFolder = "";

    // Elasticsearch settings
    private static String esHost = "";
    private static int esPort = 0;
    private static int esConnectionTimeout = 0;
    private static int esSocketTimeout = 0;
    private static String esProtocol = "";
    private static String esUser = "";
    private static String esPassword = "";

    // Proxy settings
    private static boolean enableProxy = false;
    private static String proxyHost = "";
    private static int proxyPort = 0;
    private static String proxyProtocol = "";

    private static String esProdapiCatalogIndex = "";
    private static String esProdapiArticleIndex = "";
    
    private static int bulkSize = 0;
    
    private static String catalogJsonPath = "";
    private static String articleJsonPath = "";
    
	public static String getInputFolder() {
		return inputFolder;
	}
	
	public static String getProcessedFolder() {
		return processedFolder;
	}
	
	public static String getErrorFolder() {
		return errorFolder;
	}
	
	public static String getEsHost() {
		return esHost;
	}
	
	public static int getEsPort() {
		return esPort;
	}
	
	public static int getEsConnectionTimeout() {
		return esConnectionTimeout;
	}
	
	public static int getEsSocketTimeout() {
		return esSocketTimeout;
	}
	
	public static String getEsProtocol() {
		return esProtocol;
	}
	
	public static String getEsUser() {
		return esUser;
	}
	
	public static String getEsPassword() {
		return esPassword;
	}
	
	public static boolean isEnableProxy() {
		return enableProxy;
	}
	
	public static String getProxyHost() {
		return proxyHost;
	}
	
	public static int getProxyPort() {
		return proxyPort;
	}
	
	public static String getProxyProtocol() {
		return proxyProtocol;
	}
	
	public static String getEsProdapiCatalogIndex() {
		return esProdapiCatalogIndex;
	}
	
	public static String getEsProdapiArticleIndex() {
		return esProdapiArticleIndex;
	}
	
	public static int getBulkSize() {
		return bulkSize;
	}

	public static String getCatalogJsonPath() {
		return catalogJsonPath;
	}

	public static String getArticleJsonPath() {
		return articleJsonPath;
	}
}