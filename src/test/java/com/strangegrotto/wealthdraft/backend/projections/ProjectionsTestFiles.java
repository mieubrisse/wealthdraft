package com.strangegrotto.wealthdraft.backend.projections;

import com.strangegrotto.wealthdraft.TestFileProvider;
import com.strangegrotto.wealthdraft.TestResourceDirnames;

public enum ProjectionsTestFiles implements TestFileProvider {
    CHANGE_ON_TODAY("change-on-today.yml"),
    DEPEND_ON_NONEXISTENT_SCENARIO("depend-on-nonexistent-scenario.yml"),
    DEPENDENCY_CYLE("dependency-cycle.yml"),
    DEPENDENCY_HAS_ERROR("dependency-has-error.yml"),
    INVALID_YML("invalid-yaml.yml"),
    PAST_DATE_IN_PROJECTION("past-date-in-projection.yml"),
    TWO_CHNAGES_ON_SAME_DATE("two-changes-on-same-date.yml"),
    TWO_CHANGES_ON_SAME_DATE_FROM_DIFF_SCENARIOS("two-changes-on-same-date-from-diff-scenarios.yml"),
    EXAMPLE(TestResourceDirnames.EXAMPLES, "projections.yml");

    private final TestResourceDirnames containingDirname;
    private final String filename;

    ProjectionsTestFiles(TestResourceDirnames containingDirname, String filename) {
        this.containingDirname = containingDirname;
        this.filename = filename;
    }

    ProjectionsTestFiles(String filename) {
        this(TestResourceDirnames.PROJECTIONS_DESERIALIZATION_TESTS, filename);
    }

    @Override
    public TestResourceDirnames getContainingDirname() {
        return this.containingDirname;
    }

    @Override
    public String getFilename() {
        return this.filename;

    }
}
