import javax.persistence.Query;

import java.util.List;

public class MessageDAO {
    public static void create(Message message) { JPAUtil.getEntityManager().persist(message); }

    public static List<Message> findAll() {
        String s = "select m from Message m";
        Query query = JPAUtil.getEntityManager().createQuery(s);
        return (List<Message>) query.getResultList();
    }
}
