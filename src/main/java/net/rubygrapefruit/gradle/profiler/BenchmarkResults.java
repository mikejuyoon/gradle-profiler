package net.rubygrapefruit.gradle.profiler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class BenchmarkResults {
    private final Map<String, List<BuildInvocationResult>> columns = new LinkedHashMap<>();

    public Consumer<BuildInvocationResult> version(ScenarioDefinition scenario, GradleVersion version) {
        List<BuildInvocationResult> results = getResultsForVersion(scenario, version);
        return buildInvocationResult -> results.add(buildInvocationResult);
    }

    private List<BuildInvocationResult> getResultsForVersion(ScenarioDefinition scenario, GradleVersion version) {
        String name = scenario.getName() + " " + version.getVersion();
        List<BuildInvocationResult> results = columns.get(name);
        if (results == null) {
            results = new ArrayList<>();
            columns.put(name, results);
        }
        return results;
    }

    public void writeTo(File csv) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csv))) {
            writer.write("build");
            for (String name : columns.keySet()) {
                writer.write(",");
                writer.write(name);
            }
            writer.newLine();
            for (int row = 0; ; row++) {
                boolean startRow = true;
                for (List<BuildInvocationResult> results : columns.values()) {
                    if (row >= results.size()) {
                        return;
                    }
                    BuildInvocationResult buildResult = results.get(row);
                    if (startRow) {
                        writer.write(buildResult.getDisplayName());
                        startRow = false;
                    }
                    writer.write(",");
                    writer.write(String.valueOf(buildResult.getExecutionTime().toMillis()));
                }
                writer.newLine();
            }
        }
    }
}
