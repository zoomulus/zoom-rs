package com.zoomulus.zoomrs.resource.mapper;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import lombok.NonNull;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import com.zoomulus.zoomrs.content.ConsumesAnnotationContentTypeExtractor;
import com.zoomulus.zoomrs.content.ContentType;
import com.zoomulus.zoomrs.content.ProducesAnnotationContentTypeExtractor;
import com.zoomulus.zoomrs.resource.JaxRsResource;
import com.zoomulus.zoomrs.resource.JaxRsResourceIdentifier;
import com.zoomulus.zoomrs.scanner.JaxRsResourceScanner;
import com.zoomulus.zoomrs.util.PathJoiner;

public class EagerCachingJaxRsResourceMapper implements JaxRsResourceMapper
{
    //private final Map<JaxRsResourceIdentifier, JaxRsResource> resourceMap = Maps.newConcurrentMap();
    //private final Set<String> resourcePaths = Sets.newConcurrentHashSet();
    //private final Map<String, List<String>> resourcePathParts = Maps.newConcurrentMap();
    
    private final ConsumesAnnotationContentTypeExtractor consumesExtractor = new ConsumesAnnotationContentTypeExtractor();
    private final ProducesAnnotationContentTypeExtractor producesExtractor = new ProducesAnnotationContentTypeExtractor();
    
    private static final ImmutableSet<HttpMethod> consumingMethods =
            ImmutableSet.of(HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE);

    private final JaxRsResourceCacheLoader cacheLoader = new JaxRsResourceCacheLoader();
    private final LoadingCache<HttpRequest, JaxRsResource> resourceCache; // =
//  CacheBuilder.newBuilder()
//  .expireAfterAccess(30, TimeUnit.MINUTES)
//  .maximumSize(1000)
//  .build(new CacheLoader<HttpRequest, JaxRsResource>(){
//                @Override
//                public JaxRsResource load(final HttpRequest request) throws Exception
//                {
//                    final String path = request.getUri();
//                    final HttpMethod method = request.getMethod();
//                    if (resourcePaths.contains(path))
//                    {
//                        final JaxRsResourceIdentifier resourceIdentifier = new JaxRsResourceIdentifier(path, method);
//                        if (resourceMap.containsKey(resourceIdentifier))
//                        {
//                            return resourceMap.get(resourceIdentifier);
//                        }
//                    }
//                    
//                    // we will have to go through all the resource paths
//                    // and compare them against the path provided
//                    for (final JaxRsResourceIdentifier resourceIdentifier : resourceMap.keySet())
//                    {
//                        if (resourceIdentifier.getMethod() == method)
//                        {
//                            if (isMatchingPath(resourceIdentifier.getPath(), path))
//                            {
//                                return resourceMap.get(resourceIdentifier);
//                            }
//                        }
//                    }
//                    
//                    // No match
//                    return null;
//                }
//                
//                private boolean isMatchingPath(final String resourcePath, final String path)
//                {
//                    List<String> patternParts;
//                    if (! resourcePathParts.containsKey(resourcePath))
//                    {
//                        patternParts = Lists.newArrayList();
//                        for (final String patternPart : resourcePath.split("/"))
//                        {
//                            if (0 != patternPart.length()) patternParts.add(patternPart);
//                        }
//                        resourcePathParts.put(resourcePath, patternParts);
//                    }
//                    else
//                    {
//                        patternParts = resourcePathParts.get(resourcePath);
//                    }
//                    
//                    final Map<String, String> matrixParams = Maps.newHashMap();
//                    final List<String> pathParts = Lists.newArrayList();
//                    final List<String> pathSegmentParts = Lists.newArrayList();
//                    for (final String pathPart : path.split("/"))
//                    {
//                        if (0 != pathPart.length())
//                        {
//                            pathSegmentParts.add(pathPart);
//                            String[] ppParts = pathPart.split(";");
//                            pathParts.add(ppParts[0]);
//                            if (ppParts.length > 1)
//                            {
//                                String[] matrixParamParts = ppParts[1].split("=", 2);
//                                if (matrixParamParts.length >= 2)
//                                {
//                                    matrixParams.put(matrixParamParts[0], matrixParamParts[1]);
//                                }
//                            }
//                        }
//                    }
//                    
//                    int len = patternParts.size();
//                    if (len != pathParts.size())
//                    {
//                        return false;
//                    }
//
//                    final Map<String, String> values = Maps.newHashMap();
//                    final Map<String, PathSegment> pathSegments = Maps.newHashMap();
//                    for (int i=0; i<len; i++)
//                    {
//                        final String patternPart = patternParts.get(i);
//                        if (patternPart.startsWith("{") && patternPart.endsWith("}"))
//                        {
//                            final String[] wcParts = patternPart.split(":");
//                            if (wcParts.length > 1)
//                            {
//                                // contains a regex
//                                final String key = wcParts[0].substring(1, wcParts[0].length()).trim();
//                                final String regex = wcParts[1].substring(0, wcParts[1].length()-1).trim();
//                                if (pathParts.get(i).matches(regex))
//                                {
//                                    pathSegments.put(key, new PathSegmentImpl(pathSegmentParts.get(i), false));
//                                    values.put(key, pathParts.get(i));
//                                }
//                                else
//                                {
//                                    return false;
//                                }
//                            }
//                            else
//                            {
//                                final String key = patternPart.substring(1, patternPart.length()-1);
//                                pathSegments.put(key, new PathSegmentImpl(pathSegmentParts.get(i), false));
//                                values.put(key, pathParts.get(i));
//                            }
//                        }
//                        else if (! patternPart.equals(pathParts.get(i)))
//                        {
//                            return false;
//                        }
//                    }
//
//                    return true;
//                }
//            });
    
    @Inject
    public EagerCachingJaxRsResourceMapper(final JaxRsResourceScanner scanner)
    {
        resourceCache = CacheBuilder.newBuilder()
              .expireAfterAccess(30, TimeUnit.MINUTES)
              .maximumSize(1000)
              .build(cacheLoader);
        
        scan(scanner);
    }
    
    @Override
    public void scan(final JaxRsResourceScanner scanner)
    {
        for (final Class<?> resourceClass : scanner.scan())
        {
            final Path resourceClassPath = resourceClass.getAnnotation(Path.class);
            if (null == resourceClassPath) continue;
            
            for (final Method method : resourceClass.getMethods())
            {
                final Optional<HttpMethod> httpMethod = getHttpMethodForMethod(method);
                
                if (httpMethod.isPresent())
                {
                    final String fullResourcePath = getFullResourcePath(resourceClassPath, method.getAnnotation(Path.class));
                    final JaxRsResourceIdentifier resourceIdentifier =
                            new JaxRsResourceIdentifier(fullResourcePath, httpMethod.get());
                    
                    JaxRsResource.JaxRsResourceBuilder resourceBuilder = JaxRsResource.builder()
                            .resourceClass(resourceClass)
                            .resourceMethod(method)
                            .resourceIdentifier(resourceIdentifier)
                            .producesContentTypes(producesExtractor.extract(resourceClass, method));
                    if (consumingMethods.contains(httpMethod.get()))
                    {
                        resourceBuilder = resourceBuilder
                                .consumesContentTypes(consumesExtractor.extract(
                                        resourceClass,
                                        method,
                                        ContentType.TEXT_PLAIN_TYPE
                                ));
                    }
                    
                    cacheLoader.remember(resourceIdentifier, resourceBuilder.build());
//                    final JaxRsResource resource = resourceBuilder.build();
//                  resourceMap.put(resourceIdentifier, resource); 
//                  resourcePaths.add(absolutePath);
                }
            }
        }
    }
    
    private Optional<HttpMethod> getHttpMethodForMethod(final Method method)
    {
        return Optional.ofNullable(
                null != method.getAnnotation(GET.class) ? HttpMethod.GET :
                    (null != method.getAnnotation(POST.class) ? HttpMethod.POST :
                        (null != method.getAnnotation(PUT.class) ? HttpMethod.PUT :
                            (null != method.getAnnotation(DELETE.class) ? HttpMethod.DELETE :
                                (null != method.getAnnotation(HEAD.class) ? HttpMethod.HEAD :
                                    (null != method.getAnnotation(OPTIONS.class) ? HttpMethod.OPTIONS :
                                        null))))));
    }
    
    private String getFullResourcePath(@NonNull final Path resourceClassPath, final Path resourceMethodPath)
    {
        return null == resourceMethodPath ?
                new PathJoiner().with(resourceClassPath.value()).join() :
                new PathJoiner().with(resourceClassPath.value()).with(resourceMethodPath.value()).join();
    }
    
    @Override
    public Optional<JaxRsResource> getResource(final HttpRequest request)
    {
        return Optional.ofNullable(resourceCache.getUnchecked(request));
    }

}
