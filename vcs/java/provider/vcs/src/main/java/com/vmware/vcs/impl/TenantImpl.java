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
import com.vmware.vcs.TenantProvider;
import com.vmware.vcs.TenantTypes;
import com.vmware.vcs.util.MessageFactory;


/**
 * Implementation of the Tenant service.
 */
public class TenantImpl implements TenantProvider {
    private final Logger logger = LoggerFactory.getLogger(VmGroupImpl.class);
    private static LocalizableMessageFactory msgFactory = MessageFactory.getInstance();
    private Map<String, TenantTypes.Info> tenants = new ConcurrentHashMap<String, TenantTypes.Info>();
    private int idCounter = 0;

    @Override
    public void create(TenantTypes.CreateSpec spec,
                       AsyncContext<String> asyncContext)
    {
        String tenantId = generateId();
        TenantTypes.Info info = new TenantTypes.Info();

        try {
            info.setName(spec.getName());

            info.setDatacenter(spec.getDatacenter());

            info.setCluster(spec.getCluster());
            
            if (spec.getDescription() != null) {
            	info.setDescription(spec.getDescription());
            }

            if (spec.getStorage() != null) {
            	info.setStorage(spec.getStorage());
            }
            
            if (spec.getCpu() != null) {
            	info.setCpu(spec.getCpu());
            }

            if (spec.getMemory() != null) {
            	info.setMemory(spec.getMemory());
            }
        }
        catch (Error err) {
            logger.error(err.toString());
            asyncContext.setError(err);
            return;
        }

        tenants.put(tenantId, info);
        asyncContext.setResult(tenantId);
    }

    @Override
    public void get(String tenantId, AsyncContext<TenantTypes.Info> asyncContext) {
        TenantTypes.Info info = tenants.get(tenantId);
        if (info == null) {
            NotFound err = getNotFoundError(tenantId);
            asyncContext.setError(err);
            logger.error(err.toString());
        } else {
            asyncContext.setResult(info);
        }
    }

    @Override
    public void update(String tenantId, TenantTypes.UpdateSpec spec,
                       AsyncContext<Void> asyncContext) {
        TenantTypes.Info info = tenants.get(tenantId);
        if (info == null) {
            NotFound err = getNotFoundError(tenantId);
            logger.error(err.toString());
            asyncContext.setError(err);
            return;
        }

        try {
        	if (spec.getName() != null) {
        		info.setName(spec.getName());
        	}

        	if (spec.getDatacenter() != null ) {
        		info.setDatacenter(spec.getDatacenter());
        	}

        	if (spec.getCluster() != null) {
        		info.setCluster(spec.getCluster());
        	}
            
            if (spec.getDescription() != null) {
            	info.setDescription(spec.getDescription());
            }

            if (spec.getStorage() != null) {
            	info.setStorage(spec.getStorage());
            }
            
            if (spec.getCpu() != null) {
            	info.setCpu(spec.getCpu());
            }

            if (spec.getMemory() != null) {
            	info.setMemory(spec.getMemory());
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
    public void delete(String tenantId, AsyncContext<Void> asyncContext) {
        TenantTypes.Info info = tenants.remove(tenantId);
        if (info == null) {
            NotFound err = getNotFoundError(tenantId);
            logger.error(err.toString());
            asyncContext.setError(err);
        } else {
            asyncContext.setResult(null);
        }
    }

    @Override
    public void list(AsyncContext<java.util.Set<String>> asyncContext) {
        asyncContext.setResult(tenants.keySet());
    }

    private synchronized String generateId() {
        return String.format("tenant-%d", ++idCounter);
    }

    private static NotFound getNotFoundError(String tenantId) {
        LocalizableMessage msg = msgFactory.getLocalizableMessage(
                "com.vmware.vcs.tenant.not_found", tenantId);
        return new NotFound(Arrays.asList(msg), null);
    }
}
