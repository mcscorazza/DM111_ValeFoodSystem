package br.inatel.pos.dm111.vfu.persistance.user;

import java.util.List;

public record User(String id, String name, String email, String password, UserType type, List<String> categories) {
    public enum UserType { REGULAR, RESTAURANT }
}
