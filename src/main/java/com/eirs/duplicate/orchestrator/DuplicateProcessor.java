package com.eirs.duplicate.orchestrator;

import java.time.LocalDate;

public interface DuplicateProcessor {
    void process(LocalDate localDate);
}
