/* **********************************************************
 * Copyright 2017 VMware, Inc.  All rights reserved. -- VMware Confidential
 * **********************************************************/

package com.vmware.vcs.impl;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vapi.bindings.LocalizableMessageFactory;
import com.vmware.vapi.bindings.server.AsyncContext;
import com.vmware.vapi.l10n.StringFormatTemplateFormatter;
import com.vmware.vapi.std.LocalizableMessage;
import com.vmware.vapi.std.errors.Error;
import com.vmware.vapi.std.errors.InvalidArgument;
import com.vmware.vapi.std.errors.NotFound;

import com.vmware.vcs.VmGroupProvider;
import com.vmware.vcs.VmGroupTypes;

/*
 * Implementation of the VmGroup service.
 */
public class VmGroupImpl implements VmGroupProvider {
    /**
     * Name of the message bundle. The file corresponding to this name will be
     * resolved based on the locale for which it is requested.
     * For example: The message catalog file for bundle name 'vcs' and locale
     * 'Locale.US' should be in a file named vcs_en_US.properties.
     * Message catalog files for the supported locales should be accessible
     * through the classpath.
     */
    public static final String MSG_BUNDLE_FILE = "vcs";

    private static LocalizableMessageFactory msgFactory;
    private final Logger logger = LoggerFactory.getLogger(VmGroupImpl.class);
    private Map<String, VmGroupTypes.Info> vgMap;
    private int idCounter = 0;
    private static final int NAME_MAX_LEN = 64;
    private static final int DESC_MAX_LEN = 256;

    public VmGroupImpl() {
        vgMap = new ConcurrentHashMap<String, VmGroupTypes.Info>();
    }

    static {
        ResourceBundle resourceBundle =
            ResourceBundle.getBundle(MSG_BUNDLE_FILE, Locale.US);
        msgFactory = new LocalizableMessageFactory(
                            resourceBundle, new StringFormatTemplateFormatter());
    }

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
        catch(Error err) {
            logger.error(err.toString());
            asyncContext.setError(err);
            return;
        }

        vgMap.put(vgId, info);
        asyncContext.setResult(vgId);
    }

    @Override
    public void get(String vgId, AsyncContext<VmGroupTypes.Info> asyncContext) {
        VmGroupTypes.Info info = vgMap.get(vgId);
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
        VmGroupTypes.Info info = vgMap.get(vgId);
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
        catch(Error err) {
            logger.error(err.toString());
            asyncContext.setError(err);
            return;
        }
        asyncContext.setResult(null);
    }

    @Override
    public void delete(String vgId, AsyncContext<Void> asyncContext) {
        VmGroupTypes.Info info = vgMap.remove(vgId);
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
        asyncContext.setResult(vgMap.keySet());
    }

    private synchronized String generateId() {
        return String.format("vg-%d", ++idCounter);
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
