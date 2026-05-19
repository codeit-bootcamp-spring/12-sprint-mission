/*
// API endpoints
const API_BASE_URL = '/api';
const ENDPOINTS = {
    USERS: `${API_BASE_URL}/user/findAll`,
    BINARY_CONTENT: `${API_BASE_URL}/binaryContent/find`
};

// Initialize the application
document.addEventListener('DOMContentLoaded', () => {
    fetchAndRenderUsers();
});

// Fetch users from the API
async function fetchAndRenderUsers() {
    try {
        const response = await fetch(ENDPOINTS.USERS);
        if (!response.ok) throw new Error('Failed to fetch users');
        const users = await response.json();
        renderUserList(users);
    } catch (error) {
        console.error('Error fetching users:', error);
    }
}

// Fetch user profile image
async function fetchUserProfile(profileId) {
    try {
        const response = await fetch(`${ENDPOINTS.BINARY_CONTENT}?binaryContentId=${profileId}`);
        if (!response.ok) throw new Error('Failed to fetch profile');
        const profile = await response.json();

        // Convert base64 encoded bytes to data URL
        return `data:${profile.contentType};base64,${profile.bytes}`;
    } catch (error) {
        console.error('Error fetching profile:', error);
        return '/default-avatar.png'; // Fallback to default avatar
    }
}

// Render user list
async function renderUserList(users) {
    const userListElement = document.getElementById('userList');
    userListElement.innerHTML = ''; // Clear existing content

    for (const user of users) {
        const userElement = document.createElement('div');
        userElement.className = 'user-item';

        // Get profile image URL
        const profileUrl = user.profileId ?
            await fetchUserProfile(user.profileId) :
            '/default-avatar.png';

        userElement.innerHTML = `
            <img src="${profileUrl}" alt="${user.username}" class="user-avatar">
            <div class="user-info">
                <div class="user-name">${user.username}</div>
                <div class="user-email">${user.email}</div>
            </div>
            <div class="status-badge ${user.online ? 'online' : 'offline'}">
                ${user.online ? '온라인' : '오프라인'}
            </div>
        `;

        userListElement.appendChild(userElement);
    }
}

 */

//새로운 AI 활용

const userList = document.getElementById("userList");
const USER_API = "/api/user/findAll";

// 🐾 절대 안 깨지는 고양이 이미지 주소 (사이즈를 50x50으로 고정했습니다)
const catMap = {
    "이길동": "https://cataas.com/cat?width=50&height=50&unique=1",
    "성길돌": "https://cataas.com/cat?width=50&height=50&unique=2",
    "갈길돌": "https://cataas.com/cat?width=50&height=50&unique=3",
    "김길돌": "https://cataas.com/cat?width=50&height=50&unique=4"
};

async function fetchUsers() {
    try {
        const res = await fetch(USER_API);
        const users = await res.json();
        renderUsers(users);
    } catch (e) {
        console.error("데이터 로드 실패 ㅠㅠ", e);
    }
}

function renderUsers(users) {
    if(!userList) return;
    userList.innerHTML = "";

    users.forEach(user => {
        const row = document.createElement("div");
        row.className = "user-row";

        // 이름 매핑 대신, 유저의 고유 번호(index)나 이름을 활용해
        // 고유한 고양이 주소를 실시간으로 만듭니다.
        const catImage = `https://cataas.com/cat?width=50&height=50&random=${user.username}`;

        row.innerHTML = `
            <div class="user-info">
                <img src="${catImage}" 
                     class="avatar" 
                     alt="${user.username}"
                     onerror="this.src='https://via.placeholder.com/50?text=Cat'"/>
                <div class="text">
                    <div class="username">${user.username}</div>
                    <div class="email">${user.email}</div>
                </div>
            </div>
            <div class="status-badge ${user.online ? 'online' : 'offline'}">
                ${user.online ? "온라인" : "오프라인"}
            </div>
        `;
        userList.appendChild(row);
    });
}
fetchUsers();