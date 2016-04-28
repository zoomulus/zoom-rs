package com.zoomulus.zoomrs.content;

import javax.ws.rs.Produces;

public class TestProducesOnMethod
{
    @Produces(ContentType.APPLICATION_JSON)
    public String f() { return ""; }
    @Produces({ContentType.APPLICATION_JSON, ContentType.TEXT_PLAIN})
    public String f2() { return ""; }
}
