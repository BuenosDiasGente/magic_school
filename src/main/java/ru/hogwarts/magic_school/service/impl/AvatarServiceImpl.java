package ru.hogwarts.magic_school.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.magic_school.model.Avatar;
import ru.hogwarts.magic_school.model.Student;
import ru.hogwarts.magic_school.repository.AvatarRepository;
import ru.hogwarts.magic_school.service.AvatarService;
import ru.hogwarts.magic_school.service.StudentService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static java.nio.file.StandardOpenOption.CREATE_NEW;
import static ru.hogwarts.magic_school.constant.Constant.LOGGER_METHOD_ADD;
import static ru.hogwarts.magic_school.constant.Constant.LOGGER_METHOD_GET;

@Service
@RequiredArgsConstructor
@Transactional
public class AvatarServiceImpl implements AvatarService {
    private final AvatarRepository avatarRepository;
    private final StudentService studentService;
    private final Logger logger = LoggerFactory.getLogger(AvatarServiceImpl.class);


    @Value(value = "${path.to.avatars.folder}")
    private String avatarsDir;


    @Override
    public void uploadAvatar(Long studentId, MultipartFile avatarFile) throws IOException {
        logger.info(LOGGER_METHOD_ADD);
        Student student = studentService.get(studentId);
        Path filePath = Path.of(avatarsDir, studentId + "." + getExtensions(avatarFile.getOriginalFilename()));
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);
        try (
                InputStream is = avatarFile.getInputStream();
                OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
                BufferedInputStream bis = new BufferedInputStream(is, 1024);
                BufferedOutputStream bos = new BufferedOutputStream(os, 1024)
        ) {
            bis.transferTo(bos);
        }
        Avatar avatar = findAvatar(studentId);
        avatar.setStudent(student);
        avatar.setFilePath(filePath.toString());
        avatar.setFileSize(avatarFile.getSize());
        avatar.setMediaType(avatarFile.getContentType());
        avatar.setData(avatarFile.getBytes());
        avatarRepository.save(avatar);
    }

    @Override
    public Avatar findAvatar(Long studentId) {
        logger.info("Method findAvatar was invoked.");
        return avatarRepository.findByStudentId(studentId).orElse(new Avatar());
    }

    @Override
    public List<Avatar> getAvatar(int pageNumber, int pageSize) {
        logger.info(LOGGER_METHOD_GET);
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        return avatarRepository.findAll(pageRequest).getContent();
    }

    private String getExtensions(String fileName) {
        logger.info("Method getExtensions was invoked.");
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
}
