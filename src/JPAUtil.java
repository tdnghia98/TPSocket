import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.RollbackException;

public class JPAUtil {
    public static final String PERSISTANCE_UNIT_NAME = "chat";
    private static EntityManagerFactory entityManagerFactory = null;

    private static final ThreadLocal<EntityManager> threadLocalEntityManager = new ThreadLocal<EntityManager>() {
        protected EntityManager initialValue() {
            return null;
        }
    };

    private static void pause(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            ex.hashCode();
        }
    }

    private static void log(String message) {
        System.out.flush();
        pause(5);
        System.err.println("[JPAUtil::Log] " + message);
        System.err.flush();
        pause(5);
    }

    public static synchronized void init() {
        log("Initialization of the entity manager factory");
        if (entityManagerFactory != null) {
            entityManagerFactory = null;
        }
        entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTANCE_UNIT_NAME);
    }

    public static synchronized void destroy() {
        log("Destroy of the entity manager factory");
        if (entityManagerFactory != null) {
            entityManagerFactory.close();
            entityManagerFactory = null;
        }
    }

    public static void createEntityManager() {
        log("Creation of entity manager");
        threadLocalEntityManager.set(entityManagerFactory.createEntityManager());
    }

    public static void closeEntityManager() {
        log("Close of the entity manager");
        EntityManager em = threadLocalEntityManager.get();
        em.close();
        threadLocalEntityManager.set(null);
    }

    public static void openTransaction() {
        log("Opening transaction on the entity manager");
        try {
            EntityManager em = threadLocalEntityManager.get();
            em.getTransaction().begin();
        } catch (Exception ex) {
            log("Error opening transaction");
            throw ex;
        }
    }

    public static void commitTransaction() throws RollbackException {
        log("Commit transaction");
        try {
            EntityManager em = threadLocalEntityManager.get();
            em.getTransaction().commit();
        } catch (Exception ex) {
            log("Error commiting transaction");
            throw ex;
        }
    }

    public static void rollbackTransaction() {
        try {
            log("Rolling back transaction");

            EntityManager em = threadLocalEntityManager.get();
            if (em.getTransaction().isActive()) {
                log("Rolling back an active transaction");
                em.getTransaction().rollback();
            }
        } catch (Exception ex) {
            log("Error rolling back transaction");
            throw ex;
        }
    }

    protected static EntityManager getEntityManager() {
        return threadLocalEntityManager.get();
    }

}
