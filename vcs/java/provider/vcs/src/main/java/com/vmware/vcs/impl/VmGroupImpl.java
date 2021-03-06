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
import com.vmware.vapi.std.errors.InvalidArgument;
import com.vmware.vapi.std.errors.NotFound;

import com.vmware.vcs.VmGroupProvider;
import com.vmware.vcs.VmGroupTypes;
import com.vmware.vcs.util.MessageFactory;

/**
 * Implementation of the VmGroup service.
 */
public class VmGroupImpl implements VmGroupProvider {
    private final Logger logger = LoggerFactory.getLogger(VmGroupImpl.class);
    private static LocalizableMessageFactory msgFactory = MessageFactory.getInstance();
    private Map<String, VmGroupTypes.Info> vmGroups = new ConcurrentHashMap<String, VmGroupTypes.Info>();
    private int idCounter = 0;
    private static final int NAME_MAX_LEN = 64;
    private static final int DESC_MAX_LEN = 256;

    @Override
    public void create(VmGroupTypes.CreateSpec spec,
                       AsyncContext<String> asyncContext)
    {
        String vgId = generateId();
        VmGroupTypes.Info info = new VmGroupTypes.Info();

        try {
            updateName(info, spec.getName());

            if (spec.getDescription() != null) {
                updateDescription(info, spec.getDescription());
            }

            if (spec.getDefaultDatastore() != null) {
                updateDefaultDatastore(info, spec.getDefaultDatastore());
            }
        }
        catch (Error err) {
            logger.error(err.toString());
            asyncContext.setError(err);
            return;
        }

        vmGroups.put(vgId, info);
        asyncContext.setResult(vgId);
    }

    @Override
    public void get(String vgId, AsyncContext<VmGroupTypes.Info> asyncContext) {
        VmGroupTypes.Info info = vmGroups.get(vgId);
        if (info == null) {
            NotFound err = getNotFoundError(vgId);
            asyncContext.setError(err);
            logger.error(err.toString());
        } else {
            asyncContext.setResult(info);
        }
    }

    @Override
    public void update(String vgId, VmGroupTypes.UpdateSpec spec,
                       AsyncContext<Void> asyncContext) {
        VmGroupTypes.Info info = vmGroups.get(vgId);
        if (info == null) {
            NotFound err = getNotFoundError(vgId);
            logger.error(err.toString());
            asyncContext.setError(err);
            return;
        }

        try {
            if (spec.getName() != null) {
                updateName(info, spec.getName());
            }

            if (spec.getDescription() != null) {
                updateDescription(info, spec.getDescription());
            }

            if (spec.getDefaultDatastore() != null) {
                updateDefaultDatastore(info, spec.getDefaultDatastore());
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
    public void delete(String vgId, AsyncContext<Void> asyncContext) {
        VmGroupTypes.Info info = vmGroups.remove(vgId);
        if (info == null) {
            NotFound err = getNotFoundError(vgId);
            logger.error(err.toString());
            asyncContext.setError(err);
        } else {
            asyncContext.setResult(null);
        }
    }

    @Override
    public void list(AsyncContext<java.util.Set<String>> asyncContext) {
        asyncContext.setResult(vmGroups.keySet());
    }

    private synchronized String generateId() {
        return String.format("vmgroup-%d", ++idCounter);
    }

    private void updateName(VmGroupTypes.Info info, String name) {
        if (name.length() > NAME_MAX_LEN) {
            LocalizableMessage msg = msgFactory.getLocalizableMessage(
                "com.vmware.vcs.vmgroup.invalid_name", NAME_MAX_LEN, name);
            throw new InvalidArgument(Arrays.asList(msg), null);
        } else {
        	info.setName(name);
        }
    }

    private void updateDescription(VmGroupTypes.Info info, String description) {
        if (description.length() > DESC_MAX_LEN) {
            LocalizableMessage msg = msgFactory.getLocalizableMessage(
                "com.vmware.vcs.vmgroup.invalid_description", DESC_MAX_LEN, description);
            throw new InvalidArgument(Arrays.asList(msg), null);
        } else {
            info.setDescription(description);
        }
    }

    private void updateDefaultDatastore(VmGroupTypes.Info info, String defaultDatastore) {
        info.setDefaultDatastore(defaultDatastore);
    }

    private static NotFound getNotFoundError(String vgId) {
        LocalizableMessage msg = msgFactory.getLocalizableMessage(
                "com.vmware.vcs.vmgroup.not_found", vgId);
        return new NotFound(Arrays.asList(msg), null);
    }
}
