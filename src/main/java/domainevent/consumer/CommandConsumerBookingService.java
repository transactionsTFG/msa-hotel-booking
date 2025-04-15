package domainevent.consumer;

import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import domainevent.command.handler.CommandHandler;
import domainevent.registry.EventHandlerRegistry;
import msa.commons.consts.JMSQueueNames;
import msa.commons.event.Event;

@MessageDriven(mappedName = JMSQueueNames.AGENCY_HOTEL_BOOKING_SERVICE_QUEUE)
public class CommandConsumerBookingService implements MessageListener {

    private Gson gson;
    private EventHandlerRegistry eventHandlerRegistry;
    private static final Logger LOGGER = LogManager.getLogger(CommandConsumerBookingService.class);

    @Inject
    public void setGson(Gson gson) {
        this.gson = gson;
    }

    @EJB
    public void setCommandHandlerRegistry(EventHandlerRegistry commandHandlerRegistry) {
        this.eventHandlerRegistry = commandHandlerRegistry;
    }

    @Override
    public void onMessage(Message msg) {
        try {
            if (msg instanceof TextMessage m) {
                Event event = this.gson.fromJson(m.getText(), Event.class);
                LOGGER.info("Recibido en cola {}, Evento Id: {}, Mensaje: {}",
                        JMSQueueNames.AGENCY_HOTEL_BOOKING_SERVICE_QUEUE,
                        event.getEventId(), event.getValue().toString());
                CommandHandler handler = this.eventHandlerRegistry.getHandler(event.getEventId());
                if (handler != null)
                    handler.publishCommand(this.gson.toJson(event.getValue()));
            }
        } catch (Exception e) {
            LOGGER.error("Error al recibir el mensaje: {}", e.getMessage());
        }
    }

}
