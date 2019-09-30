import java.util.ArrayList;

public class Service {
    public static synchronized void addMessage(Message message) {
        try {
            JPAUtil.createEntityManager();
            JPAUtil.openTransaction();

            MessageDAO.create(message);

            JPAUtil.commitTransaction();
        } catch (Exception ex) {
            System.out.println("Message not persisted");
            JPAUtil.rollbackTransaction();
        } finally {
            JPAUtil.closeEntityManager();
        }
    }

    public static synchronized ArrayList<Message> getAllMessages() {
        ArrayList<Message> messages = new ArrayList<Message>();
        try {
            JPAUtil.createEntityManager();
            JPAUtil.openTransaction();

            messages.addAll(MessageDAO.findAll());


            JPAUtil.commitTransaction();
        } catch (Exception ex) {
            ex.printStackTrace();
            JPAUtil.rollbackTransaction();
            messages = null;
        } finally {
            JPAUtil.closeEntityManager();
        }

        return messages;
    }
}
