package com.example.tienda.service.impl;

import com.example.tienda.dto.auth.EmpleadoResponseDTO;
import com.example.tienda.dto.auth.RolResponseDTO;
import com.example.tienda.dto.auth.UsuarioRequestDTO;
import com.example.tienda.dto.auth.UsuarioResponseDTO;
import com.example.tienda.entity.Empleado;
import com.example.tienda.entity.Rol;
import com.example.tienda.entity.Usuario;
import com.example.tienda.exception.RecursosNoEncontradoException;
import com.example.tienda.exception.ReglaNegocioException;
import com.example.tienda.mapper.UsuarioMapper;
import com.example.tienda.repository.EmpleadoRepository;
import com.example.tienda.repository.RolRepository;
import com.example.tienda.repository.UsuarioRepository;
import com.example.tienda.service.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl implements UsuarioService {
    private static final Logger log = LoggerFactory.getLogger(UsuarioServiceImpl.class);

    private final UsuarioRepository usuarioRepository;
    private final EmpleadoRepository empleadoRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper;

    public UsuarioServiceImpl(
            UsuarioRepository usuarioRepository,
            EmpleadoRepository empleadoRepository,
            RolRepository rolRepository,
            PasswordEncoder passwordEncoder,
            UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.empleadoRepository = empleadoRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
        this.usuarioMapper = usuarioMapper;
    }

    @Override
    @Transactional
    public UsuarioResponseDTO crear(UsuarioRequestDTO request) {
        String username = request.getUsername().trim().toLowerCase(Locale.ROOT);
        log.info("Registrando usuario={} empleadoId={} rolIds={}",
                username, request.getEmpleadoId(), request.getRolIds());

        Empleado empleado = empleadoRepository.findById(request.getEmpleadoId())
                .orElseThrow(() -> new RecursosNoEncontradoException(
                        "Empleado no encontrado con id: " + request.getEmpleadoId()));
        if (!Boolean.TRUE.equals(empleado.getEstado())) {
            throw new ReglaNegocioException(
                    "El empleado " + empleado.getNombreCompleto() + " está inactivo");
        }
        if (usuarioRepository.existsByEmpleadoId(empleado.getId())) {
            throw new ReglaNegocioException(
                    "El empleado " + empleado.getNombreCompleto() + " ya tiene un usuario");
        }
        if (usuarioRepository.existsByUsernameIgnoreCase(username)) {
            throw new ReglaNegocioException("Ya existe un usuario con el nombre: " + username);
        }
        if (request.getPassword().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ReglaNegocioException("La contraseña no puede superar 72 bytes");
        }

        List<Rol> roles = rolRepository.findAllById(request.getRolIds());
        if (roles.size() != request.getRolIds().size()) {
            Set<Long> missingIds = new HashSet<>(request.getRolIds());
            roles.stream().map(Rol::getId).forEach(missingIds::remove);
            throw new RecursosNoEncontradoException("Roles no encontrados: " + missingIds);
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setEstado(true);
        usuario.setEmpleado(empleado);
        usuario.setRoles(new HashSet<>(roles));

        Usuario saved = usuarioRepository.save(usuario);
        log.info("Usuario registrado id={} usuario={} roles={}",
                saved.getId(), username, usuarioMapper.nombresRoles(roles));
        return usuarioMapper.toUsuarioResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listar() {
        return usuarioRepository.findAll(Sort.by("username"))
                .stream()
                .map(usuarioMapper::toUsuarioResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmpleadoResponseDTO> listarEmpleados() {
        Set<Long> employeeIdsWithUser = usuarioRepository.findEmpleadoIdsConUsuario();
        return empleadoRepository.findAll(Sort.by("apellidos", "nombres"))
                .stream()
                .map(employee -> usuarioMapper.toEmpleadoResponse(
                        employee, employeeIdsWithUser.contains(employee.getId())))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RolResponseDTO> listarRoles() {
        return rolRepository.findAll(Sort.by("nombre"))
                .stream()
                .map(usuarioMapper::toRolResponse)
                .toList();
    }
}
