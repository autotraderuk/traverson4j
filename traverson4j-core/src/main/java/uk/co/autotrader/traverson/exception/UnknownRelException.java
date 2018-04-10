package uk.co.autotrader.traverson.exception;

import java.util.Map;
import java.util.SortedSet;

public class UnknownRelException extends IncompleteTraversalException {
    public UnknownRelException(String rel) {
        super(String.format("Rel %s not found", rel));
    }

    public UnknownRelException(String rel, SortedSet<String> knownRels) {
        super(String.format("Rel %s not in the following %s", rel, knownRels));
    }

    public UnknownRelException(String rel, Map<String, SortedSet<String>> knownRels) {
        super(String.format("Rel %s not found in %s", rel, knownRels));
    }
}
