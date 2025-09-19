package kr.plusb3b.games.gamehub.application.user.admin;

import kr.plusb3b.games.gamehub.domain.user.entity.UserLoginInfo;
import kr.plusb3b.games.gamehub.domain.user.repository.UserLoginInfoRepository;
import kr.plusb3b.games.gamehub.domain.user.service.admin.AdminUserProvider;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class AdminUserProviderImpl implements AdminUserProvider {

    private final UserLoginInfoRepository userLoginInfoRepo;

    public AdminUserProviderImpl(UserLoginInfoRepository userLoginInfoRepo) {
        this.userLoginInfoRepo = userLoginInfoRepo;
    }

    @Override
    public List<UserLoginInfo> getUserLoginInfo(Long mbId) {

        List<UserLoginInfo> result = userLoginInfoRepo.getUserLoginInfoByUser_MbId(mbId).stream()
                .filter(loginInfo -> loginInfo.getUser().isActivateUser())
                .sorted(Comparator.comparing(UserLoginInfo::getLoginTime).reversed())
                .collect(toList());

        return result.isEmpty() ? null : result;
    }
}
