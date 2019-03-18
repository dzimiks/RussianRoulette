package app.server;

public class Resources {

    private int tableSeats = 6;
    private User[] users;

    public Resources() {
        this.users = new User[this.tableSeats];
    }

    public synchronized Boolean giveSeat(User user) {
        for (int i = 0; i < tableSeats; i++) {
            if (users[i] == null) {
                users[i] = user;
                return true;
            }
        }

        return false;
    }
}
