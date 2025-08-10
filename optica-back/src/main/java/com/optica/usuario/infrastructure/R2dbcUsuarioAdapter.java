package com.optica.usuario.infrastructure;

import com.optica.usuario.domain.Usuario;
import com.optica.usuario.domain.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class R2dbcUsuarioAdapter implements UsuarioRepository {

    private final R2dbcUsuarioRepository repo;
    private final UsuarioCustomRepository customRepo;
    private final PasswordEncoder passwordEncoder;


    public R2dbcUsuarioAdapter(R2dbcUsuarioRepository repo, UsuarioCustomRepository customRepo, PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.customRepo = customRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Mono<Usuario> save(Usuario u) {
        var e = new R2dbcUsuarioEntity();
        e.setId(u.id());
        e.setNombre(u.nombre());
        e.setEmail(u.email());
        e.setRol_id(u.rol_id());
        String passwordHasheada = passwordEncoder.encode(u.contrasenaHash());
        e.setContrasenaHash(passwordHasheada);
        return repo.save(e).map(ent -> new Usuario(
            ent.getId(), ent.getNombre(), ent.getEmail(), ent.getRol_id(), ent.getContrasenaHash()));
    }

    @Override
    public Mono<Usuario> findById(Long id) {
        return repo.findById(id).map(ent -> new Usuario(
            ent.getId(), ent.getNombre(), ent.getEmail(), ent.getRol_id(), ent.getContrasenaHash()));
    }

    @Override
    public Flux<Usuario> findAll() {
        return repo.findAll().map(ent -> new Usuario(
            ent.getId(), ent.getNombre(), ent.getEmail(), ent.getRol_id(), ent.getContrasenaHash()));
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return repo.deleteById(id);
    }

    @Override
    public Mono<Usuario> findByEmail(String email) {
        return customRepo.findByEmail(email);
    }


}