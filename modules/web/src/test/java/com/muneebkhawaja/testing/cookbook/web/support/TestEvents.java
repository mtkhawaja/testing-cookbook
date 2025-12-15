package com.muneebkhawaja.testing.cookbook.web.support;

import com.muneebkhawaja.web.generated.model.*;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;


public final class TestEvents {

    private TestEvents() {
    }

    public static EventUpsertRequest upsertRequest(final String title) {
        return new EventUpsertRequest()
                .title(title)
                .description(RandomStringUtils.secure().nextAlphabetic(10))
                .severity(Severity.INFO);
    }

    public static EventUpsertRequest upsertRequest(final String title,
                                                   final Severity severity,
                                                   final OffsetDateTime timestamp,
                                                   final String description) {
        return new EventUpsertRequest()
                .title(title)
                .severity(severity)
                .timestamp(timestamp)
                .description(description);
    }

    public static Event event(final String title) {
        return event(UUID.randomUUID(), title);
    }

    public static Event event(final UUID id, final String title) {
        return new Event(id).title(title);
    }

    public static EventPatchRequest patchTitle(final String newTitle) {
        return new EventPatchRequest(Set.of(EventPatchRequest.UpdateMaskEnum.TITLE))
                .event(new EventProperties().title(newTitle));
    }

}
