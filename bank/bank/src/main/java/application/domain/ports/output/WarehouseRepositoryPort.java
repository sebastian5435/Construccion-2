package application.domain.ports.output;

import application.domain.models.Warehouse;
import application.domain.valueobjects.WarehouseType;
import java.util.List;
import java.util.Optional;

public interface WarehouseRepositoryPort {
    Warehouse save(Warehouse warehouse);
    Optional<Warehouse> findById(String id);
    List<Warehouse> findAll();
    List<Warehouse> findByType(WarehouseType type);
    List<Warehouse> findBySellerId(String sellerId);
    void deleteById(String id);
}
