const USER_API = "/api/user/findAll";
const BINARY_API = "/api/binaryContent/find";

document.addEventListener("DOMContentLoaded", () => {
    fetchAndRenderUsers();
});

async function fetchAndRenderUsers() {
    try {
        const response = await fetch(USER_API);

        if (!response.ok) {
            throw new Error("Failed to fetch users");
        }

        const users = await response.json();
        await renderUserList(users);
    } catch (error) {
        console.error(error);
    }
}

async function fetchProfileImage(profileId) {
    try {
        const response = await fetch(`${BINARY_API}?binaryContentId=${profileId}`);

        if (!response.ok) {
            throw new Error("Failed to fetch binary content");
        }

        const binaryContent = await response.json();

        return `data:${binaryContent.mimeType};base64,${base64FromBytes(binaryContent.data)}`;
    } catch (error) {
        console.error(error);
        return defaultAvatar();
    }
}

async function renderUserList(users) {
    const userListElement = document.getElementById("userList");
    userListElement.innerHTML = "";

    for (const user of users) {
        const userElement = document.createElement("div");
        userElement.className = "user-item";

        let profileUrl = defaultAvatar();

        if (user.profileId) {
            profileUrl = await fetchProfileImage(user.profileId);
        }

        userElement.innerHTML = `
            <img src="${profileUrl}" alt="${user.username}" class="user-avatar">
            <div class="user-info">
                <div class="user-name">${user.username}</div>
                <div class="user-email">${user.email}</div>
            </div>
            <div class="status-badge ${user.online ? "online" : "offline"}">
                ${user.online ? "온라인" : "오프라인"}
            </div>
        `;

        userListElement.appendChild(userElement);
    }
}

function base64FromBytes(bytes) {
    let binary = "";
    for (let i = 0; i < bytes.length; i++) {
        binary += String.fromCharCode(bytes[i] & 0xff);
    }
    return btoa(binary);
}

function defaultAvatar() {
    const svg = `
        <svg xmlns="http://www.w3.org/2000/svg" width="72" height="72">
            <rect width="100%" height="100%" fill="#dddddd"/>
            <circle cx="36" cy="26" r="14" fill="#bbbbbb"/>
            <rect x="16" y="44" width="40" height="18" rx="9" fill="#bbbbbb"/>
        </svg>
    `;
    return "data:image/svg+xml;base64," + btoa(svg);
}