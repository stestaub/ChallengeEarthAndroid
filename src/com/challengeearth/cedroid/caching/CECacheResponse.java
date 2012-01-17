package com.challengeearth.cedroid.caching;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.CacheResponse;
import java.util.List;
import java.util.Map;

public class CECacheResponse extends CacheResponse {

	Map<String, List<String>> headers;
	ByteArrayOutputStream body;
	
	public CECacheResponse(Map<String, List<String>> headers, OutputStream body) {
		super();
		this.headers = headers;
		// should be the output stream defined in a companion CacheRequest.
		this.body = (ByteArrayOutputStream)body; 
	}
	
	@Override
	public InputStream getBody() throws IOException {
		return new ByteArrayInputStream(body.toByteArray());
	}

	@Override
	public Map<String, List<String>> getHeaders() throws IOException {
		return headers;
	}

}
