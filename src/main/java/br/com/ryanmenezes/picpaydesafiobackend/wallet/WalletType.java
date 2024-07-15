package br.com.ryanmenezes.picpaydesafiobackend.wallet;

public enum WalletType {
    COMUM(1), LOJISTA(2);

    private final int value;

    private WalletType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static WalletType fromValue(int value) {
        for (WalletType type : values()) {
            if (type.value == value) {
                return type;
            }
        }
        
        throw new IllegalArgumentException("Unexpected value: " + value);
    }
}
