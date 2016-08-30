package com.zoomulus.zoomrs;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import jersey.repackaged.com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

import com.google.common.base.Joiner;
import com.zoomulus.servers.Server;
import com.zoomulus.servers.http.HttpServer;
import com.zoomulus.zoomrs.scanner.ResourceScanner;

@Slf4j
public class JaxRsServer implements Server
{
    org.eclipse.jetty.server.Server jettyServer;
    
    public static final String CONTEXT_PATH = "context-path";
    
    @Inject
    public JaxRsServer(final ResourceScanner scanner,
            @Named(CONTEXT_PATH) final String contextPath,
            @Named(HttpServer.LISTEN_PORT_NAME) final Integer port)
    {
        final ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        contextHandler.setContextPath(contextPath);
        
        jettyServer = new org.eclipse.jetty.server.Server(port.intValue());
        jettyServer.setHandler(contextHandler);
        
        final ServletHolder servlet = contextHandler.addServlet(ServletContainer.class, "/*");
        servlet.setInitOrder(0);

        final List<String> resourceNames = Lists.newArrayList();
        for (final Class<?> resource : scanner.scan())
        {
            resourceNames.add(resource.getCanonicalName());
        }
        servlet.setInitParameter("jersey.config.server.provider.classnames",
                Joiner.on(",").join(resourceNames));
    }

    @Override
    public void start()
    {
        try
        {
            jettyServer.start();
        }
        catch (Exception e)
        {
            log.warn("The Jetty server threw an exception; shutting down now", e);
            shutdown();
        }
    }

    @Override
    public void shutdown()
    {
        try
        {
            jettyServer.join();
        }
        catch (InterruptedException e)
        {
            log.warn("Exception caught during shutdown", e);
        }
        finally
        {
            jettyServer.destroy();
        }
    }
}
