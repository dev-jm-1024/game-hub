// ===== GameHub 아이디 찾기 JavaScript =====

// 전역 변수
let isInitialized = false;
let currentTab = 'email';
let isSubmitting = false;

// DOM 요소 캐시
const elements = {
    tabButtons: null,
    tabContents: null,
    emailForm: null,
    phoneForm: null,
    emailSubmit: null,
    phoneSubmit: null,
    resultContainer: null,
    inputs: null
};

// 유효성 검사 규칙
const validationRules = {
    name: {
        required: true,
        minLength: 2,
        maxLength: 20,
        pattern: /^[가-힣a-zA-Z\s]+$/,
        message: '이름은 2-20자의 한글 또는 영문으로 입력해주세요.'
    },
    email: {
        required: true,
        pattern: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
        message: '올바른 이메일 주소를 입력해주세요.'
    },
    phone: {
        required: true,
        pattern: /^010-\d{4}-\d{4}$/,
        message: '휴대폰 번호는 010-1234-5678 형식으로 입력해주세요.'
    }
};

// ===== 초기화 =====
document.addEventListener('DOMContentLoaded', function() {
    initializeElements();
    initializeEventListeners();
    initializeAnimations();
    setupPhoneFormatting();
    isInitialized = true;
    console.log('🔍 아이디 찾기 페이지 초기화 완료!');
});

// DOM 요소 초기화
function initializeElements() {
    elements.tabButtons = document.querySelectorAll('.tab-btn');
    elements.tabContents = document.querySelectorAll('.tab-content');
    elements.emailForm = document.getElementById('email-form');
    elements.phoneForm = document.getElementById('phone-form');
    elements.emailSubmit = document.getElementById('email-submit');
    elements.phoneSubmit = document.getElementById('phone-submit');
    elements.resultContainer = document.getElementById('result-container');
    elements.inputs = document.querySelectorAll('.form-input');
}

// 이벤트 리스너 초기화
function initializeEventListeners() {
    // 탭 전환
    elements.tabButtons.forEach(button => {
        button.addEventListener('click', () => switchTab(button.dataset.tab));
    });

    // 폼 제출
    if (elements.emailForm) {
        elements.emailForm.addEventListener('submit', handleEmailSubmit);
    }
    
    if (elements.phoneForm) {
        elements.phoneForm.addEventListener('submit', handlePhoneSubmit);
    }

    // 입력 필드 이벤트
    elements.inputs.forEach(input => {
        input.addEventListener('input', handleInputChange);
        input.addEventListener('blur', handleInputBlur);
        input.addEventListener('focus', handleInputFocus);
    });

    // 키보드 네비게이션
    document.addEventListener('keydown', handleKeyboard);

    // 리사이즈 이벤트
    window.addEventListener('resize', handleResize);
}

// ===== 탭 전환 =====
function switchTab(tabName) {
    if (tabName === currentTab) return;

    // 현재 탭 비활성화
    const currentTabBtn = document.querySelector(`.tab-btn[data-tab="${currentTab}"]`);
    const currentTabContent = document.getElementById(`${currentTab}-tab`);
    
    if (currentTabBtn) currentTabBtn.classList.remove('active');
    if (currentTabContent) currentTabContent.classList.remove('active');

    // 새 탭 활성화
    const newTabBtn = document.querySelector(`.tab-btn[data-tab="${tabName}"]`);
    const newTabContent = document.getElementById(`${tabName}-tab`);
    
    if (newTabBtn) {
        newTabBtn.classList.add('active');
        createRipple(newTabBtn);
    }
    
    if (newTabContent) {
        newTabContent.classList.add('active');
    }

    currentTab = tabName;
    
    // 첫 번째 입력 필드에 포커스
    const firstInput = newTabContent?.querySelector('.form-input');
    if (firstInput) {
        setTimeout(() => firstInput.focus(), 300);
    }

    showToast(`${tabName === 'email' ? '이메일' : '휴대폰'}로 찾기로 변경되었습니다.`, 'info');
}

// ===== 폼 제출 처리 =====
async function handleEmailSubmit(e) {
    e.preventDefault();
    
    if (isSubmitting) return;
    
    const formData = new FormData(e.target);
    const data = {
        name: formData.get('name')?.trim(),
        email: formData.get('email')?.trim()
    };

    if (!validateForm(data, ['name', 'email'])) {
        return;
    }

    await submitFindRequest('email', data);
}

async function handlePhoneSubmit(e) {
    e.preventDefault();
    
    if (isSubmitting) return;
    
    const formData = new FormData(e.target);
    const data = {
        name: formData.get('name')?.trim(),
        phone: formData.get('phone')?.trim()
    };

    if (!validateForm(data, ['name', 'phone'])) {
        return;
    }

    await submitFindRequest('phone', data);
}

// ===== 아이디 찾기 요청 =====
async function submitFindRequest(method, data) {
    isSubmitting = true;
    
    const submitBtn = method === 'email' ? elements.emailSubmit : elements.phoneSubmit;
    const btnText = submitBtn.querySelector('.btn-text');
    const btnLoading = submitBtn.querySelector('.btn-loading');
    
    // 버튼 상태 변경
    btnText.style.display = 'none';
    btnLoading.style.display = 'flex';
    submitBtn.disabled = true;
    
    try {
        // 실제 API 호출 시뮬레이션
        await simulateApiCall(method, data);
        
        // 성공 처리
        showResult({
            success: true,
            userId: 'user****',
            joinDate: '2024-01-15',
            method: method
        });
        
        showToast('아이디를 성공적으로 찾았습니다!', 'success');
        
    } catch (error) {
        // 오류 처리
        console.error('아이디 찾기 오류:', error);
        showToast(error.message || '아이디 찾기 중 오류가 발생했습니다.', 'error');
        
    } finally {
        // 버튼 상태 복원
        btnText.style.display = 'block';
        btnLoading.style.display = 'none';
        submitBtn.disabled = false;
        isSubmitting = false;
    }
}

// ===== API 호출 시뮬레이션 =====
function simulateApiCall(method, data) {
    return new Promise((resolve, reject) => {
        setTimeout(() => {
            // 간단한 유효성 검사 시뮬레이션
            if (method === 'email') {
                if (data.name === '테스트' && data.email === 'test@gamehub.com') {
                    resolve({ userId: 'testuser****', joinDate: '2024-01-15' });
                } else if (data.name === '관리자' && data.email === 'admin@gamehub.com') {
                    resolve({ userId: 'admin****', joinDate: '2023-12-01' });
                } else {
                    reject(new Error('입력하신 정보와 일치하는 아이디를 찾을 수 없습니다.'));
                }
            } else {
                if (data.name === '테스트' && data.phone === '010-1234-5678') {
                    resolve({ userId: 'testuser****', joinDate: '2024-01-15' });
                } else {
                    reject(new Error('입력하신 정보와 일치하는 아이디를 찾을 수 없습니다.'));
                }
            }
        }, 2000); // 2초 지연
    });
}

// ===== 결과 표시 =====
function showResult(result) {
    if (!result.success) return;

    // 결과 정보 업데이트
    const foundIdElement = document.getElementById('found-id');
    const joinDateElement = document.getElementById('join-date');
    
    if (foundIdElement) foundIdElement.textContent = result.userId;
    if (joinDateElement) joinDateElement.textContent = result.joinDate;

    // 결과 컨테이너 표시
    if (elements.resultContainer) {
        elements.resultContainer.style.display = 'block';
        elements.resultContainer.scrollIntoView({ 
            behavior: 'smooth', 
            block: 'center' 
        });
        
        // 애니메이션 효과
        elements.resultContainer.style.animation = 'slideInUp 0.6s ease-out';
    }
}

// ===== 폼 리셋 =====
function resetForm() {
    // 결과 숨기기
    if (elements.resultContainer) {
        elements.resultContainer.style.display = 'none';
    }

    // 모든 입력 필드 초기화
    elements.inputs.forEach(input => {
        input.value = '';
        input.classList.remove('error', 'success-state');
        
        // 오류 메시지 숨기기
        const errorElement = document.getElementById(input.getAttribute('aria-describedby'));
        if (errorElement) {
            errorElement.classList.remove('show');
            errorElement.textContent = '';
        }
    });

    // 첫 번째 탭으로 전환
    if (currentTab !== 'email') {
        switchTab('email');
    }

    // 첫 번째 입력 필드에 포커스
    const firstInput = document.querySelector('#email-tab .form-input');
    if (firstInput) {
        setTimeout(() => firstInput.focus(), 300);
    }

    showToast('폼이 초기화되었습니다.', 'info');
}

// 전역 함수로 등록 (HTML에서 호출)
window.resetForm = resetForm;

// ===== 유효성 검사 =====
function validateForm(data, fields) {
    let isValid = true;

    fields.forEach(field => {
        const value = data[field];
        const rule = validationRules[field];
        const input = document.querySelector(`input[name="${field}"]`);
        const errorElement = document.getElementById(input?.getAttribute('aria-describedby'));

        if (!validateField(field, value, rule)) {
            isValid = false;
            showFieldError(input, errorElement, rule.message);
        } else {
            showFieldSuccess(input, errorElement);
        }
    });

    return isValid;
}

function validateField(fieldName, value, rule) {
    if (rule.required && (!value || value.length === 0)) {
        return false;
    }

    if (value && rule.minLength && value.length < rule.minLength) {
        return false;
    }

    if (value && rule.maxLength && value.length > rule.maxLength) {
        return false;
    }

    if (value && rule.pattern && !rule.pattern.test(value)) {
        return false;
    }

    return true;
}

function showFieldError(input, errorElement, message) {
    if (input) {
        input.classList.add('error');
        input.classList.remove('success-state');
    }
    
    if (errorElement) {
        errorElement.textContent = message;
        errorElement.classList.add('show');
    }
}

function showFieldSuccess(input, errorElement) {
    if (input) {
        input.classList.remove('error');
        input.classList.add('success-state');
    }
    
    if (errorElement) {
        errorElement.classList.remove('show');
        errorElement.textContent = '';
    }
}

// ===== 입력 필드 이벤트 처리 =====
function handleInputChange(e) {
    const input = e.target;
    const fieldName = input.name;
    const value = input.value.trim();
    const rule = validationRules[fieldName];
    
    if (!rule) return;

    // 실시간 유효성 검사 (입력 중에는 관대하게)
    if (value.length > 0) {
        const errorElement = document.getElementById(input.getAttribute('aria-describedby'));
        
        if (validateField(fieldName, value, rule)) {
            showFieldSuccess(input, errorElement);
        } else if (value.length >= rule.minLength || fieldName === 'email') {
            showFieldError(input, errorElement, rule.message);
        }
    }
}

function handleInputBlur(e) {
    const input = e.target;
    const fieldName = input.name;
    const value = input.value.trim();
    const rule = validationRules[fieldName];
    
    if (!rule) return;

    const errorElement = document.getElementById(input.getAttribute('aria-describedby'));
    
    if (!validateField(fieldName, value, rule)) {
        showFieldError(input, errorElement, rule.message);
    } else if (value.length > 0) {
        showFieldSuccess(input, errorElement);
    }
}

function handleInputFocus(e) {
    const input = e.target;
    createInputFocusEffect(input);
}

// ===== 휴대폰 번호 포맷팅 =====
function setupPhoneFormatting() {
    const phoneInput = document.getElementById('user-phone');
    if (!phoneInput) return;

    phoneInput.addEventListener('input', function(e) {
        let value = e.target.value.replace(/[^\d]/g, '');
        
        if (value.length <= 3) {
            value = value;
        } else if (value.length <= 7) {
            value = value.slice(0, 3) + '-' + value.slice(3);
        } else {
            value = value.slice(0, 3) + '-' + value.slice(3, 7) + '-' + value.slice(7, 11);
        }
        
        e.target.value = value;
    });

    phoneInput.addEventListener('keydown', function(e) {
        // 백스페이스와 화살표 키는 허용
        if (e.key === 'Backspace' || e.key === 'Delete' || 
            e.key === 'ArrowLeft' || e.key === 'ArrowRight' ||
            e.key === 'Tab') {
            return;
        }
        
        // 숫자만 허용
        if (!/\d/.test(e.key)) {
            e.preventDefault();
        }
    });
}

// ===== 애니메이션 및 효과 =====
function initializeAnimations() {
    // 페이지 로드 애니메이션
    const container = document.querySelector('.find-id-container');
    if (container) {
        container.style.opacity = '0';
        container.style.transform = 'translateY(30px)';
        
        setTimeout(() => {
            container.style.transition = 'all 0.8s ease-out';
            container.style.opacity = '1';
            container.style.transform = 'translateY(0)';
        }, 200);
    }

    // 도움말 섹션 애니메이션
    const helpSection = document.querySelector('.help-section');
    if (helpSection) {
        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    entry.target.style.animation = 'slideInUp 0.8s ease-out';
                    observer.unobserve(entry.target);
                }
            });
        });
        
        observer.observe(helpSection);
    }
}

function createRipple(element, event = null) {
    const rect = element.getBoundingClientRect();
    const ripple = document.createElement('span');
    
    const size = Math.max(rect.width, rect.height);
    const x = event ? event.clientX - rect.left - size / 2 : rect.width / 2 - size / 2;
    const y = event ? event.clientY - rect.top - size / 2 : rect.height / 2 - size / 2;
    
    ripple.style.cssText = `
        position: absolute;
        width: ${size}px;
        height: ${size}px;
        left: ${x}px;
        top: ${y}px;
        background: rgba(255, 255, 255, 0.3);
        border-radius: 50%;
        transform: scale(0);
        animation: ripple 0.6s ease-out;
        pointer-events: none;
        z-index: 1;
    `;
    
    element.style.position = 'relative';
    element.style.overflow = 'hidden';
    element.appendChild(ripple);
    
    setTimeout(() => ripple.remove(), 600);
}

function createInputFocusEffect(input) {
    const wrapper = input.closest('.input-wrapper');
    if (!wrapper) return;

    const effect = document.createElement('div');
    effect.style.cssText = `
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        border: 2px solid rgba(100, 181, 246, 0.3);
        border-radius: 16px;
        pointer-events: none;
        animation: pulse 1s ease-out;
    `;
    
    wrapper.appendChild(effect);
    setTimeout(() => effect.remove(), 1000);
}

// ===== 키보드 네비게이션 =====
function handleKeyboard(e) {
    // ESC로 결과 숨기기
    if (e.key === 'Escape' && elements.resultContainer?.style.display !== 'none') {
        resetForm();
        return;
    }

    // Tab 키로 탭 전환 (Alt + 숫자)
    if (e.altKey && e.key >= '1' && e.key <= '2') {
        e.preventDefault();
        const tabIndex = parseInt(e.key) - 1;
        const tabs = ['email', 'phone'];
        if (tabs[tabIndex]) {
            switchTab(tabs[tabIndex]);
        }
    }

    // Enter로 폼 제출
    if (e.key === 'Enter' && e.target.classList.contains('form-input')) {
        const form = e.target.closest('form');
        if (form && !isSubmitting) {
            e.preventDefault();
            form.dispatchEvent(new Event('submit'));
        }
    }
}

// ===== 리사이즈 처리 =====
function handleResize() {
    // 모바일에서 가상 키보드 대응
    if (window.innerWidth <= 768) {
        const activeInput = document.activeElement;
        if (activeInput && activeInput.classList.contains('form-input')) {
            setTimeout(() => {
                activeInput.scrollIntoView({ 
                    behavior: 'smooth', 
                    block: 'center' 
                });
            }, 300);
        }
    }
}

// ===== 토스트 알림 =====
function showToast(message, type = 'info', duration = 3000) {
    // 기존 토스트 제거
    const existingToast = document.querySelector('.toast-notification');
    if (existingToast) {
        existingToast.remove();
    }

    // 새 토스트 생성
    const toast = document.createElement('div');
    toast.className = 'toast-notification';
    toast.innerHTML = `
        <div class="toast-icon">
            ${type === 'success' ? '✅' : type === 'warning' ? '⚠️' : type === 'error' ? '❌' : 'ℹ️'}
        </div>
        <div class="toast-message">${message}</div>
        <button class="toast-close" aria-label="알림 닫기">×</button>
    `;

    // 스타일 적용
    const colors = {
        success: 'rgba(76, 175, 80, 0.3)',
        warning: 'rgba(255, 193, 7, 0.3)',
        error: 'rgba(244, 67, 54, 0.3)',
        info: 'rgba(100, 181, 246, 0.3)'
    };

    toast.style.cssText = `
        position: fixed;
        top: 100px;
        right: 20px;
        background: linear-gradient(135deg, rgba(20, 20, 40, 0.98), rgba(30, 30, 60, 0.98));
        backdrop-filter: blur(25px);
        border: 1px solid ${colors[type] || colors.info};
        border-radius: 16px;
        padding: 16px 20px;
        color: white;
        box-shadow: 0 20px 60px rgba(0, 0, 0, 0.5);
        z-index: 10000;
        display: flex;
        align-items: center;
        gap: 12px;
        max-width: 400px;
        animation: slideInRight 0.5s ease-out;
        font-family: inherit;
        font-size: 14px;
    `;

    document.body.appendChild(toast);

    // 닫기 버튼 이벤트
    const closeBtn = toast.querySelector('.toast-close');
    closeBtn.style.cssText = `
        background: none;
        border: none;
        color: rgba(255, 255, 255, 0.7);
        cursor: pointer;
        font-size: 18px;
        padding: 0;
        margin-left: auto;
        transition: color 0.3s ease;
    `;

    closeBtn.addEventListener('click', () => removeToast(toast));
    closeBtn.addEventListener('mouseenter', () => {
        closeBtn.style.color = 'white';
    });
    closeBtn.addEventListener('mouseleave', () => {
        closeBtn.style.color = 'rgba(255, 255, 255, 0.7)';
    });

    // 자동 제거
    setTimeout(() => removeToast(toast), duration);
}

function removeToast(toast) {
    if (toast && toast.parentNode) {
        toast.style.animation = 'slideInRight 0.3s ease-out reverse';
        setTimeout(() => {
            if (toast.parentNode) {
                toast.parentNode.removeChild(toast);
            }
        }, 300);
    }
}

// ===== CSS 애니메이션 추가 =====
const style = document.createElement('style');
style.textContent = `
    @keyframes slideInRight {
        from { opacity: 0; transform: translateX(100%); }
        to { opacity: 1; transform: translateX(0); }
    }
    
    .toast-notification {
        animation: slideInRight 0.5s ease-out;
    }
    
    .toast-icon {
        font-size: 16px;
        flex-shrink: 0;
    }
    
    .toast-message {
        flex: 1;
        line-height: 1.4;
    }
    
    .toast-close {
        flex-shrink: 0;
    }
`;
document.head.appendChild(style);

// ===== 에러 핸들링 =====
window.addEventListener('error', (e) => {
    console.error('🚨 JavaScript 오류:', e.error);
    if (isInitialized) {
        showToast('일시적인 오류가 발생했습니다.', 'error');
    }
});

window.addEventListener('unhandledrejection', (e) => {
    console.error('🚨 Promise 거부:', e.reason);
    if (isInitialized) {
        showToast('네트워크 오류가 발생했습니다.', 'error');
    }
});

// ===== 접근성 향상 =====
document.addEventListener('DOMContentLoaded', () => {
    // 라이브 영역 생성
    const liveRegion = document.createElement('div');
    liveRegion.setAttribute('aria-live', 'polite');
    liveRegion.setAttribute('aria-atomic', 'true');
    liveRegion.style.cssText = `
        position: absolute;
        left: -10000px;
        width: 1px;
        height: 1px;
        overflow: hidden;
    `;
    document.body.appendChild(liveRegion);

    // 전역 라이브 영역 업데이트 함수
    window.updateLiveRegion = (message) => {
        liveRegion.textContent = message;
    };
});

// ===== 개발자 도구 =====
if (window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1') {
    console.log(`
    🔍 아이디 찾기 개발 모드
    ========================
    사용 가능한 명령어:
    - showToast(message, type, duration) : 토스트 알림 표시
    - resetForm() : 폼 초기화
    - switchTab(tabName) : 탭 전환 ('email' 또는 'phone')
    
    테스트 데이터:
    - 이름: 테스트, 이메일: test@gamehub.com → 성공
    - 이름: 관리자, 이메일: admin@gamehub.com → 성공
    - 이름: 테스트, 휴대폰: 010-1234-5678 → 성공
    - 기타 정보 → 실패
    
    키보드 단축키:
    - Alt + 1 : 이메일 탭
    - Alt + 2 : 휴대폰 탭
    - ESC : 결과 초기화
    - Enter : 폼 제출
    `);

    // 개발 함수들
    window.FindIdDev = {
        showToast,
        resetForm,
        switchTab,
        elements,
        currentTab,
        validationRules
    };
}

console.log('🚀 아이디 찾기 JavaScript 로드 완료!');
