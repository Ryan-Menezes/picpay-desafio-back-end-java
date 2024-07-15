DELETE FROM transactions;
DELETE FROM wallets;

INSERT INTO wallets 
    (id, full_name, cpf, email, password, type, balance) 
VALUES
    (1, 'Joao - User', '12345678900', 'joao@test.com', '123456', 1, 1000.00),
    (2, 'Maria - Lojista', '12345678901', 'maria@test.com', '123456', 2, 1000.00);