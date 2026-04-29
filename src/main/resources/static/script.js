const API_BASE_URL = '/api';

const ENDPOINTS = {
    USERS: `${API_BASE_URL}/user/findAll`,
    PROFILE: `${API_BASE_URL}/binaryContent/find`
};

document.addEventListener('DOMContentLoaded', () => {
    loadUsers();
});

async function loadUsers() {
    const response = await fetch(ENDPOINTS.USERS);
    const users = await response.json();

    renderUsers(users);
}

async function getProfile(profileId) {
    if (!profileId) {
        return '/default-avatar.png';
    }

    try {
        const response = await fetch(
            `${ENDPOINTS.PROFILE}?binaryContentId=${profileId}`
        );

        const profile = await response.json();

        return `data:${profile.contentType};base64,${profile.bytes}`;
    } catch (e) {
        return '/default-avatar.png';
    }
}

async function renderUsers(users) {
    const userList = document.getElementById('userList');
    userList.innerHTML = '';

    let number = 1;

    for (const user of users) {
        const profileUrl = await getProfile(user.profileId);

        const div = document.createElement('div');
        div.className = 'user-item';

        div.innerHTML = `
            <div class="user-left">
                <div class="user-number">${String(number).padStart(2, '0')}</div>

                <div class="user-info">
                    <div class="user-name">${user.username}</div>
                    <div class="user-email">${user.email}</div>
                </div>
            </div>

            <div class="user-right">
                <div class="status ${user.online ? '' : 'offline'}">
                    ${user.online ? '온라인' : '오프라인'}
                </div>

                <img src="${profileUrl}" class="user-avatar">
            </div>
        `;

        userList.appendChild(div);
        number++;
    }
}