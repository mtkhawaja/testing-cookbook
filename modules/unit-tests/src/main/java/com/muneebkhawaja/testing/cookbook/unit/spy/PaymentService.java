package com.muneebkhawaja.testing.cookbook.unit.spy;

import java.util.Objects;

/// Records a payment through an [EventAuditor]. In the spy test the auditor is a real spy, so the
/// audit actually happens and can also be verified.
public final class PaymentService {

    private final EventAuditor auditor;

    public PaymentService(final EventAuditor auditor) {
        this.auditor = Objects.requireNonNull(auditor, "auditor");
    }

    public void pay(final String reference) {
        Objects.requireNonNull(reference, "reference");
        auditor.audit("paid:%s".formatted(reference));
    }
}
