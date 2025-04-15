package domainevent.command.handler;

public interface CommandHandler {
    void publishCommand(String json);
}