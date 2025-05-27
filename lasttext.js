// 模拟用户数据存储
let users = [
    { username: 'test', password: 'test' }
];

// 当前登录用户
let currentUser = null;
let currentChat = null;

// 模拟在线用户列表
let onlineUsers = [
    { username: 'Alice', ip: '192.168.1.101' },
    { username: 'Bob', ip: '192.168.1.102' }
];

// DOM 元素
const authContainer = document.getElementById('auth-container');
const chatContainer = document.getElementById('chat-container');
const onlineUsersContainer = document.getElementById('online-users');
const messageContainer = document.getElementById('message-container');
const messageInput = document.getElementById('message-input');
const currentChatUser = document.getElementById('current-chat-user');
const fileInput = document.getElementById('file-input');

// 初始化
function init() {
    updateOnlineUsers();
    setupEventListeners();
}

// 设置事件监听器
function setupEventListeners() {
    messageInput.addEventListener('keypress', (e) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            sendMessage();
        }
    });

    fileInput.addEventListener('change', handleFileSelect);
}

// 切换登录/注册表单
function toggleAuth() {
    const authForm = document.querySelector('.auth-form');
    const isLogin = authForm.querySelector('h2').textContent === '登录';

    if (isLogin) {
        authForm.querySelector('h2').textContent = '注册';
        authForm.querySelector('button').textContent = '注册';
        authForm.querySelector('p').innerHTML = '已有账号？<span onclick="toggleAuth()">登录</span>';
    } else {
        authForm.querySelector('h2').textContent = '登录';
        authForm.querySelector('button').textContent = '登录';
        authForm.querySelector('p').innerHTML = '还没有账号？<span onclick="toggleAuth()">注册</span>';
    }
}

// 登录功能
function login() {
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    const user = users.find(u => u.username === username && u.password === password);

    if (user) {
        currentUser = username;
        authContainer.classList.add('hidden');
        chatContainer.classList.remove('hidden');
        // 模拟添加新用户到在线列表
        onlineUsers.push({ username: username, ip: '192.168.1.' + Math.floor(Math.random() * 255) });
        updateOnlineUsers();
    } else {
        alert('用户名或密码错误！');
    }
}

// 更新在线用户列表
function updateOnlineUsers() {
    onlineUsersContainer.innerHTML = '';
    onlineUsers.forEach(user => {
        if (user.username !== currentUser) {
            const userElement = document.createElement('div');
            userElement.className = 'user-item';
            userElement.textContent = user.username;
            userElement.onclick = () => selectChat(user);
            onlineUsersContainer.appendChild(userElement);
        }
    });
}

// 选择聊天对象
function selectChat(user) {
    currentChat = user;
    currentChatUser.textContent = `${user.username} (${user.ip})`;

    // 移除其他用户的活跃状态
    document.querySelectorAll('.user-item').forEach(item => {
        item.classList.remove('active');
        if (item.textContent === user.username) {
            item.classList.add('active');
        }
    });

    // 清空消息容器
    messageContainer.innerHTML = '';

    // 模拟加载历史消息
    addMessage('你好！', false);
    addMessage('你好！很高兴见到你！', true);
}

// 发送消息
function sendMessage() {
    if (!currentChat) {
        alert('请先选择聊天对象！');
        return;
    }

    const message = messageInput.value.trim();
    if (message) {
        addMessage(message, true);
        messageInput.value = '';

        // 模拟接收回复
        setTimeout(() => {
            addMessage('收到你的消息了！', false);
        }, 1000);
    }
}

// 添加消息到聊天界面
function addMessage(text, isSent) {
    const messageElement = document.createElement('div');
    messageElement.className = `message ${isSent ? 'sent' : 'received'}`;
    messageElement.textContent = text;
    messageContainer.appendChild(messageElement);
    messageContainer.scrollTop = messageContainer.scrollHeight;
}

// 处理文件上传
function uploadFile() {
    fileInput.click();
}

// 处理文件选择
function handleFileSelect(event) {
    const file = event.target.files[0];
    if (file) {
        const fileMessage = document.createElement('div');
        fileMessage.className = 'file-message';
        fileMessage.innerHTML = `
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M14 2H6C4.89543 2 4 2.89543 4 4V20C4 21.1046 4.89543 22 6 22H18C19.1046 22 20 21.1046 20 20V8L14 2Z" stroke="#0084ff" stroke-width="2"/>
            </svg>
            <a href="#">${file.name}</a>
        `;

        const messageElement = document.createElement('div');
        messageElement.className = 'message sent';
        messageElement.appendChild(fileMessage);
        messageContainer.appendChild(messageElement);
        messageContainer.scrollTop = messageContainer.scrollHeight;

        // 清空文件输入
        event.target.value = '';
    }
}

// 初始化应用
init();