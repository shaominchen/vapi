package com.vmware.cis.impl;

import java.util.Calendar;

import com.vmware.cis.SessionProvider;
import com.vmware.vapi.bindings.server.AsyncContext;

public class SessionImpl implements SessionProvider {

	@Override
	public void create(AsyncContext<char[]> asyncContext) {
		// TODO Auto-generated method stub
		asyncContext.setResult("temp-session-id".toCharArray());
	}

	@Override
	public void delete(AsyncContext<Void> asyncContext) {
		// TODO Auto-generated method stub
		asyncContext.setResult(null);
	}

	@Override
	public void get(AsyncContext<Info> asyncContext) {
		// TODO Auto-generated method stub
		Info info = new Info.Builder("test", Calendar.getInstance(), Calendar.getInstance()).build();
		asyncContext.setResult(info);
	}

}
