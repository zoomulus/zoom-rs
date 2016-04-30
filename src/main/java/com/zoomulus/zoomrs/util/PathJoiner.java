package com.zoomulus.zoomrs.util;

import java.util.List;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public class PathJoiner
{
    final List<String> pathElements = Lists.newArrayList();

    public PathJoiner with(final String pathElement)
    {
        if (! Strings.isNullOrEmpty(pathElement) && ! pathElement.equals("/"))
        {
            pathElements.add(pathElement.startsWith("/") ?
                    (pathElement.endsWith("/") ? pathElement.substring(0, pathElement.length()-1)
                            : pathElement) :
                    (pathElement.endsWith("/") ? "/" + pathElement.substring(0, pathElement.length()-1)
                            : "/" + pathElement));
        }
        return this;
    }

    public PathJoiner with(final List<String> pathElements)
    {
        for (final String element : pathElements)
        {
            with(element);
        }
        return this;
    }

    public String join()
    {
        final StringBuilder builder = new StringBuilder();

        for (final String element : pathElements)
        {
            builder.append(element);
        }

        builder.append("/");
        String result = builder.toString();

        return result;
    }
}
