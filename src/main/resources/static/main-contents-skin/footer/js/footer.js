// Footer 전용 JavaScript - login.js 스타일 참고 (변수명 충돌 방지용 footer- 접두사 사용)

// Footer DOM 요소들 (footer- 접두사로 충돌 방지)
const footerBackToTopBtn = document.getElementById('backToTopBtn');
const footerSocialLinks = document.querySelectorAll('.social-link');
const footerContactLink = document.querySelector('.contact-link');
const footerLegalLinks = document.querySelectorAll('.legal-link');
const footerQuickLinks = document.querySelectorAll('.quick-link');

// Footer 유틸리티 함수들
const FooterUtils = {
    // 스크롤 위치 확인
    getScrollPosition() {
        return window.pageYOffset || document.documentElement.scrollTop;
    },

    // 부드러운 스크롤
    smoothScrollTo(target, duration = 800) {
        const targetPosition = target === 0 ? 0 : target.offsetTop;
        const startPosition = this.getScrollPosition();
        const distance = targetPosition - startPosition;
        let startTime = null;

        function footerAnimation(currentTime) {
            if (startTime === null) startTime = currentTime;
            const timeElapsed = currentTime - startTime;
            const run = FooterUtils.easeInOutQuad(timeElapsed, startPosition, distance, duration);
            window.scrollTo(0, run);
            if (timeElapsed < duration) requestAnimationFrame(footerAnimation);
        }

        requestAnimationFrame(footerAnimation);
    },

    // 이징 함수 (부드러운 애니메이션)
    easeInOutQuad(t, b, c, d) {
        t /= d / 2;
        if (t < 1) return c / 2 * t * t + b;
        t--;
        return -c / 2 * (t * (t - 2) - 1) + b;
    },

    // 로딩 효과 (footer 전용)
    addFooterLoadingEffect(element, originalContent) {
        element.disabled = true;
        element.style.opacity = '0.7';
        element.innerHTML = `<i class="fas fa-spinner fa-spin"></i> 처리 중...`;

        return () => {
            element.disabled = false;
            element.style.opacity = '1';
            element.innerHTML = originalContent;
        };
    },

    // 알림 표시 (footer 전용 - 간단한 토스트)
    showFooterToast(message, type = 'info', duration = 3000) {
        // 기존 토스트 제거
        const existingToast = document.querySelector('.footer-toast');
        if (existingToast) {
            existingToast.remove();
        }

        // 새 토스트 생성
        const toast = document.createElement('div');
        toast.className = `footer-toast footer-toast-${type}`;
        toast.innerHTML = `
            <div class="footer-toast-content">
                <i class="fas fa-${this.getToastIcon(type)}"></i>
                <span>${message}</span>
            </div>
        `;

        // 토스트 스타일
        Object.assign(toast.style, {
            position: 'fixed',
            bottom: '100px',
            right: '30px',
            background: 'linear-gradient(135deg, rgba(255, 255, 255, 0.1), rgba(255, 255, 255, 0.05))',
            backdropFilter: 'blur(20px)',
            border: '1px solid rgba(255, 255, 255, 0.2)',
            borderRadius: '12px',
            padding: '12px 20px',
            color: 'white',
            fontSize: '14px',
            fontWeight: '500',
            boxShadow: '0 8px 25px rgba(0, 0, 0, 0.2)',
            zIndex: '1001',
            opacity: '0',
            transform: 'translateY(20px)',
            transition: 'all 0.3s ease',
            maxWidth: '300px'
        });

        document.body.appendChild(toast);

        // 애니메이션 시작
        requestAnimationFrame(() => {
            toast.style.opacity = '1';
            toast.style.transform = 'translateY(0)';
        });

        // 자동 제거
        setTimeout(() => {
            toast.style.opacity = '0';
            toast.style.transform = 'translateY(-20px)';
            setTimeout(() => toast.remove(), 300);
        }, duration);
    },

    // 토스트 아이콘 선택
    getToastIcon(type) {
        const icons = {
            info: 'info-circle',
            success: 'check-circle',
            warning: 'exclamation-triangle',
            error: 'times-circle'
        };
        return icons[type] || icons.info;
    }
};

// 맨 위로 버튼 기능
function initFooterBackToTop() {
    if (!footerBackToTopBtn) return;

    // 스크롤 이벤트 리스너
    function handleFooterScroll() {
        const scrollPosition = FooterUtils.getScrollPosition();
        const windowHeight = window.innerHeight;
        const documentHeight = document.documentElement.scrollHeight;

        // 페이지 중간 이후부터 버튼 표시
        if (scrollPosition > windowHeight * 0.5) {
            footerBackToTopBtn.classList.add('footer-visible');
        } else {
            footerBackToTopBtn.classList.remove('footer-visible');
        }
    }

    // 스크롤 이벤트 등록 (쓰로틀링 적용)
    let footerScrollTimeout;
    window.addEventListener('scroll', () => {
        if (footerScrollTimeout) {
            clearTimeout(footerScrollTimeout);
        }
        footerScrollTimeout = setTimeout(handleFooterScroll, 10);
    });

    // 클릭 이벤트
    footerBackToTopBtn.addEventListener('click', (e) => {
        e.preventDefault();
        FooterUtils.smoothScrollTo(0);
        FooterUtils.showFooterToast('페이지 상단으로 이동했습니다', 'success', 2000);
    });
}

// 소셜 링크 기능
function initFooterSocialLinks() {
    footerSocialLinks.forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();

            const platform = link.classList.contains('twitter') ? '트위터' :
                link.classList.contains('facebook') ? '페이스북' :
                    link.classList.contains('instagram') ? '인스타그램' :
                        link.classList.contains('youtube') ? '유튜브' : '소셜 미디어';

            const originalContent = link.innerHTML;
            const stopLoading = FooterUtils.addFooterLoadingEffect(link, originalContent);

            setTimeout(() => {
                stopLoading();
                FooterUtils.showFooterToast(`${platform} 페이지로 이동합니다`, 'info');

                // 실제로는 해당 소셜 미디어로 이동
                setTimeout(() => {
                    window.open(link.href, '_blank', 'noopener,noreferrer');
                }, 500);
            }, 800);
        });

        // 호버 효과 강화
        link.addEventListener('mouseenter', () => {
            link.style.transform = 'translateY(-6px) scale(1.05)';
        });

        link.addEventListener('mouseleave', () => {
            link.style.transform = '';
        });
    });
}

// 연락처 링크 기능
function initFooterContactLink() {
    if (!footerContactLink) return;

    footerContactLink.addEventListener('click', (e) => {
        e.preventDefault();

        const email = footerContactLink.textContent;
        const originalContent = footerContactLink.innerHTML;
        const stopLoading = FooterUtils.addFooterLoadingEffect(footerContactLink, originalContent);

        setTimeout(() => {
            stopLoading();

            // 클립보드에 이메일 복사 시도
            if (navigator.clipboard && navigator.clipboard.writeText) {
                navigator.clipboard.writeText(email).then(() => {
                    FooterUtils.showFooterToast('이메일 주소가 클립보드에 복사되었습니다', 'success');
                }).catch(() => {
                    FooterUtils.showFooterToast('이메일 클라이언트를 실행합니다', 'info');
                    window.location.href = footerContactLink.href;
                });
            } else {
                FooterUtils.showFooterToast('이메일 클라이언트를 실행합니다', 'info');
                window.location.href = footerContactLink.href;
            }
        }, 600);
    });
}

// 법적 정보 링크 기능
function initFooterLegalLinks() {
    footerLegalLinks.forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();

            const linkText = link.textContent;
            const originalContent = link.innerHTML;
            const stopLoading = FooterUtils.addFooterLoadingEffect(link, originalContent);

            setTimeout(() => {
                stopLoading();
                FooterUtils.showFooterToast(`${linkText} 페이지로 이동합니다`, 'info');

                // 실제로는 해당 페이지로 이동
                setTimeout(() => {
                    console.log(`${linkText} 페이지로 이동: ${link.href}`);
                    // window.location.href = link.href;
                }, 500);
            }, 700);
        });
    });
}

// 빠른 링크 기능
function initFooterQuickLinks() {
    footerQuickLinks.forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();

            const linkText = link.textContent;
            const originalContent = link.innerHTML;
            const stopLoading = FooterUtils.addFooterLoadingEffect(link, originalContent);

            setTimeout(() => {
                stopLoading();
                FooterUtils.showFooterToast(`${linkText} 페이지로 이동합니다`, 'info');

                // 실제로는 해당 페이지로 이동
                setTimeout(() => {
                    console.log(`${linkText} 페이지로 이동: ${link.href}`);
                    // window.location.href = link.href;
                }, 500);
            }, 600);
        });
    });
}

// 플로팅 요소 초기화 (footer 전용)
function initFooterFloatingElements() {
    // Footer에 플로팅 요소 추가
    const footer = document.querySelector('.site-footer');
    if (!footer) return;

    // 플로팅 요소 컨테이너 생성
    const floatingContainer = document.createElement('div');
    floatingContainer.className = 'footer-floating-elements';

    // 플로팅 요소들 생성
    for (let i = 0; i < 3; i++) {
        const element = document.createElement('div');
        element.className = 'footer-floating-element';

        // 랜덤한 애니메이션 지연시간 적용
        const delay = Math.random() * 3;
        element.style.animationDelay = `-${delay}s`;

        floatingContainer.appendChild(element);
    }

    footer.appendChild(floatingContainer);
}

// Footer 애니메이션 효과
function initFooterAnimations() {
    // Intersection Observer를 사용한 스크롤 애니메이션
    const footerObserver = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.style.animation = 'footer-slideInUp 0.8s ease-out';
            }
        });
    }, {
        threshold: 0.1,
        rootMargin: '0px 0px -50px 0px'
    });

    // Footer 섹션들 관찰
    const footerSections = document.querySelectorAll('.footer-section');
    footerSections.forEach(section => {
        footerObserver.observe(section);
    });
}

// 키보드 네비게이션 지원
function initFooterKeyboardNavigation() {
    const footerFocusableElements = document.querySelectorAll(
        '.site-footer a, .site-footer button, .back-to-top'
    );

    footerFocusableElements.forEach(element => {
        element.addEventListener('keydown', (e) => {
            if (e.key === 'Enter' || e.key === ' ') {
                e.preventDefault();
                element.click();
            }
        });
    });
}

// Footer 초기화 함수
function initFooter() {
    // 모든 Footer 기능 초기화
    initFooterBackToTop();
    initFooterSocialLinks();
    initFooterContactLink();
    initFooterLegalLinks();
    initFooterQuickLinks();
    initFooterFloatingElements();
    initFooterAnimations();
    initFooterKeyboardNavigation();

    console.log('=== Footer 초기화 완료 ===');
    console.log('✅ 맨 위로 버튼');
    console.log('✅ 소셜 미디어 링크');
    console.log('✅ 연락처 링크');
    console.log('✅ 법적 정보 링크');
    console.log('✅ 빠른 링크');
    console.log('✅ 플로팅 애니메이션');
    console.log('✅ 스크롤 애니메이션');
    console.log('✅ 키보드 네비게이션');
    console.log('========================');
}

// DOM 로드 완료 시 Footer 초기화
document.addEventListener('DOMContentLoaded', initFooter);

// 페이지 로드 완료 시 추가 설정
window.addEventListener('load', () => {
    // Footer 로드 완료 알림
    setTimeout(() => {
        FooterUtils.showFooterToast('페이지 로드가 완료되었습니다', 'success', 2000);
    }, 1000);
});

// Footer 전용 전역 객체 (다른 스크립트에서 접근 가능)
window.FooterManager = {
    utils: FooterUtils,
    showToast: FooterUtils.showFooterToast,
    scrollToTop: () => FooterUtils.smoothScrollTo(0),

    // Footer 상태 확인
    getStatus() {
        return {
            backToTopVisible: footerBackToTopBtn?.classList.contains('footer-visible') || false,
            socialLinksCount: footerSocialLinks?.length || 0,
            isInitialized: true
        };
    }
};

// 개발자 도구용 디버깅 함수들
if (typeof window !== 'undefined' && window.console) {
    window.footerDebug = {
        testToast: (message = 'Footer 테스트 메시지', type = 'info') => {
            FooterUtils.showFooterToast(message, type);
        },
        scrollToTop: () => FooterUtils.smoothScrollTo(0),
        getStatus: () => window.FooterManager.getStatus(),
        testSocialLinks: () => {
            console.log('소셜 링크 개수:', footerSocialLinks.length);
            footerSocialLinks.forEach((link, index) => {
                console.log(`${index + 1}:`, link.href, link.className);
            });
        }
    };
}
