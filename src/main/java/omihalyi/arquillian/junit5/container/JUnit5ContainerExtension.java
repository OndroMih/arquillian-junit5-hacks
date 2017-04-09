package omihalyi.arquillian.junit5.container;

import org.jboss.arquillian.container.test.spi.client.deployment.AuxiliaryArchiveAppender;
import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.core.spi.LoadableExtension.ExtensionBuilder;

public class JUnit5ContainerExtension implements LoadableExtension {
    @Override
    public void register(ExtensionBuilder builder) {
        builder.service(AuxiliaryArchiveAppender.class, JUnit5DeploymentAppender.class);
    }

}
