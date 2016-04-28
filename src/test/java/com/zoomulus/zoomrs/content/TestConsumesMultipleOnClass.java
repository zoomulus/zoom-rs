package com.zoomulus.zoomrs.content;

import javax.ws.rs.Consumes;

@Consumes({ContentType.APPLICATION_JSON, ContentType.TEXT_PLAIN})
public class TestConsumesMultipleOnClass
{
    public void f() { }
}
