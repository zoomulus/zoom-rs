package com.zoomulus.zoomrs.content;

import java.util.List;

import javax.ws.rs.Produces;

import com.google.common.collect.Lists;

public class ProducesAnnotationContentTypeExtractor extends AnnotationContentTypeExtractor<Produces>
{
    @Override
    protected Class<Produces> getAnnotationClass()
    {
        return Produces.class;
    }

    @Override
    protected List<String> getAnnotationValues(final Produces ann)
    {
        return Lists.newArrayList(ann.value());
    }
}
