package com.zoomulus.zoomrs.content;

import javax.ws.rs.Consumes;

public class TestConsumesOnMethod
{
    @Consumes(ContentType.APPLICATION_JSON)
    public void f() { }
    @Consumes({ContentType.APPLICATION_JSON, ContentType.TEXT_PLAIN})
    public void f2() { }
}
