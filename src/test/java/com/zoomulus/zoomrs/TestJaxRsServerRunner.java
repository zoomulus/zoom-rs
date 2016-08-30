package com.zoomulus.zoomrs;

import javax.inject.Inject;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.name.Names;
import com.zoomulus.servers.ServerPort;
import com.zoomulus.servers.http.HttpServer;
import com.zoomulus.zoomrs.scanner.ResourceScanner;
import com.zoomulus.zoomrs.testresource.TestJaxRsServerResource;

public class TestJaxRsServerRunner
{
    private final JaxRsServer server;
    
    @Inject
    public TestJaxRsServerRunner(final JaxRsServer server)
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
                bind(String.class).annotatedWith(Names.named(JaxRsServer.CONTEXT_PATH))
                .toInstance("/");
                bind(Integer.class).annotatedWith(Names.named(HttpServer.LISTEN_PORT_NAME))
                .toInstance(ServerPort.ZoomulusPort(ServerPort.PortNumber.HTTP));
                bind(String.class).annotatedWith(Names.named(ResourceScanner.RESOURCES_IDENTIFIER))
                .toInstance(TestJaxRsServerResource.class.getPackage().getName());
            }            
        }).getInstance(TestJaxRsServerRunner.class).run(args);
    }
}
