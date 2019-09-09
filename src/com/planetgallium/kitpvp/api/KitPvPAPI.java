package com.planetgallium.kitpvp.api;

import com.planetgallium.kitpvp.api.Managers.UserManager;

public class KitPvPAPI {

    private UserManager userManager;

    public KitPvPAPI() { userManager = new UserManager(); }

    public UserManager getUserManager() { return userManager; }

}
