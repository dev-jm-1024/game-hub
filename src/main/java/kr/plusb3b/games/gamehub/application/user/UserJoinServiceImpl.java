package kr.plusb3b.games.gamehub.application.user;

import jakarta.servlet.http.HttpServletRequest;
import kr.plusb3b.games.gamehub.common.util.FileValidator;
import kr.plusb3b.games.gamehub.domain.user.entity.User;
import kr.plusb3b.games.gamehub.domain.user.dto.UserSignupDto;
import kr.plusb3b.games.gamehub.domain.user.entity.UserAuth;
import kr.plusb3b.games.gamehub.domain.user.entity.UserPrivate;
import kr.plusb3b.games.gamehub.domain.user.service.UserJoinService;
import kr.plusb3b.games.gamehub.domain.user.vo.UserSignupVO;
import kr.plusb3b.games.gamehub.domain.user.repository.UserAuthRepository;
import kr.plusb3b.games.gamehub.domain.user.repository.UserPrivateRepository;
import kr.plusb3b.games.gamehub.domain.user.repository.UserRepository;
import kr.plusb3b.games.gamehub.domain.user.vo.business.*;
import kr.plusb3b.games.gamehub.security.AccessControlService;
import kr.plusb3b.games.gamehub.security.SnowflakeIdGenerator;
import kr.plusb3b.games.gamehub.upload.FileUpload;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserJoinServiceImpl implements UserJoinService {

    private final UserRepository userRepo;
    private final UserAuthRepository userAuthRepo;
    private final UserPrivateRepository userPrivateRepo;
    private final PasswordEncoder passwordEncoder;
    private final AccessControlService access;
    private final FileUpload fileUpload;

    public UserJoinServiceImpl(UserRepository userRepo, UserAuthRepository userAuthRepo, UserPrivateRepository userPrivateRepo,
                               PasswordEncoder passwordEncoder, AccessControlService access, FileUpload fileUpload) {
        this.userRepo = userRepo;
        this.userAuthRepo = userAuthRepo;
        this.userPrivateRepo = userPrivateRepo;
        this.passwordEncoder = passwordEncoder;
        this.access = access;
        this.fileUpload = fileUpload;
    }

    @Override
    public void signupUser(UserSignupDto usd, UserSignupVO usv, List<MultipartFile> files) {
        try {
            // prod 값으로 역할 구분
            User tempUser = new User();
            User.Role role = tempUser.prodDifferentiate(usd.getProd());

            // 프로필 사진 업로드 처리
            String profileUrl = uploadProfileImage(files);

            // 각 엔티티 조립 (순서 중요: User 먼저 생성)
            User user = buildUser(usd, usv, role, profileUrl);
            UserAuth userAuth = buildUserAuth(usd, user);
            UserPrivate userPrivate = buildUserPrivate(usd, user);

            // 저장
            userRepo.save(user);
            userAuthRepo.save(userAuth);
            userPrivateRepo.save(userPrivate);

        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 회원가입 요청입니다: " + e.getMessage());
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "중복된 사용자 정보가 존재합니다.");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "회원가입 중 알 수 없는 오류가 발생했습니다.");
        }
    }

    /**
     * 프로필 이미지 업로드 처리
     * @param files 업로드할 파일 리스트
     * @return 프로필 이미지 URL (없으면 빈 문자열)
     */
    private String uploadProfileImage(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return "";
        }

        try {
            // 프로필 이미지는 첫 번째 파일만 사용
            MultipartFile profileFile = files.get(0);

            // 파일 유효성 검증
            if (profileFile.isEmpty()) {
                return "";
            }

            // 이미지 파일인지 확인
            String contentType = profileFile.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("프로필 사진은 이미지 파일만 업로드 가능합니다.");
            }

            // 파일 크기 제한 (예: 5MB)
            long maxSize = 5 * 1024 * 1024; // 5MB
            if (profileFile.getSize() > maxSize) {
                throw new IllegalArgumentException("프로필 사진 크기는 5MB를 초과할 수 없습니다.");
            }

            // 단일 파일 업로드를 위한 리스트 생성
            List<MultipartFile> profileFiles = List.of(profileFile);
            Map<String, String> fileUrlAndType = fileUpload.getFileUrlAndType(profileFiles);

            // Map에서 첫 번째 URL 추출
            return fileUrlAndType.keySet().iterator().next();

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "프로필 사진 업로드 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Override
    public boolean isLogin(HttpServletRequest request) {
        User user = access.getAuthenticatedUser(request);
        return user != null;
    }

    @Override
    public boolean isDuplicatedLoginId(String loginId) {
        Optional<UserAuth> userAuthOpt = userAuthRepo.findById(AuthUserId.of(loginId));
        return userAuthOpt.isPresent();
    }

    @Override
    public boolean checkDistinctEmail(String email) {
        return userPrivateRepo.findAll().stream()
                .map(UserPrivate::getPriEmail)
                .anyMatch(e -> e.equals(email));
    }

    private UserAuth buildUserAuth(UserSignupDto userSignupDto, User user) {
        return new UserAuth(
                userSignupDto.getAuthUserId(),
                user,
                passwordEncoder.encode(userSignupDto.getAuthPassword()),
                null
        );
    }

    private User buildUser(UserSignupDto userSignupDto, UserSignupVO userSignupVO, User.Role role, String profileUrl) {
        SnowflakeIdGenerator snowflakeIdGenerator = new SnowflakeIdGenerator(0, 0);
        Long mbId = snowflakeIdGenerator.nextId();

        return new User(
                userSignupVO.getMbReportCnt(),
                mbId,
                MbNickName.of(userSignupDto.getMbNickname()),
                profileUrl, // 프로필 URL 설정
                userSignupDto.getMbStatusMessage(),
                LocalDateTime.now(),
                userSignupVO.getMbAct(),
                role
        );
    }

    private UserPrivate buildUserPrivate(UserSignupDto userSignupDto, User user) {
        SnowflakeIdGenerator snowflakeIdGenerator = new SnowflakeIdGenerator(1, 0);
        Long priMbId = snowflakeIdGenerator.nextId();

        return new UserPrivate(
                priMbId,
                user,
                PriEmail.of(userSignupDto.getPriEmail()),
                PriBirth.of(userSignupDto.getPriBirth()),
                PriGender.of(userSignupDto.getPriGender())
        );
    }
}