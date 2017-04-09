package omihalyi.arquillian.junit5.container;

import omihalyi.arquillian.junit5.ArquillianExt;
import org.jboss.arquillian.container.test.spi.*;
import org.jboss.arquillian.container.test.spi.client.deployment.CachedAuxilliaryArchiveAppender;
import org.jboss.shrinkwrap.api.*;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

public class JUnit5DeploymentAppender extends CachedAuxilliaryArchiveAppender {
    @Override
    protected Archive<?> buildArchive() {
        JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "arquillian-junit5.jar")
            .addPackages(
                true,
                "org.junit",
                "org.hamcrest",
                        ArquillianExt.class.getPackage().getName())
            .addAsServiceProvider(
                TestRunner.class,
                JUnit5TestRunner.class);
        return archive;
    }
}
