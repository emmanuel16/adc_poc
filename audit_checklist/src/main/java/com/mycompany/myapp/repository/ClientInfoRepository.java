package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.ClientInfo;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the ClientInfo entity.
 */
@SuppressWarnings("unused")
public interface ClientInfoRepository extends JpaRepository<ClientInfo,Long> {

}
