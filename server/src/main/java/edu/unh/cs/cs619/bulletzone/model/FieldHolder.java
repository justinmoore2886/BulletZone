package edu.unh.cs.cs619.bulletzone.model;

import com.google.common.base.Optional;

import java.util.HashMap;
import java.util.Map;

import edu.unh.cs.cs619.bulletzone.model.terrain.Empty;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * <h1> FieldHolder Class! </h1>
 * This class consists of getters and setter for entities in the field.
 * @author given
 * @version 1.0
 * @since Halloween
 */
public class FieldHolder {

    private final Map<Direction, FieldHolder> neighbors = new HashMap<Direction, FieldHolder>();
    private Optional<FieldEntity> entityHolder = Optional.absent();

    public void addNeighbor(Direction direction, FieldHolder fieldHolder) {
        neighbors.put(checkNotNull(direction), checkNotNull(fieldHolder));
    }

    public FieldHolder getNeighbor(Direction direction) {
        return neighbors.get(checkNotNull(direction,
                "Direction cannot be null."));
    }

    public boolean isPresent() {
        return entityHolder.isPresent();
    }


    public FieldEntity getEntity() {
        return entityHolder.get();
    }

    public void setFieldEntity(FieldEntity entity) {
        if(entityHolder.isPresent()){clearField();}
        if(entity == null){
            entity = new Empty();
            entityHolder = Optional.of(checkNotNull(entity,
                "FieldEntity cannot be null."));
        }
        entityHolder = Optional.of(checkNotNull(entity,
                "FieldEntity cannot be null."));
        entity.setParent(this);
    }

    public void clearField() {
        if (entityHolder.isPresent()) {
            entityHolder = Optional.absent();
        }
    }

}
