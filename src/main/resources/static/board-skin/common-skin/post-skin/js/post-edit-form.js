// ===== 게시글 수정 폼 JavaScript - 글래스모피즘 디자인 =====

// 전역 변수
let isSubmitting = false;
let originalData = {};
let hasChanges = false;

// DOM 요소들
const postForm = document.getElementById('postForm');
const titleInput = document.getElementById('title');
const contentTextarea = document.getElementById('content');
const boardIdInput = document.querySelector('input[name="boardId"]');
const postIdInput = document.querySelector('input[name="postId"]');
const submitButton = document.querySelector('.submit-button');
const cancelButton = document.querySelector('.cancel-button');

// 초기화 함수
document.addEventListener('DOMContentLoaded', function() {
    initializeTinyMCE();
    initializeEventListeners();
    initializeValidation();
    storeOriginalData();
    setupAccessibility();
    addInteractiveEffects();

    console.log('✏️ 게시글 수정 시스템이 초기화되었습니다.');

    // 개발자 도구 안내
    console.log('%c개발자 도구 명령어:', 'color: #3f51b5; font-weight: bold; font-size: 14px;');
    console.log('%c• validateForm() - 폼 유효성 검사', 'color: #7986cb;');
    console.log('%c• resetForm() - 폼 초기화', 'color: #7986cb;');
    console.log('%c• submitToApi() - 실제 API 제출', 'color: #7986cb;');
    console.log('%c• checkChanges() - 변경사항 확인', 'color: #7986cb;');
});

// TinyMCE 초기화
function initializeTinyMCE() {
    tinymce.init({
        selector: '#content',
        height: 400,
        language: 'ko_KR',
        plugins: [
            'advlist', 'autolink', 'lists', 'link', 'image', 'charmap', 'preview',
            'anchor', 'searchreplace', 'visualblocks', 'code', 'fullscreen',
            'insertdatetime', 'media', 'table', 'help', 'wordcount', 'emoticons'
        ],
        toolbar: 'undo redo | blocks | bold italic forecolor backcolor | ' +
            'alignleft aligncenter alignright alignjustify | ' +
            'bullist numlist outdent indent | removeformat | ' +
            'link image media table | code preview fullscreen | emoticons',
        content_style: `
            body {
                font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
                font-size: 14px;
                line-height: 1.6;
                background: transparent;
                color: #333;
            }
        `,
        // 이미지 업로드 설정
        automatic_uploads: true,
        file_picker_types: 'image',
        file_picker_callback: function (callback, value, meta) {
            if (meta.filetype === 'image') {
                const input = document.createElement('input');
                input.setAttribute('type', 'file');
                input.setAttribute('accept', 'image/*');

                input.onchange = function () {
                    const file = this.files[0];
                    const reader = new FileReader();
                    reader.onload = function () {
                        callback(reader.result, {
                            alt: file.name
                        });
                    };
                    reader.readAsDataURL(file);
                };

                input.click();
            }
        },
        setup: function (editor) {
            editor.on('change', function () {
                editor.save();
                hasChanges = true;
            });

            editor.on('init', function () {
                // TinyMCE 로드 후 원본 데이터 저장
                setTimeout(storeOriginalData, 100);
            });
        },
        // 스킨 설정 (글래스모피즘과 어울리게)
        skin: 'oxide-dark',
        content_css: 'dark'
    });
}

// 이벤트 리스너 초기화
function initializeEventListeners() {
    // 폼 제출 이벤트
    postForm.addEventListener('submit', handleFormSubmit);

    // 입력 필드 이벤트
    titleInput.addEventListener('input', handleTitleInput);
    titleInput.addEventListener('blur', () => validateTitle());

    // 변경사항 감지
    titleInput.addEventListener('input', () => hasChanges = true);

    // 키보드 단축키
    document.addEventListener('keydown', handleKeyboardShortcuts);

    // 페이지 벗어날 때 경고
    window.addEventListener('beforeunload', handleBeforeUnload);
}

// 원본 데이터 저장
function storeOriginalData() {
    originalData = {
        title: titleInput.value.trim(),
        content: tinymce.get('content') ? tinymce.get('content').getContent() : contentTextarea.value.trim(),
        boardId: boardIdInput.value,
        postId: postIdInput.value
    };

    console.log('📄 원본 데이터가 저장되었습니다:', originalData);
}

// 변경사항 확인
function checkChanges() {
    if (!originalData.title) return false;

    const currentData = {
        title: titleInput.value.trim(),
        content: tinymce.get('content') ? tinymce.get('content').getContent() : contentTextarea.value.trim(),
        boardId: boardIdInput.value,
        postId: postIdInput.value
    };

    const changed =
        currentData.title !== originalData.title ||
        currentData.content !== originalData.content;

    hasChanges = changed;
    return changed;
}

// 페이지 벗어날 때 경고
function handleBeforeUnload(e) {
    if (checkChanges() && !isSubmitting) {
        e.preventDefault();
        e.returnValue = '수정 중인 내용이 있습니다. 정말 페이지를 벗어나시겠습니까?';
        return e.returnValue;
    }
}

// 폼 제출 처리
async function handleFormSubmit(e) {
    e.preventDefault();

    if (isSubmitting) return;

    console.log('📝 폼 제출 시작...');

    // TinyMCE 내용 저장
    if (tinymce.get('content')) {
        tinymce.get('content').save();
    }

    // 유효성 검사
    if (!validateForm()) {
        showToast('입력 내용을 확인해주세요.', 'error');
        return;
    }

    // 변경사항 확인
    if (!checkChanges()) {
        showToast('변경된 내용이 없습니다.', 'info');
        return;
    }

    try {
        isSubmitting = true;
        setLoadingState(true);

        const formData = collectFormData();
        console.log('📤 제출 데이터:', formData);

        // 실제 API 호출
        await submitToApi(formData);

        showToast('게시글이 성공적으로 수정되었습니다! 🎉', 'success');

        // 성공 후 처리
        hasChanges = false;
        setTimeout(() => {
            window.location.href = "/board";
        }, 1500);

    } catch (error) {
        console.error('❌ 제출 오류:', error);
        showToast('수정 중 오류가 발생했습니다. 다시 시도해주세요.', 'error');
    } finally {
        isSubmitting = false;
        setLoadingState(false);
    }
}

// 제목 입력 처리
function handleTitleInput(e) {
    const title = e.target.value;

    // 실시간 글자 수 표시
    updateCharCount(e.target, title.length, 100);

    // 실시간 검증
    if (title.length > 0) {
        validateTitle();
    }
}

// 키보드 단축키 처리
function handleKeyboardShortcuts(e) {
    // Ctrl + Enter: 폼 제출
    if ((e.ctrlKey || e.metaKey) && e.key === 'Enter') {
        e.preventDefault();
        if (!isSubmitting) {
            postForm.dispatchEvent(new Event('submit'));
        }
    }

    // Ctrl + R: 폼 초기화 (개발자 도구용)
    if ((e.ctrlKey || e.metaKey) && e.key === 'r' && e.shiftKey) {
        e.preventDefault();
        resetForm();
    }

    // ESC: 취소
    if (e.key === 'Escape') {
        if (confirm('수정을 취소하시겠습니까?')) {
            history.back();
        }
    }
}

// 폼 유효성 검사
function validateForm() {
    console.log('🔍 폼 유효성 검사 시작...');

    const titleValid = validateTitle();
    const contentValid = validateContent();

    const isValid = titleValid && contentValid;
    console.log(`✅ 검증 결과: ${isValid ? '통과' : '실패'}`);

    return isValid;
}

// 제목 검증
function validateTitle() {
    const title = titleInput.value.trim();

    if (!title) {
        showFieldError(titleInput, '제목을 입력해주세요.');
        return false;
    }

    if (title.length < 2) {
        showFieldError(titleInput, '제목은 2자 이상 입력해주세요.');
        return false;
    }

    if (title.length > 100) {
        showFieldError(titleInput, '제목은 100자 이하로 입력해주세요.');
        return false;
    }

    showFieldSuccess(titleInput);
    return true;
}

// 내용 검증
function validateContent() {
    // TinyMCE에서 내용 가져오기
    const content = tinymce.get('content') ? tinymce.get('content').getContent() : contentTextarea.value;
    const textContent = content.replace(/<[^>]*>/g, '').trim(); // HTML 태그 제거

    if (!textContent) {
        showFieldError(contentTextarea, '내용을 입력해주세요.');
        return false;
    }

    if (textContent.length < 10) {
        showFieldError(contentTextarea, '내용은 10자 이상 입력해주세요.');
        return false;
    }

    if (textContent.length > 5000) {
        showFieldError(contentTextarea, '내용은 5000자 이하로 입력해주세요.');
        return false;
    }

    showFieldSuccess(contentTextarea);
    return true;
}

// 필드 에러 표시
function showFieldError(field, message) {
    const formGroup = field.closest('.form-group');

    // 기존 메시지 제거
    const existingMessage = formGroup.querySelector('.error-message, .success-message');
    if (existingMessage) {
        existingMessage.remove();
    }

    // 에러 상태 추가
    formGroup.classList.remove('success');
    formGroup.classList.add('error');

    // 에러 메시지 추가
    const errorDiv = document.createElement('div');
    errorDiv.className = 'error-message';
    errorDiv.textContent = message;
    formGroup.appendChild(errorDiv);

    // 필드 포커스
    field.focus();
}

// 필드 성공 표시
function showFieldSuccess(field) {
    const formGroup = field.closest('.form-group');

    // 기존 메시지 제거
    const existingMessage = formGroup.querySelector('.error-message, .success-message');
    if (existingMessage) {
        existingMessage.remove();
    }

    // 성공 상태 추가
    formGroup.classList.remove('error');
    formGroup.classList.add('success');
}

// 로딩 상태 설정
function setLoadingState(loading) {
    if (loading) {
        submitButton.disabled = true;
        submitButton.classList.add('loading');
        submitButton.textContent = '수정 중...';

        cancelButton.disabled = true;
        cancelButton.style.opacity = '0.5';
    } else {
        submitButton.disabled = false;
        submitButton.classList.remove('loading');
        submitButton.textContent = '수정 완료';

        cancelButton.disabled = false;
        cancelButton.style.opacity = '1';
    }
}

// 폼 데이터 수집
function collectFormData() {
    // TinyMCE 내용 저장
    if (tinymce.get('content')) {
        tinymce.get('content').save();
    }

    return {
        postId: postIdInput.value,
        title: titleInput.value.trim(),
        content: tinymce.get('content') ? tinymce.get('content').getContent() : contentTextarea.value.trim(),
        boardId: boardIdInput.value,
        timestamp: new Date().toISOString()
    };
}

// 실제 API 제출
async function submitToApi(formData) {
    // CSRF 토큰 가져오기
    const csrfToken = document.querySelector('meta[name="_csrf"]')?.content || '';
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content || '';

    const data = {
        postTitle: formData.title,
        postContent: formData.content,
        boardId: formData.boardId,
        postId: formData.postId
    };

    const response = await fetch(`/api/v1/board/${data.boardId}/posts/${data.postId}`, {
        method: "PATCH",
        headers: {
            "Content-Type": "application/json",
            [csrfHeader]: csrfToken
        },
        credentials: "include",
        body: JSON.stringify(data)
    });

    if (response.ok) {
        console.log('✅ API 응답: 수정 성공');
        return {
            success: true,
            message: '게시글이 성공적으로 수정되었습니다.',
            data: formData
        };
    } else {
        const errorText = await response.text();
        throw new Error(errorText || '수정 실패');
    }
}

// 폼 초기화
function resetForm() {
    if (!confirm('작성 중인 내용이 모두 삭제됩니다. 계속하시겠습니까?')) {
        return;
    }

    titleInput.value = originalData.title || '';

    if (tinymce.get('content')) {
        tinymce.get('content').setContent(originalData.content || '');
    } else {
        contentTextarea.value = originalData.content || '';
    }

    // 에러 상태 제거
    document.querySelectorAll('.form-group').forEach(group => {
        group.classList.remove('error', 'success');
        const message = group.querySelector('.error-message, .success-message');
        if (message) message.remove();
    });

    hasChanges = false;
    showToast('폼이 초기화되었습니다.', 'info');
    console.log('🔄 폼이 초기화되었습니다.');
}

// 글자 수 표시 업데이트
function updateCharCount(element, current, max) {
    let countElement = element.parentNode.querySelector('.char-count');

    if (!countElement) {
        countElement = document.createElement('div');
        countElement.className = 'char-count';
        countElement.style.cssText = `
            position: absolute;
            right: 16px;
            top: 12px;
            font-size: 0.75rem;
            color: rgba(255, 255, 255, 0.6);
            pointer-events: none;
            z-index: 1;
        `;
        element.parentNode.style.position = 'relative';
        element.parentNode.appendChild(countElement);
    }

    countElement.textContent = `${current}/${max}`;

    // 글자 수에 따른 색상 변경
    if (current > max * 0.9) {
        countElement.style.color = '#ff6b6b';
    } else if (current > max * 0.7) {
        countElement.style.color = '#ffa726';
    } else {
        countElement.style.color = 'rgba(255, 255, 255, 0.6)';
    }
}

// 토스트 메시지 표시
function showToast(message, type = 'info', duration = 3000) {
    // 기존 토스트 제거
    const existingToast = document.querySelector('.toast-message');
    if (existingToast) {
        existingToast.remove();
    }

    const toast = document.createElement('div');
    toast.className = `toast-message toast-${type}`;

    const colors = {
        success: 'rgba(76, 175, 80, 0.9)',
        error: 'rgba(244, 67, 54, 0.9)',
        warning: 'rgba(255, 152, 0, 0.9)',
        info: 'rgba(33, 150, 243, 0.9)'
    };

    const icons = {
        success: '✅',
        error: '❌',
        warning: '⚠️',
        info: 'ℹ️'
    };

    toast.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        background: ${colors[type]};
        backdrop-filter: blur(20px);
        color: white;
        padding: 16px 24px;
        border-radius: 12px;
        box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
        z-index: 10000;
        font-weight: 500;
        transform: translateX(100%);
        transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        max-width: 400px;
        word-wrap: break-word;
    `;

    toast.innerHTML = `${icons[type]} ${message}`;
    document.body.appendChild(toast);

    // 애니메이션
    setTimeout(() => {
        toast.style.transform = 'translateX(0)';
    }, 10);

    // 자동 제거
    setTimeout(() => {
        toast.style.transform = 'translateX(100%)';
        setTimeout(() => {
            if (toast.parentNode) {
                toast.parentNode.removeChild(toast);
            }
        }, 300);
    }, duration);

    console.log(`📢 토스트: ${message}`);
}

// 접근성 설정
function setupAccessibility() {
    // 키보드 네비게이션
    const focusableElements = postForm.querySelectorAll(
        'input:not([disabled]), textarea:not([disabled]), button:not([disabled]), select:not([disabled])'
    );

    focusableElements.forEach((element, index) => {
        element.addEventListener('keydown', (e) => {
            if (e.key === 'Tab') {
                // Tab 순서 관리는 브라우저에 위임
                return;
            }

            if (e.key === 'Enter' && element.tagName !== 'TEXTAREA') {
                e.preventDefault();
                const nextElement = focusableElements[index + 1];
                if (nextElement) {
                    nextElement.focus();
                } else {
                    submitButton.focus();
                }
            }
        });
    });

    // ARIA 라벨 설정
    titleInput.setAttribute('aria-describedby', 'title-help');
    contentTextarea.setAttribute('aria-describedby', 'content-help');

    console.log('♿ 접근성 기능이 설정되었습니다.');
}

// 인터랙티브 효과 추가
function addInteractiveEffects() {
    // 리플 효과
    [submitButton, cancelButton].forEach(button => {
        button.addEventListener('click', createRippleEffect);
    });

    // 입력 필드 포커스 효과
    [titleInput, contentTextarea].forEach(input => {
        input.addEventListener('focus', () => {
            input.parentNode.style.transform = 'translateY(-2px)';
        });

        input.addEventListener('blur', () => {
            input.parentNode.style.transform = 'translateY(0)';
        });
    });

    console.log('✨ 인터랙티브 효과가 추가되었습니다.');
}

// 리플 효과 생성
function createRippleEffect(e) {
    const button = e.currentTarget;
    const ripple = document.createElement('span');

    const rect = button.getBoundingClientRect();
    const size = Math.max(rect.width, rect.height);
    const x = e.clientX - rect.left - size / 2;
    const y = e.clientY - rect.top - size / 2;

    ripple.style.cssText = `
        position: absolute;
        width: ${size}px;
        height: ${size}px;
        left: ${x}px;
        top: ${y}px;
        background: rgba(255, 255, 255, 0.3);
        border-radius: 50%;
        transform: scale(0);
        animation: ripple 0.6s linear;
        pointer-events: none;
    `;

    button.appendChild(ripple);

    setTimeout(() => {
        ripple.remove();
    }, 600);
}

// 리플 애니메이션 CSS 추가
function addRippleStyles() {
    if (document.getElementById('ripple-styles')) return;

    const style = document.createElement('style');
    style.id = 'ripple-styles';
    style.textContent = `
        @keyframes ripple {
            to {
                transform: scale(4);
                opacity: 0;
            }
        }
        
        .submit-button,
        .cancel-button {
            position: relative;
            overflow: hidden;
        }
    `;
    document.head.appendChild(style);
}

// 스타일 초기화
addRippleStyles();

// 전역 함수로 노출 (개발자 도구용)
window.validateForm = validateForm;
window.resetForm = resetForm;
window.submitToApi = submitToApi;
window.checkChanges = checkChanges;
