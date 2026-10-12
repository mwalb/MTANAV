package tz.mtanav.withdrawal;

import org.springframework.stereotype.Service;
import tz.mtanav.earning.Commission;
import tz.mtanav.earning.CommissionRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
public class WithdrawalService {

    private final WithdrawalDestinationRepository destinationRepository;
    private final WithdrawalRequestRepository requestRepository;
    private final CommissionRepository commissionRepository;

    public WithdrawalService(
            WithdrawalDestinationRepository destinationRepository,
            WithdrawalRequestRepository requestRepository,
            CommissionRepository commissionRepository
    ) {
        this.destinationRepository = destinationRepository;
        this.requestRepository = requestRepository;
        this.commissionRepository = commissionRepository;
    }

    public List<WithdrawalDestination> getDestinations(Long userId) {
        List<WithdrawalDestination> list = destinationRepository.findByUserId(userId);
        if (list.isEmpty()) {
            WithdrawalDestination d = new WithdrawalDestination();
            d.setUserId(userId);
            d.setProviderName("M_PESA");
            d.setAccountNumber("+255711123456");
            d.setAccountHolderName("Juma Salum");
            d.setIsDefault(true);
            list.add(destinationRepository.save(d));
        }
        return list;
    }

    public WithdrawalDestination addDestination(Long userId, String providerName, String accountNumber, String accountHolderName) {
        WithdrawalDestination d = new WithdrawalDestination();
        d.setUserId(userId);
        d.setProviderName(providerName);
        d.setAccountNumber(accountNumber);
        d.setAccountHolderName(accountHolderName);
        d.setIsDefault(destinationRepository.findByUserId(userId).isEmpty());
        return destinationRepository.save(d);
    }

    public List<WithdrawalRequest> getRequests(Long userId) {
        return requestRepository.findByUserId(userId);
    }

    public WithdrawalRequest requestWithdrawal(Long userId, Long destinationId, BigDecimal amount) {
        WithdrawalRequest req = new WithdrawalRequest();
        req.setUserId(userId);
        req.setDestinationId(destinationId);
        req.setAmount(amount);
        BigDecimal fee = amount.multiply(new BigDecimal("0.01")); // 1% provider fee
        req.setFee(fee);
        req.setNetAmount(amount.subtract(fee));
        req.setStatus("REQUESTED");
        req.setTransactionReference("TXN-" + System.currentTimeMillis());
        return requestRepository.save(req);
    }
}
