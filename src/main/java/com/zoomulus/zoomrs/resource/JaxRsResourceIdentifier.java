package com.zoomulus.zoomrs.resource;

import io.netty.handler.codec.http.HttpMethod;
import lombok.Value;

@Value
public class JaxRsResourceIdentifier
{
    String path;
    HttpMethod method;
}
