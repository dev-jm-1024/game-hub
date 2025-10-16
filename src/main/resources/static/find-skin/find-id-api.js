// ===== GameHub 아이디 찾기 API JavaScript =====
// 기존 find-id.js를 오버라이드하여 실제 API 연동

document.addEventListener('DOMContentLoaded', function() {
    console.log('🔍 아이디 찾기 API 오버라이드 모듈 로드!');

    // 기존 이메일 폼 이벤트 제거 후 새로 등록
    const emailForm = document.getElementById('email-form');

    if (emailForm) {
        // 기존 이벤트 리스너 제거를 위해 새 폼으로 교체
        const newForm = emailForm.cloneNode(true);
        emailForm.parentNode.replaceChild(newForm, emailForm);

        // 새 이벤트 리스너 등록
        newForm.addEventListener('submit', handleEmailSubmitAPI);
    }
});

// ===== API 연동 이메일 제출 핸들러 =====
async function handleEmailSubmitAPI(e) {
    e.preventDefault();

    const formData = new FormData(e.target);
    const name = formData.get('name')?.trim();
    const email = formData.get('email')?.trim();

    // 유효성 검사
    if (!validateInputsAPI(name, email)) {
        return;
    }

    // 버튼 상태 변경
    const submitBtn = document.getElementById('email-submit');
    const btnText = submitBtn.querySelector('.btn-text');
    const btnLoading = submitBtn.querySelector('.btn-loading');

    btnText.style.display = 'none';
    btnLoading.style.display = 'flex';
    submitBtn.disabled = true;

    try {
        // 실제 API 호출
        const response = await fetch(`/api/v1/find/id?name=${encodeURIComponent(name)}&email=${encodeURIComponent(email)}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            // 성공 - 아이디 받아오기
            const userId = await response.text();
            showResultAPI(userId);
            showToast('아이디를 성공적으로 찾았습니다!', 'success');
        } else if (response.status === 404) {
            // 회원 없음
            const message = await response.text();
            showToast(message, 'error');
            clearInputErrorsAPI();
        } else {
            // 기타 오류
            showToast('아이디 찾기 중 오류가 발생했습니다.', 'error');
        }

    } catch (error) {
        console.error('API 호출 오류:', error);
        showToast('네트워크 오류가 발생했습니다. 잠시 후 다시 시도해주세요.', 'error');
    } finally {
        // 버튼 상태 복원
        btnText.style.display = 'block';
        btnLoading.style.display = 'none';
        submitBtn.disabled = false;
    }
}

// ===== 유효성 검사 =====
function validateInputsAPI(name, email) {
    let isValid = true;

    // 이름 검사
    const nameInput = document.getElementById('user-name');
    const nameError = document.getElementById('name-error');

    if (!name || name.length < 2) {
        showFieldErrorAPI(nameInput, nameError, '닉네임은 2자 이상 입력해주세요.');
        isValid = false;
    } else {
        clearFieldErrorAPI(nameInput, nameError);
    }

    // 이메일 검사
    const emailInput = document.getElementById('user-email');
    const emailError = document.getElementById('email-error');
    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (!email || !emailPattern.test(email)) {
        showFieldErrorAPI(emailInput, emailError, '올바른 이메일 주소를 입력해주세요.');
        isValid = false;
    } else {
        clearFieldErrorAPI(emailInput, emailError);
    }

    return isValid;
}

// ===== 필드 오류 표시 =====
function showFieldErrorAPI(input, errorElement, message) {
    if (input) {
        input.classList.add('error');
        input.classList.remove('success-state');
    }

    if (errorElement) {
        errorElement.textContent = message;
        errorElement.classList.add('show');
    }
}

function clearFieldErrorAPI(input, errorElement) {
    if (input) {
        input.classList.remove('error');
        input.classList.add('success-state');
    }

    if (errorElement) {
        errorElement.classList.remove('show');
        errorElement.textContent = '';
    }
}

function clearInputErrorsAPI() {
    const inputs = document.querySelectorAll('.form-input');
    inputs.forEach(input => {
        input.classList.remove('error', 'success-state');
    });

    const errors = document.querySelectorAll('.error-message');
    errors.forEach(error => {
        error.classList.remove('show');
        error.textContent = '';
    });
}

// ===== 결과 표시 =====
function showResultAPI(userId) {
    const resultContainer = document.getElementById('result-container');
    const foundIdElement = document.getElementById('found-id');

    if (foundIdElement) {
        foundIdElement.textContent = userId;
    }

    // 가입일은 API에서 안 주니까 숨김
    const joinDateElement = document.getElementById('join-date');
    if (joinDateElement && joinDateElement.parentElement) {
        joinDateElement.parentElement.style.display = 'none';
    }

    if (resultContainer) {
        resultContainer.style.display = 'block';
        resultContainer.scrollIntoView({
            behavior: 'smooth',
            block: 'center'
        });
    }
}

// ===== resetForm 오버라이드 (전역 함수) =====
window.resetForm = function() {
    const resultContainer = document.getElementById('result-container');
    if (resultContainer) {
        resultContainer.style.display = 'none';
    }

    const emailForm = document.getElementById('email-form');
    if (emailForm) {
        emailForm.reset();
    }

    clearInputErrorsAPI();

    const firstInput = document.getElementById('user-name');
    if (firstInput) {
        setTimeout(() => firstInput.focus(), 300);
    }

    // showToast는 기존 find-id.js의 함수 사용
    if (typeof showToast === 'function') {
        showToast('폼이 초기화되었습니다.', 'info');
    }
};

console.log('🚀 아이디 찾기 API 오버라이드 완료!');