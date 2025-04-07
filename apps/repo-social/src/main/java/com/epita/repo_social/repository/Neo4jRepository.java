package com.epita.repo_social.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;

@ApplicationScoped
public class Neo4jRepository {
    @Inject
    public Driver driver;

    // create a node
    public void createNode(String cypherRequest) {
        try (Session session = driver.session()) {
            session.executeWrite(tx -> {
                tx.run(cypherRequest);
                return null;
            });
        } catch (Exception e) {
            throw new RuntimeException("Error while creating node", e);
        }
    }

    // create a relationship
    public void createRelation(String cypherRequest) {
        try (Session session = driver.session()) {
            int createdRelationships = session
                    .executeWrite(tx -> tx.run(cypherRequest)
                            .consume()
                            .counters()
                            .relationshipsCreated());

            if (createdRelationships == 0) {
                throw new RuntimeException("No relationships were created");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while creating relation", e);
        }
    }

    // delete a node
    public void deleteNode(String cypherRequest) {
        try (Session session = driver.session()) {
            session.executeWrite(tx -> {
                tx.run(cypherRequest);
                return null;
            });
        } catch (Exception e) {
            throw new RuntimeException("Error while deleting node", e);
        }
    }

    // delete a relationship
    public void deleteRelation(String cypherRequest) {
        try (Session session = driver.session()) {
            int deletedRelationships = session
                    .executeWrite(tx -> tx.run(cypherRequest)
                            .consume()
                            .counters()
                            .relationshipsDeleted());

            if (deletedRelationships == 0) {
                throw new RuntimeException("No relationships were deleted");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while deleting relation", e);
        }
    }


    // Check a node exists
    public boolean checkNodeExists(String cypherRequest) {
        try (Session session = driver.session()) {
            return session.executeRead(tx -> {
                var result = tx.run(cypherRequest);
                return result.hasNext();
            });
        } catch (Exception e) {
            throw new RuntimeException("Error while checking node existence", e);
        }
    }

    // check if a relationship exists
    public boolean checkRelationExists(String cypherRequest) {
        try (Session session = driver.session()) {
            return session.executeRead(tx -> {
                var result = tx.run(cypherRequest);
                return result.hasNext();
            });
        } catch (Exception e) {
            throw new RuntimeException("Error while checking relation existence", e);
        }
    }
}
