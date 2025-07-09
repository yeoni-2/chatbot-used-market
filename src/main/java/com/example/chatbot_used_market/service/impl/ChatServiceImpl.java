package com.example.chatbot_used_market.service.impl;

import com.example.chatbot_used_market.dto.MessageDto;
import com.example.chatbot_used_market.entity.Chatroom;
import com.example.chatbot_used_market.entity.Message;
import com.example.chatbot_used_market.entity.Trade;
import com.example.chatbot_used_market.entity.User;
import com.example.chatbot_used_market.repository.ChatroomRepository;
import com.example.chatbot_used_market.repository.MessageRepository;
import com.example.chatbot_used_market.repository.TradeRepository;
import com.example.chatbot_used_market.repository.UserRepository;
import com.example.chatbot_used_market.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatServiceImpl implements ChatService {
    private final ChatroomRepository chatroomRepository;
    private final UserRepository userRepository;
    private final TradeRepository tradeRepository;
    private final MessageRepository messageRepository;

    @Override
    @Transactional
    public Chatroom createOrGetChatroom(Long tradeId, Long buyerId) {
        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));
        User seller = trade.getSeller();
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        return chatroomRepository.findByTradeIdAndBuyerIdAndSellerId(trade.getId(), buyer.getId(), seller.getId())
                .orElseGet(() -> {
                    Chatroom newChatroom = new Chatroom(seller, buyer, trade);
                    return chatroomRepository.save(newChatroom);
                });
    }

    @Override
    public List<MessageDto> getMessages(Long chatroomId) {
        return messageRepository.findByChatroomIdOrderByCreatedAtAsc(chatroomId)
                .stream().map(MessageDto::new).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Message createMessage(Long chatroomId, Long senderId, String content) {
        Chatroom chatroom = chatroomRepository.findById(chatroomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다."));
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        Message message = new Message(content, sender, chatroom);

        return messageRepository.save(message);
    }
}
