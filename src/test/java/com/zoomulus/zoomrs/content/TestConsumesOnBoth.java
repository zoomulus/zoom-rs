package com.zoomulus.zoomrs.content;

import javax.ws.rs.Consumes;

@Consumes(ContentType.TEXT_PLAIN)
public class TestConsumesOnBoth
{
    @Consumes(ContentType.APPLICATION_JSON)
    public void f() { }
}
