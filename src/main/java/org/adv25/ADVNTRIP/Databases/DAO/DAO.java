package org.adv25.ADVNTRIP.Databases.DAO;

public interface DAO<Entity, Key> {
    boolean create(Entity model);
    Entity read (Key key);
    boolean update(Entity model);
    boolean delete(Entity model);
}
