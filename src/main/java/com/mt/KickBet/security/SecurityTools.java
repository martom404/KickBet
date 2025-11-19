package com.mt.KickBet.security;

import com.mt.KickBet.model.entity.Role;
import com.mt.KickBet.model.entity.User;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

public class SecurityTools {

    public static User getUser() {
        SecurityContext sc = SecurityContextHolder.getContext();

        if (sc != null
                && sc.getAuthentication() != null
                && sc.getAuthentication().getPrincipal() instanceof User)
        {
            return (User) sc.getAuthentication().getPrincipal();
        }
        return null;
    }

    public static Boolean hasRole(Role role) {
        if (getUser() == null) return false;

        return role.equals(getUser().getRole());
    }

    public static Boolean hasRole(List<Role> roles) {
        if (getUser() == null) return false;

        return roles.contains(getUser().getRole());
    }
}