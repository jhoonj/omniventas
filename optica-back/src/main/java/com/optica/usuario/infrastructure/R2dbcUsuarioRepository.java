package com.optica.usuario.infrastructure;

import com.optica.usuario.domain.Usuario;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface R2dbcUsuarioRepository extends ReactiveCrudRepository<R2dbcUsuarioEntity, Long> {

}