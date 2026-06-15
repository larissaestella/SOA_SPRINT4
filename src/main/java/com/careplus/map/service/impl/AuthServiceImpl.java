package com.careplus.map.service.impl;

import com.careplus.map.exception.RegraNegocioException;
import com.careplus.map.model.dto.UsuarioResponseDTO;
import com.careplus.map.model.dto.auth.LoginRequestDTO;
import com.careplus.map.model.dto.auth.LoginResponseDTO;
import com.careplus.map.model.dto.auth.RegisterRequestDTO;
import com.careplus.map.model.entity.Avatar;
import com.careplus.map.model.entity.Usuario;
import com.careplus.map.model.enums.PerfilUsuario;
import com.careplus.map.repository.UsuarioRepository;
import com.careplus.map.security.JwtService;
import com.careplus.map.service.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementação do serviço de autenticação.
 * Gerencia registro de novos usuários e autenticação via JWT.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public UsuarioResponseDTO registrar(RegisterRequestDTO dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RegraNegocioException("Já existe um usuário com o e-mail: " + dto.getEmail());
        }

        Usuario usuario = Usuario.builder()
                .nome(dto.getNome())
                .email(dto.getEmail())
                .senha(passwordEncoder.encode(dto.getSenha()))
                .dataNascimento(dto.getDataNascimento())
                .perfil(PerfilUsuario.USUARIO)
                .build();

        Avatar avatar = Avatar.builder()
                .usuario(usuario)
                .nomeAvatar(dto.getNomeAvatar())
                .nivel(1)
                .pontosTotal(0)
                .saude(0)
                .hidratacao(0)
                .sono(0)
                .exercicio(0)
                .bemEstar(0)
                .build();

        usuario.setAvatar(avatar);
        Usuario salvo = usuarioRepository.save(usuario);

        return UsuarioResponseDTO.builder()
                .id(salvo.getId())
                .nome(salvo.getNome())
                .email(salvo.getEmail())
                .dataNascimento(salvo.getDataNascimento())
                .criadoEm(salvo.getCriadoEm())
                .build();
    }

    @Override
    public LoginResponseDTO autenticar(LoginRequestDTO dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getSenha())
        );

        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RegraNegocioException("Usuário não encontrado"));

        String token = jwtService.gerarToken(usuario.getEmail());

        return LoginResponseDTO.builder()
                .token(token)
                .tipo("Bearer")
                .email(usuario.getEmail())
                .perfil(usuario.getPerfil().name())
                .build();
    }
}
