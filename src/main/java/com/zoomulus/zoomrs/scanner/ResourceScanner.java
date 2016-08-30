package com.zoomulus.zoomrs.scanner;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Path;

import lombok.extern.slf4j.Slf4j;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.reflect.ClassPath;

@Slf4j
public class ResourceScanner
{
    public static final String RESOURCES_IDENTIFIER = "resources";
    
    private final List<String> resourceNames;
    
    public ResourceScanner()
    {
        resourceNames = Lists.newArrayList();
    }
    
    @Inject
    public ResourceScanner(@Named(RESOURCES_IDENTIFIER) final String resourceName)
    {
        this(null != resourceName ? Lists.newArrayList(resourceName.split(",")) : Lists.newArrayList());
    }
    
    public ResourceScanner(final List<String> resourceNames)
    {
        this.resourceNames = null != resourceNames ? resourceNames : Lists.newArrayList();
    }
    
    public ResourceScanner add(final String resourceName)
    {
        resourceNames.addAll(Lists.newArrayList(resourceName.split(",")));
        return this;
    }
    
    public Set<Class<?>> scan()
    {
        log.debug("Begin resource scan...");
        
        final List<Class<?>> possibleResources = Lists.newArrayList();

        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try
        {
            final ClassPath classPath = ClassPath.from(classLoader);
            for (final String resourceName : resourceNames)
            {
                log.debug("  Scanning {}", resourceName);
                
                log.debug("  Checking resource {} as a package", resourceName);
                final ImmutableSet<ClassPath.ClassInfo> classesInPackage = classPath.getTopLevelClasses(resourceName);
                if (classesInPackage.size() > 0)
                {
                    log.debug("   Resource {} is a package - finding all classes", resourceName);
                    for (final ClassPath.ClassInfo info : classesInPackage)
                    {
                        log.debug("   Found class {}", info.getName());
                        possibleResources.add(info.load());
                    }
                }
                else
                {
                    log.debug("  Resource {} not a package - checking it as a class", resourceName);
                    try
                    {
                        possibleResources.add(classLoader.loadClass(resourceName));
                        log.debug("   Found class {}", resourceName);
                    }
                    catch (ClassNotFoundException e1)
                    {
                        log.warn("Resource {} does not appear to be a package or a class", resourceName);
                    }
                }
            }
        }
        catch (IOException e)
        {
            log.warn("Unable to load class path", e);
        }
        
        log.debug("... resource scan complete.");
        
        return scan(possibleResources);
    }
    
    public static Set<Class<?>> scan(final List<Class<?>> possibleResources)
    {
        final Set<Class<?>> resources = Sets.newConcurrentHashSet();
        
        for (final Class<?> possibleResource : possibleResources)
        {
            if (null != possibleResource.getAnnotation(Path.class))
            {
                resources.add(possibleResource);
            }
        }
        
        return resources;
    }
}
