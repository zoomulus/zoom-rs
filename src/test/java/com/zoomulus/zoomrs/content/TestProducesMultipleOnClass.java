package com.zoomulus.zoomrs.content;

import javax.ws.rs.Produces;

@Produces({ContentType.APPLICATION_JSON, ContentType.TEXT_PLAIN})
public class TestProducesMultipleOnClass
{
    public String f() { return ""; }
}
