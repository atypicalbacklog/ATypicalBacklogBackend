package at.atypicalbacklog.management.importing.parser.model;

import java.time.LocalDate;

public record ParsedGameInstanceRow(
    String gameName,
    String platform,
    boolean backlog,
    boolean started,
    boolean finished,
    boolean finishedPlus,
    LocalDate releaseDate,
    LocalDate receivedDate,
    LocalDate backlogRemoved,
    LocalDate backlogAdded,
    LocalDate finishedOn,
    boolean free
) {}