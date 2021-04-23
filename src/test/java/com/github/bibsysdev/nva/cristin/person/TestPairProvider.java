package com.github.bibsysdev.nva.cristin.person;

import java.nio.file.Path;
import java.util.stream.Stream;
import nva.commons.core.ioutils.IoUtils;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

public class TestPairProvider implements ArgumentsProvider {

    public static final String NVA_PERSONS_JSON_FILE = IoUtils.stringFromResources(
        Path.of("nva_persons.json"));

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.of(Arguments.of(NVA_PERSONS_JSON_FILE));
    }
}