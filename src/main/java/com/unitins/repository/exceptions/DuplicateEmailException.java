package com.unitins.repository.exceptions;

// Exceção customizada para indicar que um email já existe no banco de dados
public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String email) {
        super("O email '" + email + "' já está cadastrado.");
    }

    public DuplicateEmailException(String email, Throwable cause) {
        super("O email '" + email + "' já está cadastrado.", cause);
    }
}
