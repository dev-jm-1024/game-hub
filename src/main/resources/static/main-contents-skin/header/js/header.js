// Test 페이지 전용 JavaScript - login.js, footer.js 스타일 참고

// Test 페이지 DOM 요소들 (간소화된 헤더용)
const testHeader = document.querySelector('.site-header');
const testNavLinks = document.querySelectorAll('.nav-link');
const testDropdownTriggers = document.querySelectorAll('.dropdown-trigger');
const testSearchInput = document.getElementById('search-input');
const testSearchForm = document.querySelector('.search-form');
const testSearchToggle = document.querySelector('.search-toggle');
const testSearchOverlay = document.querySelector('.search-overlay');
const testSearchClose = document.querySelector('.search-close');
const testMobileMenuBtn = document.querySelector('.mobile-menu-btn');
const testAuthButtons = document.querySelectorAll('.dropdown-link[href*="game-hub"]');

// Test 페이지 유틸리티 함수들
const TestUtils = {
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

        function testAnimation(currentTime) {
            if (startTime === null) startTime = currentTime;
            const timeElapsed = currentTime - startTime;
            const run = TestUtils.easeInOutQuad(timeElapsed, startPosition, distance, duration);
            window.scrollTo(0, run);
            if (timeElapsed < duration) requestAnimationFrame(testAnimation);
        }

        requestAnimationFrame(testAnimation);
    },

    // 이징 함수 (부드러운 애니메이션)
    easeInOutQuad(t, b, c, d) {
        t /= d / 2;
        if (t < 1) return c / 2 * t * t + b;
        t--;
        return -c / 2 * (t * (t - 2) - 1) + b;
    },

    // 로딩 효과 (test 페이지 전용)
    addTestLoadingEffect(element, originalContent) {
        element.disabled = true;
        element.style.opacity = '0.7';
        const originalHTML = element.innerHTML;
        element.innerHTML = `<i class="fas fa-spinner fa-spin"></i> 처리 중...`;

        return () => {
            element.disabled = false;
            element.style.opacity = '1';
            element.innerHTML = originalContent || originalHTML;
        };
    },

    // 알림 표시 (test 페이지 전용 토스트)
    showTestToast(message, type = 'info', duration = 3000) {
        // 기존 토스트 제거
        const existingToast = document.querySelector('.test-toast');
        if (existingToast) {
            existingToast.remove();
        }

        // 새 토스트 생성
        const toast = document.createElement('div');
        toast.className = `test-toast test-toast-${type}`;
        toast.innerHTML = `
            <div class="test-toast-content">
                <i class="fas fa-${this.getToastIcon(type)}"></i>
                <span>${message}</span>
            </div>
        `;

        // 토스트 스타일
        Object.assign(toast.style, {
            position: 'fixed',
            top: '30px',
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
            zIndex: '10001',
            opacity: '0',
            transform: 'translateX(100px)',
            transition: 'all 0.3s ease',
            maxWidth: '320px'
        });

        document.body.appendChild(toast);

        // 애니메이션 시작
        requestAnimationFrame(() => {
            toast.style.opacity = '1';
            toast.style.transform = 'translateX(0)';
        });

        // 자동 제거
        setTimeout(() => {
            toast.style.opacity = '0';
            toast.style.transform = 'translateX(100px)';
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
    },

    // 리플 효과 생성
    createRippleEffect(element, event) {
        const ripple = document.createElement('span');
        const rect = element.getBoundingClientRect();
        const size = Math.max(rect.width, rect.height);
        const x = event.clientX - rect.left - size / 2;
        const y = event.clientY - rect.top - size / 2;

        ripple.style.cssText = `
            position: absolute;
            border-radius: 50%;
            background: rgba(255, 255, 255, 0.3);
            transform: scale(0);
            animation: test-ripple 0.6s linear;
            width: ${size}px;
            height: ${size}px;
            left: ${x}px;
            top: ${y}px;
            pointer-events: none;
        `;

        element.style.position = 'relative';
        element.style.overflow = 'hidden';
        element.appendChild(ripple);

        setTimeout(() => {
            if (ripple.parentNode) {
                ripple.parentNode.removeChild(ripple);
            }
        }, 600);
    }
};

// Header 스크롤 효과
function initTestHeaderScroll() {
    if (!testHeader) return;

    let lastScrollTop = 0;

    function handleTestScroll() {
        const scrollTop = TestUtils.getScrollPosition();

        // 스크롤 방향에 따른 헤더 효과
        if (scrollTop > lastScrollTop && scrollTop > 100) {
            // 아래로 스크롤 - 헤더 축소
            testHeader.style.transform = 'translateY(-10px)';
            testHeader.style.opacity = '0.95';
        } else {
            // 위로 스크롤 - 헤더 원상복구
            testHeader.style.transform = 'translateY(0)';
            testHeader.style.opacity = '1';
        }

        // 스크롤에 따른 배경 투명도 조절
        const opacity = Math.min(scrollTop / 200, 0.15);
        testHeader.style.background = `linear-gradient(135deg, 
            rgba(255, 255, 255, ${0.08 + opacity}) 0%, 
            rgba(255, 255, 255, ${0.03 + opacity}) 100%)`;

        lastScrollTop = scrollTop;
    }

    // 스크롤 이벤트 등록 (쓰로틀링 적용)
    let testScrollTimeout;
    window.addEventListener('scroll', () => {
        if (testScrollTimeout) {
            clearTimeout(testScrollTimeout);
        }
        testScrollTimeout = setTimeout(handleTestScroll, 10);
    });
}

// Navigation 인터랙션
function initTestNavigation() {
    // 네비게이션 링크 클릭 이벤트
    testNavLinks.forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();

            const linkText = link.querySelector('.nav-text')?.textContent || link.textContent;
            const originalContent = link.innerHTML;
            const stopLoading = TestUtils.addTestLoadingEffect(link, originalContent);

            // 리플 효과
            TestUtils.createRippleEffect(link, e);

            setTimeout(() => {
                stopLoading();
                TestUtils.showTestToast(`${linkText} 페이지로 이동합니다`, 'info');

                // 실제로는 해당 페이지로 이동
                setTimeout(() => {
                    console.log(`${linkText} 페이지로 이동: ${link.href}`);
                    // window.location.href = link.href;
                }, 500);
            }, 1000);
        });

        // 호버 효과 강화
        link.addEventListener('mouseenter', () => {
            link.style.transform = 'translateY(-3px) scale(1.02)';
        });

        link.addEventListener('mouseleave', () => {
            link.style.transform = '';
        });
    });

    // 드롭다운 메뉴 토글
    testDropdownTriggers.forEach(trigger => {
        trigger.addEventListener('click', (e) => {
            e.preventDefault();

            const isExpanded = trigger.getAttribute('aria-expanded') === 'true';
            trigger.setAttribute('aria-expanded', !isExpanded);

            // 다른 드롭다운 닫기
            testDropdownTriggers.forEach(otherTrigger => {
                if (otherTrigger !== trigger) {
                    otherTrigger.setAttribute('aria-expanded', 'false');
                }
            });
        });
    });

    // 외부 클릭 시 드롭다운 닫기
    document.addEventListener('click', (e) => {
        if (!e.target.closest('.has-dropdown')) {
            testDropdownTriggers.forEach(trigger => {
                trigger.setAttribute('aria-expanded', 'false');
            });
        }
    });
}

// 검색 기능 (토글 방식)
function initTestSearch() {
    if (!testSearchForm || !testSearchInput || !testSearchToggle || !testSearchOverlay) return;

    // 검색 오버레이 토글
    testSearchToggle.addEventListener('click', () => {
        testSearchOverlay.classList.add('active');
        setTimeout(() => testSearchInput.focus(), 100);
        TestUtils.showTestToast('검색창이 열렸습니다', 'info', 2000);
    });

    // 검색 오버레이 닫기
    function closeSearchOverlay() {
        testSearchOverlay.classList.remove('active');
        testSearchInput.value = '';
        TestUtils.showTestToast('검색창이 닫혔습니다', 'info', 2000);
    }

    if (testSearchClose) {
        testSearchClose.addEventListener('click', closeSearchOverlay);
    }

    // 오버레이 배경 클릭 시 닫기
    testSearchOverlay.addEventListener('click', (e) => {
        if (e.target === testSearchOverlay) {
            closeSearchOverlay();
        }
    });

    // ESC 키로 닫기
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape' && testSearchOverlay.classList.contains('active')) {
            closeSearchOverlay();
        }
    });

    // 검색 폼 제출
    testSearchForm.addEventListener('submit', (e) => {
        e.preventDefault();

        const query = testSearchInput.value.trim();
        if (!query) {
            TestUtils.showTestToast('검색어를 입력해주세요', 'warning');
            testSearchInput.focus();
            return;
        }

        const searchBtn = testSearchForm.querySelector('.search-submit');
        const originalContent = searchBtn.innerHTML;
        const stopLoading = TestUtils.addTestLoadingEffect(searchBtn, originalContent);

        setTimeout(() => {
            stopLoading();
            TestUtils.showTestToast(`"${query}" 검색 결과를 불러옵니다`, 'success');
            closeSearchOverlay();

            // 실제로는 검색 페이지로 이동
            setTimeout(() => {
                console.log(`검색 실행: ${query}`);
                // window.location.href = `/search?q=${encodeURIComponent(query)}`;
            }, 500);
        }, 1500);
    });
}

// 모바일 메뉴 토글
function initTestMobileMenu() {
    if (!testMobileMenuBtn || !testMobileNav) return;

    testMobileMenuBtn.addEventListener('click', () => {
        const isExpanded = testMobileMenuBtn.getAttribute('aria-expanded') === 'true';
        const newState = !isExpanded;

        testMobileMenuBtn.setAttribute('aria-expanded', newState);
        testMobileNav.classList.toggle('active', newState);

        // 햄버거 메뉴 애니메이션
        const lines = testMobileMenuBtn.querySelectorAll('.hamburger-line');
        lines.forEach((line, index) => {
            if (newState) {
                if (index === 0) line.style.transform = 'rotate(45deg) translate(5px, 5px)';
                if (index === 1) line.style.opacity = '0';
                if (index === 2) line.style.transform = 'rotate(-45deg) translate(7px, -6px)';
            } else {
                line.style.transform = '';
                line.style.opacity = '';
            }
        });

        // 바디 스크롤 제어
        document.body.style.overflow = newState ? 'hidden' : '';

        TestUtils.showTestToast(
            newState ? '모바일 메뉴가 열렸습니다' : '모바일 메뉴가 닫혔습니다',
            'info',
            2000
        );
    });

    // ESC 키로 모바일 메뉴 닫기
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape' && testMobileNav.classList.contains('active')) {
            testMobileMenuBtn.click();
        }
    });
}

// 인증 버튼 기능
function initTestAuthButtons() {
    testAuthButtons.forEach(button => {
        button.addEventListener('click', (e) => {
            e.preventDefault();

            const buttonText = button.textContent.replace(/[🔑📝👥]/g, '').trim();
            const originalContent = button.innerHTML;
            const stopLoading = TestUtils.addTestLoadingEffect(button, originalContent);

            // 리플 효과
            TestUtils.createRippleEffect(button, e);

            setTimeout(() => {
                stopLoading();
                TestUtils.showTestToast(`${buttonText} 페이지로 이동합니다`, 'info');

                // 실제로는 해당 페이지로 이동
                setTimeout(() => {
                    console.log(`${buttonText} 페이지로 이동: ${button.href}`);
                    // window.location.href = button.href;
                }, 500);
            }, 1200);
        });

        // 호버 효과
        button.addEventListener('mouseenter', () => {
            button.style.transform = 'translateX(6px) scale(1.02)';
        });

        button.addEventListener('mouseleave', () => {
            button.style.transform = '';
        });
    });
}

// 간소화된 헤더에서는 다국어 선택 제거됨
function initTestLanguageSwitch() {
    // 간소화된 헤더에서는 다국어 선택이 드롭다운으로 이동하거나 제거됨
    console.log('언어 선택 기능은 간소화된 헤더에서 제거되었습니다');
}

// 플로팅 요소 초기화
function initTestFloatingElements() {
    // Header 플로팅 요소
    if (testHeader) {
        const headerFloatingContainer = document.createElement('div');
        headerFloatingContainer.className = 'header-floating-elements';

        for (let i = 0; i < 3; i++) {
            const element = document.createElement('div');
            element.className = 'header-floating-element';

            // 크기와 위치 설정
            const size = 40 + Math.random() * 40;
            element.style.width = `${size}px`;
            element.style.height = `${size}px`;
            element.style.left = `${Math.random() * 80}%`;
            element.style.top = `${Math.random() * 60}%`;

            // 랜덤한 애니메이션 지연시간
            const delay = Math.random() * 3;
            element.style.animationDelay = `-${delay}s`;

            headerFloatingContainer.appendChild(element);
        }

        testHeader.appendChild(headerFloatingContainer);
    }

    // Main 콘텐츠 플로팅 요소
    const mainContent = document.querySelector('.main-content');
    if (mainContent) {
        const mainFloatingContainer = document.createElement('div');
        mainFloatingContainer.className = 'main-floating-elements';

        for (let i = 0; i < 5; i++) {
            const element = document.createElement('div');
            element.className = 'main-floating-element';

            // 크기와 위치 설정
            const size = 60 + Math.random() * 80;
            element.style.width = `${size}px`;
            element.style.height = `${size}px`;
            element.style.left = `${Math.random() * 90}%`;
            element.style.top = `${Math.random() * 80}%`;

            // 랜덤한 애니메이션 지연시간
            const delay = Math.random() * 4;
            element.style.animationDelay = `-${delay}s`;

            mainFloatingContainer.appendChild(element);
        }

        mainContent.appendChild(mainFloatingContainer);
    }
}

// 키보드 네비게이션 지원
function initTestKeyboardNavigation() {
    const testFocusableElements = document.querySelectorAll(
        '.site-header a, .site-header button, .nav-link, .search-btn'
    );

    testFocusableElements.forEach(element => {
        element.addEventListener('keydown', (e) => {
            if (e.key === 'Enter' || e.key === ' ') {
                if (!element.matches('input')) {
                    e.preventDefault();
                    element.click();
                }
            }
        });

        // 포커스 시각적 효과
        element.addEventListener('focus', () => {
            element.style.outline = '2px solid rgba(100, 181, 246, 0.5)';
            element.style.outlineOffset = '2px';
        });

        element.addEventListener('blur', () => {
            element.style.outline = '';
            element.style.outlineOffset = '';
        });
    });
}

// 애니메이션 스타일 추가
function initTestAnimationStyles() {
    const style = document.createElement('style');
    style.textContent = `
        @keyframes test-ripple {
            to {
                transform: scale(2);
                opacity: 0;
            }
        }
        
        .test-toast-content {
            display: flex;
            align-items: center;
            gap: 8px;
        }
        
        .has-value .search-input-wrapper {
            border-color: rgba(100, 181, 246, 0.3) !important;
        }
        
        .nav-indicator {
            position: absolute;
            bottom: -2px;
            left: 0;
            height: 2px;
            background: linear-gradient(90deg, #64b5f6, #42a5f5);
            border-radius: 1px;
            transition: all 0.3s ease;
            opacity: 0;
        }
        
        .nav-link:hover + .nav-indicator,
        .nav-link:focus + .nav-indicator {
            opacity: 1;
            width: 100%;
        }
    `;
    document.head.appendChild(style);
}

// Test 페이지 초기화 함수
function initTestPage() {
    // 모든 Test 페이지 기능 초기화
    initTestHeaderScroll();
    initTestNavigation();
    initTestSearch();
    initTestMobileMenu();
    initTestAuthButtons();
    initTestLanguageSwitch();
    initTestFloatingElements();
    initTestKeyboardNavigation();
    initTestAnimationStyles();

    console.log('=== Test 페이지 초기화 완료 ===');
    console.log('✅ Header 스크롤 효과');
    console.log('✅ Navigation 인터랙션');
    console.log('✅ 검색 기능');
    console.log('✅ 모바일 메뉴');
    console.log('✅ 인증 버튼');
    console.log('✅ 다국어 선택');
    console.log('✅ 플로팅 애니메이션');
    console.log('✅ 키보드 네비게이션');
    console.log('✅ 리플 효과');
    console.log('============================');
}

// DOM 로드 완료 시 Test 페이지 초기화
document.addEventListener('DOMContentLoaded', initTestPage);

// 페이지 로드 완료 시 추가 설정
window.addEventListener('load', () => {
    // 로드 완료 알림
    setTimeout(() => {
        TestUtils.showTestToast('Test 페이지가 준비되었습니다! 🎉', 'success', 3000);
    }, 500);

    // 개발자 콘솔에 도움말 출력
    console.log('=== Test 페이지 도움말 ===');
    console.log('• TestUtils.showTestToast(message, type) - 토스트 알림 표시');
    console.log('• TestManager.getStatus() - 페이지 상태 확인');
    console.log('• testDebug.testToast() - 토스트 테스트');
    console.log('========================');
});

// Test 페이지 전용 전역 객체 (다른 스크립트에서 접근 가능)
window.TestManager = {
    utils: TestUtils,
    showToast: TestUtils.showTestToast,

    // Test 페이지 상태 확인
    getStatus() {
        return {
            headerLoaded: !!testHeader,
            navLinksCount: testNavLinks?.length || 0,
            searchEnabled: !!testSearchForm,
            mobileMenuEnabled: !!testMobileMenuBtn,
            userButtonsCount: testUserButtons?.length || 0,
            isInitialized: true
        };
    },

    // 모든 인터랙션 테스트
    testAllInteractions() {
        setTimeout(() => TestUtils.showTestToast('Header 테스트', 'info'), 100);
        setTimeout(() => TestUtils.showTestToast('Navigation 테스트', 'success'), 800);
        setTimeout(() => TestUtils.showTestToast('Search 테스트', 'warning'), 1500);
        setTimeout(() => TestUtils.showTestToast('Mobile Menu 테스트', 'error'), 2200);
    }
};

// 개발자 도구용 디버깅 함수들
if (typeof window !== 'undefined' && window.console) {
    window.testDebug = {
        testToast: (message = 'Test 페이지 메시지', type = 'info') => {
            TestUtils.showTestToast(message, type);
        },
        getStatus: () => window.TestManager.getStatus(),
        testAllToasts: () => window.TestManager.testAllInteractions(),
        showElementCounts: () => {
            const status = window.TestManager.getStatus();
            console.log('=== Test 페이지 요소 개수 ===');
            console.log('Navigation Links:', status.navLinksCount);
            console.log('User Buttons:', status.userButtonsCount);
            console.log('Header Loaded:', status.headerLoaded);
            console.log('Search Enabled:', status.searchEnabled);
            console.log('Mobile Menu Enabled:', status.mobileMenuEnabled);
            console.log('===========================');
        }
    };
}