package com.dev.backendapi;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

/**
 * JUnit 5 Extension that automatically generates detailed test documentation
 * in Markdown format after test execution completes.
 */
public class TestDocumentationListener implements TestWatcher {

    private static final Map<String, TestResult> globalTestResults = new ConcurrentHashMap<>();
    private static final Map<String, TestMetrics> globalTestMetrics = new ConcurrentHashMap<>();
    private static LocalDateTime globalTestSuiteStartTime;
    private static LocalDateTime globalTestSuiteEndTime;
    private static boolean shutdownHookRegistered = false;
    private static int testClassCount = 0;
    private static final int TOTAL_TEST_CLASSES = 3; // ProfileControllerTest, ProfileServiceImplTest, ProfileServiceIntegrationTest

    private final Map<String, TestResult> testResults = new ConcurrentHashMap<>();
    private final Map<String, TestMetrics> testMetrics = new ConcurrentHashMap<>();
    private LocalDateTime testSuiteStartTime;
    private LocalDateTime testSuiteEndTime;

    public TestDocumentationListener() {
        // Register shutdown hook on first instantiation
        if (!shutdownHookRegistered) {
            registerShutdownHook();
            shutdownHookRegistered = true;
        }
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        recordTestResult(context, "SUCCESSFUL", null);
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        recordTestResult(context, "FAILED", cause);
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        recordTestResult(context, "ABORTED", cause);
    }

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        // For disabled tests, we don't record them as they didn't execute
    }

    private static void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            generateGlobalTestReport();
        }));
    }

    // Alternative: Generate report immediately for demonstration
    public static void generateReportNow() {
        generateGlobalTestReport();
    }

    private static void generateGlobalTestReport() {
        if (globalTestResults.isEmpty()) {
            return; // No tests were recorded
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        String reportFileName = String.format("GLOBAL_TEST_EXECUTION_REPORT_%s.md", timestamp);

        try {
            Path reportPath = Paths.get("target", "test-reports", reportFileName);
            Files.createDirectories(reportPath.getParent());

            try (FileWriter writer = new FileWriter(reportPath.toFile())) {
                writeGlobalReportHeader(writer);
                writeGlobalTestSummary(writer);
                writeGlobalDetailedTestAnalysis(writer);
                writeGlobalImpactAnalysis(writer);
                writeGlobalRecommendations(writer);
                writeGlobalReportFooter(writer);
            }

            System.out.println("📄 Global test report generated: " + reportPath.toAbsolutePath());

        } catch (IOException e) {
            System.err.println("❌ Failed to generate global test report: " + e.getMessage());
        }
    }

    private void recordTestResult(ExtensionContext context, String status, Throwable cause) {
        if (context.getTestMethod().isPresent()) { // Only record actual test methods
            String testId = context.getUniqueId();
            String displayName = context.getDisplayName();
            String className = context.getRequiredTestClass().getSimpleName();

            if (testSuiteStartTime == null) {
                testSuiteStartTime = LocalDateTime.now();
            }

            if (globalTestSuiteStartTime == null) {
                globalTestSuiteStartTime = LocalDateTime.now();
            }

            // Get or create metrics for instance
            TestMetrics metrics = testMetrics.computeIfAbsent(testId,
                k -> new TestMetrics(displayName, LocalDateTime.now()));

            // Get or create global metrics
            TestMetrics globalMetrics = globalTestMetrics.computeIfAbsent(testId,
                k -> new TestMetrics(displayName, LocalDateTime.now()));

            LocalDateTime endTime = LocalDateTime.now();
            metrics.endTime = endTime;
            globalMetrics.endTime = endTime;

            metrics.duration = java.time.Duration.between(metrics.startTime, metrics.endTime);
            globalMetrics.duration = java.time.Duration.between(globalMetrics.startTime, globalMetrics.endTime);

            TestResult result = new TestResult(
                displayName,
                className,
                status,
                metrics.duration,
                cause
            );

            testResults.put(testId, result);
            globalTestResults.put(testId, result); // Also store in global map

            // Update global end time
            globalTestSuiteEndTime = LocalDateTime.now();
        }
    }

    private void generateTestReport() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        String reportFileName = String.format("TEST_EXECUTION_REPORT_%s.md", timestamp);

        try {
            Path reportPath = Paths.get("target", "test-reports", reportFileName);
            Files.createDirectories(reportPath.getParent());

            try (FileWriter writer = new FileWriter(reportPath.toFile())) {
                writeReportHeader(writer);
                writeTestSummary(writer);
                writeDetailedTestAnalysis(writer);
                writeImpactAnalysis(writer);
                writeRecommendations(writer);
                writeReportFooter(writer);
            }

            System.out.println("📄 Test report generated: " + reportPath.toAbsolutePath());

        } catch (IOException e) {
            System.err.println("❌ Failed to generate test report: " + e.getMessage());
        }
    }

    private void writeReportHeader(FileWriter writer) throws IOException {
        writer.write("# 🔬 Automated Test Execution Report\n\n");
        writer.write("**Generated by:** TestDocumentationListener\n");
        writer.write("**Execution Date:** " + testSuiteStartTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n");
        writer.write("**Duration:** " + java.time.Duration.between(testSuiteStartTime, testSuiteEndTime).toSeconds() + " seconds\n\n");

        writer.write("## 🎯 Report Overview\n");
        writer.write("This report provides comprehensive analysis of automated test execution, including:\n");
        writer.write("- Test execution metrics and performance\n");
        writer.write("- Detailed test case analysis\n");
        writer.write("- System impact assessment\n");
        writer.write("- Recommendations for improvement\n\n");
    }

    private void writeTestSummary(FileWriter writer) throws IOException {
        writer.write("## 📊 Test Execution Summary\n\n");

        long totalTests = testResults.size();
        long passedTests = testResults.values().stream()
            .mapToLong(r -> r.status.equals("SUCCESSFUL") ? 1 : 0).sum();
        long failedTests = totalTests - passedTests;

        writer.write("| Metric | Value |\n");
        writer.write("|--------|-------|\n");
        writer.write(String.format("| Total Tests | %d |\n", totalTests));
        writer.write(String.format("| Passed | %d |\n", passedTests));
        writer.write(String.format("| Failed | %d |\n", failedTests));
        writer.write(String.format("| Success Rate | %.1f%% |\n", (double) passedTests / totalTests * 100));
        writer.write(String.format("| Execution Time | %d seconds |\n",
            java.time.Duration.between(testSuiteStartTime, testSuiteEndTime).toSeconds()));
        writer.write("\n");
    }

    private void writeDetailedTestAnalysis(FileWriter writer) throws IOException {
        writer.write("## 🔍 Detailed Test Analysis\n\n");

        for (Map.Entry<String, TestResult> entry : testResults.entrySet()) {
            TestResult result = entry.getValue();
            writer.write(String.format("### %s\n\n", result.displayName));
            writer.write(String.format("**Test Class:** %s\n", result.className));
            writer.write(String.format("**Status:** %s %s\n",
                result.status.equals("SUCCESSFUL") ? "✅" : "❌", result.status));
            writer.write(String.format("**Duration:** %d ms\n", result.duration.toMillis()));

            if (result.exception != null) {
                writer.write(String.format("**Error:** %s\n", result.exception.getMessage()));
            }

            writer.write("\n**Purpose & Impact:**\n");
            writeTestPurposeAnalysis(writer, result);
            writer.write("\n---\n\n");
        }
    }

    private void writeTestPurposeAnalysis(FileWriter writer, TestResult result) throws IOException {
        String testName = result.displayName.toLowerCase();

        if (testName.contains("valid") && testName.contains("request")) {
            writer.write("- **Purpose:** Validates successful user registration workflow\n");
            writer.write("- **Impact:** Ensures core business functionality works correctly\n");
            writer.write("- **System Areas:** Controller, Service, Repository, Database\n");
            writer.write("- **Risk Level:** HIGH - Core feature validation\n");
        } else if (testName.contains("email") && testName.contains("already")) {
            writer.write("- **Purpose:** Prevents duplicate user accounts based on email\n");
            writer.write("- **Impact:** Maintains data integrity and prevents security issues\n");
            writer.write("- **System Areas:** Service layer validation, Database constraints\n");
            writer.write("- **Risk Level:** HIGH - Data integrity protection\n");
        } else if (testName.contains("invalid") || testName.contains("validation")) {
            writer.write("- **Purpose:** Ensures input validation works correctly\n");
            writer.write("- **Impact:** Protects against malformed data and potential security vulnerabilities\n");
            writer.write("- **System Areas:** Controller validation, Bean validation\n");
            writer.write("- **Risk Level:** MEDIUM - Input sanitization\n");
        } else if (testName.contains("database") && testName.contains("save")) {
            writer.write("- **Purpose:** Verifies actual database persistence operations\n");
            writer.write("- **Impact:** Confirms data durability and storage functionality\n");
            writer.write("- **System Areas:** JPA/Hibernate, Database connectivity\n");
            writer.write("- **Risk Level:** HIGH - Data persistence validation\n");
        } else if (testName.contains("service")) {
            writer.write("- **Purpose:** Tests business logic in isolation\n");
            writer.write("- **Impact:** Validates service layer functionality without external dependencies\n");
            writer.write("- **System Areas:** Service classes, Business logic\n");
            writer.write("- **Risk Level:** MEDIUM - Logic validation\n");
        } else {
            writer.write("- **Purpose:** General functionality validation\n");
            writer.write("- **Impact:** Ensures system components work as expected\n");
            writer.write("- **System Areas:** Multiple layers depending on test scope\n");
            writer.write("- **Risk Level:** MEDIUM - General validation\n");
        }
    }

    private void writeImpactAnalysis(FileWriter writer) throws IOException {
        writer.write("## 🎯 System Impact Analysis\n\n");

        writer.write("### Performance Impact\n");
        writer.write("- **Database Operations:** " + countDatabaseTests() + " tests involve database interactions\n");
        writer.write("- **Memory Usage:** Test execution requires additional heap space\n");
        writer.write("- **I/O Operations:** File system and network operations during testing\n");
        writer.write("- **Cleanup Operations:** Automatic data cleanup prevents test pollution\n\n");

        writer.write("### Resource Utilization\n");
        writer.write("- **Database Connections:** Tests may consume connection pool resources\n");
        writer.write("- **Thread Resources:** Parallel test execution uses system threads\n");
        writer.write("- **Disk Space:** Test reports and logs consume storage\n");
        writer.write("- **Network:** External service tests may require network access\n\n");

        writer.write("### Test Environment Effects\n");
        writer.write("- **Data Isolation:** Tests run in isolated environment to prevent interference\n");
        writer.write("- **State Management:** Automatic cleanup ensures clean test state\n");
        writer.write("- **Resource Cleanup:** Proper teardown prevents resource leaks\n");
        writer.write("- **Configuration:** Test-specific configuration overrides\n\n");
    }

    private void writeRecommendations(FileWriter writer) throws IOException {
        writer.write("## 💡 Recommendations & Best Practices\n\n");

        writer.write("### Test Maintenance\n");
        writer.write("1. **Regular Review:** Update tests when business logic changes\n");
        writer.write("2. **Performance Monitoring:** Track test execution times\n");
        writer.write("3. **Coverage Analysis:** Ensure adequate test coverage\n");
        writer.write("4. **Flaky Test Detection:** Identify and fix intermittent failures\n\n");

        writer.write("### System Health Checks\n");
        writer.write("1. **Database Connectivity:** Monitor connection pool health\n");
        writer.write("2. **Resource Usage:** Track memory and CPU during test execution\n");
        writer.write("3. **Cleanup Verification:** Ensure test data cleanup works properly\n");
        writer.write("4. **Performance Benchmarks:** Establish baseline performance metrics\n\n");

        writer.write("### Automation Improvements\n");
        writer.write("1. **CI/CD Integration:** Include tests in automated pipelines\n");
        writer.write("2. **Parallel Execution:** Optimize test parallelization\n");
        writer.write("3. **Reporting Integration:** Send reports to monitoring systems\n");
        writer.write("4. **Alert Configuration:** Set up alerts for test failures\n\n");
    }

    private void writeReportFooter(FileWriter writer) throws IOException {
        writer.write("## 📈 Report Metadata\n\n");
        writer.write("**Generated At:** " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n");
        writer.write("**Report Version:** 1.0\n");
        writer.write("**Testing Framework:** JUnit 5 + Spring Boot Test\n");
        writer.write("**Documentation Generator:** TestDocumentationListener\n\n");

        writer.write("---\n\n");
        writer.write("*This report is automatically generated after each test execution to provide comprehensive insights into test quality and system impact.*\n");
    }

    private long countDatabaseTests() {
        return testResults.values().stream()
            .mapToLong(r -> r.displayName.toLowerCase().contains("database") ||
                           r.displayName.toLowerCase().contains("integration") ? 1 : 0)
            .sum();
    }

    // Global report writing methods using static data
    private static void writeGlobalReportHeader(FileWriter writer) throws IOException {
        if (globalTestSuiteStartTime == null) {
            globalTestSuiteStartTime = LocalDateTime.now().minusSeconds(1);
        }
        if (globalTestSuiteEndTime == null) {
            globalTestSuiteEndTime = LocalDateTime.now();
        }

        writer.write("# 🔬 Global Automated Test Execution Report\n\n");
        writer.write("**Generated by:** TestDocumentationListener\n");
        writer.write("**Execution Date:** " + globalTestSuiteStartTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n");
        writer.write("**Duration:** " + java.time.Duration.between(globalTestSuiteStartTime, globalTestSuiteEndTime).toSeconds() + " seconds\n\n");

        writer.write("## 🎯 Report Overview\n");
        writer.write("This report provides comprehensive analysis of all automated test execution across the entire test suite, including:\n");
        writer.write("- Global test execution metrics and performance\n");
        writer.write("- Detailed test case analysis from all test classes\n");
        writer.write("- System impact assessment\n");
        writer.write("- Recommendations for improvement\n\n");
    }

    private static void writeGlobalTestSummary(FileWriter writer) throws IOException {
        writer.write("## 📊 Global Test Execution Summary\n\n");

        long totalTests = globalTestResults.size();
        long passedTests = globalTestResults.values().stream()
            .mapToLong(r -> r.status.equals("SUCCESSFUL") ? 1 : 0).sum();
        long failedTests = totalTests - passedTests;

        writer.write("| Metric | Value |\n");
        writer.write("|--------|-------|\n");
        writer.write(String.format("| Total Tests | %d |\n", totalTests));
        writer.write(String.format("| Passed | %d |\n", passedTests));
        writer.write(String.format("| Failed | %d |\n", failedTests));
        writer.write(String.format("| Success Rate | %.1f%% |\n", totalTests > 0 ? (double) passedTests / totalTests * 100 : 0));
        writer.write(String.format("| Execution Time | %d seconds |\n",
            java.time.Duration.between(globalTestSuiteStartTime, globalTestSuiteEndTime).toSeconds()));
        writer.write("\n");
    }

    private static void writeGlobalDetailedTestAnalysis(FileWriter writer) throws IOException {
        writer.write("## 🔍 Global Detailed Test Analysis\n\n");

        for (Map.Entry<String, TestResult> entry : globalTestResults.entrySet()) {
            TestResult result = entry.getValue();
            writer.write(String.format("### %s\n\n", result.displayName));
            writer.write(String.format("**Test Class:** %s\n", result.className));
            writer.write(String.format("**Status:** %s %s\n",
                result.status.equals("SUCCESSFUL") ? "✅" : "❌", result.status));
            writer.write(String.format("**Duration:** %d ms\n", result.duration.toMillis()));

            if (result.exception != null) {
                writer.write(String.format("**Error:** %s\n", result.exception.getMessage()));
            }

            writer.write("\n**Purpose & Impact:**\n");
            writeGlobalTestPurposeAnalysis(writer, result);
            writer.write("\n---\n\n");
        }
    }

    private static void writeGlobalTestPurposeAnalysis(FileWriter writer, TestResult result) throws IOException {
        String testName = result.displayName.toLowerCase();

        if (testName.contains("valid") && testName.contains("request")) {
            writer.write("- **Purpose:** Validates successful user registration workflow\n");
            writer.write("- **Impact:** Ensures core business functionality works correctly\n");
            writer.write("- **System Areas:** Controller, Service, Repository, Database\n");
            writer.write("- **Risk Level:** HIGH - Core feature validation\n");
        } else if (testName.contains("email") && testName.contains("already")) {
            writer.write("- **Purpose:** Prevents duplicate user accounts based on email\n");
            writer.write("- **Impact:** Maintains data integrity and prevents security issues\n");
            writer.write("- **System Areas:** Service layer validation, Database constraints\n");
            writer.write("- **Risk Level:** HIGH - Data integrity protection\n");
        } else if (testName.contains("invalid") || testName.contains("validation")) {
            writer.write("- **Purpose:** Ensures input validation works correctly\n");
            writer.write("- **Impact:** Protects against malformed data and potential security vulnerabilities\n");
            writer.write("- **System Areas:** Controller validation, Bean validation\n");
            writer.write("- **Risk Level:** MEDIUM - Input sanitization\n");
        } else if (testName.contains("database") && testName.contains("save")) {
            writer.write("- **Purpose:** Verifies actual database persistence operations\n");
            writer.write("- **Impact:** Confirms data durability and storage functionality\n");
            writer.write("- **System Areas:** JPA/Hibernate, Database connectivity\n");
            writer.write("- **Risk Level:** HIGH - Data persistence validation\n");
        } else if (testName.contains("service")) {
            writer.write("- **Purpose:** Tests business logic in isolation\n");
            writer.write("- **Impact:** Validates service layer functionality without external dependencies\n");
            writer.write("- **System Areas:** Service classes, Business logic\n");
            writer.write("- **Risk Level:** MEDIUM - Logic validation\n");
        } else {
            writer.write("- **Purpose:** General functionality validation\n");
            writer.write("- **Impact:** Ensures system components work as expected\n");
            writer.write("- **System Areas:** Multiple layers depending on test scope\n");
            writer.write("- **Risk Level:** MEDIUM - General validation\n");
        }
    }

    private static void writeGlobalImpactAnalysis(FileWriter writer) throws IOException {
        writer.write("## 🎯 Global System Impact Analysis\n\n");

        writer.write("### Performance Impact\n");
        writer.write("- **Database Operations:** " + countGlobalDatabaseTests() + " tests involve database interactions\n");
        writer.write("- **Memory Usage:** Test execution requires additional heap space\n");
        writer.write("- **I/O Operations:** File system and network operations during testing\n");
        writer.write("- **Cleanup Operations:** Automatic data cleanup prevents test pollution\n\n");

        writer.write("### Resource Utilization\n");
        writer.write("- **Database Connections:** Tests may consume connection pool resources\n");
        writer.write("- **Thread Resources:** Parallel test execution uses system threads\n");
        writer.write("- **Disk Space:** Test reports and logs consume storage\n");
        writer.write("- **Network:** External service tests may require network access\n\n");

        writer.write("### Test Environment Effects\n");
        writer.write("- **Data Isolation:** Tests run in isolated environment to prevent interference\n");
        writer.write("- **State Management:** Automatic cleanup ensures clean test state\n");
        writer.write("- **Resource Cleanup:** Proper teardown prevents resource leaks\n");
        writer.write("- **Configuration:** Test-specific configuration overrides\n\n");
    }

    private static void writeGlobalRecommendations(FileWriter writer) throws IOException {
        writer.write("## 💡 Global Recommendations & Best Practices\n\n");

        writer.write("### Test Maintenance\n");
        writer.write("1. **Regular Review:** Update tests when business logic changes\n");
        writer.write("2. **Performance Monitoring:** Track test execution times\n");
        writer.write("3. **Coverage Analysis:** Ensure adequate test coverage\n");
        writer.write("4. **Flaky Test Detection:** Identify and fix intermittent failures\n\n");

        writer.write("### System Health Checks\n");
        writer.write("1. **Database Connectivity:** Monitor connection pool health\n");
        writer.write("2. **Resource Usage:** Track memory and CPU during test execution\n");
        writer.write("3. **Cleanup Verification:** Ensure test data cleanup works properly\n");
        writer.write("4. **Performance Benchmarks:** Establish baseline performance metrics\n\n");

        writer.write("### Automation Improvements\n");
        writer.write("1. **CI/CD Integration:** Include tests in automated pipelines\n");
        writer.write("2. **Parallel Execution:** Optimize test parallelization\n");
        writer.write("3. **Reporting Integration:** Send reports to monitoring systems\n");
        writer.write("4. **Alert Configuration:** Set up alerts for test failures\n\n");
    }

    private static void writeGlobalReportFooter(FileWriter writer) throws IOException {
        writer.write("## 📈 Global Report Metadata\n\n");
        writer.write("**Generated At:** " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n");
        writer.write("**Report Version:** 1.0\n");
        writer.write("**Testing Framework:** JUnit 5 + Spring Boot Test\n");
        writer.write("**Documentation Generator:** TestDocumentationListener\n\n");

        writer.write("---\n\n");
        writer.write("*This global report is automatically generated after all test executions complete, providing comprehensive insights into test quality and system impact across the entire test suite.*\n");
    }

    private static long countGlobalDatabaseTests() {
        return globalTestResults.values().stream()
            .mapToLong(r -> r.displayName.toLowerCase().contains("database") ||
                           r.displayName.toLowerCase().contains("integration") ? 1 : 0)
            .sum();
    }

    private static class TestResult {
        final String displayName;
        final String className;
        final String status;
        final java.time.Duration duration;
        final Throwable exception;

        TestResult(String displayName, String className, String status,
                  java.time.Duration duration, Throwable exception) {
            this.displayName = displayName;
            this.className = className;
            this.status = status;
            this.duration = duration;
            this.exception = exception;
        }
    }

    private static class TestMetrics {
        final String testName;
        final LocalDateTime startTime;
        LocalDateTime endTime;
        java.time.Duration duration;

        TestMetrics(String testName, LocalDateTime startTime) {
            this.testName = testName;
            this.startTime = startTime;
        }
    }
}
