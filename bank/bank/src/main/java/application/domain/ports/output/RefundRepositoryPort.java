package application.domain.ports.output;

import application.domain.models.Refund;
import java.util.List;
import java.util.Optional;

public interface RefundRepositoryPort {
    Refund save(Refund refund);
    Optional<Refund> findById(String id);
    Optional<Refund> findByReturnId(String returnId);
    List<Refund> findAll();
    void deleteById(String id);
}
