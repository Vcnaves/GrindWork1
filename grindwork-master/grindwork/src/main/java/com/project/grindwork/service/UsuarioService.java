package com.project.grindwork.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.project.grindwork.config.FileStorageProperties;
import com.project.grindwork.dto.DownloadDTO;
import com.project.grindwork.exceptions.AuthorizationException;
import com.project.grindwork.exceptions.ObjectBadRequestException;
import com.project.grindwork.exceptions.ObjectConflictException;
import com.project.grindwork.model.DadosUsuario;
import com.project.grindwork.model.Usuario;
import com.project.grindwork.repository.UsuarioRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Cloudinary cloudinary;

    private final Path fileStorageLocation;

    @Autowired
    private ImageService imageService;

    @Value("${img.prefix.client.profile}")
    private String prefix;

    @Value("${img.profile.size}")
    private Integer size;

    private final BCryptPasswordEncoder passwordEncoder;


    private UsuarioService(UsuarioRepository usuarioRepository, FileStorageProperties fileStorageProperties, Cloudinary cloudinary) {
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.usuarioRepository = usuarioRepository;
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();
        this.cloudinary = cloudinary;
    }

    public String cadastrarUsuario(DadosUsuario usuario) {
        if (!usuarioRepository.novoUsuario(usuario)) {
            throw new ObjectConflictException("Email já cadastrado!");
        }
        Long localidade_id = usuarioRepository.validaLocalidade(usuario.getLocalidade().getCidade(),
                usuario.getLocalidade().getEstado());
        if (localidade_id == 0) {
            localidade_id = usuarioRepository.cadastraLocalidade(usuario.getLocalidade().getCidade().trim(), usuario.getLocalidade().getEstado().trim());
        }
        usuario.getLocalidade().setId(localidade_id);
        usuario.setSenha(encriptarSenha(usuario.getSenha().trim()));
        return usuarioRepository.cadastraUsuario(usuario);
    }

    public String excluirUsuario(String email) {
        return usuarioRepository.excluirUsuario(email);
    }

    public ArrayList<DadosUsuario> dadosUsuario(String email) {
        return usuarioRepository.consultaDadosUsuario(email);
    }

    public String encriptarSenha(String senha) {
        return passwordEncoder.encode(senha);
    }

    public boolean verificarSenha(String senha, String senhaEncriptada) {
        return passwordEncoder.matches(senha, senhaEncriptada);
    }

    public String fotoPerfil(MultipartFile file) {
        Usuario user = UserService.authenticated();
    
        // Verifica se o usuário está autenticado
        if (user == null) {
            throw new AuthorizationException("Não autorizado!");
        }
    
        // Verifica se o arquivo é uma imagem
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new ObjectBadRequestException("Tipo de arquivo não permitido. Somente imagens são aceitas.");
        }
    
        // Processa a imagem (corta e redimensiona)
        BufferedImage jpgImage = imageService.getJpgImageFromFile(file);
        jpgImage = imageService.cropSquare(jpgImage);
        jpgImage = imageService.resize(jpgImage, size);
    
        try {
            // Converte a imagem BufferedImage em bytes
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(jpgImage, "jpg", baos);
            byte[] imageBytes = baos.toByteArray();
    
            // Faz o upload da imagem para o Cloudinary
            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = cloudinary.uploader().upload(imageBytes, ObjectUtils.emptyMap());
            
            // Obtém a URL da imagem
            String imageUrl = uploadResult.get("url").toString();
            
            // Atualiza a imagem do usuário no banco de dados
            Usuario usuario = usuarioRepository.findById(user.getId());
            usuario.setImagemUrl(imageUrl);
            usuarioRepository.atuaizaImagemUsuario(usuario);
    
            return "[{\"msg\":\"Upload completado! Download no link:\",\"imageUrl\":\"" + imageUrl + "\"}]";
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            throw new ObjectBadRequestException("Erro ao fazer upload da imagem");
        }
    }
    

    public DownloadDTO downloadFoto(String fileName, HttpServletRequest request) {
        DownloadDTO download = new DownloadDTO();
        Path filePath = fileStorageLocation.resolve(fileName).normalize();

        try {
            download.setResource(new UrlResource(filePath.toUri()));
            download.setContentType(
                    request.getServletContext().getMimeType(download.getResource().getFile().getAbsolutePath()));

            if (download.getContentType() == null) {
                download.setContentType("application/octet-stream");
            }
            return download;
        } catch (Exception e) {
            throw new ObjectBadRequestException("Não foi possivel fazer o download!");
        }
    }

    public List<String> listarFotos() {
        try {
            List<String> fileNames = Files.list(fileStorageLocation)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toList());

            return fileNames;
        } catch (IOException e) {
            throw new ObjectBadRequestException("Não foi possivel Listar as imagens!");
        }
    }

    public ArrayList<DadosUsuario> atualizaDadosUsuario(DadosUsuario usuario) {
        if (usuario.getId() < 0) {
            throw new ObjectBadRequestException("Usuario não encontrado.");
        }
        if(!usuario.getSenha().isEmpty()){
            String encrypt = encriptarSenha(usuario.getSenha().trim());
            usuario.setSenha(encrypt);
        }
        boolean atualizado = usuarioRepository.atualizarDadosUsuario(usuario);
        if (!atualizado) {
            throw new ObjectBadRequestException("Instabilidade ao atualizar informações. Tente novamente mais tarde!");
        }
        return usuarioRepository.consultaDadosUsuario(usuario.getEmail());
    }

}
