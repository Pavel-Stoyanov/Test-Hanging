package com.example.test;

import com.example.test.config.ConfigProperties;
import com.example.test.elasticsearch.ElasticsearchClientService;
import com.example.test.elasticsearch.GlobalES;
import com.example.test.monitor.MonitorES;
import com.example.test.utils.StreamUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.json.JsonObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsfr.json.Collector;
import org.jsfr.json.JsonSurfer;
import org.jsfr.json.JsonSurferJackson;
import org.jsfr.json.ValueBox;
import org.jsfr.json.compiler.JsonPathCompiler;
import org.jsfr.json.path.JsonPath;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class TestEsApplication {
    
    private static final Logger log = LogManager.getLogger(TestEsApplication.class);

    public static void main(String[] args) {
    	log.info("STARTING APP");
    	StreamUtils streamUtils = null;
    	ElasticsearchClientService esService = null;
    	MonitorES monitor = new MonitorES();
    	try {
            Thread thread = new Thread(monitor);
            thread.start();
    		
			esService = new ElasticsearchClientService();
			streamUtils = new StreamUtils();
			// recreate clean indices
			esService.deleteStorage(ConfigProperties.getEsProdapiCatalogIndex());
		    esService.deleteStorage(ConfigProperties.getEsProdapiArticleIndex());
		
		    esService.createStorage(ConfigProperties.getEsProdapiCatalogIndex());
		    esService.createStorage(ConfigProperties.getEsProdapiArticleIndex());
			
		    File inputFolder = new File(ConfigProperties.getInputFolder());
		    if (inputFolder.exists() && inputFolder.isDirectory()) {
		        if (inputFolder.list().length > 0) {
		            List<File> allFiles = getAllFiles(inputFolder);
		            log.info("The list of the files that will be processed: {}", allFiles);
		
		            for (File file : allFiles) {
		                log.info("Started processing file with name - {}", file.getName());
		                testProcessingOfEntities(file, esService, streamUtils);
		            }
		        }
		    }
    	} catch(Exception e) {
    		log.error("Something wrong happened while processing entities - ", e);
    	} finally {
    		if(streamUtils != null) {
    			streamUtils.close();
    		}
    		
    		if(esService != null) {
    			GlobalES.shutdown();
    		}
    		monitor.stopThread();
    	}
    	log.info("STOPPING APP");
    }

    private static List<File> getAllFiles(File inputFolder) {
        return Arrays.asList(inputFolder.listFiles());
    }

    public static JsonSurfer getJsonSurferJackson() {
        JsonSurfer surfer = JsonSurferJackson.INSTANCE;
        surfer.setParserCharset(StandardCharsets.UTF_8);
        return surfer;
    }
    
    public static ObjectMapper getObjectMapper() {
    	ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper;
    }

    private static void testProcessingOfEntities(File file, ElasticsearchClientService esService, StreamUtils streamUtils) throws IOException {
        JsonSurfer surfer = getJsonSurferJackson();
        ObjectMapper mapper = getObjectMapper();
        Collector collector = surfer.collector(streamUtils.get(file));

        JsonPath jsonPathCatalogs = JsonPathCompiler.compile(ConfigProperties.getCatalogJsonPath());
        ValueBox<Object> catalogsJsonResults = collector.collectOne(jsonPathCatalogs, Object.class);

        JsonPath jsonPathArticles = JsonPathCompiler.compile(ConfigProperties.getArticleJsonPath());
        ValueBox<Object> articlesResults = collector.collectOne(jsonPathArticles, Object.class);

        collector.exec();

        log.info("START PROCESSING OF CATALOGS");
        List<Object> listOfRawCatalogs = new ArrayList<>();
        Object catalogs = catalogsJsonResults.get();
        
        if(catalogs instanceof Map<?,?> map) {
        	// if we have only one catalog in the file
            listOfRawCatalogs.add(mapper.convertValue(map, ObjectNode.class));
            esService.upsert(ConfigProperties.getEsProdapiCatalogIndex(), listOfRawCatalogs);
        } else if(catalogs instanceof List<?> list) {
        	// if we have multiple catalogs in the file
            for(Object rawCatalog : list) {
            	ObjectNode obj = mapper.convertValue(rawCatalog, ObjectNode.class);
                listOfRawCatalogs.add(obj);
                if(listOfRawCatalogs.size() == ConfigProperties.getBulkSize()) {
                    esService.upsert(ConfigProperties.getEsProdapiCatalogIndex(), listOfRawCatalogs);
                    listOfRawCatalogs = new ArrayList<>();
                }
            }
        }
        listOfRawCatalogs.clear();

        log.info("START PROCESSING OF ARTICLES");
        esService.resetIncrements();
        List<Object> listOfRawArticles = new ArrayList<>();
        Object articles = articlesResults.get();
        
        if(articles instanceof Map<?,?> map) {
        	// if we have only one articles in the file
            listOfRawArticles.add(mapper.convertValue(map, ObjectNode.class));
            esService.upsert(ConfigProperties.getEsProdapiArticleIndex(), listOfRawArticles);
        } else if(articles instanceof List<?> list) {
        	// if we have multiple articles in the file
            for(Object rawArticle : list) {
            	ObjectNode obj = mapper.convertValue(rawArticle, ObjectNode.class);
            	listOfRawArticles.add(obj);
                if(listOfRawArticles.size() == ConfigProperties.getBulkSize()) {
                    esService.upsert(ConfigProperties.getEsProdapiArticleIndex(), listOfRawArticles);
                    listOfRawArticles = new ArrayList<>();
                }
            }
        }
        
        log.info("Procesing has ended successfully !");
    }
}
