public class MessageDAO {
    public static void create(Message message) { JPAUtil.getEntityManager().persist(message); }
}
