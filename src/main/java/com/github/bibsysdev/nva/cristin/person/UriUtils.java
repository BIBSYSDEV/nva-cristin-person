package com.github.bibsysdev.nva.cristin.person;

import static nva.commons.core.attempt.Try.attempt;
import java.net.URI;
import nva.commons.core.JacocoGenerated;

@JacocoGenerated
public class UriUtils {

    public static URI buildUri(String... parts) {
        return attempt(() -> new URI(String.join("/", parts))).orElseThrow();
    }
}
