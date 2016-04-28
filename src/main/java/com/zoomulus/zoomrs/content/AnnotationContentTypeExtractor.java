package com.zoomulus.zoomrs.content;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public abstract class AnnotationContentTypeExtractor<T extends Annotation>
{
    protected abstract Class<T> getAnnotationClass();
    protected abstract List<String> getAnnotationValues(final T ann);
    
    protected Set<ContentType> getContentTypesFromAnnotation(final T ann)
    {
        final Set<ContentType> contentTypes = Sets.newHashSet();
        for (final String cts : getAnnotationValues(ann))
        {
            try
            {
                contentTypes.add(ContentType.valueOf(cts));
            }
            catch (IllegalArgumentException e) { }
        }
        return contentTypes;
    }
    
    public List<ContentType> extract(final Class<?> klass, final Method method)
    {
        return extract(klass, method, null);
    }
    
    @SuppressWarnings("unchecked")
    public List<ContentType> extract(
            final Class<?> klass,
            final Method method,
            final ContentType defaultContentType
    )
    {
        final Set<ContentType> contentTypes = Sets.newHashSet();
        final Class<T> annotationClass = getAnnotationClass();
        
        Annotation ann = method.getAnnotation(annotationClass);
        if (null != ann)
        {
            contentTypes.addAll(getContentTypesFromAnnotation((T) ann));
        }
        
        if (0 == contentTypes.size())
        {
            ann = klass.getAnnotation(annotationClass);
            if (null != ann)
            {
                contentTypes.addAll(getContentTypesFromAnnotation((T) ann));
            }
        }
        
        if (null != defaultContentType && ! contentTypes.contains(defaultContentType))
        {
            contentTypes.add(defaultContentType);
        }
        
        final List<ContentType> result = Lists.newArrayList(contentTypes);
        return result;
    }
}
