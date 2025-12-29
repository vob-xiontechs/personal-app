package com.dev.backendapi;

/**
 * Demo class to test the TestDocumentationListener functionality
 * without running full JUnit tests
 */
public class TestReportDemo {
    public static void main(String[] args) {
        System.out.println("🚀 Testing TestDocumentationListener functionality...");

        // Create an instance of the listener (this will register the shutdown hook)
        TestDocumentationListener listener = new TestDocumentationListener();

        // Simulate some test results
        simulateTestResults(listener);

        System.out.println("✅ Test results recorded. Generating report now...");

        // Generate report immediately for demonstration
        TestDocumentationListener.generateReportNow();

        System.out.println("🎉 Report generation completed!");
    }

    private static void simulateTestResults(TestDocumentationListener listener) {
        // Simulate test results by directly adding to the global test results map
        // This demonstrates the functionality without needing JUnit

        System.out.println("📝 Simulating test results...");

        // Directly add test results to the global map for demonstration
        try {
            var globalResultsField = TestDocumentationListener.class.getDeclaredField("globalTestResults");
            globalResultsField.setAccessible(true);

            @SuppressWarnings("unchecked")
            var globalResults = (java.util.Map<String, Object>) globalResultsField.get(null);

            // Simulate 3 test results
            for (int i = 1; i <= 4; i++) {
                String testName = "testMethod" + i;
                String className = "DemoTestClass";
                String status = (i % 2 == 0) ? "SUCCESSFUL" : "FAILED";
                Throwable exception = (i % 2 != 0) ? new RuntimeException("Test failed: " + testName) : null;

                // Create a TestResult instance using reflection
                var testResultClass = Class.forName("com.dev.backendapi.TestDocumentationListener$TestResult");
                var constructor = testResultClass.getDeclaredConstructor(
                    String.class, String.class, String.class, java.time.Duration.class, Throwable.class);
                constructor.setAccessible(true);

                var result = constructor.newInstance(
                    testName, className, status, java.time.Duration.ofMillis(100 + i * 50), exception);

                globalResults.put(testName + "_id", result);
            }

            System.out.println("📊 Simulated 4 test results recorded in global map");

        } catch (Exception e) {
            System.err.println("❌ Error simulating test results: " + e.getMessage());
        }
    }
}
