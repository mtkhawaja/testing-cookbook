package com.muneebkhawaja.testing.cookbook.unit.spy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

/// The case where a spy makes the most sense: wrap a real, trustworthy collaborator so you keep its
/// real behaviour AND can verify how it was called. A plain `@Mock` here would report
/// `auditedCount() == 0`; the spy runs the real method, so it reports `1`.
@ExtendWith(MockitoExtension.class)
class SpyCollaboratorTest {

    @Spy
    private EventAuditor auditor = new EventAuditor();

    @InjectMocks
    private PaymentService paymentService;

    @DisplayName("Should run real auditing and allow verification When the collaborator is a spy")
    @Test
    void shouldRunRealAuditingAndAllowVerificationWhenTheCollaboratorIsASpy() {
        paymentService.pay("ref-1");

        verify(auditor).audit("paid:ref-1");
        assertThat(auditor.auditedCount()).isEqualTo(1);
    }
}
