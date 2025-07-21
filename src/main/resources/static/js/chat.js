let ws;
let currentChatroomId = null;
let currentPage = 0;
let isLastPage = false;
let isLoading = false;

const chatroomListDiv = document.getElementById('chatroom-list');
const messageContainer = document.getElementById('message-container');
const messageListContainer = document.getElementById('message-list');
const messageForm = document.getElementById('message-form');
const messageInput = document.getElementById('message-input');

document.addEventListener('DOMContentLoaded', function () {
    loadChatrooms();

    messageForm.addEventListener('submit', function (event) {
        event.preventDefault();

        if (currentChatroomId === 'chatbot')
            sendChatbotMessage();
        else
            sendRealtimeMessage();
    });

    setupIntersectionObserver();
});

function formatTime(timeString) {
    if (!timeString) return '';

    const now = new Date();
    const messageTime = new Date(timeString);
    const diffSeconds = Math.floor((now - messageTime) / 1000);
    const diffMinutes = Math.floor(diffSeconds / 60);

    if (diffSeconds < 60) return '방금 전';
    if (diffMinutes < 60) return `${diffMinutes}분 전`;

    return `${messageTime.getFullYear()}-${messageTime.getMonth() + 1}-${messageTime.getDate()}`;
}

function loadChatrooms() {
    fetch('/chats')
        .then(response => response.json())
        .then(data => {
            const chatroomListDiv = document.getElementById('chatroom-list');
            const chatbotItem = document.createElement('div');

            chatroomListDiv.innerHTML = '';
            chatbotItem.className = 'chatroom-item';
            chatbotItem.innerHTML = `
                <div class="profile-pic-container">
                    <span class="chatbot-icon">🤖</span>
                </div>
                <div class="chatroom-info">
                    <div class="info-header">
                        <span class="nickname">챗봇 상담사</span>
                    </div>
                    <div class="last-message">궁금한 점을 물어보세요!</div>
                </div>
            `;
            chatbotItem.dataset.chatroomId = 'chatbot';
            chatbotItem.addEventListener('click', () => loadChatbotUI());
            chatroomListDiv.appendChild(chatbotItem);

            data.forEach(chatroom => {
                const chatroomElement = document.createElement('div');

                chatroomElement.className = 'chatroom-item';
                chatroomElement.innerHTML = `
                    <div class="profile-pic-container">
                        <img src="${chatroom.opponentProfileImgUrl || '/images/default-profile.png'}" alt="profile" class="profile-pic">
                    </div>
                    <div class="chatroom-info">
                        <div class="info-header">
                            <span class="nickname">${chatroom.opponentNickname}</span>
                            <span class="time">${formatTime(chatroom.lastMessageTime)}</span>
                        </div>
                        <div class="last-message">${chatroom.lastMessage}</div>
                    </div>
                `;
                chatroomElement.dataset.chatroomId = chatroom.chatroomId;
                chatroomElement.addEventListener('click', () => loadMessages(chatroom.chatroomId));
                chatroomListDiv.appendChild(chatroomElement);
            });
        });
}

function loadChatbotUI() {
    if (currentChatroomId === 'chatbot') return;

    currentChatroomId = 'chatbot';
    currentPage = 0;
    isLastPage = false;
    messageListContainer.innerHTML = '';
    document.getElementById('trade-header').innerHTML = '';
    messageForm.style.display = 'flex';

    loadMoreChatbotMessages();

    if (ws) ws.close();
}

function setupIntersectionObserver() {
    const trigger = document.getElementById('scroll-trigger');

    const observer = new IntersectionObserver((entries, observer) => {
        const entry = entries[0];

        if (entry.isIntersecting && !isLoading && !isLastPage) {
            if (currentChatroomId === 'chatbot')
                loadMoreChatbotMessages();
            else if (currentChatroomId !== null)
                loadMoreMessages();
        }
    }, {
        root: messageContainer,
        threshold: 1.0
    });

    observer.observe(trigger);
}

function loadMoreChatbotMessages() {
    if (isLastPage || isLoading) return;
    isLoading = true;

    const oldScrollHeight = messageContainer.scrollHeight;

    fetch(`/chatbot/messages?page=${currentPage}&size=20`)
        .then(response => response.json())
        .then(data => {
            if (data.content && data.content.length > 0) {
                if (currentPage === 0) {
                    data.content.reverse();
                    data.content.forEach(message => {
                        appendMessage({ senderNickname: '나', content: message.userMessage }, false);
                        appendMessage({ senderNickname: '챗봇', content: message.botResponse  }, false);
                    });
                    messageContainer.scrollTop = messageContainer.scrollHeight;
                } else {
                    data.content.forEach(message => {
                        appendMessage({ senderNickname: '챗봇', content: message.botResponse }, true);
                        appendMessage({ senderNickname: '나', content: message.userMessage }, true);
                    });
                    messageContainer.scrollTop = messageContainer.scrollHeight - oldScrollHeight;
                }
            } else if (currentPage === 0)
                appendMessage({ senderNickname: '챗봇', content: '안녕하세요! 무엇을 도와드릴까요?' });

            isLastPage = data.last;
            currentPage++;

            setTimeout(() => {
                if (messageContainer.scrollHeight <= messageContainer.clientHeight && !isLastPage)
                    loadMoreChatbotMessages();
            }, 100);
        })
        .finally(() => { isLoading = false; });
}

function sendChatbotMessage() {
    const userMessage = messageInput.value.trim();

    if (userMessage === '') return;

    appendMessage({ senderNickname: '나', content: userMessage });
    messageContainer.scrollTop = messageContainer.scrollHeight;
    messageInput.value = '';

    fetch('/chatbot/messages', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ userMessage: userMessage })
    })
    .then(response => response.json())
    .then(data => {
        appendMessage({ senderNickname: '챗봇', content: data.reply });
        messageContainer.scrollTop = messageContainer.scrollHeight;
    });
}

function loadMessages(chatroomId) {
    if (currentChatroomId === chatroomId) return;

    currentChatroomId = chatroomId;
    currentPage = 0;
    isLastPage = false;
    messageListContainer.innerHTML = '';
    messageForm.style.display = 'flex';
    loadTradeHeader(chatroomId);
    loadMoreMessages();
    connectWebSocket();
}

function loadTradeHeader(chatroomId) {
    const headerDiv = document.getElementById('trade-header');

    fetch(`/chats/${chatroomId}/details`)
        .then(response => response.json())
        .then(details => {
            let buttonHtml = '';

            if (loginUserId === details.sellerId && details.tradeStatus === '판매중') {
                buttonHtml = `<button class="trade-confirm-btn" data-trade-id="${details.tradeId}">거래확정하기</button>`;
            } else if (details.tradeStatus === '거래완료') {
                buttonHtml = `<button class="trade-complete-btn" disabled>거래완료</button>`;
            }

            headerDiv.innerHTML = `
                <div class="trade-info">
                    <img src="${details.tradeThumbnailUrl || '/images/default-image.png'}" alt="trade-thumb" class="trade-thumbnail">
                    <div class="trade-details">
                        <span class="trade-title">${details.tradeTitle}</span>
                        <span class="trade-price">${details.tradePrice.toLocaleString()}원</span>
                    </div>
                    ${buttonHtml}
                </div>
            `;

            const confirmBtn = headerDiv.querySelector('.trade-confirm-btn');
            if (confirmBtn) {
                confirmBtn.addEventListener('click', handleTradeConfirm);
            }
        });
}

function handleTradeConfirm(event) {
    const tradeId = event.target.dataset.tradeId;
    if (!confirm('거래를 확정하시겠습니까?')) return;

    fetch(`/trades/${tradeId}/status`, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ status: '거래완료' })
    })
    .then(response => {
        if (!response.ok) throw new Error('상태 변경 실패');
        return response.json();
    })
    .then(updatedTrade => {
        const button = event.target;
        button.textContent = '거래완료';
        button.className = 'trade-complete-btn';
        button.disabled = true;
        alert('거래가 완료되었습니다.');
    })
    .catch(error => alert(error.message));
}

function connectWebSocket() {
    if (ws) ws.close();
    ws = new WebSocket("ws://localhost:8080/ws/chat");
    ws.onopen = () => ws.send(JSON.stringify({ type: 'JOIN', chatroomId: currentChatroomId }));
    ws.onmessage = (event) => {
        const message = JSON.parse(event.data);

        appendMessage(message);
        messageContainer.scrollTop = messageContainer.scrollHeight;
    };
}

function loadMoreMessages() {
    if (isLastPage || isLoading) return;
    isLoading = true;

    const oldScrollHeight = messageContainer.scrollHeight;

    fetch(`/chats/${currentChatroomId}/messages?page=${currentPage}&size=20`)
        .then(response => response.json())
        .then(data => {
            if (data.content && data.content.length > 0) {
                if (currentPage === 0) {
                    data.content.reverse();
                    data.content.forEach(message => {
                        appendMessage(message, false);
                    });
                    messageContainer.scrollTop = messageContainer.scrollHeight;
                } else {
                    data.content.forEach(message => {
                        appendMessage(message, true);
                    });
                    messageContainer.scrollTop = messageContainer.scrollHeight - oldScrollHeight;
                }
            }

            isLastPage = data.last;
            currentPage++;

            setTimeout(() => {
                if (messageContainer.scrollHeight <= messageContainer.clientHeight && !isLastPage)
                    loadMoreMessages();
            }, 100);
        })
        .catch(error => console.error('Error:', error))
        .finally(() => {
            isLoading = false;
        });
}

function sendRealtimeMessage() {
    const messageContent = messageInput.value.trim();

    if (messageContent !== "" && ws && ws.readyState === WebSocket.OPEN) {
        const talkMessage = {
        type: 'TALK',
        chatroomId: currentChatroomId,
        senderId: loginUserId,
        content: messageContent
        };

        ws.send(JSON.stringify(talkMessage));
        messageInput.value = '';
    }
}

function appendMessage(message, prepend = false) {
    const messageRow = document.createElement('div');
    const messageBubble = document.createElement('div');

    messageRow.className = 'message-row';
    if (loginUserId === message.senderId || message.senderNickname === '나')
        messageRow.classList.add('my-message-row');
    else
        messageRow.classList.add('opponent-message-row');
    messageBubble.className = 'message-bubble';
    messageBubble.textContent = message.content;
    messageRow.appendChild(messageBubble);

    if (message.senderNickname === '챗봇') {
        const ttsButton = document.createElement('button');

        ttsButton.className = 'tts-button';
        ttsButton.textContent = '🔊';
        ttsButton.addEventListener('click', () => {
            speechSynthesis.cancel();
            const utterance = new SpeechSynthesisUtterance(message.content);

            utterance.lang = 'ko-KR';
            speechSynthesis.speak(utterance);
        });
        messageRow.appendChild(ttsButton);
    }

    if (prepend)
        messageListContainer.prepend(messageRow);
    else
        messageListContainer.appendChild(messageRow);
}