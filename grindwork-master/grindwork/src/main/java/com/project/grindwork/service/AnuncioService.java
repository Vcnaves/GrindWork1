package com.project.grindwork.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.project.grindwork.config.CloudinaryConfig;
import com.project.grindwork.config.FileStorageProperties;
import com.project.grindwork.dto.DownloadDTO;
import com.project.grindwork.dto.EnvioOrcamentoDTO;
import com.project.grindwork.exceptions.AuthorizationException;
import com.project.grindwork.exceptions.CustomErrorException;
import com.project.grindwork.exceptions.ObjectBadRequestException;
import com.project.grindwork.exceptions.ObjectNotFoundException;
import com.project.grindwork.model.Anuncio;
import com.project.grindwork.model.Avaliacao;
import com.project.grindwork.model.Usuario;
import com.project.grindwork.repository.AnuncioRepository;

import jakarta.servlet.http.HttpServletRequest;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AnuncioService {

  @Autowired
  private AnuncioRepository anuncioRepository;

  private final Cloudinary cloudinary;

  private Path fileStorageLocation;

  CustomErrorException errorException = null;

  @Autowired
  private ImageService imageService;

  @Autowired
  private EmailService emailService;

  @Value("${img.prefix.client.anun}")
  private String prefix;

  @Value("${img.profile.size}")
  private Integer size;

  private AnuncioService(AnuncioRepository anuncioRepository, FileStorageProperties fileStorageProperties, Cloudinary cloudinary) {
    this.anuncioRepository = anuncioRepository;
    this.cloudinary = cloudinary;
    this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDirAnun()).toAbsolutePath().normalize();
  }

  public String cadastrarAnuncio(Anuncio anuncio, MultipartFile imagem) {
    if (imagem != null) {
      anuncio.setImageUrl(fotoAnuncio(imagem));
    }
    return anuncioRepository.cadastrarAnuncio(anuncio);
  }

  public String atualizarAnuncio(Anuncio anuncio, MultipartFile imagem) {
    if (imagem != null) {
      anuncio.setImageUrl(fotoAnuncio(imagem));
    }
    return anuncioRepository.atualizarAnuncio(anuncio);
  }

  public String fotoAnuncio(MultipartFile file) {
    Usuario user = UserService.authenticated();

    if (user == null) {
      throw new AuthorizationException("Não autorizado!");
    }

    String contentType = file.getContentType();
    if (contentType == null || !contentType.startsWith("image/")) {
      throw new ObjectBadRequestException("Tipo de arquivo não permitido. Somente imagens são aceitas.");
    }

    BufferedImage jpgImage = imageService.getJpgImageFromFile(file);
    String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    String fileName = "anuncios/" + user.getId() + "_" + timestamp + ".jpg";

    // Converter BufferedImage para array de bytes
    byte[] imageBytes;
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      ImageIO.write(jpgImage, "jpg", baos);
      baos.flush();
      imageBytes = baos.toByteArray();
    } catch (IOException e) {
      throw new ObjectBadRequestException("Erro ao processar a imagem.");
    }

    try {
      @SuppressWarnings("unchecked")
      Map<String, Object> uploadResult = cloudinary.uploader().upload(imageBytes, ObjectUtils.asMap(
          "public_id", fileName,
          "resource_type", "image"));
      return uploadResult.get("url").toString();
    } catch (IOException e) {
      throw new ObjectBadRequestException("Erro ao fazer upload da imagem para o Cloudinary.");
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

  public ArrayList<Anuncio> listarAnuncio(long localidade_id) {
    if (!anuncioRepository.validaLocalidade(localidade_id)) {
      throw new ObjectNotFoundException("Localidade não encontrada!");
    }
    ArrayList<Anuncio> listarAnuncio = anuncioRepository.listarAnuncio(localidade_id);
    if (listarAnuncio == null) {
      throw new ObjectNotFoundException("Não encontrado anúncios para essa localidade!");
    }
    return listarAnuncio;
  }

  public String excluirAnuncio(Long id) {
    return anuncioRepository.excluirAnuncio(id);
  }

  public String inserirAvaliacao(Avaliacao avaliacao) {
    if (avaliacao.getNota() == 0) {
      throw new ObjectNotFoundException("Favor fornecer uma avaliação valida!");
    }
    String anuncio = null;
    if (anuncioRepository.findAnuncioById(avaliacao)) {
      anuncio = anuncioRepository.alterarAvaliacao(avaliacao);
    } else {
      anuncio = anuncioRepository.inserirAvaliacao(avaliacao);
    }

    if (anuncio != null) {
      ArrayList<Avaliacao> listaNotas = anuncioRepository.listarAvaliacoes(avaliacao.getAnuncioId());
      calcularMediaNotas(listaNotas, avaliacao.getAnuncioId());
    }

    return anuncio;
  }

  public ArrayList<Avaliacao> listarAvaliacao(long anuncio_id) {
    ArrayList<Avaliacao> listaAvaliacao = anuncioRepository.listarAvaliacoes(anuncio_id);
    if (listaAvaliacao == null) {
      throw new ObjectNotFoundException("Não encontrado avaliações para esse anúncio!");
    }
    return listaAvaliacao;
  }

  public Page<Anuncio> findPage(Integer page, Integer linesPerPage, String orderBy, String direction, String servico,
      String localidade) {
    Sort sort = Sort.by(Sort.Direction.fromString(direction), orderBy);
    PageRequest pageRequest = PageRequest.of(page, linesPerPage, sort);
    return anuncioRepository.findAll(pageRequest, servico, localidade);
  }

  public void sendNewBudget(EnvioOrcamentoDTO cliente) {
    emailService.sendNewBudgetEmail(cliente);
  }

  public Page<Anuncio> findMyAnuncioById(Integer page, Integer linesPerPage, String orderBy, String direction,
      String servico, Anuncio anuncio) {
    Sort sort = Sort.by(Sort.Direction.fromString(direction), orderBy);
    PageRequest pageRequest = PageRequest.of(page, linesPerPage, sort);
    return anuncioRepository.findMyAnuncioById(pageRequest, servico, anuncio);
  }

  public void calcularMediaNotas(List<Avaliacao> avaliacoes, Long anuncio_id) {
    double notaTotal = avaliacoes.stream()
        .mapToDouble(Avaliacao::getNota)
        .average()
        .orElse(0.0);
    anuncioRepository.atualizaNotaTotalAnuncio(notaTotal, anuncio_id);
  }

}
