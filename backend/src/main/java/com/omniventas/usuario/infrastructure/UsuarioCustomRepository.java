package com.omniventas.usuario.infrastructure;



import com.omniventas.usuario.domain.Usuario;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class UsuarioCustomRepository {

    private final R2dbcEntityTemplate template;

    public UsuarioCustomRepository(R2dbcEntityTemplate template) {
        this.template = template;
    }
    public Mono<Usuario> findByEmail(String email) {
        return template.select(R2dbcUsuarioEntity.class)
                .matching(Query.query(Criteria.where("email").is(email)))
                .one()
                .map(e -> new Usuario(
                        e.getId(),
                        e.getNombre(),
                        e.getEmail(),
                        e.getRol_id(),
                        e.getContrasenaHash()
                ));
    }
}
