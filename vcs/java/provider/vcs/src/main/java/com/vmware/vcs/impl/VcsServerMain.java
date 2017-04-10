/*
 * **********************************************************************
 * Copyright 2017 VMware, Inc. All rights reserved. VMware Confidential
 * **********************************************************************
 */
package com.vmware.vcs.impl;


import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.vmware.vapi.protocol.server.rpc.http.impl.TcServer;

/**
 * The simple code that just brings up the spring context and then waits the server to finish its work.
 */
public class VcsServerMain {

    @SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext applicationContext =
                new ClassPathXmlApplicationContext(new String[]{"classpath:vcs-spring.xml", "classpath:rest-modules.xml"});
        TcServer tcServer = (TcServer) applicationContext.getBean("httpOnlyServer");
        tcServer.join();
    }
}
