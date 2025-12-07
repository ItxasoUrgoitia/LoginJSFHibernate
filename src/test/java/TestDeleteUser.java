

import businessLogic.BLFacade;
import eredua.bean.FacadeBean;

public class TestDeleteUser {
    public static void main(String[] args) {
        System.out.println("=== TESTING DELETE USER DIRECTLY ===");
        
        try {
            BLFacade facade = FacadeBean.getBusinessLogic();
            String testEmail = "ama@gmail.com";
            
            // 1. Check if user exists
            System.out.println("1. Checking if user exists: " + testEmail);
            boolean exists = facade.userExists(testEmail);
            System.out.println("   User exists: " + exists);
            
            // 2. Try to delete
            System.out.println("\n2. Attempting to delete user...");
            boolean result = facade.deleteUser(testEmail);
            System.out.println("   Delete result: " + result);
            
            // 3. Check again
            System.out.println("\n3. Checking again if user exists...");
            exists = facade.userExists(testEmail);
            System.out.println("   User still exists: " + exists);
            
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}