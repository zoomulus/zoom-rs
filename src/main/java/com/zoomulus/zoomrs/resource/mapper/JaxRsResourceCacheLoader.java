package com.zoomulus.zoomrs.resource.mapper;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.PathSegment;

import org.jboss.resteasy.specimpl.PathSegmentImpl;

import com.google.common.cache.CacheLoader;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.zoomulus.zoomrs.resource.JaxRsResource;
import com.zoomulus.zoomrs.resource.JaxRsResourceIdentifier;

public class JaxRsResourceCacheLoader extends
        CacheLoader<HttpRequest, JaxRsResource>
{
    private final Map<JaxRsResourceIdentifier, JaxRsResource> resourceMap = Maps.newConcurrentMap();
    private final Set<String> resourcePaths = Sets.newConcurrentHashSet();
    private final Map<String, List<String>> resourcePathParts = Maps.newConcurrentMap();

    public void remember(final JaxRsResourceIdentifier resourceIdentifier, final JaxRsResource resource)
    {
        resourceMap.put(resourceIdentifier, resource); 
        resourcePaths.add(resourceIdentifier.getPath());
    }

    @Override
    public JaxRsResource load(HttpRequest request) throws Exception
    {
        final String path = request.getUri();
        final HttpMethod method = request.getMethod();
        if (resourcePaths.contains(path))
        {
            final JaxRsResourceIdentifier resourceIdentifier = new JaxRsResourceIdentifier(path, method);
            if (resourceMap.containsKey(resourceIdentifier))
            {
                return resourceMap.get(resourceIdentifier);
            }
        }

        // we will have to go through all the resource paths
        // and compare them against the path provided
        for (final JaxRsResourceIdentifier resourceIdentifier : resourceMap.keySet())
        {
            if (resourceIdentifier.getMethod() == method)
            {
                if (isMatchingPath(resourceIdentifier.getPath(), path))
                {
                    return resourceMap.get(resourceIdentifier);
                }
            }
        }

        // No match
        return null;
    }
    
    private boolean isMatchingPath(final String resourcePath, final String path)
    {
        List<String> patternParts;
        if (! resourcePathParts.containsKey(resourcePath))
        {
            patternParts = Lists.newArrayList();
            for (final String patternPart : resourcePath.split("/"))
            {
                if (0 != patternPart.length()) patternParts.add(patternPart);
            }
            resourcePathParts.put(resourcePath, patternParts);
        }
        else
        {
            patternParts = resourcePathParts.get(resourcePath);
        }

        final Map<String, String> matrixParams = Maps.newHashMap();
        final List<String> pathParts = Lists.newArrayList();
        final List<String> pathSegmentParts = Lists.newArrayList();
        for (final String pathPart : path.split("/"))
        {
            if (0 != pathPart.length())
            {
                pathSegmentParts.add(pathPart);
                String[] ppParts = pathPart.split(";");
                pathParts.add(ppParts[0]);
                if (ppParts.length > 1)
                {
                    String[] matrixParamParts = ppParts[1].split("=", 2);
                    if (matrixParamParts.length >= 2)
                    {
                        matrixParams.put(matrixParamParts[0], matrixParamParts[1]);
                    }
                }
            }
        }

        int len = patternParts.size();
        if (len != pathParts.size())
        {
            return false;
        }

        final Map<String, String> values = Maps.newHashMap();
        final Map<String, PathSegment> pathSegments = Maps.newHashMap();
        for (int i=0; i<len; i++)
        {
            final String patternPart = patternParts.get(i);
            if (patternPart.startsWith("{") && patternPart.endsWith("}"))
            {
                final String[] wcParts = patternPart.split(":");
                if (wcParts.length > 1)
                {
                    // contains a regex
                    final String key = wcParts[0].substring(1, wcParts[0].length()).trim();
                    final String regex = wcParts[1].substring(0, wcParts[1].length()-1).trim();
                    if (pathParts.get(i).matches(regex))
                    {
                        pathSegments.put(key, new PathSegmentImpl(pathSegmentParts.get(i), false));
                        values.put(key, pathParts.get(i));
                    }
                    else
                    {
                        return false;
                    }
                }
                else
                {
                    final String key = patternPart.substring(1, patternPart.length()-1);
                    pathSegments.put(key, new PathSegmentImpl(pathSegmentParts.get(i), false));
                    values.put(key, pathParts.get(i));
                }
            }
            else if (! patternPart.equals(pathParts.get(i)))
            {
                return false;
            }
        }

        return true;
    }
}
