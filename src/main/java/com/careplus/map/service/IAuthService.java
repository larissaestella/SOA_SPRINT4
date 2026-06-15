package com.careplus.map.service;

import com.careplus.map.model.dto.auth.LoginRequestDTO;
import com.careplus.map.model.dto.auth.LoginResponseDTO;
import com.careplus.map.model.dto.auth.RegisterRequestDTO;
import com.careplus.map.model.dto.UsuarioResponseDTO;

/**
 * Contrato de serviço para autenticação e registro de usuários.
 */
public interface IAuthService {

    LoginResponseDTO autenticar(LoginRequestDTO dto);

    UsuarioResponseDTO registrar(RegisterRequestDTO dto);
}
