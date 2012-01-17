package com.challengeearth.cedroid.caching;

import java.io.IOException;
import java.net.CacheRequest;
import java.net.CacheResponse;
import java.net.ResponseCache;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CEResponseCache extends ResponseCache {

	Map<URI, CacheResponse> mCache = new HashMap<URI, CacheResponse>();
	
	@Override
	public CacheResponse get(URI uri, String requestMethod,
			Map<String, List<String>> requestHeaders) throws IOException {
		CacheResponse resp = mCache.get(uri);
		return resp;
	}

	@Override
	public CacheRequest put(URI uri, URLConnection connection)
			throws IOException {
		CacheRequest req =  (CacheRequest)new CECacheRequest();
		 
		Map<String, List<String>> headers = connection.getHeaderFields();
		CacheResponse resp = (CacheResponse) new CECacheResponse(headers, req.getBody());
 
		// For some reason the path of the URI being passed is an empty string.
		// Get a good URI from the connection object.
		try {
			uri = connection.getURL().toURI();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		mCache.put(uri, resp);
 
		return req;
	}

}
