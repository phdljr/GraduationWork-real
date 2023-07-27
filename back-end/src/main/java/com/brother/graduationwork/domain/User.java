package com.brother.graduationwork.domain;

import com.brother.graduationwork.domain.Status.RoomStatus;
import com.brother.graduationwork.exception.CustomException;
import com.brother.graduationwork.exception.ErrorCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@NamedEntityGraph(
        name = "user.room.orderList",
        attributeNodes = {
                @NamedAttributeNode("room"),
        }
)
@Entity
@Getter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue
    @Column(name = "USER_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ROOM_ID")
    private Room room;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private final List<Order> orderList = new ArrayList<>();

    private boolean joinRoom;

    private String email;

    private String pw;

    private String nickname;

    private String phoneNumber;

    private int money;

    private int totalAmount;

    @Enumerated(EnumType.STRING)
    private RoomStatus roomStatus;

    public User(String user_email, String user_pw, String nickname, String phoneNumber) {
        this.email = user_email;
        this.pw = user_pw;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.money = 0;
        this.totalAmount = 0;
        this.joinRoom = false;
        this.roomStatus = RoomStatus.RECRUIT;
    }

    public void increaseMoney(int chargedMoney) {
        this.money += chargedMoney;
    }

    public int decreaseMoney(int pay) {
        int result = 1;
        if (pay > this.money)
            result = 0;
        else
            this.money -= pay;

        return result;
    }

    public boolean isJoinRoom() {
        return joinRoom;
    }

    public void setJoinRoom(boolean joinRoom) {
        this.joinRoom = joinRoom;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public void exitRoom() {
        this.setJoinRoom(false);
        this.resetTotalAmount();
        this.setRoom(null);
        this.setRoomStatus(RoomStatus.RECRUIT);
    }

    public void addTotalAmount(int amount) {
        this.totalAmount += amount;
    }

    public void resetTotalAmount() {
        this.totalAmount = 0;
    }

    public void setRoomStatus(RoomStatus roomStatus) {
        this.roomStatus = roomStatus;
    }
}
