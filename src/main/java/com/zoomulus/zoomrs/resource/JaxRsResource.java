package com.zoomulus.zoomrs.resource;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import lombok.Builder;
import lombok.Value;

import com.google.common.collect.Sets;
import com.zoomulus.zoomrs.content.ContentType;

@Value
@Builder
public class JaxRsResource
{
    Class<?> resourceClass;
    Method resourceMethod;
    JaxRsResourceIdentifier resourceIdentifier;
    List<ContentType> consumesContentTypes;
    List<ContentType> producesContentTypes;
    
    public boolean canSupportRequestContentType(final String path, final HttpMethod httpMethod, final HttpHeaders headers)
    {
        return false;
    }
    
    public boolean canProvideAcceptedContentType(final String path, final HttpMethod httpMethod, final HttpHeaders headers)
    {
        return false;
    }
    
    public Set<String> getAcceptedContentTypes()
    {
        return Sets.newHashSet();
    }
    
    public Optional<Object> invoke(final String path, final HttpMethod httpMethod, final HttpHeaders headers)
    {
        return Optional.ofNullable(null);
    }
}
