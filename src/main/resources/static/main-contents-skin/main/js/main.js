// ===== GameHub JavaScript - 메인 스크립트 =====

// 전역 변수 초기화
let isInitialized = false;
let resizeTimeout, scrollTimeout;

// DOM 요소 캐시
const elements = {
    header: null,
    searchInput: null,
    searchForm: null,
    dropdownTriggers: null,
    mobileMenuBtn: null,
    gameCards: null,
    postItems: null,
    widgets: null
};

// ===== 초기화 =====
document.addEventListener('DOMContentLoaded', function() {
    initializeElements();
    initializeEventListeners();
    initializeAnimations();
    initializeObserver();
    isInitialized = true;
    console.log('🎮 GameHub 초기화 완료!');
});

// DOM 요소 초기화
function initializeElements() {
    elements.header = document.querySelector('.site-header');
    elements.searchInput = document.getElementById('search-input');
    elements.searchForm = document.querySelector('.search-form');
    elements.dropdownTriggers = document.querySelectorAll('.dropdown-trigger');
    elements.mobileMenuBtn = document.querySelector('.mobile-menu-btn');
    elements.gameCards = document.querySelectorAll('.game-card');
    elements.postItems = document.querySelectorAll('.post-item');
    elements.widgets = document.querySelectorAll('.widget');
}

// 이벤트 리스너 초기화
function initializeEventListeners() {
    initializeDropdowns();
    initializeSearch();
    initializeMobileMenu();
    initializeScrollEffects();
    initializeCardEffects();

    window.addEventListener('resize', handleResize);
    document.addEventListener('keydown', handleKeyboard);
}

// ===== 드롭다운 메뉴 =====
function initializeDropdowns() {
    elements.dropdownTriggers.forEach(trigger => {
        const dropdown = trigger.closest('.has-dropdown');
        const menu = dropdown?.querySelector('.dropdown-menu');

        if (!dropdown || !menu) return;

        let timeout;

        dropdown.addEventListener('mouseenter', () => {
            clearTimeout(timeout);
            showDropdown(trigger, menu);
        });

        dropdown.addEventListener('mouseleave', () => {
            timeout = setTimeout(() => hideDropdown(trigger, menu), 150);
        });

        trigger.addEventListener('click', (e) => {
            e.preventDefault();
            toggleDropdown(trigger, menu);
        });
    });

    // 외부 클릭으로 드롭다운 닫기
    document.addEventListener('click', (e) => {
        if (!e.target.closest('.has-dropdown')) {
            closeAllDropdowns();
        }
    });
}

function showDropdown(trigger, menu) {
    trigger.setAttribute('aria-expanded', 'true');
    menu.style.opacity = '1';
    menu.style.visibility = 'visible';
    menu.style.transform = 'translateY(0)';
}

function hideDropdown(trigger, menu) {
    trigger.setAttribute('aria-expanded', 'false');
    menu.style.opacity = '0';
    menu.style.visibility = 'hidden';
    menu.style.transform = 'translateY(-10px)';
}

function toggleDropdown(trigger, menu) {
    const isOpen = trigger.getAttribute('aria-expanded') === 'true';
    closeAllDropdowns();

    if (!isOpen) {
        showDropdown(trigger, menu);
    }
}

function closeAllDropdowns() {
    elements.dropdownTriggers.forEach(trigger => {
        const dropdown = trigger.closest('.has-dropdown');
        const menu = dropdown?.querySelector('.dropdown-menu');
        if (menu) hideDropdown(trigger, menu);
    });
}

// ===== 검색 기능 =====
function initializeSearch() {
    if (!elements.searchInput || !elements.searchForm) return;

    let searchTimeout;

    elements.searchInput.addEventListener('input', (e) => {
        clearTimeout(searchTimeout);
        const query = e.target.value.trim();

        if (query.length > 2) {
            searchTimeout = setTimeout(() => showSearchSuggestions(query), 300);
        } else {
            hideSearchSuggestions();
        }
    });

    elements.searchForm.addEventListener('submit', (e) => {
        e.preventDefault();
        const query = elements.searchInput.value.trim();

        if (query.length > 0) {
            performSearch(query);
        } else {
            showToast('검색어를 입력해주세요.', 'warning');
        }
    });

    elements.searchInput.addEventListener('focus', () => {
        elements.searchInput.closest('.search-input-wrapper').classList.add('focused');
    });

    elements.searchInput.addEventListener('blur', () => {
        setTimeout(() => {
            elements.searchInput.closest('.search-input-wrapper').classList.remove('focused');
            hideSearchSuggestions();
        }, 200);
    });
}

function showSearchSuggestions(query) {
    hideSearchSuggestions();

    const suggestions = [
        { type: 'game', title: '레이저 디펜더', category: '액션' },
        { type: 'game', title: '타일 바니아', category: '플랫폼' },
        { type: 'post', title: '게임 공략법', author: '게임마스터' }
    ].filter(item =>
        item.title.toLowerCase().includes(query.toLowerCase())
    );

    if (suggestions.length === 0) return;

    const container = document.createElement('div');
    container.className = 'search-suggestions';
    container.innerHTML = suggestions.slice(0, 5).map(item => `
        <div class="suggestion-item" data-type="${item.type}">
            <div class="suggestion-icon">${item.type === 'game' ? '🎮' : '📝'}</div>
            <div class="suggestion-info">
                <div class="suggestion-title">${item.title}</div>
                <div class="suggestion-meta">
                    ${item.type === 'game' ? item.category : `작성자: ${item.author}`}
                </div>
            </div>
        </div>
    `).join('');

    // 스타일 적용
    container.style.cssText = `
        position: absolute;
        top: calc(100% + 8px);
        left: 0;
        right: 0;
        background: linear-gradient(135deg, rgba(20, 20, 40, 0.98), rgba(30, 30, 60, 0.98));
        backdrop-filter: blur(25px);
        border: 1px solid rgba(255, 255, 255, 0.2);
        border-radius: 16px;
        padding: 8px 0;
        box-shadow: 0 20px 60px rgba(0, 0, 0, 0.5);
        z-index: 1002;
        animation: slideInDown 0.3s ease-out;
    `;

    elements.searchForm.style.position = 'relative';
    elements.searchForm.appendChild(container);

    container.querySelectorAll('.suggestion-item').forEach(item => {
        item.addEventListener('click', () => {
            const title = item.querySelector('.suggestion-title').textContent;
            elements.searchInput.value = title;
            performSearch(title);
            hideSearchSuggestions();
        });
    });
}

function hideSearchSuggestions() {
    const suggestions = document.querySelector('.search-suggestions');
    if (suggestions) {
        suggestions.remove();
    }
}

function performSearch(query) {
    showToast(`"${query}" 검색 중...`, 'info');

    const submitBtn = elements.searchForm.querySelector('.search-submit');
    const originalHTML = submitBtn.innerHTML;

    submitBtn.innerHTML = '<div style="width:18px;height:18px;border:2px solid transparent;border-top:2px solid white;border-radius:50%;animation:spin 1s linear infinite;"></div>';
    submitBtn.style.pointerEvents = 'none';

    setTimeout(() => {
        submitBtn.innerHTML = originalHTML;
        submitBtn.style.pointerEvents = 'auto';
        showToast(`"${query}" 검색 완료!`, 'success');
        console.log(`검색 실행: ${query}`);
    }, 1500);
}

// ===== 모바일 메뉴 =====
function initializeMobileMenu() {
    if (!elements.mobileMenuBtn) return;

    elements.mobileMenuBtn.addEventListener('click', () => {
        const isExpanded = elements.mobileMenuBtn.getAttribute('aria-expanded') === 'true';
        elements.mobileMenuBtn.setAttribute('aria-expanded', !isExpanded);

        const lines = elements.mobileMenuBtn.querySelectorAll('.hamburger-line');
        if (!isExpanded) {
            lines[0].style.transform = 'rotate(45deg) translate(6px, 6px)';
            lines[1].style.opacity = '0';
            lines[2].style.transform = 'rotate(-45deg) translate(6px, -6px)';
        } else {
            lines.forEach(line => {
                line.style.transform = 'none';
                line.style.opacity = '1';
            });
        }

        showToast(isExpanded ? '메뉴 닫기' : '메뉴 열기', 'info');
    });
}

// ===== 스크롤 효과 =====
function initializeScrollEffects() {
    let lastScrollY = window.scrollY;

    window.addEventListener('scroll', () => {
        clearTimeout(scrollTimeout);
        scrollTimeout = setTimeout(() => {
            const currentScrollY = window.scrollY;

            if (elements.header) {
                if (currentScrollY > lastScrollY && currentScrollY > 100) {
                    elements.header.style.transform = 'translateX(-50%) translateY(-100%)';
                    elements.header.style.opacity = '0.8';
                } else {
                    elements.header.style.transform = 'translateX(-50%) translateY(0)';
                    elements.header.style.opacity = '1';
                }
            }

            lastScrollY = currentScrollY;
        }, 10);
    });
}

// ===== 애니메이션 초기화 =====
function initializeAnimations() {
    animateCounters();
    initializeRippleEffects();
}

function animateCounters() {
    const counters = document.querySelectorAll('.stat-number');

    counters.forEach(counter => {
        const target = parseInt(counter.textContent.replace(/[^\d]/g, ''));
        const increment = Math.ceil(target / 100);
        let current = 0;

        const updateCounter = () => {
            if (current < target) {
                current += increment;
                if (current > target) current = target;

                let displayValue = current.toLocaleString();
                if (counter.textContent.includes('+')) displayValue += '+';
                if (counter.textContent.includes('%')) displayValue += '%';

                counter.textContent = displayValue;
                requestAnimationFrame(updateCounter);
            }
        };

        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    setTimeout(updateCounter, Math.random() * 500);
                    observer.unobserve(entry.target);
                }
            });
        });

        observer.observe(counter);
    });
}

function initializeRippleEffects() {
    const rippleElements = document.querySelectorAll('.btn-primary, .btn-secondary, .search-submit, .nav-link');

    rippleElements.forEach(element => {
        element.addEventListener('click', createRipple);
    });
}

function createRipple(e) {
    const element = e.currentTarget;
    const rect = element.getBoundingClientRect();
    const ripple = document.createElement('span');

    const size = Math.max(rect.width, rect.height);
    const x = (e.clientX || rect.left + rect.width / 2) - rect.left - size / 2;
    const y = (e.clientY || rect.top + rect.height / 2) - rect.top - size / 2;

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

// ===== Intersection Observer =====
function initializeObserver() {
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.style.opacity = '1';
                entry.target.style.transform = 'translateY(0)';
                observer.unobserve(entry.target);
            }
        });
    }, { threshold: 0.1, rootMargin: '50px' });

    const observeElements = document.querySelectorAll(`
        .game-card, .post-item, .widget, .stat-card,
        .hero-section, .featured-games-section, .community-section
    `);

    observeElements.forEach(el => {
        el.style.opacity = '0';
        el.style.transform = 'translateY(30px)';
        el.style.transition = 'opacity 0.6s ease-out, transform 0.6s ease-out';
        observer.observe(el);
    });
}

// ===== 카드 효과 =====
function initializeCardEffects() {
    elements.gameCards.forEach(card => {
        card.addEventListener('mouseenter', () => {
            card.style.transform = 'translateY(-8px) scale(1.02)';
            card.style.boxShadow = '0 25px 80px rgba(0, 0, 0, 0.5)';
        });

        card.addEventListener('mouseleave', () => {
            card.style.transform = 'translateY(0) scale(1)';
            card.style.boxShadow = '';
        });
    });

    elements.postItems.forEach(item => {
        item.addEventListener('mouseenter', () => {
            item.style.transform = 'translateX(12px)';
            item.style.boxShadow = '0 20px 60px rgba(0, 0, 0, 0.4)';
        });

        item.addEventListener('mouseleave', () => {
            item.style.transform = 'translateX(0)';
            item.style.boxShadow = '';
        });
    });
}

// ===== 키보드 네비게이션 =====
function handleKeyboard(e) {
    if (e.key === 'Escape') {
        closeAllDropdowns();
        hideSearchSuggestions();
    }

    if ((e.ctrlKey || e.metaKey) && e.key === 'k') {
        e.preventDefault();
        if (elements.searchInput) {
            elements.searchInput.focus();
            showToast('검색창 활성화', 'info');
        }
    }
}

// ===== 리사이즈 핸들러 =====
function handleResize() {
    clearTimeout(resizeTimeout);
    resizeTimeout = setTimeout(() => {
        if (window.innerWidth > 768) {
            if (elements.mobileMenuBtn) {
                elements.mobileMenuBtn.setAttribute('aria-expanded', 'false');
                const lines = elements.mobileMenuBtn.querySelectorAll('.hamburger-line');
                lines.forEach(line => {
                    line.style.transform = 'none';
                    line.style.opacity = '1';
                });
            }
        }

        closeAllDropdowns();
    }, 250);
}

// ===== 토스트 알림 =====
function showToast(message, type = 'info', duration = 3000) {
    const existingToast = document.querySelector('.toast-notification');
    if (existingToast) existingToast.remove();

    const toast = document.createElement('div');
    toast.className = 'toast-notification';
    toast.innerHTML = `
        <div class="toast-icon">
            ${type === 'success' ? '✅' : type === 'warning' ? '⚠️' : type === 'error' ? '❌' : 'ℹ️'}
        </div>
        <div class="toast-message">${message}</div>
        <button class="toast-close" aria-label="닫기">×</button>
    `;

    toast.style.cssText = `
        position: fixed;
        top: 100px;
        right: 20px;
        background: linear-gradient(135deg, rgba(20, 20, 40, 0.98), rgba(30, 30, 60, 0.98));
        backdrop-filter: blur(25px);
        border: 1px solid rgba(255, 255, 255, 0.2);
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

    const closeBtn = toast.querySelector('.toast-close');
    closeBtn.style.cssText = `
        background: none;
        border: none;
        color: rgba(255, 255, 255, 0.7);
        cursor: pointer;
        font-size: 18px;
        padding: 0;
        margin-left: auto;
    `;

    closeBtn.addEventListener('click', () => removeToast(toast));
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
    
    @keyframes slideInDown {
        from { opacity: 0; transform: translateY(-30px); }
        to { opacity: 1; transform: translateY(0); }
    }
    
    @keyframes ripple {
        0% { transform: scale(0); opacity: 1; }
        100% { transform: scale(4); opacity: 0; }
    }
    
    @keyframes spin {
        0% { transform: rotate(0deg); }
        100% { transform: rotate(360deg); }
    }
    
    .suggestion-item {
        padding: 12px 16px;
        display: flex;
        align-items: center;
        gap: 12px;
        cursor: pointer;
        transition: background 0.3s ease;
        color: rgba(255, 255, 255, 0.9);
    }
    
    .suggestion-item:hover {
        background: rgba(100, 181, 246, 0.2);
    }
    
    .suggestion-icon {
        font-size: 16px;
    }
    
    .suggestion-title {
        font-weight: 500;
        margin-bottom: 2px;
    }
    
    .suggestion-meta {
        font-size: 12px;
        color: rgba(255, 255, 255, 0.6);
    }
`;
document.head.appendChild(style);

// ===== 개발자 도구 =====
if (window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1') {
    console.log('🎮 GameHub 개발 모드 활성화');
    window.GameHubDev = { showToast, createRipple, elements };
}

console.log('🚀 GameHub JavaScript 로드 완료!');
