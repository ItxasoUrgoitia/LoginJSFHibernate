import dataAccess.HibernateDataAccess;

public class TestHibernate {
    public static void main(String[] args) {
        System.out.println("=== TEST HIBERNATE ===");
        try {
            HibernateDataAccess dao = new HibernateDataAccess();
            dao.test();
            System.out.println("✅ TODO OK");
        } catch (Exception e) {
            System.err.println("❌ ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}