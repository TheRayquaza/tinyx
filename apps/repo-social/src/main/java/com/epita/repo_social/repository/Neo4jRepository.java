package com.epita.repo_social.repository;

import com.epita.repo_social.RepoSocialErrorCode;
import com.epita.repo_social.repository.model.PostNode;
import com.epita.repo_social.repository.model.UserNode;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;

@ApplicationScoped
public class Neo4jRepository {
  @Inject public Driver driver;

  // create a node
  public void createOrUpdateNode(String cypherRequest) {
    try (Session session = driver.session()) {
      session.executeWrite(
          tx -> {
            tx.run(cypherRequest);
            return null;
          });
    } catch (Exception e) {
      throw RepoSocialErrorCode.ERROR_DURING_CYPHER_EXEC.createError(
          "Error while creating or updating node", e);
    }
  }

  // create a relationship
  public void createRelation(String cypherRequest) {
    try (Session session = driver.session()) {
      session.executeWrite(
          tx -> {
            tx.run(cypherRequest);
            return null;
          });
    } catch (Exception e) {
      throw RepoSocialErrorCode.ERROR_DURING_CYPHER_EXEC.createError(
          "Error while creating relation", e);
    }
  }

  // delete a node
  public void deleteNode(String cypherRequest) {
    try (Session session = driver.session()) {
      session.executeWrite(
          tx -> {
            tx.run(cypherRequest);
            return null;
          });
    } catch (Exception e) {
      throw RepoSocialErrorCode.ERROR_DURING_CYPHER_EXEC.createError(
          "Error while deleting node", e);
    }
  }

  // delete a relationship
  public void deleteRelation(String cypherRequest) {
    try (Session session = driver.session()) {
      session.executeWrite(
          tx -> {
            tx.run(cypherRequest);
            return null;
          });
    } catch (Exception e) {
      throw RepoSocialErrorCode.ERROR_DURING_CYPHER_EXEC.createError(
          "Error while deleting relation", e);
    }
  }

  // Check a node exists
  public boolean checkNodeExists(String cypherRequest) {
    try (Session session = driver.session()) {
      return session.executeRead(
          tx -> {
            var result = tx.run(cypherRequest);
            return result.hasNext();
          });
    } catch (Exception e) {
      throw RepoSocialErrorCode.ERROR_DURING_CYPHER_EXEC.createError(
          "Error while checking node existence", e);
    }
  }

  // check if a relationship exists
  public boolean checkRelationExists(String cypherRequest) {
    try (Session session = driver.session()) {
      return session.executeRead(
          tx -> {
            var result = tx.run(cypherRequest);
            return result.hasNext();
          });
    } catch (Exception e) {
      throw RepoSocialErrorCode.ERROR_DURING_CYPHER_EXEC.createError(
          "Error while checking relation existence", e);
    }
  }

  public UserNode getUser(String cypherRequest) {
    try (Session session = driver.session()) {
      return session.executeRead(
          tx -> {
            var result = tx.run(cypherRequest);
            if (result.hasNext()) {
              var userNode = result.single().get("u").asNode();
              return UserNode.from(userNode);
            }
            return null;
          });
    } catch (Exception e) {
      throw RepoSocialErrorCode.ERROR_DURING_CYPHER_EXEC.createError(
          "Error while fetching node", e);
    }
  }

  public PostNode getPost(String cypherRequest) {
    try (Session session = driver.session()) {
      return session.executeRead(
          tx -> {
            var result = tx.run(cypherRequest);
            if (result.hasNext()) {
              var postNode = result.single().get("p").asNode();
              return PostNode.from(postNode);
            }
            return null;
          });
    } catch (Exception e) {
      throw RepoSocialErrorCode.ERROR_DURING_CYPHER_EXEC.createError(
          "Error while fetching node", e);
    }
  }

  public List<UserNode> getUsers(String cypherQuery) {
    try (Session session = driver.session()) {
      return session.executeRead(
          tx -> {
            var result = tx.run(cypherQuery);
            return result.list(
                record -> {
                  var userNode = record.get("u").asNode();
                  return UserNode.from(userNode);
                });
          });
    } catch (Exception e) {
      throw RepoSocialErrorCode.ERROR_DURING_CYPHER_EXEC.createError(
          "Error while fetching likes", e);
    }
  }

  public List<PostNode> getPosts(String cypherQuery) {
    try (Session session = driver.session()) {
      return session.executeRead(
          tx -> {
            var result = tx.run(cypherQuery);
            return result.list(
                record -> {
                  var postNode = record.get("p").asNode();
                  return PostNode.from(postNode);
                });
          });
    } catch (Exception e) {
      throw RepoSocialErrorCode.ERROR_DURING_CYPHER_EXEC.createError(
          "Error while fetching likes", e);
    }
  }
}
