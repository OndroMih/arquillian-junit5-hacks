package omihalyi.arquillian.junit5;

import java.lang.reflect.Method;
import java.util.Optional;
import org.jboss.arquillian.test.spi.*;
import org.junit.jupiter.api.extension.*;

public class ArquillianExt implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback {

    @Override
    // before suite
    public void beforeAll(ContainerExtensionContext context) throws Exception {
        TestRunnerAdaptor adaptor = TestRunnerAdaptorBuilder.build();
        adaptor.beforeSuite();
        if (context.getTestClass().isPresent()) {
            adaptor.beforeClass(context.getTestClass().get(), LifecycleMethodExecutor.NO_OP);
        }
        getStore(context).testRunnerAdaptor = Optional.of(adaptor);
    }

    @Override
    public void afterAll(ContainerExtensionContext context) throws Exception {
        if (isTestRunnerAdaptorPresent(context)) {
            final TestRunnerAdaptor adaptor = getTestRunnerAdaptor(context);
            if (context.getTestClass().isPresent()) {
                adaptor.afterClass(context.getTestClass().get(), LifecycleMethodExecutor.NO_OP);
            }
            adaptor.afterSuite();
            adaptor.shutdown();
        }
    }    

    @Override
    public void beforeEach(TestExtensionContext context) throws Exception {
        if (isTestRunnerAdaptorPresent(context)) {
            final TestRunnerAdaptor adaptor = getTestRunnerAdaptor(context);
            adaptor.before(context.getTestInstance(), context.getTestMethod().get(), LifecycleMethodExecutor.NO_OP);
        }
        runTestInContainer(context);
    }

    @Override
    public void afterEach(TestExtensionContext context) throws Exception {
        if (isTestRunnerAdaptorPresent(context)) {
            final TestRunnerAdaptor adaptor = getTestRunnerAdaptor(context);
            adaptor.after(context.getTestInstance(), context.getTestMethod().get(), LifecycleMethodExecutor.NO_OP);
        }
    }

    private void runTestInContainer(TestExtensionContext context) throws Exception {
        if (isTestRunnerAdaptorPresent(context)) {
            final TestRunnerAdaptor adaptor = getTestRunnerAdaptor(context);
            final Optional<Method> testMethod = context.getTestMethod();
            final Object testInstance = context.getTestInstance();
            adaptor.test(new TestMethodExecutor() {
                @Override
                public void invoke(Object... parameters) throws Throwable {
                    try {
                        if (testMethod.isPresent()) {
                            Method method = testMethod.get();
                            method.invoke(testInstance, parameters);
                        }
                    } catch (Throwable e) {
                        // Force a way to return the thrown Exception from the Container to the client.
                        throw e;
                    }
                }

                public Method getMethod() {
                    return testMethod.orElse(null);
                }

                public Object getInstance() {
                    return testInstance;
                }
            });
        }
    }

    private ArquillianContext getStore(ExtensionContext cec) {
        final ExtensionContext.Store store = cec.getStore();
        return store.getOrComputeIfAbsent("arquillianContext", x -> new ArquillianContext(), ArquillianContext.class);
    }

    private static class ArquillianContext {

        private Optional<TestRunnerAdaptor> testRunnerAdaptor = Optional.empty();
    }

    private TestRunnerAdaptor getTestRunnerAdaptor(ExtensionContext context) {
        return getStore(context).testRunnerAdaptor.get();
    }

    private boolean isTestRunnerAdaptorPresent(ExtensionContext context) {
        return getStore(context).testRunnerAdaptor.isPresent();
    }
    
}
