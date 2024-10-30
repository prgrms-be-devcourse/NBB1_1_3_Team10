package core.application.users.models.entities;

import jakarta.persistence.*;
import java.util.*;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
@Entity
@Table(name = "dib_table")
public class DibEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long   dibId;

    @Column(nullable = false, columnDefinition = "binary(16)", length = 16)
    private UUID   userId;

    @Column(nullable = false, length = 50)
    private String movieId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DibEntity dibEntity = (DibEntity) o;
        return Objects.equals(dibId, dibEntity.dibId) && Objects.equals(userId,
                dibEntity.userId) && Objects.equals(movieId, dibEntity.movieId);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(dibId);
        result = 31 * result + Objects.hashCode(userId);
        result = 31 * result + Objects.hashCode(movieId);
        return result;
    }
}
