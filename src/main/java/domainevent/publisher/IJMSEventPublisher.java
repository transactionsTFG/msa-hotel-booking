package domainevent.publisher;

import msa.commons.event.EventId;

public interface IJMSEventPublisher {
    void publish(EventId eventId, Object event);
}
