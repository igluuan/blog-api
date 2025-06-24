package com.devluan.blog_api.application.dto.user.request;

public record UserRegisterRequest(String username,
                                  String email,
                                  String password) {

    public void validate(){
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username não pode ser vazio");
        }
        if (email == null || !email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new IllegalArgumentException("Email inválido");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Senha deve ter no mínimo 6 caracteres");
        }

    }
}
