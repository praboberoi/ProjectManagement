package nz.ac.canterbury.seng302.portfolio.model;

import javax.persistence.*;

import nz.ac.canterbury.seng302.portfolio.utils.UserField;


/**
 * Creates a Sort class to store the users sorting prefernce in the database
 */
@Entity
public class PersistentSort {

    @Id
    @Column(nullable = false)
    private int userId;

    @Column(nullable = false)
    private UserField userListSortBy = UserField.FIRSTNAME;

    /**
     * True for ascending order
     */
    @Column(nullable = false)
    private boolean userListOrder = true;

    /**
     * Constructor for the Sprint class without arguments
     */
    public PersistentSort() {}

    public PersistentSort(int userId) {
        this.userId = userId;
    }


    public UserField getUserListSortBy() {
        return userListSortBy;
    }

    public boolean isUserListAscending() {
        return userListOrder;
    }

    public void setUserListSortBy(UserField userListSortBy) {
        this.userListSortBy = userListSortBy;
    }

    public void setIsAscendingOrder(boolean userListOrder) {
        this.userListOrder = userListOrder;
    }


}
