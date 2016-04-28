package com.zoomulus.zoomrs.content;

import javax.ws.rs.Produces;

@Produces(ContentType.TEXT_PLAIN)
public class TestProducesOnBoth
{
    @Produces(ContentType.APPLICATION_JSON)
    public String f() { return ""; }
}
