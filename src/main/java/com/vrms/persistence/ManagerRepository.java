package com.vrms.persistence;

import com.vrms.domain.Manager;

public interface ManagerRepository {

    Manager findByUsername(String username);
}