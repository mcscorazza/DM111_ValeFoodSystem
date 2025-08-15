package br.inatel.pos.dm111.vfa.persistance.user;

public record User(String id, String name, String email, String password, UserType type) {
    public enum UserType { REGULAR, RESTAURANT }
}
