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
}
