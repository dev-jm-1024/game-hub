// comment-reaction.js - 댓글 리액션 처리

document.addEventListener('DOMContentLoaded', function() {
    // CSRF 토큰 가져오기
    const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');

    // 로그인 상태 확인
    const isLoggedIn = window.postDetailData?.isLoggedIn || false;

    // 댓글 좋아요 버튼 이벤트 리스너
    document.addEventListener('click', function(e) {
        if (e.target.closest('.comment-like-button')) {
            e.preventDefault();

            if (!isLoggedIn) {
                alert('로그인이 필요합니다.');
                window.location.href = '/game-hub/login';
                return;
            }

            const button = e.target.closest('.comment-like-button');
            const commentId = button.getAttribute('data-comment-id');

            handleCommentLike(commentId, button);
        }
    });

    // 댓글 싫어요 버튼 이벤트 리스너
    document.addEventListener('click', function(e) {
        if (e.target.closest('.comment-dislike-button')) {
            e.preventDefault();

            if (!isLoggedIn) {
                alert('로그인이 필요합니다.');
                window.location.href = '/login';
                return;
            }

            const button = e.target.closest('.comment-dislike-button');
            const commentId = button.getAttribute('data-comment-id');

            handleCommentDislike(commentId, button);
        }
    });

    /**
     * 댓글 좋아요 처리
     * @param {string} commentId - 댓글 ID
     * @param {HTMLElement} button - 클릭된 버튼 요소
     */
    async function handleCommentLike(commentId, button) {
        try {
            // 버튼 비활성화 (중복 클릭 방지)
            button.disabled = true;

            const isLiked = button.classList.contains('liked');
            const method = isLiked ? 'DELETE' : 'POST';
            const url = `/api/v1/board/posts/comments/${commentId}/reactions/likes`;

            const response = await fetch(url, {
                method: method,
                headers: {
                    'Content-Type': 'application/json',
                    [csrfHeader]: csrfToken
                }
            });

            const result = await response.text();

            if (response.ok) {
                // UI 업데이트
                updateCommentLikeUI(commentId, button, !isLiked);

                // 성공 메시지 (선택사항)
                console.log(result);
            } else {
                // 오류 처리
                console.error('댓글 좋아요 처리 실패:', result);
                if (response.status === 401) {
                    alert('로그인이 필요합니다.');
                    window.location.href = '/login';
                } else if (response.status === 409) {
                    alert('이미 반응이 등록되어 있습니다.');
                } else {
                    alert('요청 처리 중 오류가 발생했습니다.');
                }
            }
        } catch (error) {
            console.error('네트워크 오류:', error);
            alert('네트워크 오류가 발생했습니다.');
        } finally {
            // 버튼 재활성화
            button.disabled = false;
        }
    }

    /**
     * 댓글 싫어요 처리
     * @param {string} commentId - 댓글 ID
     * @param {HTMLElement} button - 클릭된 버튼 요소
     */
    async function handleCommentDislike(commentId, button) {
        try {
            // 버튼 비활성화 (중복 클릭 방지)
            button.disabled = true;

            const isDisliked = button.classList.contains('disliked');
            const method = isDisliked ? 'DELETE' : 'POST';
            const url = `/api/v1/board/posts/comments/${commentId}/reactions/dislikes`;

            const response = await fetch(url, {
                method: method,
                headers: {
                    'Content-Type': 'application/json',
                    [csrfHeader]: csrfToken
                }
            });

            const result = await response.text();

            if (response.ok) {
                // UI 업데이트
                updateCommentDislikeUI(commentId, button, !isDisliked);

                // 성공 메시지 (선택사항)
                console.log(result);
            } else {
                // 오류 처리
                console.error('댓글 싫어요 처리 실패:', result);
                if (response.status === 401) {
                    alert('로그인이 필요합니다.');
                    window.location.href = '/login';
                } else if (response.status === 409) {
                    alert('이미 반응이 등록되어 있습니다.');
                } else {
                    alert('요청 처리 중 오류가 발생했습니다.');
                }
            }
        } catch (error) {
            console.error('네트워크 오류:', error);
            alert('네트워크 오류가 발생했습니다.');
        } finally {
            // 버튼 재활성화
            button.disabled = false;
        }
    }

    /**
     * 댓글 좋아요 UI 업데이트
     * @param {string} commentId - 댓글 ID
     * @param {HTMLElement} likeButton - 좋아요 버튼 요소
     * @param {boolean} isLiked - 좋아요 상태
     */
    function updateCommentLikeUI(commentId, likeButton, isLiked) {
        const likeCountSpan = likeButton.querySelector('.like-count');
        const dislikeButton = document.querySelector(`[data-comment-id="${commentId}"].comment-dislike-button`);
        const dislikeCountSpan = dislikeButton?.querySelector('.dislike-count');

        let currentLikeCount = parseInt(likeCountSpan?.textContent) || 0;
        let currentDislikeCount = parseInt(dislikeCountSpan?.textContent) || 0;

        if (isLiked) {
            // 좋아요 추가
            likeButton.classList.add('liked');
            currentLikeCount += 1;

            // 싫어요가 활성화되어 있다면 제거
            if (dislikeButton?.classList.contains('disliked')) {
                dislikeButton.classList.remove('disliked');
                currentDislikeCount = Math.max(0, currentDislikeCount - 1);
            }
        } else {
            // 좋아요 제거
            likeButton.classList.remove('liked');
            currentLikeCount = Math.max(0, currentLikeCount - 1);
        }

        // 카운트 업데이트
        if (likeCountSpan) {
            likeCountSpan.textContent = currentLikeCount;
        }
        if (dislikeCountSpan) {
            dislikeCountSpan.textContent = currentDislikeCount;
        }
    }

    /**
     * 댓글 싫어요 UI 업데이트
     * @param {string} commentId - 댓글 ID
     * @param {HTMLElement} dislikeButton - 싫어요 버튼 요소
     * @param {boolean} isDisliked - 싫어요 상태
     */
    function updateCommentDislikeUI(commentId, dislikeButton, isDisliked) {
        const dislikeCountSpan = dislikeButton.querySelector('.dislike-count');
        const likeButton = document.querySelector(`[data-comment-id="${commentId}"].comment-like-button`);
        const likeCountSpan = likeButton?.querySelector('.like-count');

        let currentDislikeCount = parseInt(dislikeCountSpan?.textContent) || 0;
        let currentLikeCount = parseInt(likeCountSpan?.textContent) || 0;

        if (isDisliked) {
            // 싫어요 추가
            dislikeButton.classList.add('disliked');
            currentDislikeCount += 1;

            // 좋아요가 활성화되어 있다면 제거
            if (likeButton?.classList.contains('liked')) {
                likeButton.classList.remove('liked');
                currentLikeCount = Math.max(0, currentLikeCount - 1);
            }
        } else {
            // 싫어요 제거
            dislikeButton.classList.remove('disliked');
            currentDislikeCount = Math.max(0, currentDislikeCount - 1);
        }

        // 카운트 업데이트
        if (dislikeCountSpan) {
            dislikeCountSpan.textContent = currentDislikeCount;
        }
        if (likeCountSpan) {
            likeCountSpan.textContent = currentLikeCount;
        }
    }

    /**
     * 댓글 리액션 상태 새로고침 (필요시 사용)
     * @param {string} commentId - 댓글 ID
     */
    async function refreshCommentReactions(commentId) {
        try {
            const response = await fetch(`/api/v1/board/posts/comments/${commentId}/reactions`);

            if (response.ok) {
                const reactionData = await response.json();

                // UI 업데이트
                const likeCountSpan = document.querySelector(`[data-comment-id="${commentId}"] .like-count`);
                const dislikeCountSpan = document.querySelector(`[data-comment-id="${commentId}"] .dislike-count`);

                if (likeCountSpan) {
                    likeCountSpan.textContent = reactionData.likeCount || 0;
                }
                if (dislikeCountSpan) {
                    dislikeCountSpan.textContent = reactionData.dislikeCount || 0;
                }
            }
        } catch (error) {
            console.error('댓글 리액션 정보 새로고침 실패:', error);
        }
    }

    // 전역 함수로 노출 (필요시)
    window.commentReactionHandler = {
        refreshCommentReactions
    };
});