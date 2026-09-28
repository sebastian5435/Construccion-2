package application.domain.ports.output;

import application.domain.models.Return;
import java.util.List;
import java.util.Optional;

public interface ReturnRepositoryPort {
    Return save(Return returnProcess);
    Optional<Return> findById(String id);
    Optional<Return> findByOrderId(String orderId);
    List<Return> findAll();
    void deleteById(String id);
}
