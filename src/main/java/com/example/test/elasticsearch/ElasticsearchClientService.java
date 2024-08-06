package com.example.test.elasticsearch;

import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.bulk.UpdateOperation;
import co.elastic.clients.elasticsearch.indices.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;

public class ElasticsearchClientService {
	
	private static final Logger log = LogManager.getLogger(ElasticsearchClientService.class);

    private int idIncrementer = 1;
    private int bulkCounter = 1;

    public boolean upsert(final String storageName, final List<Object> objList) {

        var bulkRequestBuilder = new BulkRequest.Builder();

        for (final var docObj : objList) {

            UpdateOperation<?, ?> updateOperation = UpdateOperation.of(a -> a.index(storageName).id(String.valueOf(idIncrementer)).action(b -> b.doc(docObj).upsert(docObj)));
            bulkRequestBuilder.operations(a -> a.update(updateOperation));
            idIncrementer++;
        }

        this.bulk(bulkRequestBuilder.build());

        return true;
    }

    private void bulk(BulkRequest bulkRequest) {
        BulkResponse bulkResponse = null;
        try {
            log.info("Bulk request executing number - {}", bulkCounter++);
            bulkResponse = GlobalES.getClient().bulk(bulkRequest);
        } catch (Exception e) {
        	log.error("async: Bulk had errors: {}. It has been executed {}", e.getMessage(), bulkRequest, e);
        } finally {
        	if (bulkResponse != null && !bulkResponse.errors()) {
                log.trace("BulkResponse: \"errors\":{}, \"took\": {}, \"items size\":{}.", bulkResponse.errors(), bulkResponse.took(),
                		bulkResponse.items().size());
            }
        }

        log.info("Bulk request has ended!");
    }

    /**
     * Function to delete the index in the ES.
     */
    public void deleteStorage(String storageName) {
        if (storageExists(storageName)) {
            DeleteIndexRequest request = new DeleteIndexRequest.Builder().index(storageName).build();
            try {
            	GlobalES.getClient().indices().delete(request);
                log.info("Index Deleted '{}'", storageName);
            } catch (IOException e) {
                log.error("Unexpected error on deletion of index '{}'", storageName, e);
            }
        }
    }

    /**
     * Function to check the existence of index in ES
     * @return true If the index is present in ES
     */
    public boolean storageExists(String storageName) {
        ExistsRequest existsRequest = new ExistsRequest.Builder().index(storageName).build();
        try {
            return GlobalES.getClient().indices().exists(existsRequest).value();
        } catch (IOException e) {
            log.error("Unexpected error when checking if '{}' index exists - ", storageName, e);
        }
        return false;
    }

    /**
     * Function to perform the index creation in ES
     */
    public void createStorage(String storageName) {

        IndexSettings.Builder indexSettings = new IndexSettings.Builder();
        indexSettings.maxResultWindow(1000000);

        CreateIndexRequest request = new CreateIndexRequest.Builder().index(storageName).settings(indexSettings.build()).build();

        try {
            var result = GlobalES.getClient().indices().exists(ExistsRequest.of(e -> e.index(storageName)));
            if (!result.value()) {
            	GlobalES.getClient().indices().create(request);
            }
        } catch (IOException e) {
            log.error("Unexpected error when creating '{}' index - ", storageName, e);
        }
    }
    
    public void resetIncrements() {
    	this.bulkCounter = 1;
    	this.idIncrementer = 1;
    }
}

