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

public class JaxRsResourceScannerTest
{
    private static final Set<Class<?>> expectedEmpty = Sets.newHashSet();
    private static final Set<Class<?>> expectedSingle = Sets.newHashSet(TestJaxRsResourceA.class);
    private static final Set<Class<?>> expectedMultiple = Sets.newHashSet(TestJaxRsResourceA.class, TestJaxRsResourceB.class);
    
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
        verifyEqual(expectedSingle, new JaxRsResourceScanner(TestJaxRsResourceA.class.getName()).scan());
    }
    
    @Test
    public void testScanMultipleJaxRsResourcesFindsAll()
    {
        final List<String> resources = Lists.newArrayList(TestJaxRsResourceA.class.getName(), TestJaxRsResourceB.class.getName());
        verifyEqual(expectedMultiple, new JaxRsResourceScanner(resources).scan());
    }
    
    @Test
    public void testScanPackageFindsCorrect()
    {
        verifyEqual(expectedMultiple, new JaxRsResourceScanner(TestJaxRsResourceA.class.getPackage().getName()).scan());
    }
    
    @Test
    public void testScanMixedJaxRsResourcesFindsCorrect()
    {
        final List<String> resources = Lists.newArrayList(TestJaxRsResourceA.class.getName(), TestJaxRsResourceB.class.getName(), TestNotAJaxRsResourceA.class.getName());
        verifyEqual(expectedMultiple, new JaxRsResourceScanner(resources).scan());
    }
    
    @Test
    public void testScanSingleInvalidResource()
    {
        verifyEqual(expectedEmpty, new JaxRsResourceScanner(TestNotAJaxRsResourceA.class.getName()).scan());
    }
    
    @Test
    public void testScanMultipleInvalidResources()
    {
        final List<String> resources = Lists.newArrayList(TestNotAJaxRsResourceA.class.getName(), TestNotAJaxRsResourceB.class.getName());
        verifyEqual(expectedEmpty, new JaxRsResourceScanner(resources).scan());
    }
    
    @Test
    public void testScanInvalidPackage()
    {
        verifyEqual(expectedEmpty, new JaxRsResourceScanner("com.zoomulus.not.a.package").scan());
    }
    
    @Test
    public void testScanEmptyList()
    {
        verifyEqual(expectedEmpty, new JaxRsResourceScanner(Lists.newArrayList()).scan());
    }
    
    @Test
    public void testScanNullSingleArgument()
    {
        final String nullString = null;
        verifyEqual(expectedEmpty, new JaxRsResourceScanner(nullString).scan());
    }
    
    @Test
    public void testScanCommaDelimitedString()
    {
        verifyEqual(expectedMultiple, new JaxRsResourceScanner(Joiner.on(",").join(TestJaxRsResourceA.class.getName(), TestJaxRsResourceB.class.getName())).scan());
    }
    
    @Test
    public void testScanWithIncrementalBuilder()
    {
        verifyEqual(expectedMultiple,
                new JaxRsResourceScanner()
                .add(TestJaxRsResourceA.class.getName())
                .add(TestJaxRsResourceB.class.getPackage().getName())
                .add(TestNotAJaxRsResourceA.class.getName())
                .scan());
    }
    
    @Test
    public void testScanInjectedSingleJaxRsResource()
    {
        final Injector injector = Guice.createInjector(new AbstractModule(){
            @Override public void configure()
            {
                bind(String.class)
                    .annotatedWith(Names.named(JaxRsResourceScanner.RESOURCES_IDENTIFIER))
                    .toInstance(TestJaxRsResourceA.class.getName());
            }
        });
        verifyEqual(expectedSingle, injector.getInstance(JaxRsResourceScanner.class).scan());
    }
    
    @Test
    public void testScanInjectedCommaSeparatedResources()
    {
        final Injector injector = Guice.createInjector(new AbstractModule(){
            @Override public void configure()
            {
                bind(String.class)
                    .annotatedWith(Names.named(JaxRsResourceScanner.RESOURCES_IDENTIFIER))
                    .toInstance(Joiner.on(",").join(
                            TestJaxRsResourceA.class.getName(),
                            TestJaxRsResourceB.class.getPackage().getName(),
                            TestNotAJaxRsResourceA.class.getName()));
            }
        });
        verifyEqual(expectedMultiple, injector.getInstance(JaxRsResourceScanner.class).scan());
    }
}
