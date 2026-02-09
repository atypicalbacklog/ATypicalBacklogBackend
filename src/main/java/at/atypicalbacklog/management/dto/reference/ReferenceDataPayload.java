package at.atypicalbacklog.management.dto.reference;

import java.util.List;

public record ReferenceDataPayload(
    List<SimpleLookupDto> completionStates,
    List<ConsoleDto> consoles,
    List<PlatformDto> platforms,
    List<InputDto> inputs,
    List<SimpleLookupDto> variants,
    List<SimpleLookupDto> playthroughStates,
    List<SimpleLookupDto> playthroughTypes,
    List<SimpleLookupDto> tiers
) {}
