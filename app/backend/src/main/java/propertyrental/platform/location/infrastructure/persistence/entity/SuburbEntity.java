package propertyrental.platform.location.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "suburbs")
@Getter
@Setter
@NoArgsConstructor
public class SuburbEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Plain column instead of an entity link (the service checks that the city exists)
    @Column(name = "city_id", nullable = false)
    private Long cityId;

    // This sets the length to match the migration
    @Column(name = "name", nullable = false, length = 100)
    private String name;
}
