package com.appfountain.external;


import com.appfountain.model.User;

import java.util.List;

public class UsersSource extends BaseSource{
	private List<User> users;

    public List<User> getUsers() {
        return users;
    }
}
