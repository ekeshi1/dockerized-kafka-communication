package org.example.dao;

import org.example.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ClientsRepository extends JpaRepository<Client, Long> {

}
