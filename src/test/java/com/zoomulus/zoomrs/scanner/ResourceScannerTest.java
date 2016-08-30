package com.zoomulus.zoomrs.scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;

public class ResourceScannerTest
{
    private static final Set<Class<?>> expectedEmpty = Sets.newHashSet();
    private static final Set<Class<?>> expectedSingle = Sets.newHashSet(TestResourceA.class);
    private static final Set<Class<?>> expectedMultiple = Sets.newHashSet(TestResourceA.class, TestResourceB.class);
    
    private void verifyEqual(final Set<Class<?>> expected, final Set<Class<?>> actual)
    {
        assertEquals(expected.size(), actual.size());
        for (final Class<?> k : expected)
        {
            assertTrue(actual.contains(k));
        }
        for (final Class<?> k : actual)
        {
            assertTrue(expected.contains(k));
        }
    }
    
    @Test
    public void testScanSingleJaxRsResource()
    {
        verifyEqual(expectedSingle, new ResourceScanner(TestResourceA.class.getName()).scan());
    }
    
    @Test
    public void testScanMultipleJaxRsResourcesFindsAll()
    {
        final List<String> resources = Lists.newArrayList(TestResourceA.class.getName(), TestResourceB.class.getName());
        verifyEqual(expectedMultiple, new ResourceScanner(resources).scan());
    }
    
    @Test
    public void testScanPackageFindsCorrect()
    {
        verifyEqual(expectedMultiple, new ResourceScanner(TestResourceA.class.getPackage().getName()).scan());
    }
    
    @Test
    public void testScanMixedJaxRsResourcesFindsCorrect()
    {
        final List<String> resources = Lists.newArrayList(TestResourceA.class.getName(), TestResourceB.class.getName(), TestNotAResourceA.class.getName());
        verifyEqual(expectedMultiple, new ResourceScanner(resources).scan());
    }
    
    @Test
    public void testScanSingleInvalidResource()
    {
        verifyEqual(expectedEmpty, new ResourceScanner(TestNotAResourceA.class.getName()).scan());
    }
    
    @Test
    public void testScanMultipleInvalidResources()
    {
        final List<String> resources = Lists.newArrayList(TestNotAResourceA.class.getName(), TestNotAResourceB.class.getName());
        verifyEqual(expectedEmpty, new ResourceScanner(resources).scan());
    }
    
    @Test
    public void testScanInvalidPackage()
    {
        verifyEqual(expectedEmpty, new ResourceScanner("com.zoomulus.not.a.package").scan());
    }
    
    @Test
    public void testScanEmptyList()
    {
        verifyEqual(expectedEmpty, new ResourceScanner(Lists.newArrayList()).scan());
    }
    
    @Test
    public void testScanNullSingleArgument()
    {
        final String nullString = null;
        verifyEqual(expectedEmpty, new ResourceScanner(nullString).scan());
    }
    
    @Test
    public void testScanCommaDelimitedString()
    {
        verifyEqual(expectedMultiple, new ResourceScanner(Joiner.on(",").join(TestResourceA.class.getName(), TestResourceB.class.getName())).scan());
    }
    
    @Test
    public void testScanWithIncrementalBuilder()
    {
        verifyEqual(expectedMultiple,
                new ResourceScanner()
                .add(TestResourceA.class.getName())
                .add(TestResourceB.class.getPackage().getName())
                .add(TestNotAResourceA.class.getName())
                .scan());
    }
    
    @Test
    public void testScanInjectedSingleJaxRsResource()
    {
        final Injector injector = Guice.createInjector(new AbstractModule(){
            @Override public void configure()
            {
                bind(String.class)
                    .annotatedWith(Names.named(ResourceScanner.RESOURCES_IDENTIFIER))
                    .toInstance(TestResourceA.class.getName());
            }
        });
        verifyEqual(expectedSingle, injector.getInstance(ResourceScanner.class).scan());
    }
    
    @Test
    public void testScanInjectedCommaSeparatedResources()
    {
        final Injector injector = Guice.createInjector(new AbstractModule(){
            @Override public void configure()
            {
                bind(String.class)
                    .annotatedWith(Names.named(ResourceScanner.RESOURCES_IDENTIFIER))
                    .toInstance(Joiner.on(",").join(
                            TestResourceA.class.getName(),
                            TestResourceB.class.getPackage().getName(),
                            TestNotAResourceA.class.getName()));
            }
        });
        verifyEqual(expectedMultiple, injector.getInstance(ResourceScanner.class).scan());
    }
}
