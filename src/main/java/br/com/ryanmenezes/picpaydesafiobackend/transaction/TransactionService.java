package br.com.ryanmenezes.picpaydesafiobackend.transaction;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.ryanmenezes.picpaydesafiobackend.authorization.AuthorizerService;
import br.com.ryanmenezes.picpaydesafiobackend.notification.NotificationService;
import br.com.ryanmenezes.picpaydesafiobackend.wallet.Wallet;
import br.com.ryanmenezes.picpaydesafiobackend.wallet.WalletRepository;
import br.com.ryanmenezes.picpaydesafiobackend.wallet.WalletType;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final AuthorizerService authorizerService;
    private final NotificationService notificationService;

    public TransactionService(TransactionRepository transactionRepository, WalletRepository walletRepository, AuthorizerService authorizerService, NotificationService notificationService) {
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
        this.authorizerService = authorizerService;
        this.notificationService = notificationService;
    }

    public List<Transaction> getAll() {
        return transactionRepository.findAll();
    }

    @Transactional
    public Transaction create(Transaction transaction) {
        // 1 - validar
        validate(transaction);

        // 2 - criar transação
        var newTransaction = transactionRepository.save(transaction);

        // 3 - debitar da carteira
        var walletPayer = walletRepository.findById(transaction.payer()).get();
        walletRepository.save(walletPayer.debit(transaction.value()));

        var walletPayee = walletRepository.findById(transaction.payee()).get();
        walletRepository.save(walletPayee.credit(transaction.value()));

        // 4 - chamar serviços externos
        authorizerService.authorize(transaction);

        // 5 - notificação
        notificationService.notify(transaction);

        return newTransaction;
    }

    /**
     * - the payer has a commom wallet
     * - the payer has enough balance
     * - the payer is not the payee
     */
    private void validate(Transaction transaction) {
        walletRepository.findById(transaction.payee())
            .map(payee -> walletRepository.findById(transaction.payer())
                .map(payer -> isTransactionValid(transaction, payer) ? transaction : null)
                .orElseThrow(() -> new InvalidTransactionException("Invalid transaction - %s".formatted(transaction))))
            .orElseThrow(() -> new InvalidTransactionException("Invalid transaction - %s".formatted(transaction)));
    }

    private boolean isTransactionValid(Transaction transaction, Wallet payer) {
        return payer.type() == WalletType.COMUM.getValue() && 
            payer.balance().compareTo(transaction.value()) >= 0 &&
            !payer.id().equals(transaction.payee());
    }
}
