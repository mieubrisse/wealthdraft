package com.strangegrotto.wealthdraft.networth.projections;

import java.net.URL;

enum ProjectionsFile implements TestFileProvider {
    CHANGE_ON_TODAY("change-on-today.yml"),
    DEPEND_ON_NONEXISTENT_SCENARIO("depend-on-nonexistent-scenario.yml"),
    DEPENDENCY_CYLE("dependency-cycle.yml"),
    DEPENDENCY_HAS_ERROR("dependency-has-error.yml"),
    INVALID_YML("invalid-yaml.yml"),
    PAST_DATE_IN_PROJECTION("past-date-in-projection.yml"),
    TWO_CHNAGES_ON_SAME_DATE("two-changes-on-same-date.yml"),
    TWO_CHANGES_ON_SAME_DATE_FROM_DIFF_SCENARIOS("two-changes-on-same-date-from-diff-scenarios.yml"),
    EXAMPLE("examples", "projections-example.yml");

    private static final String DESERIALIZATION_TESTS_DIRNAME = "projections-deserialization";

    private final String containingDirname;
    private final String filename;

    ProjectionsFile(String containingDirname, String filename) {
        this.containingDirname = containingDirname;
        this.filename = filename;
    }

    ProjectionsFile(String filename) {
        this(DESERIALIZATION_TESTS_DIRNAME, filename);
    }

    @Override
    public URL getResource() {
        return getClass().getClassLoader().getResource(this.containingDirname + "/" + this.filename);
    }
}
