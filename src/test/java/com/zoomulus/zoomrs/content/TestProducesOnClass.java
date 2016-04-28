package com.zoomulus.zoomrs.content;

import javax.ws.rs.Produces;

@Produces(ContentType.APPLICATION_JSON)
public class TestProducesOnClass
{
    public String f() { return ""; }
}
