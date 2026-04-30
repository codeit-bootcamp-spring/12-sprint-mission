// API endpoints
const ENDPOINTS = {
    USERS: '/api/user/',
    BINARY_CONTENT: '/api/binaryContent/find'
};

// Global state
let allUsers = [];

// DOM Elements
const userListElement = document.getElementById('userList');
const searchInput = document.getElementById('searchInput');
const sortSelect = document.getElementById('sortSelect');
const totalCountElement = document.getElementById('totalCount');
const onlineCountElement = document.getElementById('onlineCount');

// Initialize the application
document.addEventListener('DOMContentLoaded', () => {
    fetchAndRenderUsers();
    
    // Setup event listeners for search and sort
    searchInput.addEventListener('input', handleFilterAndSort);
    sortSelect.addEventListener('change', handleFilterAndSort);
});

// Fetch users from the API
async function fetchAndRenderUsers() {
    try {
        renderSkeleton();

        const response = await fetch(ENDPOINTS.USERS);
        if (!response.ok) throw new Error('Failed to fetch users');

        allUsers = await response.json();
        updateStats(allUsers);
        await handleFilterAndSort();
    } catch (error) {
        console.error(error);
        renderMessage('멤버 목록을 불러오지 못했습니다. 잠시 후 다시 시도해 주세요.');
    }
}

// Count-up animation for stats
function animateValue(element, start, end, duration) {
    if (isNaN(end)) return;
    let startTimestamp = null;
    const step = (timestamp) => {
        if (!startTimestamp) startTimestamp = timestamp;
        const progress = Math.min((timestamp - startTimestamp) / duration, 1);
        element.innerText = Math.floor(progress * (end - start) + start);
        if (progress < 1) {
            window.requestAnimationFrame(step);
        }
    };
    window.requestAnimationFrame(step);
}

function updateStats(users) {
    const totalCount = users.length;
    const onlineCount = users.filter(u => u.online).length;
    
    const currentTotal = parseInt(totalCountElement.innerText) || 0;
    const currentOnline = parseInt(onlineCountElement.innerText) || 0;

    animateValue(totalCountElement, currentTotal, totalCount, 1000);
    animateValue(onlineCountElement, currentOnline, onlineCount, 1000);
}

function renderMessage(message) {
    userListElement.innerHTML = `<p class="list-message">${message}</p>`;
}

function renderSkeleton() {
    let skeletonHTML = '';
    for (let i = 0; i < 5; i++) {
        skeletonHTML += `
            <div class="skeleton-item">
                <div class="skeleton-avatar"></div>
                <div class="skeleton-info">
                    <div class="skeleton-line short"></div>
                    <div class="skeleton-line long"></div>
                </div>
            </div>
        `;
    }
    userListElement.innerHTML = skeletonHTML;
}

// Filter and Sort Logic
async function handleFilterAndSort() {
    const searchTerm = searchInput.value.toLowerCase();
    const sortBy = sortSelect.value;

    // Filter
    let filteredUsers = allUsers.filter(user => {
        const name = (user.username || '').toLowerCase();
        const email = (user.email || '').toLowerCase();
        return name.includes(searchTerm) || email.includes(searchTerm);
    });

    // Sort
    if (sortBy === 'name') {
        filteredUsers.sort((a, b) => (a.username || '').localeCompare(b.username || ''));
    } else if (sortBy === 'status') {
        filteredUsers.sort((a, b) => (b.online ? 1 : 0) - (a.online ? 1 : 0));
    }

    await renderUserList(filteredUsers);
}

// Fetch user profile image
async function fetchUserProfile(profileId, username) {
    if (!profileId) return buildInitialAvatar(username);

    try {
        const response = await fetch(`${ENDPOINTS.BINARY_CONTENT}/${profileId}`);
        if (!response.ok) throw new Error('Profile fetch failed');

        const profile = await response.json();
        const contentType = profile.contentType || 'image/png';

        if (typeof profile.bytes === 'string' && profile.bytes.length > 0) {
            return `data:${contentType};base64,${profile.bytes}`;
        } else if (Array.isArray(profile.bytes) && profile.bytes.length > 0) {
            const binary = new Uint8Array(profile.bytes).reduce((acc, byte) => acc + String.fromCharCode(byte), '');
            return `data:${contentType};base64,${btoa(binary)}`;
        }
    } catch (error) {
        console.error(error);
    }
    return buildInitialAvatar(username);
}

function buildInitialAvatar(username = 'U') {
    const initial = (username.trim()[0] || 'U').toUpperCase();
    const colors = ['#6366f1', '#818cf8', '#a855f7', '#ec4899', '#f43f5e', '#3b82f6'];
    const charCode = initial.charCodeAt(0);
    const bgColor = colors[charCode % colors.length];
    
    const svg = `
        <svg xmlns='http://www.w3.org/2000/svg' width='120' height='120' viewBox='0 0 120 120'>
            <rect width='120' height='120' rx='40' fill='${encodeURIComponent(bgColor)}'/>
            <text x='50%' y='54%' dominant-baseline='middle' text-anchor='middle'
                font-size='60' font-family='Inter, sans-serif' font-weight='800' fill='white'>${initial}</text>
        </svg>
    `;
    return `data:image/svg+xml,${svg}`;
}

// 3D Tilt Effect
function handleTilt(e) {
    const card = e.currentTarget;
    const box = card.getBoundingClientRect();
    const x = e.clientX - box.left;
    const y = e.clientY - box.top;
    const centerX = box.width / 2;
    const centerY = box.height / 2;
    const rotateX = (centerY - y) / 10;
    const rotateY = (x - centerX) / 20;

    card.style.transform = `perspective(1000px) rotateX(${rotateX}deg) rotateY(${rotateY}deg) translateZ(10px)`;
}

function resetTilt(e) {
    e.currentTarget.style.transform = `perspective(1000px) rotateX(0deg) rotateY(0deg) translateZ(0px)`;
}

// Render user list
async function renderUserList(users) {
    if (users.length === 0) {
        renderMessage('검색 결과와 일치하는 멤버가 없습니다.');
        return;
    }

    userListElement.innerHTML = '';
    const fragment = document.createDocumentFragment();

    const onlineUsers = users.filter(u => u.online);
    const offlineUsers = users.filter(u => !u.online);

    // Render Online Section
    if (onlineUsers.length > 0) {
        const header = document.createElement('div');
        header.className = 'list-category-header';
        header.textContent = `온라인 — ${onlineUsers.length}명`;
        fragment.appendChild(header);
        
        for (let i = 0; i < onlineUsers.length; i++) {
            const userItem = await createUserItemElement(onlineUsers[i], i);
            fragment.appendChild(userItem);
        }
    }

    // Render Offline Section
    if (offlineUsers.length > 0) {
        const header = document.createElement('div');
        header.className = 'list-category-header';
        header.textContent = `오프라인 — ${offlineUsers.length}명`;
        fragment.appendChild(header);
        
        for (let i = 0; i < offlineUsers.length; i++) {
            const userItem = await createUserItemElement(offlineUsers[i], i + onlineUsers.length);
            fragment.appendChild(userItem);
        }
    }

    userListElement.appendChild(fragment);
}

async function createUserItemElement(user, index) {
    const profileUrl = await fetchUserProfile(user.profileId, user.username);
    const online = Boolean(user.online);
    const userElement = document.createElement('article');
    
    userElement.className = 'user-item';
    userElement.style.animation = `slideIn 0.5s ease-out ${index * 0.05}s both`;
    
    // Add Tilt Listeners
    userElement.addEventListener('mousemove', handleTilt);
    userElement.addEventListener('mouseleave', resetTilt);
    
    userElement.innerHTML = `
        <div class="user-avatar-container">
            <img src="${profileUrl}" alt="${user.username || '멤버'}" class="user-avatar">
            <div class="status-indicator ${online ? 'online' : 'offline'}"></div>
        </div>
        <div class="user-info">
            <p class="user-name">${user.username || '-'}</p>
            <p class="user-email">${user.email || '-'}</p>
        </div>
        <span class="status-badge ${online ? 'online' : 'offline'}">${online ? '온라인' : '오프라인'}</span>
    `;
    return userElement;
}
