package com.zoomulus.zoomrs.resource.mapper;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.zoomulus.zoomrs.content.ContentType;

@Path("/")
public class TestResource
{
    @Path("/resource/path/1/")
    @Produces(ContentType.TEXT_PLAIN)
    public String endpoint1()
    {
        return "";
    }
}
