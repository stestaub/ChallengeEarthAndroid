package com.challengeearth.cedroid.caching;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.CacheRequest;

public class CECacheRequest extends CacheRequest {

	final ByteArrayOutputStream body = new ByteArrayOutputStream();
	
	@Override
	public void abort() {
		
	}

	@Override
	public OutputStream getBody() throws IOException {
		return body;
	}

}
