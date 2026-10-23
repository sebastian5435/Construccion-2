package application.adapters.persistence.inmemory;

import application.domain.models.Refund;
import application.domain.ports.output.RefundRepositoryPort;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("inmemory")
public class InMemoryRefundRepository implements RefundRepositoryPort {

    private final Map<String, Refund> refunds = new ConcurrentHashMap<>();

    @Override
    public Refund save(Refund refund) {
        if (refund.getId() == null) {
            refund.setId(UUID.randomUUID().toString());
        }
        refunds.put(refund.getId(), refund);
        return refund;
    }

    @Override
    public Optional<Refund> findById(String id) {
        return Optional.ofNullable(refunds.get(id));
    }

    @Override
    public Optional<Refund> findByReturnId(String returnId) {
        return refunds.values().stream()
                .filter(r -> r.getReturnProcess() != null
                        && returnId.equals(r.getReturnProcess().getId()))
                .findFirst();
    }

    @Override
    public List<Refund> findAll() {
        return new ArrayList<>(refunds.values());
    }

    @Override
    public void deleteById(String id) {
        refunds.remove(id);
    }
}
