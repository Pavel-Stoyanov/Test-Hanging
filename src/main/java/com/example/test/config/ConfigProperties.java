package com.example.test.config;

public class ConfigProperties {
	
	private ConfigProperties() {
	    throw new IllegalStateException("Config class");
    }

	private static String inputFolder = "";

    // Elasticsearch settings
    private static String esHost = "";
    private static int esPort = 0;
    private static int esConnectionTimeout = 60000;
    private static int esSocketTimeout = 60000;
    private static String esProtocol = "";
    private static String esUser = "";
    private static String esPassword = "";

    // Proxy settings
    private static boolean enableProxy = true;
    private static String proxyHost = "localhost";
    private static int proxyPort = 3128;
    private static String proxyProtocol = "http";

    private static String esProdapiCatalogIndex = "";
    private static String esProdapiArticleIndex = "";
    
    private static int bulkSize = 50;
    
    private static String catalogJsonPath = "$.STEP-ProductInformation.Classifications.Classification";
    private static String articleJsonPath = "$.STEP-ProductInformation.Products.Product";
    
	public static String getInputFolder() {
		return inputFolder;
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