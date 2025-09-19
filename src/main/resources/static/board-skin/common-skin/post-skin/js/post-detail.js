// ===== 게시글 상세 페이지 JavaScript - 글래스모피즘 디자인 =====

// 전역 변수
let isReacting = false;
let isDeleting = false;

// DOM 요소들
const likeButton = document.getElementById('like-button');
const dislikeButton = document.getElementById('dislike-button');
const likeCount = document.getElementById('like-count');
const dislikeCount = document.getElementById('dislike-count');
const likeIcon = document.getElementById('like-icon');
const dislikeIcon = document.getElementById('dislike-icon');
const likeText = document.getElementById('like-text');
const dislikeText = document.getElementById('dislike-text');
const deleteForm = document.getElementById('deletePosts');

// 초기화 함수
document.addEventListener('DOMContentLoaded', function() {
    initializeEventListeners();
    initializeReactionButtons();
    setupAccessibility();
    addInteractiveEffects();

    console.log('📖 게시글 상세 시스템이 초기화되었습니다.');
    console.log('📊 서버 데이터:', window.postDetailData);

    // 개발자 도구 안내
    console.log('%c개발자 도구 명령어:', 'color: #3f51b5; font-weight: bold; font-size: 14px;');
    console.log('%c• callRealReactionAPI(postId, type) - 실제 반응 API', 'color: #7986cb;');
    console.log('%c• getCurrentReactionState() - 현재 반응 상태', 'color: #7986cb;');
    console.log('%c• resetReactions() - 반응 초기화', 'color: #7986cb;');
});

// 이벤트 리스너 초기화
function initializeEventListeners() {
    // 좋아요/싫어요 버튼 이벤트
    if (likeButton) {
        likeButton.addEventListener('click', () => handleReaction('LIKE'));
    }

    if (dislikeButton) {
        dislikeButton.addEventListener('click', () => handleReaction('DISLIKE'));
    }

    // 삭제 폼 이벤트
    if (deleteForm) {
        deleteForm.addEventListener('submit', handleDeletePost);
    }

    // 이미지 클릭 이벤트 (확대)
    const postImages = document.querySelectorAll('.post-image');
    postImages.forEach(img => {
        img.addEventListener('click', () => showImageModal(img.src, img.alt));
        img.style.cursor = 'pointer';
    });

    // 키보드 단축키
    document.addEventListener('keydown', handleKeyboardShortcuts);
}

// 반응 버튼 초기화
function initializeReactionButtons() {
    if (!window.postDetailData) {
        console.warn('⚠️ 서버 데이터가 없습니다.');
        return;
    }

    const { reactType, likeCount: likes, dislikeCount: dislikes, isLoggedIn } = window.postDetailData;

    // 로그인하지 않은 경우 버튼 비활성화
    if (!isLoggedIn) {
        if (likeButton) likeButton.disabled = true;
        if (dislikeButton) dislikeButton.disabled = true;
        return;
    }

    // 현재 반응 상태 적용
    updateReactionUI(reactType, likes, dislikes);

    console.log(`👍 현재 반응 상태: ${reactType}, 좋아요: ${likes}, 싫어요: ${dislikes}`);
}

// 반응 처리
async function handleReaction(type) {
    if (isReacting) return;

    // 로그인 체크
    if (!window.postDetailData || !window.postDetailData.isLoggedIn) {
        showToast('로그인이 필요합니다.', 'warning');
        return;
    }

    const currentReaction = window.postDetailData.reactType;
    const postId = window.postDetailData.postId;

    console.log(`🎯 반응 처리: ${type}, 현재 상태: ${currentReaction}`);

    try {
        isReacting = true;
        setReactionLoadingState(true);

        // 반응 로직 결정
        let newReaction;
        if (currentReaction === type) {
            // 같은 반응 클릭 시 취소
            newReaction = 'NONE';
        } else {
            // 다른 반응 또는 처음 반응
            newReaction = type;
        }

        // 실제 API 호출
        const result = await callRealReactionAPI(postId, newReaction);

        // UI 업데이트
        window.postDetailData.reactType = newReaction;
        window.postDetailData.likeCount = result.likeCount;
        window.postDetailData.dislikeCount = result.dislikeCount;

        updateReactionUI(newReaction, result.likeCount, result.dislikeCount);

        // 성공 메시지
        const messages = {
            'LIKE': '👍 좋아요를 눌렀습니다!',
            'DISLIKE': '👎 싫어요를 눌렀습니다!',
            'NONE': '반응이 취소되었습니다.'
        };

        showToast(messages[newReaction], 'success');

        // 버튼 애니메이션
        animateReactionButton(type);

    } catch (error) {
        console.error('❌ 반응 처리 오류:', error);
        showToast('반응 처리 중 오류가 발생했습니다.', 'error');
    } finally {
        isReacting = false;
        setReactionLoadingState(false);
    }
}

// 반응 UI 업데이트
function updateReactionUI(reactType, likes, dislikes) {
    if (!likeButton || !dislikeButton) return;

    // 좋아요 버튼 상태
    if (reactType === 'LIKE') {
        likeButton.classList.add('liked');
        if (likeIcon) likeIcon.textContent = '💔';
        if (likeText) likeText.textContent = '좋아요 취소';
    } else {
        likeButton.classList.remove('liked');
        if (likeIcon) likeIcon.textContent = '❤️';
        if (likeText) likeText.textContent = '좋아요';
    }

    // 싫어요 버튼 상태
    if (reactType === 'DISLIKE') {
        dislikeButton.classList.add('disliked');
        if (dislikeIcon) dislikeIcon.textContent = '👍';
        if (dislikeText) dislikeText.textContent = '싫어요 취소';
    } else {
        dislikeButton.classList.remove('disliked');
        if (dislikeIcon) dislikeIcon.textContent = '👎';
        if (dislikeText) dislikeText.textContent = '싫어요';
    }

    // 카운트 업데이트
    if (likeCount) likeCount.textContent = likes;
    if (dislikeCount) dislikeCount.textContent = dislikes;

    // 통계 업데이트
    updateReactionStats(likes, dislikes);
}

// 반응 통계 업데이트
function updateReactionStats(likes, dislikes) {
    const statsElement = document.querySelector('.reaction-stats');
    if (statsElement) {
        const total = likes + dislikes;
        statsElement.innerHTML = `
            총 반응: <span>${total}</span>개
            (좋아요 <span>${likes}</span>개,
            싫어요 <span>${dislikes}</span>개)
        `;
    }
}

// 반응 로딩 상태 설정
function setReactionLoadingState(loading) {
    if (likeButton) {
        likeButton.disabled = loading;
        if (loading) {
            likeButton.style.opacity = '0.7';
        } else {
            likeButton.style.opacity = '1';
        }
    }

    if (dislikeButton) {
        dislikeButton.disabled = loading;
        if (loading) {
            dislikeButton.style.opacity = '0.7';
        } else {
            dislikeButton.style.opacity = '1';
        }
    }
}

// 반응 버튼 애니메이션
function animateReactionButton(type) {
    const button = type === 'LIKE' ? likeButton : dislikeButton;
    if (!button) return;

    // 펄스 애니메이션
    button.style.animation = 'pulse 0.6s ease-in-out';

    setTimeout(() => {
        button.style.animation = '';
    }, 600);
}

// 게시글 삭제 처리
async function handleDeletePost(e) {
    e.preventDefault();

    if (isDeleting) return;

    // 확인 대화상자
    if (!confirm('정말로 이 게시글을 삭제하시겠습니까?\n삭제된 게시글은 복구할 수 없습니다.')) {
        return;
    }

    const formData = new FormData(deleteForm);
    const postId = formData.get('postId');
    const boardId = formData.get('boardId');

    console.log(`🗑️ 게시글 삭제 시작: postId=${postId}, boardId=${boardId}`);

    try {
        isDeleting = true;
        setDeleteLoadingState(true);

        // 실제 API 호출
        await callRealDeleteAPI(postId);

        showToast('게시글이 성공적으로 삭제되었습니다.', 'success');

        // 목록으로 리디렉션
        setTimeout(() => {
            window.location.href = `/board/${boardId}/view`;
        }, 1500);

    } catch (error) {
        console.error('❌ 삭제 오류:', error);
        showToast('게시글 삭제 중 오류가 발생했습니다.', 'error');
    } finally {
        isDeleting = false;
        setDeleteLoadingState(false);
    }
}

// 삭제 로딩 상태 설정
function setDeleteLoadingState(loading) {
    const deleteButton = deleteForm?.querySelector('.delete-button');
    if (!deleteButton) return;

    if (loading) {
        deleteButton.disabled = true;
        deleteButton.textContent = '삭제 중...';
        deleteButton.style.opacity = '0.7';
    } else {
        deleteButton.disabled = false;
        deleteButton.textContent = '삭제하기';
        deleteButton.style.opacity = '1';
    }
}

// 이미지 모달 표시
function showImageModal(src, alt) {
    // 기존 모달 제거
    const existingModal = document.querySelector('.image-modal');
    if (existingModal) {
        existingModal.remove();
    }

    const modal = document.createElement('div');
    modal.className = 'image-modal';
    modal.style.cssText = `
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: rgba(0, 0, 0, 0.8);
        backdrop-filter: blur(20px);
        display: flex;
        justify-content: center;
        align-items: center;
        z-index: 10000;
        cursor: pointer;
        opacity: 0;
        transition: opacity 0.3s ease;
    `;

    const img = document.createElement('img');
    img.src = src;
    img.alt = alt;
    img.style.cssText = `
        max-width: 90%;
        max-height: 90%;
        border-radius: 16px;
        box-shadow: 0 16px 64px rgba(0, 0, 0, 0.5);
        transform: scale(0.8);
        transition: transform 0.3s ease;
    `;

    modal.appendChild(img);
    document.body.appendChild(modal);

    // 애니메이션
    setTimeout(() => {
        modal.style.opacity = '1';
        img.style.transform = 'scale(1)';
    }, 10);

    // 클릭으로 닫기
    modal.addEventListener('click', () => {
        modal.style.opacity = '0';
        img.style.transform = 'scale(0.8)';
        setTimeout(() => modal.remove(), 300);
    });

    // ESC로 닫기
    const handleEscape = (e) => {
        if (e.key === 'Escape') {
            modal.click();
            document.removeEventListener('keydown', handleEscape);
        }
    };
    document.addEventListener('keydown', handleEscape);
}

// 키보드 단축키 처리
function handleKeyboardShortcuts(e) {
    // 모달이 열려있으면 단축키 비활성화
    if (document.querySelector('.image-modal')) return;

    // L: 좋아요
    if (e.key === 'l' || e.key === 'L') {
        e.preventDefault();
        if (likeButton && !likeButton.disabled) {
            likeButton.click();
        }
    }

    // D: 싫어요
    if (e.key === 'd' || e.key === 'D') {
        e.preventDefault();
        if (dislikeButton && !dislikeButton.disabled) {
            dislikeButton.click();
        }
    }

    // B: 뒤로가기
    if (e.key === 'b' || e.key === 'B') {
        e.preventDefault();
        const backButton = document.querySelector('.back-button');
        if (backButton) {
            backButton.click();
        }
    }
}

// 실제 API 호출 함수
async function callRealReactionAPI(postId, reactionType) {
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    let url, method;

    if (reactionType === 'LIKE') {
        url = `/api/v1/board/posts/${postId}/reactions/likes`;
        method = 'POST';
    } else if (reactionType === 'DISLIKE') {
        url = `/api/v1/board/posts/${postId}/reactions/dislikes`;
        method = 'POST';
    } else if (reactionType === 'NONE') {
        // 현재 반응에 따라 취소 URL 결정
        const currentReaction = window.postDetailData.reactType;
        if (currentReaction === 'LIKE') {
            url = `/api/v1/board/posts/${postId}/reactions/likes`;
            method = 'DELETE';
        } else if (currentReaction === 'DISLIKE') {
            url = `/api/v1/board/posts/${postId}/reactions/dislikes`;
            method = 'DELETE';
        }
    }

    const headers = {
        'Content-Type': 'application/json',
        'X-Requested-With': 'XMLHttpRequest'
    };
    headers[csrfHeader] = csrfToken;

    const response = await fetch(url, {
        method: method,
        headers: headers
    });

    if (!response.ok) {
        throw new Error('API 호출 실패');
    }

    // 현재 카운트 업데이트 후 반환
    const reactionCountResponse = await fetch(`/api/v1/board/posts/${postId}/reactions`);
    const reactionData = await reactionCountResponse.json();

    return {
        success: true,
        likeCount: reactionData.likeCount,
        dislikeCount: reactionData.dislikeCount,
        userReaction: reactionType
    };
}

// 실제 삭제 API 호출 함수
async function callRealDeleteAPI(postId) {
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    const headers = {
        'Content-Type': 'application/json',
        'X-Requested-With': 'XMLHttpRequest'
    };
    headers[csrfHeader] = csrfToken;

    const response = await fetch(`/api/v1/board/posts/${postId}`, {
        method: 'DELETE',
        headers: headers
    });

    if (!response.ok) {
        throw new Error('삭제 권한이 없거나 서버 오류가 발생했습니다.');
    }

    return { success: true };
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
    // 키보드 네비게이션을 위한 tabindex 설정
    const interactiveElements = document.querySelectorAll(
        '.like-button, .dislike-button, .edit-button, .delete-button, .back-button, .post-image'
    );

    interactiveElements.forEach((element, index) => {
        if (!element.hasAttribute('tabindex')) {
            element.setAttribute('tabindex', '0');
        }

        // Enter 키로 클릭 가능하게
        element.addEventListener('keydown', (e) => {
            if (e.key === 'Enter' || e.key === ' ') {
                e.preventDefault();
                element.click();
            }
        });
    });

    // ARIA 라벨 개선
    if (likeButton) {
        likeButton.setAttribute('aria-describedby', 'like-count');
    }
    if (dislikeButton) {
        dislikeButton.setAttribute('aria-describedby', 'dislike-count');
    }

    console.log('♿ 접근성 기능이 설정되었습니다.');
}

// 인터랙티브 효과 추가
function addInteractiveEffects() {
    // 리플 효과
    const buttons = document.querySelectorAll(
        '.like-button, .dislike-button, .edit-button, .delete-button'
    );

    buttons.forEach(button => {
        button.addEventListener('click', createRippleEffect);
    });

    // 스크롤 애니메이션
    const observer = new IntersectionObserver(
        (entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    entry.target.style.opacity = '1';
                    entry.target.style.transform = 'translateY(0)';
                }
            });
        },
        { threshold: 0.1 }
    );

    // 관찰할 요소들
    const animatedElements = document.querySelectorAll(
        '.post-header, .post-content-wrapper, .post-reactions, .comment-section'
    );

    animatedElements.forEach(element => {
        element.style.opacity = '0';
        element.style.transform = 'translateY(20px)';
        element.style.transition = 'opacity 0.6s ease, transform 0.6s ease';
        observer.observe(element);
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

// 현재 반응 상태 반환 (개발자 도구용)
function getCurrentReactionState() {
    return {
        postId: window.postDetailData?.postId,
        reactType: window.postDetailData?.reactType,
        likeCount: window.postDetailData?.likeCount,
        dislikeCount: window.postDetailData?.dislikeCount,
        isLoggedIn: window.postDetailData?.isLoggedIn
    };
}

// 반응 초기화 (개발자 도구용)
function resetReactions() {
    if (window.postDetailData) {
        window.postDetailData.reactType = 'NONE';
        window.postDetailData.likeCount = 0;
        window.postDetailData.dislikeCount = 0;
        updateReactionUI('NONE', 0, 0);
        showToast('반응이 초기화되었습니다.', 'info');
    }
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
        
        .like-button,
        .dislike-button,
        .edit-button,
        .delete-button {
            position: relative;
            overflow: hidden;
        }
    `;
    document.head.appendChild(style);
}

// 스타일 초기화
addRippleStyles();

// 전역 함수로 노출 (개발자 도구용)
window.callRealReactionAPI = callRealReactionAPI;
window.callRealDeleteAPI = callRealDeleteAPI;
window.getCurrentReactionState = getCurrentReactionState;
window.resetReactions = resetReactions;
