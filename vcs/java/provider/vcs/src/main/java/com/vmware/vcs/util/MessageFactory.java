package com.vmware.vcs.util;

import java.util.Locale;
import java.util.ResourceBundle;

import com.vmware.vapi.bindings.LocalizableMessageFactory;
import com.vmware.vapi.l10n.StringFormatTemplateFormatter;

public class MessageFactory {
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

    static {
        ResourceBundle resourceBundle =
            ResourceBundle.getBundle(MSG_BUNDLE_FILE, Locale.US);
        msgFactory = new LocalizableMessageFactory(
                            resourceBundle, new StringFormatTemplateFormatter());
    }

    public static LocalizableMessageFactory getInstance() {
    	return msgFactory;
    }
}
