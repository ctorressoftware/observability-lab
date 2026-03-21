package com.ctorres.observabilitylab.dto;

import java.util.Set;

public record SuggestionsResponse(String code, Set<String> suggestions) {}