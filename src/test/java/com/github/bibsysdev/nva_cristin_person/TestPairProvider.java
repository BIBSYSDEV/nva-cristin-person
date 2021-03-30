package com.github.bibsysdev.nva_cristin_person;

import java.nio.file.Path;
import java.util.stream.Stream;
import nva.commons.core.ioutils.IoUtils;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

public class TestPairProvider implements ArgumentsProvider {

    public static final String QUERY_NVA_PERSONS_RESPONSE_JSON = IoUtils.stringFromResources(
        Path.of("query_nva_persons_response.json"));

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.of(Arguments.of(QUERY_NVA_PERSONS_RESPONSE_JSON));
    }
}