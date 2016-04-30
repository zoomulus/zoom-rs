package com.zoomulus.zoomrs.resource.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;

import java.lang.reflect.Method;

import javax.ws.rs.Path;

import org.junit.Before;
import org.junit.Test;

import com.zoomulus.zoomrs.content.ProducesAnnotationContentTypeExtractor;
import com.zoomulus.zoomrs.resource.JaxRsResource;
import com.zoomulus.zoomrs.resource.JaxRsResourceIdentifier;
import com.zoomulus.zoomrs.util.PathJoiner;

public class JaxRsResourceCacheLoaderTest
{
    private final JaxRsResourceCacheLoader sut = new JaxRsResourceCacheLoader();
    private final ProducesAnnotationContentTypeExtractor pExtractor = new ProducesAnnotationContentTypeExtractor();
    private JaxRsResourceIdentifier resourceIdentifier;
    private JaxRsResource resource;
    private final HttpRequest mockRequest = mock(HttpRequest.class);
    
    @Before
    public void setup() throws NoSuchMethodException, SecurityException
    {
        final Class<?> resourceClass = TestResource.class;
        final Method resourceMethod = resourceClass.getMethod("endpoint1");
        final Path resourceClassPath = resourceClass.getAnnotation(Path.class);
        assertNotNull(resourceClassPath);
        final Path resourceMethodPath = resourceMethod.getAnnotation(Path.class);
        final String definedRequestPath = null == resourceMethodPath ?
                new PathJoiner().with(resourceClassPath.value()).join() :
                new PathJoiner().with(resourceClassPath.value()).with(resourceMethodPath.value()).join();
        resourceIdentifier = new JaxRsResourceIdentifier(definedRequestPath, HttpMethod.GET);
        resource = JaxRsResource.builder()
                .resourceClass(resourceClass)
                .resourceMethod(resourceMethod)
                .producesContentTypes(pExtractor.extract(resourceClass, resourceMethod))
                .build();
        sut.remember(resourceIdentifier, resource);
    }
    
    @Test
    public void testFindsExactMatch() throws Exception
    {
        when(mockRequest.getUri()).thenReturn("/resource/path/1/");
        when(mockRequest.getMethod()).thenReturn(HttpMethod.GET);
        
        assertEquals(resource, sut.load(mockRequest));
    }
    
    @Test
    public void testPathWithoutTrailingSlashMatches() throws Exception
    {
        when(mockRequest.getUri()).thenReturn("/resource/path/1");
        when(mockRequest.getMethod()).thenReturn(HttpMethod.GET);
        
        assertEquals(resource, sut.load(mockRequest));
    }
    
    @Test
    public void testSubPathMatchFails() throws Exception
    {
        when(mockRequest.getUri()).thenReturn("/resource/path/");
        when(mockRequest.getMethod()).thenReturn(HttpMethod.GET);
        
        final JaxRsResource matchedResource = sut.load(mockRequest);
        assertNotEquals(resource, matchedResource);
        assertNull(matchedResource);
    }
    
    @Test
    public void testNonMatchingPathFails() throws Exception
    {
        when(mockRequest.getUri()).thenReturn("/some/other/path");
        when(mockRequest.getMethod()).thenReturn(HttpMethod.GET);
        
        final JaxRsResource matchedResource = sut.load(mockRequest);
        assertNotEquals(resource, matchedResource);
        assertNull(matchedResource);
    }
    
    @Test
    public void testNonMatchingMethodFails() throws Exception
    {
        when(mockRequest.getUri()).thenReturn("/resource/path/1/");
        when(mockRequest.getMethod()).thenReturn(HttpMethod.HEAD);
        
        final JaxRsResource matchedResource = sut.load(mockRequest);
        assertNotEquals(resource, matchedResource);
        assertNull(matchedResource);
    }
}
