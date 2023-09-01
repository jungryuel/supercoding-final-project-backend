package com.github.supercodingfinalprojectbackend.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import org.hibernate.annotations.CreationTimestamp;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Entity
@Table(name = "chat_rooms")
public class ChatRoom {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "chat_room_id",nullable = false)
    private Long chatRoomId;

    @Column(name = "chat_name")
    private String  chatName;

    @Column(name = "is_chat")
    private Boolean isChat;

    @CreationTimestamp
    @Column(name = "created_at")
    private Timestamp createdAt;
}
