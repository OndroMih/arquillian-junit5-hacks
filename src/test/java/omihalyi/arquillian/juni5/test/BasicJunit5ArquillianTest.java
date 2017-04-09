package omihalyi.arquillian.juni5.test;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import omihalyi.arquillian.junit5.ArquillianExt;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 *
 * @author Ondrej Mihalyi
 */
@ExtendWith(ArquillianExt.class)
public class BasicJunit5ArquillianTest {

    @Inject
    private HttpSession session;

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Test
    public void hello() {
        assertNotNull(session, "should run in container");
        fail("TODO");
    }
}
