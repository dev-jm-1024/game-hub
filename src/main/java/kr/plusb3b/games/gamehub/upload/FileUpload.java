package kr.plusb3b.games.gamehub.upload;

import com.cloudinary.*;
import com.cloudinary.utils.ObjectUtils;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class FileUpload {
// Set your Cloudinary credentials

    Dotenv dotenv = Dotenv.load();
    Cloudinary cloudinary = new Cloudinary(dotenv.get("CLOUDINARY_URL"));

}
