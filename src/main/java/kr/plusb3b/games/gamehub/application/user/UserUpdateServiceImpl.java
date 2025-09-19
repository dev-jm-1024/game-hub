package kr.plusb3b.games.gamehub.application.user;

import kr.plusb3b.games.gamehub.domain.user.dto.ChangePasswordDto;
import kr.plusb3b.games.gamehub.domain.user.dto.RequestUserUpdateDto;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.domain.user.entity.UserAuth;
import kr.plusb3b.games.gamehub.domain.user.entity.UserPrivate;
import kr.plusb3b.games.gamehub.domain.user.repository.UserAuthRepository;
import kr.plusb3b.games.gamehub.domain.user.repository.UserPrivateRepository;
import kr.plusb3b.games.gamehub.domain.user.repository.UserRepository;
import kr.plusb3b.games.gamehub.domain.user.service.UserUpdateService;
import kr.plusb3b.games.gamehub.domain.user.vo.business.MbNickName;
import kr.plusb3b.games.gamehub.domain.user.vo.business.PriBirth;
import kr.plusb3b.games.gamehub.domain.user.vo.business.PriEmail;
import kr.plusb3b.games.gamehub.domain.user.vo.business.PriGender;
import kr.plusb3b.games.gamehub.upload.FileUpload;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
public class UserUpdateServiceImpl implements UserUpdateService {

    private final UserRepository userRepository;
    private final UserAuthRepository userAuthRepo;
    private final UserPrivateRepository userPrivateRepo;
    private final PasswordEncoder passwordEncoder;
    private final FileUpload fileUpload;

    public UserUpdateServiceImpl(UserRepository userRepository, UserAuthRepository userAuthRepo,
                                 UserPrivateRepository userPrivateRepo, PasswordEncoder passwordEncoder,
                                 FileUpload fileUpload) {
        this.userRepository = userRepository;
        this.userAuthRepo = userAuthRepo;
        this.userPrivateRepo = userPrivateRepo;
        this.passwordEncoder = passwordEncoder;
        this.fileUpload = fileUpload;
    }

    @Override
    @Transactional
    public void updateUserProfile(Long mbId, RequestUserUpdateDto userUpdateDto) {
        System.out.println("[DEBUG] =========================");
        System.out.println("[DEBUG] 프로필 업데이트 시작 - mbId: " + mbId);
        System.out.println("[DEBUG] 받은 데이터: " + userUpdateDto.toString()); // toString() 메서드가 있다면

        User user = userRepository.findById(mbId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        UserPrivate userPrivate = userPrivateRepo.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("UserPrivate not found"));

        // 실제 변경 수행
        user.changeMbNickName(MbNickName.of(userUpdateDto.getMbNickName()));
        user.changeMbProfileUrl(userUpdateDto.getMbProfileUrl());
        user.changeMbStatusMessage(userUpdateDto.getMbStatusMessage());

        userPrivate.changeBirth(PriBirth.of(userUpdateDto.getPriBirth()));
        userPrivate.changeEmail(PriEmail.of(userUpdateDto.getPriEmail()));
        userPrivate.changeGender(PriGender.of(userUpdateDto.getPriGender()));


        // 명시적 저장 (변경감지가 안될 경우 대비)
        userRepository.save(user);
        userPrivateRepo.save(userPrivate);
    }

    @Override
    @Transactional
    public void updateUserPassword(Long mbId, ChangePasswordDto dto) {
        User user = userRepository.findById(mbId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        UserAuth userAuth = userAuthRepo.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("UserAuth not found"));

        // 현재 비밀번호 확인 로직 수정
        if(!passwordEncoder.matches(dto.getOldPassword(), userAuth.getAuthPassword().getAuthPassword())){
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        String encodedPassword = passwordEncoder.encode(dto.getNewPassword());
        userAuth.changePassword(encodedPassword);
    }

    @Override
    @Transactional
    public String uploadAndUpdateProfileImage(Long mbId, MultipartFile file) {
        // 1. 사용자 조회
        User user = userRepository.findById(mbId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        try {
            // 2. 프로필 이미지 전용 업로드 메서드 사용
            String profileImageUrl = fileUpload.uploadProfileImage(file);

            // 3. 사용자 프로필 URL 업데이트 (변경감지로 자동 저장)
            user.changeMbProfileUrl(profileImageUrl);

            return profileImageUrl;

        } catch (Exception e) {
            throw new RuntimeException("프로필 이미지 업로드 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
}