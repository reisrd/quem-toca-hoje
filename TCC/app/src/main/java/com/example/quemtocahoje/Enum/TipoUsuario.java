package com.example.quemtocahoje.Enum;

public enum TipoUsuario {
    ESPECTADOR(1)
    ,ESTABELECIMENTO(2)
    ,MUSICO(3)
    ,BANDA(4)
    ,ADMIN(5);

    private final int valor;

    TipoUsuario(int valor) {
        this.valor = valor;
    }

    public int getValor() {
        return valor;
    }
}
