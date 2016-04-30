package com.zoomulus.zoomrs.resource.mapper;

import io.netty.handler.codec.http.HttpRequest;

import java.util.Optional;

import com.zoomulus.zoomrs.resource.JaxRsResource;
import com.zoomulus.zoomrs.scanner.JaxRsResourceScanner;

public interface JaxRsResourceMapper
{
    void scan(final JaxRsResourceScanner scanner);
    Optional<JaxRsResource> getResource(final HttpRequest request);
}
