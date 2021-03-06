package velascogculebras.personalizedfitworkouts.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import velascogculebras.personalizedfitworkouts.Entities.Entrenador;
import velascogculebras.personalizedfitworkouts.Entities.Usuario;
import velascogculebras.personalizedfitworkouts.Repositories.EntrenadorRepository;
import velascogculebras.personalizedfitworkouts.Repositories.UsuarioReporsitory;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;

@Controller
public class ModifyTrainerProfileController {
    @Autowired
    public EntrenadorRepository entrenadorRepository;
    //Falta coger la imagen y cambiarla por la antigua
    @PostMapping("/modifyTrainerProfile")
    @CacheEvict(value = {"profile", "trainer"})
    public String saveProfile(Model model, HttpSession session, @RequestParam String name,
                               @RequestParam String passwordHash, @RequestParam String email,
                               @RequestParam String bio, @RequestParam("fileImage") MultipartFile fileImage) throws IOException {
        Usuario usuario = (Usuario) session.getAttribute("user");
        if(usuario.getRoles().contains("ROLE_TRAINER")){
            Entrenador trainer = (Entrenador) session.getAttribute("user");
            if (!name.isEmpty()) {
                trainer.setName(name);
            }
            if (!passwordHash.isEmpty()) {
                trainer.setPasswordHash(passwordHash);
            }
            if (!email.isEmpty()) {
                trainer.setMail(email);
            }
            if (!bio.isEmpty()) {
                trainer.setBiografia(bio);
            }
            entrenadorRepository.save(trainer);
            session.setAttribute("user", trainer);

            if (!fileImage.getOriginalFilename().equals("")) {

                if (trainer.getProfileIcon() != null) {
                    Resource res = new ClassPathResource("static" + trainer.getProfileIcon());
                    File file = res.getFile();
                    System.out.println(file.getAbsolutePath());
                    boolean status = file.delete();
                    if (status) {
                        System.out.println("Se ha eliminado");
                    }
                }

                String originalFilename = fileImage.getOriginalFilename();
                String extension = originalFilename.substring(originalFilename.lastIndexOf('.'), originalFilename.length());
                String newFilename = usuario.getId() + extension;
                Resource res = new ClassPathResource("static/trainers/images" + newFilename);
                File file = res.getFile();
                file.createNewFile();
                fileImage.transferTo(file);
                trainer.setProfileIcon(newFilename);
                entrenadorRepository.save(trainer);
            }


        }
        return "redirect:/";
    }
}

