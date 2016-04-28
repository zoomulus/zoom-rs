package com.zoomulus.zoomrs.content;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.Test;

public class ConsumesAnnotationContentTypeExtractorTest
{
    private ConsumesAnnotationContentTypeExtractor extractor = new ConsumesAnnotationContentTypeExtractor();
    
    @Test
    public void testExtractContentTypeFromMethodAnnotation() throws NoSuchMethodException, SecurityException
    {
        final Class<?> klass = TestConsumesOnMethod.class;
        final Method method = klass.getMethod("f");
        final List<ContentType> contentTypes = extractor.extract(klass, method);
        assertEquals(1, contentTypes.size());
        assertTrue(contentTypes.contains(ContentType.APPLICATION_JSON_TYPE));
    }
    
    @Test
    public void testExtractContentTypeFromClassAnnotation() throws NoSuchMethodException, SecurityException
    {
        final Class<?> klass = TestConsumesOnClass.class;
        final Method method = klass.getMethod("f");
        final List<ContentType> contentTypes = extractor.extract(klass, method);
        assertEquals(1, contentTypes.size());
        assertTrue(contentTypes.contains(ContentType.APPLICATION_JSON_TYPE));
    }
    
    @Test
    public void testExtractContentTypeOnBothPrefersMethodAnnotation() throws NoSuchMethodException, SecurityException
    {
        final Class<?> klass = TestConsumesOnBoth.class;
        final Method method = klass.getMethod("f");
        final List<ContentType> contentTypes = extractor.extract(klass, method);
        assertEquals(1, contentTypes.size());
        assertTrue(contentTypes.contains(ContentType.APPLICATION_JSON_TYPE));
    }
    
    @Test
    public void testExtractContentTypeWithNoAnnotationsReturnsDefault() throws NoSuchMethodException, SecurityException
    {
        final Class<?> klass = TestConsumesOnNeither.class;
        final Method method = klass.getMethod("f");
        List<ContentType> contentTypes = extractor.extract(klass, method);
        assertEquals(0, contentTypes.size());
        
        contentTypes = extractor.extract(klass, method, ContentType.APPLICATION_JSON_TYPE);
        assertEquals(1, contentTypes.size());
        assertTrue(contentTypes.contains(ContentType.APPLICATION_JSON_TYPE));
    }
    
    @Test
    public void testExtractMultipleContentTypesFromMethod() throws NoSuchMethodException, SecurityException
    {
        final Class<?> klass = TestConsumesOnMethod.class;
        final Method method = klass.getMethod("f2");
        List<ContentType> contentTypes = extractor.extract(klass, method);
        assertEquals(2, contentTypes.size());
        assertTrue(contentTypes.contains(ContentType.APPLICATION_JSON_TYPE));
        assertTrue(contentTypes.contains(ContentType.TEXT_PLAIN_TYPE));
    }
    
    @Test
    public void testExtractMultipleContentTypesFromClass() throws NoSuchMethodException, SecurityException
    {
        final Class<?> klass = TestConsumesMultipleOnClass.class;
        final Method method = klass.getMethod("f");
        List<ContentType> contentTypes = extractor.extract(klass, method);
        assertEquals(2, contentTypes.size());
        assertTrue(contentTypes.contains(ContentType.APPLICATION_JSON_TYPE));
        assertTrue(contentTypes.contains(ContentType.TEXT_PLAIN_TYPE));
    }
}
