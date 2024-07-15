CREATE TABLE IF NOT EXISTS wallets (
    id SERIAL PRIMARY KEY,
    full_name VARCHAR(100),
    cpf CHAR(11) UNIQUE,
    email VARCHAR(100) UNIQUE,
    password VARCHAR(100),
    type INT,
    balance DECIMAL(10, 2)
);

CREATE TABLE IF NOT EXISTS transactions (
    id SERIAL PRIMARY KEY,
    payer INT,
    payee INT,
    value DECIMAL(10, 2),
    created_at TIMESTAMP,
    FOREIGN KEY (payer) REFERENCES wallets(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (payee) REFERENCES wallets(id) ON DELETE CASCADE ON UPDATE CASCADE
);