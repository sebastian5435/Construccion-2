package application.adapters.persistence.inmemory;

import application.domain.models.Return;
import application.domain.ports.output.ReturnRepositoryPort;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryReturnRepository implements ReturnRepositoryPort {

    private final Map<String, Return> returns = new ConcurrentHashMap<>();

    @Override
    public Return save(Return returnProcess) {
        if (returnProcess.getId() == null) {
            returnProcess.setId(UUID.randomUUID().toString());
        }
        returns.put(returnProcess.getId(), returnProcess);
        return returnProcess;
    }

    @Override
    public Optional<Return> findById(String id) {
        return Optional.ofNullable(returns.get(id));
    }

    @Override
    public Optional<Return> findByOrderId(String orderId) {
        return returns.values().stream()
                .filter(r -> r.getOrder() != null
                        && orderId.equals(r.getOrder().getId()))
                .findFirst();
    }

    @Override
    public List<Return> findAll() {
        return new ArrayList<>(returns.values());
    }

    @Override
    public void deleteById(String id) {
        returns.remove(id);
    }
}
