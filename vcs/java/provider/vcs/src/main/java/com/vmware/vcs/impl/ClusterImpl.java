/* **********************************************************
 * Copyright 2017 VMware, Inc.  All rights reserved. -- VMware Confidential
 * **********************************************************/

package com.vmware.vcs.impl;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vapi.bindings.LocalizableMessageFactory;
import com.vmware.vapi.bindings.server.AsyncContext;
import com.vmware.vapi.std.LocalizableMessage;
import com.vmware.vapi.std.errors.Error;
import com.vmware.vapi.std.errors.NotFound;
import com.vmware.vcs.ClusterProvider;
import com.vmware.vcs.ClusterTypes;
import com.vmware.vcs.util.MessageFactory;

/**
 * Implementation of the Cluster service.
 */
public class ClusterImpl implements ClusterProvider {
    private final Logger logger = LoggerFactory.getLogger(VmGroupImpl.class);
    private static LocalizableMessageFactory msgFactory = MessageFactory.getInstance();
    private Map<String, ClusterTypes.Info> clusters = new ConcurrentHashMap<String, ClusterTypes.Info>();
    private int idCounter = 0;

    @Override
    public void create(ClusterTypes.CreateSpec spec,
                       AsyncContext<String> asyncContext)
    {
        String clusterId = generateId();
        ClusterTypes.Info info = new ClusterTypes.Info();

        try {
            info.setName(spec.getName());
            info.setType(spec.getType());
            info.setImage(spec.getImage());
            info.setVersion(spec.getVersion());
            info.setTenant(spec.getTenant());
            info.setWorkerCount(spec.getWorkerCount());
            info.setMasterCount(spec.getMasterCount());
        }
        catch (Error err) {
            logger.error(err.toString());
            asyncContext.setError(err);
            return;
        }

        clusters.put(clusterId, info);
        asyncContext.setResult(clusterId);
    }

    @Override
    public void get(String clusterId, AsyncContext<ClusterTypes.Info> asyncContext) {
        ClusterTypes.Info info = clusters.get(clusterId);
        if (info == null) {
            NotFound err = getNotFoundError(clusterId);
            asyncContext.setError(err);
            logger.error(err.toString());
        } else {
            asyncContext.setResult(info);
        }
    }

    @Override
    public void update(String clusterId, ClusterTypes.UpdateSpec spec,
                       AsyncContext<Void> asyncContext) {
        ClusterTypes.Info info = clusters.get(clusterId);
        if (info == null) {
            NotFound err = getNotFoundError(clusterId);
            logger.error(err.toString());
            asyncContext.setError(err);
            return;
        }

        try {
        	if (spec.getWorkerCount() != null) {
        		info.setWorkerCount(spec.getWorkerCount());
        	}

        	if (spec.getMasterCount() != null) {
        		info.setMasterCount(spec.getMasterCount());
        	}

        	if (spec.getVersion() != null) {
        		info.setVersion(spec.getVersion());
        	}
        }
        catch (Error err) {
            logger.error(err.toString());
            asyncContext.setError(err);
            return;
        }
        asyncContext.setResult(null);
    }

    @Override
    public void delete(String clusterId, AsyncContext<Void> asyncContext) {
        ClusterTypes.Info info = clusters.remove(clusterId);
        if (info == null) {
            NotFound err = getNotFoundError(clusterId);
            logger.error(err.toString());
            asyncContext.setError(err);
        } else {
            asyncContext.setResult(null);
        }
    }

    @Override
    public void list(AsyncContext<java.util.Set<String>> asyncContext) {
        asyncContext.setResult(clusters.keySet());
    }

    private synchronized String generateId() {
        return String.format("cluster-%d", ++idCounter);
    }

    private static NotFound getNotFoundError(String clusterId) {
        LocalizableMessage msg = msgFactory.getLocalizableMessage(
                "com.vmware.vcs.cluster.not_found", clusterId);
        return new NotFound(Arrays.asList(msg), null);
    }
}
