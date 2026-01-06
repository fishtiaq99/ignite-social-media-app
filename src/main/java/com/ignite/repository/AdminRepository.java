package com.ignite.repository;

import com.ignite.model.Admin;

public interface AdminRepository {
    Admin findByUsername(String username);

}
