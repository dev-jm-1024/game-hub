// ===== 비밀번호 찾기 페이지 JavaScript - 글래스모피즘 디자인 =====

// 전역 변수
let currentTab = 'email';
let verificationTimer = null;
let timeLeft = 180; // 3분 (180초)
let isVerificationSent = false;

// DOM 요소들
const tabButtons = document.querySelectorAll('.tab-btn');
const tabContents = document.querySelectorAll('.tab-content');
const emailForm = document.getElementById('email-form');
const phoneForm = document.getElementById('phone-form');
const verificationSection = document.getElementById('verification-section');
const verificationForm = document.getElementById('verification-form');
const resultContainer = document.getElementById('result-container');
const timerCountdown = document.getElementById('timer-countdown');
const resendBtn = document.getElementById('resend-btn');

// 초기화 함수
document.addEventListener('DOMContentLoaded', function() {
    initializeEventListeners();
    initializeValidation();
    setupAccessibility();
    
    console.log('🔐 비밀번호 찾기 시스템이 초기화되었습니다.');
    console.log('📧 이메일 또는 📱 휴대폰으로 비밀번호를 찾을 수 있습니다.');
    
    // 개발자 도구 안내
    console.log('%c개발자 도구 명령어:', 'color: #ff9800; font-weight: bold; font-size: 14px;');
    console.log('%c• switchTab("email") - 이메일 탭으로 전환', 'color: #64b5f6;');
    console.log('%c• switchTab("phone") - 휴대폰 탭으로 전환', 'color: #64b5f6;');
    console.log('%c• resetForm() - 폼 초기화', 'color: #64b5f6;');
    console.log('%c• simulateSuccess() - 성공 상태 시뮬레이션', 'color: #64b5f6;');
});

// 이벤트 리스너 초기화
function initializeEventListeners() {
    // 탭 전환 이벤트
    tabButtons.forEach(button => {
        button.addEventListener('click', (e) => {
            const tabName = e.currentTarget.dataset.tab;
            switchTab(tabName);
        });
    });

    // 폼 제출 이벤트
    emailForm.addEventListener('submit', handleEmailFormSubmit);
    phoneForm.addEventListener('submit', handlePhoneFormSubmit);
    verificationForm.addEventListener('submit', handleVerificationFormSubmit);

    // 실시간 입력 검증
    setupRealTimeValidation();

    // 휴대폰 번호 자동 포맷팅
    const phoneInput = document.getElementById('user-phone');
    if (phoneInput) {
        phoneInput.addEventListener('input', formatPhoneNumber);
    }

    // 인증번호 입력 제한
    const verificationInput = document.getElementById('verification-code');
    if (verificationInput) {
        verificationInput.addEventListener('input', formatVerificationCode);
    }

    // 재전송 버튼
    if (resendBtn) {
        resendBtn.addEventListener('click', resendVerificationCode);
    }

    // 키보드 네비게이션
    document.addEventListener('keydown', handleKeyboardNavigation);

    // 리플 효과
    addRippleEffect();
}

// 탭 전환 함수
function switchTab(tabName) {
    // 현재 탭 업데이트
    currentTab = tabName;
    
    // 탭 버튼 활성화 상태 변경
    tabButtons.forEach(btn => {
        btn.classList.toggle('active', btn.dataset.tab === tabName);
    });
    
    // 탭 콘텐츠 표시/숨김
    tabContents.forEach(content => {
        content.classList.toggle('active', content.id === `${tabName}-tab`);
    });
    
    // 인증 섹션 숨김
    verificationSection.style.display = 'none';
    resultContainer.style.display = 'none';
    
    // 타이머 초기화
    clearVerificationTimer();
    
    console.log(`📋 ${tabName === 'email' ? '이메일' : '휴대폰'} 탭으로 전환되었습니다.`);
    
    // 접근성을 위한 포커스 이동
    setTimeout(() => {
        const firstInput = document.querySelector(`#${tabName}-tab .form-input`);
        if (firstInput) {
            firstInput.focus();
        }
    }, 100);
}

// 실시간 입력 검증 설정
function setupRealTimeValidation() {
    const inputs = document.querySelectorAll('.form-input');
    
    inputs.forEach(input => {
        input.addEventListener('input', (e) => {
            validateField(e.target);
        });
        
        input.addEventListener('blur', (e) => {
            validateField(e.target);
        });
        
        input.addEventListener('focus', (e) => {
            clearFieldError(e.target);
        });
    });
}

// 필드 검증 함수
function validateField(field) {
    const fieldName = field.name;
    const value = field.value.trim();
    let isValid = true;
    let errorMessage = '';

    switch (fieldName) {
        case 'userId':
            if (!value) {
                isValid = false;
                errorMessage = '아이디를 입력해주세요.';
            } else if (value.length < 4) {
                isValid = false;
                errorMessage = '아이디는 4자 이상이어야 합니다.';
            } else if (!/^[a-zA-Z0-9_]+$/.test(value)) {
                isValid = false;
                errorMessage = '아이디는 영문, 숫자, 언더스코어만 사용 가능합니다.';
            }
            break;

        case 'name':
            if (!value) {
                isValid = false;
                errorMessage = '이름을 입력해주세요.';
            } else if (value.length < 2) {
                isValid = false;
                errorMessage = '이름은 2자 이상이어야 합니다.';
            } else if (!/^[가-힣a-zA-Z\s]+$/.test(value)) {
                isValid = false;
                errorMessage = '이름은 한글 또는 영문만 입력 가능합니다.';
            }
            break;

        case 'email':
            if (!value) {
                isValid = false;
                errorMessage = '이메일을 입력해주세요.';
            } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)) {
                isValid = false;
                errorMessage = '올바른 이메일 형식을 입력해주세요.';
            }
            break;

        case 'phone':
            if (!value) {
                isValid = false;
                errorMessage = '휴대폰 번호를 입력해주세요.';
            } else if (!/^010-\d{4}-\d{4}$/.test(value)) {
                isValid = false;
                errorMessage = '올바른 휴대폰 번호 형식을 입력해주세요. (010-1234-5678)';
            }
            break;

        case 'verificationCode':
            if (!value) {
                isValid = false;
                errorMessage = '인증번호를 입력해주세요.';
            } else if (!/^\d{6}$/.test(value)) {
                isValid = false;
                errorMessage = '인증번호는 6자리 숫자입니다.';
            }
            break;
    }

    // UI 업데이트
    if (isValid) {
        showFieldSuccess(field);
    } else {
        showFieldError(field, errorMessage);
    }

    return isValid;
}

// 필드 오류 표시
function showFieldError(field, message) {
    field.classList.add('error');
    field.classList.remove('success-state');
    
    const errorElement = document.getElementById(`${field.id}-error`);
    if (errorElement) {
        errorElement.textContent = message;
        errorElement.classList.add('show');
    }
}

// 필드 성공 표시
function showFieldSuccess(field) {
    field.classList.remove('error');
    field.classList.add('success-state');
    
    const errorElement = document.getElementById(`${field.id}-error`);
    if (errorElement) {
        errorElement.classList.remove('show');
        errorElement.textContent = '';
    }
}

// 필드 오류 제거
function clearFieldError(field) {
    field.classList.remove('error');
    
    const errorElement = document.getElementById(`${field.id}-error`);
    if (errorElement) {
        errorElement.classList.remove('show');
    }
}

// 휴대폰 번호 포맷팅
function formatPhoneNumber(e) {
    let value = e.target.value.replace(/[^\d]/g, '');
    
    if (value.length >= 3 && value.length <= 7) {
        value = value.replace(/(\d{3})(\d+)/, '$1-$2');
    } else if (value.length > 7) {
        value = value.replace(/(\d{3})(\d{4})(\d+)/, '$1-$2-$3');
    }
    
    e.target.value = value;
}

// 인증번호 포맷팅
function formatVerificationCode(e) {
    let value = e.target.value.replace(/[^\d]/g, '');
    if (value.length > 6) {
        value = value.substring(0, 6);
    }
    e.target.value = value;
}

// 이메일 폼 제출 처리
async function handleEmailFormSubmit(e) {
    e.preventDefault();
    
    const formData = new FormData(e.target);
    const data = Object.fromEntries(formData);
    
    // 모든 필드 검증
    const fields = e.target.querySelectorAll('.form-input');
    let isFormValid = true;
    
    fields.forEach(field => {
        if (!validateField(field)) {
            isFormValid = false;
        }
    });
    
    if (!isFormValid) {
        showToast('입력 정보를 다시 확인해주세요.', 'error');
        return;
    }
    
    // 로딩 상태 표시
    const submitBtn = document.getElementById('email-submit');
    showButtonLoading(submitBtn, true);
    
    try {
        // API 호출 시뮬레이션
        await simulateApiCall();
        
        // 성공 처리
        showResult('email', data);
        showToast('비밀번호 재설정 링크가 이메일로 전송되었습니다.', 'success');
        
        console.log('📧 이메일로 비밀번호 재설정 링크가 전송되었습니다.');
        
    } catch (error) {
        console.error('이메일 전송 오류:', error);
        showToast('이메일 전송에 실패했습니다. 다시 시도해주세요.', 'error');
    } finally {
        showButtonLoading(submitBtn, false);
    }
}

// 휴대폰 폼 제출 처리
async function handlePhoneFormSubmit(e) {
    e.preventDefault();
    
    const formData = new FormData(e.target);
    const data = Object.fromEntries(formData);
    
    // 모든 필드 검증
    const fields = e.target.querySelectorAll('.form-input');
    let isFormValid = true;
    
    fields.forEach(field => {
        if (!validateField(field)) {
            isFormValid = false;
        }
    });
    
    if (!isFormValid) {
        showToast('입력 정보를 다시 확인해주세요.', 'error');
        return;
    }
    
    // 로딩 상태 표시
    const submitBtn = document.getElementById('phone-submit');
    showButtonLoading(submitBtn, true);
    
    try {
        // API 호출 시뮬레이션
        await simulateApiCall();
        
        // 인증번호 전송 성공
        showVerificationSection();
        startVerificationTimer();
        isVerificationSent = true;
        
        showToast(`${data.phone}로 인증번호가 전송되었습니다.`, 'success');
        console.log('📱 휴대폰으로 인증번호가 전송되었습니다.');
        
    } catch (error) {
        console.error('인증번호 전송 오류:', error);
        showToast('인증번호 전송에 실패했습니다. 다시 시도해주세요.', 'error');
    } finally {
        showButtonLoading(submitBtn, false);
    }
}

// 인증번호 확인 처리
async function handleVerificationFormSubmit(e) {
    e.preventDefault();
    
    const formData = new FormData(e.target);
    const verificationCode = formData.get('verificationCode');
    
    // 인증번호 검증
    if (!validateField(document.getElementById('verification-code'))) {
        return;
    }
    
    // 로딩 상태 표시
    const submitBtn = e.target.querySelector('.submit-btn');
    showButtonLoading(submitBtn, true);
    
    try {
        // API 호출 시뮬레이션
        await simulateApiCall();
        
        // 인증 성공 (시뮬레이션)
        if (verificationCode === '123456' || verificationCode.length === 6) {
            clearVerificationTimer();
            showResult('phone', { verificationCode });
            showToast('인증이 완료되었습니다. 비밀번호를 재설정할 수 있습니다.', 'success');
            console.log('✅ 휴대폰 인증이 완료되었습니다.');
        } else {
            throw new Error('잘못된 인증번호입니다.');
        }
        
    } catch (error) {
        console.error('인증 확인 오류:', error);
        showToast('인증번호가 올바르지 않습니다.', 'error');
    } finally {
        showButtonLoading(submitBtn, false);
    }
}

// 인증 섹션 표시
function showVerificationSection() {
    verificationSection.style.display = 'block';
    verificationSection.scrollIntoView({ behavior: 'smooth', block: 'center' });
    
    // 인증번호 입력 필드에 포커스
    setTimeout(() => {
        const verificationInput = document.getElementById('verification-code');
        if (verificationInput) {
            verificationInput.focus();
        }
    }, 500);
}

// 인증 타이머 시작
function startVerificationTimer() {
    timeLeft = 180; // 3분 리셋
    updateTimerDisplay();
    
    verificationTimer = setInterval(() => {
        timeLeft--;
        updateTimerDisplay();
        
        if (timeLeft <= 0) {
            clearVerificationTimer();
            showToast('인증 시간이 만료되었습니다. 다시 요청해주세요.', 'warning');
            resendBtn.disabled = false;
            resendBtn.textContent = '재전송';
        }
    }, 1000);
    
    // 재전송 버튼 비활성화
    resendBtn.disabled = true;
    resendBtn.textContent = '전송됨';
}

// 타이머 표시 업데이트
function updateTimerDisplay() {
    const minutes = Math.floor(timeLeft / 60);
    const seconds = timeLeft % 60;
    const timeString = `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
    
    if (timerCountdown) {
        timerCountdown.textContent = timeString;
        
        // 시간이 30초 이하일 때 경고 색상
        if (timeLeft <= 30) {
            timerCountdown.style.color = '#ef5350';
        } else {
            timerCountdown.style.color = '#ff9800';
        }
    }
}

// 인증 타이머 정리
function clearVerificationTimer() {
    if (verificationTimer) {
        clearInterval(verificationTimer);
        verificationTimer = null;
    }
}

// 인증번호 재전송
async function resendVerificationCode() {
    if (!isVerificationSent) {
        showToast('먼저 휴대폰 번호를 확인해주세요.', 'warning');
        return;
    }
    
    resendBtn.disabled = true;
    resendBtn.textContent = '전송 중...';
    
    try {
        // API 호출 시뮬레이션
        await simulateApiCall();
        
        startVerificationTimer();
        showToast('인증번호가 재전송되었습니다.', 'success');
        console.log('🔄 인증번호가 재전송되었습니다.');
        
    } catch (error) {
        console.error('재전송 오류:', error);
        showToast('재전송에 실패했습니다. 다시 시도해주세요.', 'error');
        resendBtn.disabled = false;
        resendBtn.textContent = '재전송';
    }
}

// 결과 표시
function showResult(method, data) {
    // 모든 섹션 숨김
    document.querySelectorAll('.tab-content, #verification-section').forEach(section => {
        section.style.display = 'none';
    });
    
    // 결과 메시지 설정
    const resultMessage = document.getElementById('result-message');
    if (method === 'email') {
        resultMessage.innerHTML = `
            <strong>${data.email}</strong>로 비밀번호 재설정 링크를 전송했습니다.<br>
            이메일을 확인하여 비밀번호를 재설정해주세요.
        `;
    } else {
        resultMessage.innerHTML = `
            휴대폰 인증이 완료되었습니다.<br>
            이제 새로운 비밀번호를 설정할 수 있습니다.
        `;
    }
    
    // 결과 컨테이너 표시
    resultContainer.style.display = 'block';
    resultContainer.scrollIntoView({ behavior: 'smooth', block: 'center' });
}

// 버튼 로딩 상태 표시
function showButtonLoading(button, isLoading) {
    const btnText = button.querySelector('.btn-text');
    const btnLoading = button.querySelector('.btn-loading');
    
    if (isLoading) {
        btnText.style.display = 'none';
        btnLoading.style.display = 'flex';
        button.disabled = true;
        button.classList.add('loading');
    } else {
        btnText.style.display = 'block';
        btnLoading.style.display = 'none';
        button.disabled = false;
        button.classList.remove('loading');
    }
}

// 토스트 메시지 표시
function showToast(message, type = 'info') {
    // 기존 토스트 제거
    const existingToast = document.querySelector('.toast-message');
    if (existingToast) {
        existingToast.remove();
    }
    
    // 토스트 생성
    const toast = document.createElement('div');
    toast.className = `toast-message toast-${type}`;
    toast.innerHTML = `
        <div class="toast-content">
            <div class="toast-icon">
                ${getToastIcon(type)}
            </div>
            <div class="toast-text">${message}</div>
            <button class="toast-close" onclick="this.parentElement.parentElement.remove()">
                <i class="fas fa-times"></i>
            </button>
        </div>
    `;
    
    // 토스트 스타일 추가
    addToastStyles();
    
    // 문서에 추가
    document.body.appendChild(toast);
    
    // 자동 제거
    setTimeout(() => {
        if (toast.parentElement) {
            toast.classList.add('fade-out');
            setTimeout(() => toast.remove(), 300);
        }
    }, 4000);
    
    console.log(`🔔 ${type.toUpperCase()}: ${message}`);
}

// 토스트 아이콘 가져오기
function getToastIcon(type) {
    const icons = {
        success: '<i class="fas fa-check-circle"></i>',
        error: '<i class="fas fa-exclamation-circle"></i>',
        warning: '<i class="fas fa-exclamation-triangle"></i>',
        info: '<i class="fas fa-info-circle"></i>'
    };
    return icons[type] || icons.info;
}

// 토스트 스타일 추가
function addToastStyles() {
    if (document.getElementById('toast-styles')) return;
    
    const style = document.createElement('style');
    style.id = 'toast-styles';
    style.textContent = `
        .toast-message {
            position: fixed;
            top: 20px;
            right: 20px;
            z-index: 10000;
            min-width: 300px;
            max-width: 500px;
            background: linear-gradient(135deg, rgba(255, 255, 255, 0.15) 0%, rgba(255, 255, 255, 0.08) 100%);
            backdrop-filter: blur(40px) saturate(180%);
            -webkit-backdrop-filter: blur(40px) saturate(180%);
            border: 1px solid rgba(255, 255, 255, 0.18);
            border-top: 1px solid rgba(255, 255, 255, 0.25);
            border-left: 1px solid rgba(255, 255, 255, 0.25);
            border-radius: 16px;
            box-shadow: 0 8px 32px 0 rgba(0, 0, 0, 0.37);
            animation: slideInRight 0.4s ease-out;
            transition: all 0.3s ease;
        }
        
        .toast-content {
            display: flex;
            align-items: center;
            padding: 16px 20px;
            gap: 12px;
        }
        
        .toast-icon {
            font-size: 18px;
            flex-shrink: 0;
        }
        
        .toast-success .toast-icon { color: #4caf50; }
        .toast-error .toast-icon { color: #ef5350; }
        .toast-warning .toast-icon { color: #ffca28; }
        .toast-info .toast-icon { color: #29b6f6; }
        
        .toast-text {
            color: white;
            font-size: 14px;
            line-height: 1.4;
            flex-grow: 1;
        }
        
        .toast-close {
            background: none;
            border: none;
            color: rgba(255, 255, 255, 0.7);
            cursor: pointer;
            padding: 4px;
            border-radius: 4px;
            transition: all 0.3s ease;
            flex-shrink: 0;
        }
        
        .toast-close:hover {
            background: rgba(255, 255, 255, 0.1);
            color: white;
        }
        
        .toast-message.fade-out {
            animation: slideOutRight 0.3s ease-in forwards;
        }
        
        @keyframes slideInRight {
            from {
                transform: translateX(100%);
                opacity: 0;
            }
            to {
                transform: translateX(0);
                opacity: 1;
            }
        }
        
        @keyframes slideOutRight {
            to {
                transform: translateX(100%);
                opacity: 0;
            }
        }
        
        @media (max-width: 768px) {
            .toast-message {
                top: 10px;
                right: 10px;
                left: 10px;
                min-width: auto;
                max-width: none;
            }
        }
    `;
    document.head.appendChild(style);
}

// API 호출 시뮬레이션
function simulateApiCall(delay = 1500) {
    return new Promise((resolve, reject) => {
        setTimeout(() => {
            // 90% 성공률로 시뮬레이션
            if (Math.random() > 0.1) {
                resolve({ success: true });
            } else {
                reject(new Error('네트워크 오류가 발생했습니다.'));
            }
        }, delay);
    });
}

// 폼 초기화
function resetForm() {
    // 모든 폼 초기화
    [emailForm, phoneForm, verificationForm].forEach(form => {
        if (form) {
            form.reset();
        }
    });
    
    // 모든 오류 상태 제거
    document.querySelectorAll('.form-input').forEach(input => {
        input.classList.remove('error', 'success-state');
    });
    
    document.querySelectorAll('.error-message').forEach(error => {
        error.classList.remove('show');
        error.textContent = '';
    });
    
    // 섹션 초기화
    verificationSection.style.display = 'none';
    resultContainer.style.display = 'none';
    
    // 탭을 이메일로 초기화
    switchTab('email');
    
    // 타이머 정리
    clearVerificationTimer();
    isVerificationSent = false;
    
    showToast('폼이 초기화되었습니다.', 'info');
    console.log('🔄 폼이 초기화되었습니다.');
}

// 키보드 네비게이션 처리
function handleKeyboardNavigation(e) {
    // Escape 키로 모달이나 섹션 닫기
    if (e.key === 'Escape') {
        const activeToast = document.querySelector('.toast-message');
        if (activeToast) {
            activeToast.remove();
            return;
        }
    }
    
    // Tab 키 네비게이션 개선
    if (e.key === 'Tab') {
        const focusableElements = document.querySelectorAll(
            'button:not([disabled]), input:not([disabled]), select:not([disabled]), textarea:not([disabled]), [tabindex]:not([tabindex="-1"])'
        );
        
        const firstElement = focusableElements[0];
        const lastElement = focusableElements[focusableElements.length - 1];
        
        if (e.shiftKey && document.activeElement === firstElement) {
            e.preventDefault();
            lastElement.focus();
        } else if (!e.shiftKey && document.activeElement === lastElement) {
            e.preventDefault();
            firstElement.focus();
        }
    }
    
    // Enter 키로 폼 제출
    if (e.key === 'Enter' && e.target.classList.contains('form-input')) {
        const form = e.target.closest('form');
        if (form) {
            const submitBtn = form.querySelector('.submit-btn');
            if (submitBtn && !submitBtn.disabled) {
                submitBtn.click();
            }
        }
    }
}

// 접근성 설정
function setupAccessibility() {
    // ARIA 라벨 추가
    document.querySelectorAll('.form-input').forEach(input => {
        const label = document.querySelector(`label[for="${input.id}"]`);
        if (label && !input.getAttribute('aria-label')) {
            input.setAttribute('aria-label', label.textContent.trim());
        }
    });
    
    // 오류 메시지와 입력 필드 연결
    document.querySelectorAll('.error-message').forEach(error => {
        const inputId = error.id.replace('-error', '');
        const input = document.getElementById(inputId);
        if (input) {
            input.setAttribute('aria-describedby', error.id);
        }
    });
    
    // 탭 버튼에 ARIA 속성 추가
    tabButtons.forEach(button => {
        button.setAttribute('role', 'tab');
        button.setAttribute('aria-selected', button.classList.contains('active'));
    });
    
    console.log('♿ 접근성 기능이 설정되었습니다.');
}

// 리플 효과 추가
function addRippleEffect() {
    const rippleElements = document.querySelectorAll('.submit-btn, .action-btn, .tab-btn');
    
    rippleElements.forEach(element => {
        element.addEventListener('click', function(e) {
            const ripple = document.createElement('span');
            ripple.classList.add('ripple');
            
            const rect = this.getBoundingClientRect();
            const size = Math.max(rect.width, rect.height);
            const x = e.clientX - rect.left - size / 2;
            const y = e.clientY - rect.top - size / 2;
            
            ripple.style.width = ripple.style.height = size + 'px';
            ripple.style.left = x + 'px';
            ripple.style.top = y + 'px';
            
            this.appendChild(ripple);
            
            setTimeout(() => {
                ripple.remove();
            }, 600);
        });
    });
    
    // 리플 효과 CSS 추가
    if (!document.getElementById('ripple-styles')) {
        const style = document.createElement('style');
        style.id = 'ripple-styles';
        style.textContent = `
            .ripple {
                position: absolute;
                border-radius: 50%;
                background: rgba(255, 255, 255, 0.3);
                pointer-events: none;
                transform: scale(0);
                animation: ripple-animation 0.6s linear;
                z-index: 1;
            }
            
            @keyframes ripple-animation {
                to {
                    transform: scale(4);
                    opacity: 0;
                }
            }
        `;
        document.head.appendChild(style);
    }
}

// 성공 상태 시뮬레이션 (개발자 도구용)
function simulateSuccess() {
    if (currentTab === 'email') {
        showResult('email', { 
            userId: 'testuser', 
            name: '홍길동', 
            email: 'test@gamehub.com' 
        });
    } else {
        showResult('phone', { 
            userId: 'testuser', 
            name: '홍길동', 
            phone: '010-1234-5678' 
        });
    }
    showToast('시뮬레이션: 비밀번호 찾기가 완료되었습니다!', 'success');
}

// 초기화 완료 후 전역 함수로 노출
window.switchTab = switchTab;
window.resetForm = resetForm;
window.simulateSuccess = simulateSuccess;

// 페이지 언로드 시 타이머 정리
window.addEventListener('beforeunload', () => {
    clearVerificationTimer();
});

// 개발 모드에서 콘솔 로그 활성화
if (window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1') {
    console.log('%c🔐 비밀번호 찾기 개발 모드', 'color: #ff9800; font-size: 16px; font-weight: bold;');
    console.log('사용 가능한 테스트 데이터:');
    console.log('• 아이디: testuser, admin, user123');
    console.log('• 이름: 홍길동, 김철수, 이영희');
    console.log('• 이메일: test@gamehub.com');
    console.log('• 휴대폰: 010-1234-5678');
    console.log('• 인증번호: 123456 (테스트용)');
}
