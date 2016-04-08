package com.zoomulus.zoomrs;

import javax.inject.Inject;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.name.Names;
import com.zoomulus.servers.ServerPort;
import com.zoomulus.servers.http.HttpServer;
import com.zoomulus.servers.http.responder.HttpResponder;
import com.zoomulus.zoomrs.responder.JaxRsHttpResponder;

public class TestJaxRsServerRunner
{
    private final HttpServer server;
    
    @Inject
    public TestJaxRsServerRunner(final HttpServer server)
    {
        this.server = server;
    }
    
    public void run(final String[] args)
    {
        server.start();
    }
    
    public static void main(final String[] args)
    {
        Guice.createInjector(new AbstractModule()
        {
            @Override protected void configure()
            {
                bind(HttpResponder.class).to(JaxRsHttpResponder.class);
                bind(Integer.class).annotatedWith(Names.named(HttpServer.LISTEN_PORT_NAME))
                .toInstance(ServerPort.ZoomulusPort(ServerPort.PortNumber.HTTP));}            
        }).getInstance(TestJaxRsServerRunner.class).run(args);
    }
}
