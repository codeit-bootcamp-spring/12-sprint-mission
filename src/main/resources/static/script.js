const DEFAULT_AVATAR = '/default-avatar.png';

const API_BASE_URL = '/api';
const ENDPOINTS = {
    USERS: `${API_BASE_URL}/user/findAll`,
    BINARY_CONTENT: `${API_BASE_URL}/binaryContent/find`
};

document.addEventListener('DOMContentLoaded', () => {
    fetchAndRenderUsers();
});

async function fetchAndRenderUsers() {
    const userListElement = document.getElementById('userList');

    userListElement.innerHTML = `
        <div class="list-notice">
            <span class="notice-icon">⏳</span>
            <p>친구들을 불러오는 중...</p>
        </div>`;

    try {
        const response = await fetch(ENDPOINTS.USERS);
        if (!response.ok) throw new Error('데이터를 가져오지 못했습니다.');

        const users = await response.json();

        if (!users || users.length === 0) {
            userListElement.innerHTML = `
                <div class="list-notice">
                    <span class="notice-icon">💨</span>
                    <p>등록된 사용자가 없습니다.</p>
                </div>`;
            return;
        }

        const profileImagePromises = users.map(async user => {
            if (!user.profileId) return DEFAULT_AVATAR;

            try {
                return await fetchUserProfile(user.profileId);
            } catch (e) {
                return DEFAULT_AVATAR;
            }
        });

        const profileUrls = await Promise.all(profileImagePromises);

        renderUserList(users, profileUrls);

    } catch (error) {
        console.error('Fetch Error:', error);
        userListElement.innerHTML = `
            <div class="list-notice">
                <span class="notice-icon">❌</span>
                <p>서버 연결에 실패했습니다.</p>
            </div>`;
    }
}

async function fetchUserProfile(profileId) {
    const response = await fetch(`${ENDPOINTS.BINARY_CONTENT}?binaryContentId=${profileId}`);
    if (!response.ok) throw new Error('Profile fail');

    const profile = await response.json();
    return `data:${profile.contentType};base64,${profile.bytes}`;
}

function renderUserList(users, profileUrls) {
    const userListElement = document.getElementById('userList');

    const listHtml = users.map((user, index) => `
        <div class="user-item">
            <div class="avatar-container">
                <img 
                    src="${profileUrls[index]}" 
                    alt="${user.username}" 
                    class="user-avatar"
                    onerror="this.src='${DEFAULT_AVATAR}'"
                >
                <div class="status-dot ${user.online ? 'online' : 'offline'}"></div>
            </div>

            <div class="user-info">
                <div class="user-name-row">
                    <span class="user-name">${user.username}</span>
                    <span class="status-text ${user.online ? 'text-online' : 'text-offline'}">
                        ${user.online ? '온라인' : '오프라인'}
                    </span>
                </div>
                <div class="user-email">${user.email}</div>
            </div>

            <div style="color: #d1d1d6; font-weight: bold;">›</div>
        </div>
    `).join('');

    userListElement.innerHTML = listHtml;
}