package application.adapters.persistence.jpa.repositories;

import application.adapters.persistence.jpa.entities.WarehouseEntity;
import application.domain.valueobjects.WarehouseType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataWarehouseRepository extends JpaRepository<WarehouseEntity, String> {

    List<WarehouseEntity> findByWarehouseType(WarehouseType warehouseType);

    List<WarehouseEntity> findBySellerIdentifier(String sellerId);
}
