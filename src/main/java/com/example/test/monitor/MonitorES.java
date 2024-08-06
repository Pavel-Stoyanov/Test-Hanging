package com.example.test.monitor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.example.test.elasticsearch.GlobalES;

import co.elastic.clients.elasticsearch.cluster.HealthResponse;
import co.elastic.clients.elasticsearch.indices.IndicesStatsResponse;
import co.elastic.clients.elasticsearch.nodes.NodesStatsResponse;
import co.elastic.clients.transport.endpoints.BooleanResponse;

public class MonitorES implements Runnable {
	
	private static final Logger log = LogManager.getLogger(MonitorES.class);
	
	private boolean appIsRunning = true;
	
    @Override
    public void run() {
        while(appIsRunning) {
        	
        	try {
                // Ping the cluster
                BooleanResponse pingResponse = GlobalES.getClient().ping();
                log.info("Elasticsearch cluster is available: {}", pingResponse.value());

                // Get cluster health
                HealthResponse healthResponse = GlobalES.getClient().cluster().health();
                log.info("Cluster health status: {}", healthResponse.status());

                // Get nodes stats
                NodesStatsResponse nodesStatsResponse = GlobalES.getClient().nodes().stats();
                log.info("Nodes stats: {}", nodesStatsResponse);

                // Get indices stats
                IndicesStatsResponse indicesStatsResponse = GlobalES.getClient().indices().stats();
                log.info("Indices stats: {}", indicesStatsResponse);
                
                log.info(GlobalES.getClient().tasks().list());
            } catch (Exception e) {
                log.error("Error monitoring Elasticsearch client", e);
            }
        	
        	try {
				Thread.sleep(30000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
        
        log.info("MONITORING IS STOPPING");
    }
    
    public void stopThread() {
    	this.appIsRunning=false;
    }
}
