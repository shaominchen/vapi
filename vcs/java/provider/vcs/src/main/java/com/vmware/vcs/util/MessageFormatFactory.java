/* **********************************************************************
 * Copyright 2017 VMware, Inc.  All rights reserved. VMware Confidential
 * *********************************************************************/

package com.vmware.vcs.util;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;

import com.vmware.vapi.l10n.MessageFormatTemplateFormatter;

public class MessageFormatFactory {

    public MessageFormatTemplateFormatter createMessageFormatTemplateFormatter() {
        MessageFormatTemplateFormatter messageFormatter = new MessageFormatTemplateFormatter() {

            @Override
            public String format(String msgTemplate, List<String> args, Locale locale) {
                return MessageFormat.format(msgTemplate, args.toArray());
            }
        };
        return messageFormatter;
    }
}
