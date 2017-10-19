import java.util.Comparator;

/**
 * Holds the types of users (agent and ATM)
 */
public enum UserType {
    ATM("atm", 0),
    AGENT("agent", 1);

    private final int userTypeID;
    private final String userTypeName;

    UserType(String name, int id) {
        this.userTypeID = id;
        this.userTypeName = name;
    }

    public int getID() {
        return this.userTypeID;
    }

    public String getName() { return this.userTypeName; }

    public boolean equals(UserType user) {
        if (userTypeID == user.getID())
            return true;
        else
            return false;
    }
}
