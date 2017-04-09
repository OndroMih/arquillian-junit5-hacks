package omihalyi.arquillian.junit5.container;

import org.jboss.arquillian.container.test.spi.TestRunner;
import org.jboss.arquillian.test.spi.TestResult;
import static org.jboss.arquillian.test.spi.TestResult.*;
import org.junit.platform.commons.util.PreconditionViolationException;
import org.junit.platform.engine.TestExecutionResult;
import static org.junit.platform.engine.discovery.DiscoverySelectors.*;
import org.junit.platform.launcher.*;
import static org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder.request;
import org.junit.platform.launcher.core.LauncherFactory;

public class JUnit5TestRunner implements TestRunner {

    @Override
    public TestResult execute(Class<?> testClass, String methodName) {
        final Launcher launcher = LauncherFactory.create();
        LauncherDiscoveryRequest discoveryRequest = request()
                .selectors(selectMethod(testClass, methodName))
                .build();
        final SingleTestExecutionListener listener = new SingleTestExecutionListener();
        launcher.execute(discoveryRequest, listener);
        return listener.testResult;
    }

    private static class SingleTestExecutionListener implements TestExecutionListener {

        private TestResult testResult = TestResult.skipped();

        @Override
        public void testPlanExecutionFinished(TestPlan testPlan) {
            this.testResult.setEnd(System.currentTimeMillis());
        }

        @Override
        public void executionSkipped(TestIdentifier testIdentifier, String reason) {
            this.testResult = skipped(reason);
        }

        @Override
        public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
            switch (testExecutionResult.getStatus()) {

                case SUCCESSFUL: {
                    if (testIdentifier.isTest()) {
                        this.testResult = passed();
                    }
                    break;
                }

                case ABORTED: {
                    if (testIdentifier.isTest()) {
                        if (testExecutionResult.getThrowable().isPresent()) {
                            this.testResult = TestResult.skipped(testExecutionResult.getThrowable().get());
                        } else {
                            this.testResult = TestResult.skipped("Aborted");
                        }
                    }
                    break;
                }

                case FAILED: {
                    if (testIdentifier.isTest()) {
                        final Throwable exception = testExecutionResult.getThrowable()
                                .orElseGet(() -> new Exception("Failed"));
                        this.testResult = TestResult.failed(exception);
                    }
                    break;
                }

                default:
                    throw new PreconditionViolationException(
                            "Unsupported execution status:" + testExecutionResult.getStatus());
            }
        }
    }

}
