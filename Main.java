import java.util.List;

public class Main {
    public static void main(String[] args) {

        DBManager manager = new DBManager();
        manager.initialize("db");

        User u1 = new User("John", "Doe", "johndoe@gmail.com");
        User u2 = new User("Jane", "Doe", "janedoe@gmail.com");
        User u3 = new User("Alan", "Turing", "alanturing@gmail.com");
        User u4 = new User("Claude", "Shannon", "claudeshannon@gmail.com");

        manager.addUser(u1);
        manager.addUser(u2);
        manager.addUser(u3);
        manager.addUser(u4);

        Filter f = new Filter();
        f.addFilter("id", 2);
        User user = manager.queryUser(f);

        System.out.println("Retrieved by Query:");
        System.out.println(" - " + user);

        user.setFirstName("Jenifer");
        System.out.println(manager.updateUser(user));

        f = new Filter();
        // f.addFilter("id", 2);
        List<User> new_users = manager.queryUsers(f);

        System.out.println("Retrieved by Query:");
        for (User u : new_users) {
            System.out.println(" - " + u);
        }
    }
}