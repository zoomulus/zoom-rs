package com.zoomulus.zoomrs.content;

import java.util.List;

import javax.ws.rs.Consumes;

import com.google.common.collect.Lists;

public class ConsumesAnnotationContentTypeExtractor extends AnnotationContentTypeExtractor<Consumes>
{
    @Override
    protected Class<Consumes> getAnnotationClass()
    {
        return Consumes.class;
    }

    @Override
    protected List<String> getAnnotationValues(final Consumes ann)
    {
        return Lists.newArrayList(ann.value());
    }
}
