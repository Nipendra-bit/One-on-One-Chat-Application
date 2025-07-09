'use strict';

// DOM Elements
const usernamePage = document.querySelector('#username-page');
const chatPage = document.querySelector('#chat-page');
const usernameForm = document.querySelector('#usernameForm');
const messageForm = document.querySelector('#messageForm');
const messageInput = document.querySelector('#message');
const connectingElement = document.querySelector('.connecting');
const chatArea = document.querySelector('#chat-messages');
const logout = document.querySelector('#logout');

// App Variables
let stompClient = null;
let nickname = null;
let fullname = null;
let selectedUserId = null;

// Connect and register user
function connect(event) {
    nickname = document.querySelector('#nickname').value.trim();
    fullname = document.querySelector('#fullname').value.trim();

    if (nickname && fullname) {
        usernamePage.classList.add('hidden');
        chatPage.classList.remove('hidden');

        const socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, onConnected, onError);
    }

    event.preventDefault();
}

// On successful connection
function onConnected() {
    // Subscribe to private message queue
    stompClient.subscribe(`/user/${nickname}/queue/messages`, onMessageReceived);

    // Notify server about the connected user
    stompClient.send("/app/user.addUser", {}, JSON.stringify({
        nickName: nickname,
        fullName: fullname,
        status: 'ONLINE'
    }));

    document.querySelector('#connected-user-fullname').textContent = fullname;

    // Load users
    findAndDisplayConnectedUsers();
}

// Handle connection error
function onError() {
    if (connectingElement) {
        connectingElement.textContent = '⚠️ Could not connect to the server. Please refresh!';
        connectingElement.style.color = 'red';
    }
}

// Fetch online users and display
async function findAndDisplayConnectedUsers() {
    const res = await fetch('/users');
    let users = await res.json();

    users = users.filter(user => user.nickName !== nickname); // Exclude self
    const list = document.getElementById('connectedUsers');
    list.innerHTML = '';

    users.forEach((user, index) => {
        appendUserElement(user, list);

        // Add separator except after the last one
        if (index < users.length - 1) {
            const separator = document.createElement('li');
            separator.classList.add('separator');
            list.appendChild(separator);
        }
    });
}

// Append a user element to sidebar
function appendUserElement(user, list) {
    const item = document.createElement('li');
    item.classList.add('user-item');
    item.id = user.nickName;

    const avatar = document.createElement('img');
    avatar.src = '/img/user_icon.png';
    avatar.alt = user.fullName;

    const nameSpan = document.createElement('span');
    nameSpan.textContent = user.fullName;

    const msgBadge = document.createElement('span');
    msgBadge.textContent = '0';
    msgBadge.classList.add('nbr-msg', 'hidden');

    item.appendChild(avatar);
    item.appendChild(nameSpan);
    item.appendChild(msgBadge);

    item.addEventListener('click', userItemClick);

    list.appendChild(item);
}

// User click to start chat
function userItemClick(event) {
    document.querySelectorAll('.user-item').forEach(item => item.classList.remove('active'));
    messageForm.classList.remove('hidden');

    const clickedUser = event.currentTarget;
    clickedUser.classList.add('active');
    selectedUserId = clickedUser.getAttribute('id');

    fetchAndDisplayUserChat();

    // Reset badge
    const badge = clickedUser.querySelector('.nbr-msg');
    badge.classList.add('hidden');
    badge.textContent = '0';
}

// Display a single message bubble
function displayMessage(senderId, content) {
    const container = document.createElement('div');
    container.classList.add('message');
    container.classList.add(senderId === nickname ? 'sender' : 'receiver');

    const avatar = document.createElement('img');
    avatar.src = '../img/user_icon.png';
    avatar.alt = senderId;
    avatar.classList.add('profile-pic');

    const text = document.createElement('p');
    text.textContent = content;

    container.appendChild(avatar);
    container.appendChild(text);
    chatArea.appendChild(container);

    chatArea.scrollTop = chatArea.scrollHeight;
}

// Fetch and display all messages with the selected user
async function fetchAndDisplayUserChat() {
    const response = await fetch(`/messages/${nickname}/${selectedUserId}`);
    const messages = await response.json();

    chatArea.innerHTML = '';

    messages.forEach(chat => displayMessage(chat.senderId, chat.content));

    chatArea.scrollTop = chatArea.scrollHeight;
}

// Handle incoming message
async function onMessageReceived(payload) {
    await findAndDisplayConnectedUsers(); // Refresh sidebar

    const message = JSON.parse(payload.body);

    if (selectedUserId === message.senderId) {
        displayMessage(message.senderId, message.content);
    }

    // Update active state
    if (selectedUserId) {
        document.querySelector(`#${selectedUserId}`)?.classList.add('active');
    } else {
        messageForm.classList.add('hidden');
    }

    // Notification badge logic
    const userElement = document.querySelector(`#${message.senderId}`);
    if (userElement && !userElement.classList.contains('active')) {
        const badge = userElement.querySelector('.nbr-msg');
        badge.classList.remove('hidden');
        badge.textContent = ''; // or increment if needed
    }
}

// Send message
function sendMessage(event) {
    const messageContent = messageInput.value.trim();

    if (messageContent && stompClient) {
        const chatMessage = {
            senderId: nickname,
            recipientId: selectedUserId,
            content: messageContent
        };

        stompClient.send("/app/chat", {}, JSON.stringify(chatMessage));
        displayMessage(nickname, messageContent);
        messageInput.value = '';
    }

    chatArea.scrollTop = chatArea.scrollHeight;
    event.preventDefault();
}

// Handle logout
function onLogout() {
    if (stompClient) {
        stompClient.send("/app/user.disconnectUser", {}, JSON.stringify({
            nickName: nickname,
            fullName: fullname,
            status: 'OFFLINE'
        }));
    }

    window.location.reload();
}

// Event Listeners
usernameForm.addEventListener('submit', connect, true);
messageForm.addEventListener('submit', sendMessage, true);
logout.addEventListener('click', onLogout, true);
window.onbeforeunload = () => onLogout();

// Theme toggle
const themeToggle = document.getElementById('themeToggle');

// Load saved theme from localStorage
if (localStorage.getItem('chat-theme') === 'dark') {
    document.body.classList.add('dark-mode');
    themeToggle.checked = true;
}

themeToggle.addEventListener('change', () => {
    document.body.classList.toggle('dark-mode');
    if (document.body.classList.contains('dark-mode')) {
        localStorage.setItem('chat-theme', 'dark');
    } else {
        localStorage.setItem('chat-theme', 'light');
    }
});

