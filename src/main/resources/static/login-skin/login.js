// DOM 요소들
const loginForm = document.getElementById('loginForm');
const usernameInput = document.getElementById('username');
const passwordInput = document.getElementById('password');
const passwordToggle = document.getElementById('passwordToggle');
const signupBtn = document.getElementById('signupBtn');
const guestBtn = document.getElementById('guestBtn');
const forgotAccount = document.getElementById('forgotAccount');

// 모달 요소들
const modalOverlay = document.getElementById('modalOverlay');
const modalTitle = document.getElementById('modalTitle');
const modalMessage = document.getElementById('modalMessage');
const modalClose = document.getElementById('modalClose');
const modalConfirm = document.getElementById('modalConfirm');

// 유틸리티 함수들
function showModal(title, message, callback = null) {
    modalTitle.textContent = title;
    modalMessage.textContent = message;
    modalOverlay.classList.add('active');
    
    // 콜백 함수가 있으면 확인 버튼에 연결
    if (callback) {
        modalConfirm.onclick = () => {
            hideModal();
            callback();
        };
    } else {
        modalConfirm.onclick = hideModal;
    }
}

function hideModal() {
    modalOverlay.classList.remove('active');
}

function validateForm(username, password) {
    if (!username.trim()) {
        showModal('입력 오류', '아이디를 입력해주세요.');
        usernameInput.focus();
        return false;
    }
    
    if (username.length < 3) {
        showModal('입력 오류', '아이디는 3자 이상 입력해주세요.');
        usernameInput.focus();
        return false;
    }
    
    if (!password.trim()) {
        showModal('입력 오류', '비밀번호를 입력해주세요.');
        passwordInput.focus();
        return false;
    }
    
    if (password.length < 4) {
        showModal('입력 오류', '비밀번호는 4자 이상 입력해주세요.');
        passwordInput.focus();
        return false;
    }
    
    return true;
}

function addLoadingEffect(button, originalText) {
    button.disabled = true;
    button.innerHTML = `<i class="fas fa-spinner fa-spin"></i> 처리 중...`;
    
    return () => {
        button.disabled = false;
        button.innerHTML = originalText;
    };
}

// 비밀번호 보기/숨기기 토글
passwordToggle.addEventListener('click', function() {
    const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
    passwordInput.setAttribute('type', type);
    
    const icon = this.querySelector('i');
    if (type === 'text') {
        icon.classList.remove('fa-eye');
        icon.classList.add('fa-eye-slash');
    } else {
        icon.classList.remove('fa-eye-slash');
        icon.classList.add('fa-eye');
    }
});

// 로그인 폼 제출
loginForm.addEventListener('submit', async function(e) {
    e.preventDefault();
    
    const username = usernameInput.value;
    const password = passwordInput.value;
    
    if (!validateForm(username, password)) {
        return;
    }
    
    // 로딩 효과 시작
    const submitBtn = loginForm.querySelector('button[type="submit"]');
    const originalText = submitBtn.innerHTML;
    const stopLoading = addLoadingEffect(submitBtn, originalText);
    
    try {
        // CSRF 토큰 가져오기
        const csrfToken = document.querySelector("meta[name='_csrf']")?.getAttribute("content");
        const csrfHeader = document.querySelector("meta[name='_csrf_header']")?.getAttribute("content");
        
        // FormData 생성
        const formData = new FormData();
        formData.append('authUserId', username);
        formData.append('authPassword', password);
        
        // API 요청
        const response = await fetch('/api/v1/login', {
            method: 'POST',
            headers: {
                ...(csrfToken && csrfHeader ? { [csrfHeader]: csrfToken } : {})
            },
            body: formData
        });
        
        stopLoading();
        
        if (response.ok) {
            const responseData = await response.json();
            
            showModal('로그인 성공', `환영합니다, ${responseData.nickname || username}님!`, () => {
                // 사용자 역할에 따라 다른 페이지로 리다이렉트
                if (responseData.role === 'ROLE_PRODUCER') {
                    window.location.href = '/game-hub/prod';
                } else {
                    window.location.href = '/';
                }
            });
        } else {
            const responseText = await response.text();
            showModal('로그인 실패', responseText || '로그인에 실패했습니다.');
        }
        
    } catch (error) {
        stopLoading();
        console.error('로그인 오류:', error);
        showModal('네트워크 오류', '네트워크 오류가 발생했습니다. 잠시 후 다시 시도해주세요.');
    }
});

// 회원가입 버튼
signupBtn.addEventListener('click', function() {
    const originalText = this.innerHTML;
    const stopLoading = addLoadingEffect(this, originalText);
    
    setTimeout(() => {
        stopLoading();
        showModal('회원가입', '회원가입 페이지로 이동합니다.', () => {
            // 실제로는 회원가입 페이지로 리다이렉트
            console.log('회원가입 페이지로 이동');
        });
    }, 1000);
});

// 게스트 로그인 버튼
guestBtn.addEventListener('click', function() {
    const originalText = this.innerHTML;
    const stopLoading = addLoadingEffect(this, originalText);
    
    setTimeout(() => {
        stopLoading();
        showModal('게스트 로그인', '게스트로 로그인되었습니다.', () => {
            // 실제로는 게스트 권한으로 메인 페이지로 이동
            console.log('게스트로 메인 페이지 이동');
        });
    }, 1500);
});

// 계정 찾기 링크
forgotAccount.addEventListener('click', function(e) {
    e.preventDefault();
    showModal('계정 찾기', '계정 찾기 페이지로 이동합니다.', () => {
        // 실제로는 계정 찾기 페이지로 리다이렉트
        console.log('계정 찾기 페이지로 이동');
    });
});

// 모달 닫기 이벤트들
modalClose.addEventListener('click', hideModal);
modalOverlay.addEventListener('click', function(e) {
    if (e.target === modalOverlay) {
        hideModal();
    }
});

// ESC 키로 모달 닫기
document.addEventListener('keydown', function(e) {
    if (e.key === 'Escape' && modalOverlay.classList.contains('active')) {
        hideModal();
    }
});

// Enter 키로 로그인 (포커스가 입력 필드에 있을 때)
usernameInput.addEventListener('keydown', function(e) {
    if (e.key === 'Enter') {
        passwordInput.focus();
    }
});

passwordInput.addEventListener('keydown', function(e) {
    if (e.key === 'Enter') {
        loginForm.dispatchEvent(new Event('submit'));
    }
});

// 입력 필드 애니메이션 효과
[usernameInput, passwordInput].forEach(input => {
    input.addEventListener('focus', function() {
        this.parentElement.classList.add('focused');
    });
    
    input.addEventListener('blur', function() {
        this.parentElement.classList.remove('focused');
    });
    
    // 입력값이 있을 때 스타일 유지
    input.addEventListener('input', function() {
        if (this.value.trim()) {
            this.parentElement.classList.add('has-value');
        } else {
            this.parentElement.classList.remove('has-value');
        }
    });
});

// 페이지 로드 완료 시 초기 설정
document.addEventListener('DOMContentLoaded', function() {
    // 첫 번째 입력 필드에 포커스
    usernameInput.focus();
    
    // 개발자 콘솔에 테스트 계정 정보 출력
    console.log('=== GameHub 로그인 테스트 계정 ===');
    console.log('1. 관리자: admin / admin');
    console.log('2. 테스트: test / 1234');
    console.log('================================');
});

// 플로팅 요소들에 랜덤 애니메이션 적용
function initFloatingElements() {
    const floatingElements = document.querySelectorAll('.floating-element');
    
    floatingElements.forEach((element, index) => {
        // 랜덤한 애니메이션 지연시간 적용
        const delay = Math.random() * 2;
        element.style.animationDelay = `-${delay}s`;
        
        // 마우스 호버 시 반응 효과
        element.addEventListener('mouseenter', function() {
            this.style.transform = 'scale(1.2)';
            this.style.opacity = '0.8';
        });
        
        element.addEventListener('mouseleave', function() {
            this.style.transform = 'scale(1)';
            this.style.opacity = '1';
        });
    });
}

// 페이지 로드 시 플로팅 요소 초기화
window.addEventListener('load', initFloatingElements);

// 폼 입력 시 실시간 유효성 검사 (선택사항)
function setupRealTimeValidation() {
    usernameInput.addEventListener('input', function() {
        const value = this.value.trim();
        const wrapper = this.parentElement;
        
        if (value.length > 0 && value.length < 3) {
            wrapper.classList.add('error');
            wrapper.classList.remove('success');
        } else if (value.length >= 3) {
            wrapper.classList.add('success');
            wrapper.classList.remove('error');
        } else {
            wrapper.classList.remove('error', 'success');
        }
    });
    
    passwordInput.addEventListener('input', function() {
        const value = this.value.trim();
        const wrapper = this.parentElement;
        
        if (value.length > 0 && value.length < 4) {
            wrapper.classList.add('error');
            wrapper.classList.remove('success');
        } else if (value.length >= 4) {
            wrapper.classList.add('success');
            wrapper.classList.remove('error');
        } else {
            wrapper.classList.remove('error', 'success');
        }
    });
}

// 실시간 유효성 검사 활성화 (선택사항)
setupRealTimeValidation();
